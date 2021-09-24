package by.katz.proxy;

import by.katz.Settings;
import com.google.gson.GsonBuilder;
import lombok.extern.java.Log;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static by.katz.proxy.ProxyItem.MAX_TIMEOUT;

@Log
class ProxyChecker {

    private static final String FILE_FOR_PROXIES = "proxies_good.json";
    private static final int N_THREADS = Settings.getInstance().getThreadCount();

    ProxyChecker(List<ProxyItem> allProxies, IProxiesCallback callback) {

        final ExecutorService threadPool = Executors.newFixedThreadPool(N_THREADS);
        final CountDownLatch cdl = new CountDownLatch(allProxies.size());
        final List<ProxyItem> goodProxies = new ArrayList<>();

        allProxies.forEach(p -> threadPool.submit(() -> {
            callback.onOneProxyCheck();
            if (p.checkProxyByRealSites() && cdl.getCount() > 0) {
                goodProxies.add(p);
                callback.onOneFullValidProxyFound();
            }
            cdl.countDown();
        }));
        threadCheck(cdl);
        try { cdl.await(); } catch (InterruptedException ignored) { }
        threadPool.shutdown();

        // sort
        final Comparator<ProxyItem> comparator = Comparator.comparing(ProxyItem::getType)
            .reversed()
            .thenComparing(ProxyItem::getResponseTime);
        final List<ProxyItem> sortedProxies = goodProxies.stream()
            .sorted(comparator)
            .collect(Collectors.toList());

        saveProxiesToJson(sortedProxies);
        callback.onCompleteProxiesCheck(sortedProxies);
    }

    private void threadCheck(CountDownLatch latch) {
        new Thread(() -> {
            long lastCheck = latch.getCount();
            while (latch.getCount() != 0) {
                try { Thread.sleep(MAX_TIMEOUT + MAX_TIMEOUT / 10); } catch (InterruptedException ignored) { }
                if (lastCheck == latch.getCount()) {
                    log.severe("timeout error " + latch.getCount());
                    latch.countDown();
                } else lastCheck = latch.getCount();
            }
        }).start();
    }

    private void saveProxiesToJson(List<ProxyItem> goodProxies) {
        try (FileWriter fw = new FileWriter(FILE_FOR_PROXIES)) {
            fw.write(new GsonBuilder().setPrettyPrinting().create().toJson(goodProxies));
        } catch (Exception ignored) {}
    }
}
