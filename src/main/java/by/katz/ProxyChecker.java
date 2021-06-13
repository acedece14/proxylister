package by.katz;

import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

class ProxyChecker {

    private static final String FILE_FOR_PROXIES = "proxies_good.json";
    private static final int N_THREADS = Settings.getInstance().getThreadCount();

    ProxyChecker(List<ProxyItem> proxies, ICallback callback) {

        ExecutorService threadPool = Executors.newFixedThreadPool(N_THREADS);

        List<Future<ProxyItem>> futures = new ArrayList<>();
        for (ProxyItem p : proxies)
            futures.add(CompletableFuture.supplyAsync(()
                    -> p.checkProxy() ? p : null, threadPool));

        List<ProxyItem> goodProxies = new ArrayList<>();

        for (Future<ProxyItem> future : futures) {
            try {
                if (future.get() != null)
                    goodProxies.add(future.get());
            } catch (InterruptedException | ExecutionException e) { e.printStackTrace(); }
        }
        threadPool.shutdown();

        // sort
        Comparator<ProxyItem> comparator = Comparator.comparing(ProxyItem::getType)
                .reversed()
                .thenComparing(ProxyItem::getResponseTime);
        goodProxies = goodProxies.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
        saveProxiesToJson(goodProxies);

        callback.onComplete(goodProxies);
    }

    private void saveProxiesToJson(List<ProxyItem> goodProxies) {

        try (FileWriter fw = new FileWriter(FILE_FOR_PROXIES)) {
            fw.write(new GsonBuilder().setPrettyPrinting().create().toJson(goodProxies));
        } catch (Exception ignored) {}
    }

}
