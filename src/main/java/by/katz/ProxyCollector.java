package by.katz;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.jsoup.Jsoup;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ProxyCollector {

    private final static Type TYPE_HASHMAP = new TypeToken<List<ProxyItem>>() {}.getType();
    private static final String URL_PROXIES = "https://raw.githubusercontent.com/fate0/proxylist/master/proxy.list";

    public ProxyCollector(Callback main) {
        collect(main);
    }

    public void collect(Callback main) {


        try {
            // getting data
            String json = Jsoup.connect(URL_PROXIES)
                    .ignoreContentType(true)
                    .get()
                    .body()
                    .text();
            json = getParsedData(json);

            // convert json 2 objs
            List<ProxyItem> proxies = new Gson().fromJson(json, TYPE_HASHMAP);

            System.out.println("Total:" + proxies.size());
            main.onGet(proxies.size());

            // sort by resp time
            proxies.sort(Comparator.comparing(ProxyItem::getResponseTime));
            // some viebons
            proxies = proxies.stream()
                    .filter(p -> p.getType().equals("https") || p.getType().equals("http"))
                    .limit(500)
                    .collect(Collectors.toList());

            System.out.println("--- 2");
            // proxies.forEach(System.out::println);
            new ProxyChecker(proxies, main);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private static String getParsedData(String json) {
        // magic
        json = "[" + json + "]";
        json = json.replaceAll("}", "},");
        json = json.replace("},]", "}]");
        return json;
    }

    static class ProxyChecker {

        private static final String FILE_FOR_PROXIES = "proxies_good.json";

        ProxyChecker(List<ProxyItem> proxies, Callback main) {

            ExecutorService threadPool = Executors.newFixedThreadPool(100);

            List<Future<ProxyItem>> futures = new ArrayList<>();
            for (ProxyItem p : proxies)
                futures.add(CompletableFuture.supplyAsync(() -> {
                    if (p.checkProxy())
                        return p;
                    return null;
                }, threadPool));


            List<ProxyItem> goodProxies = new ArrayList<>();

            for (Future<ProxyItem> future : futures) {
                try {
                    if (future.get() != null)
                        goodProxies.add(future.get());
                } catch (InterruptedException | ExecutionException e) { e.printStackTrace(); }
            }
            threadPool.shutdown();

            Comparator<ProxyItem> comparator = Comparator.comparing(ProxyItem::getType)
                    .reversed()
                    .thenComparing(ProxyItem::getResponseTime);
            goodProxies = goodProxies.stream()
                    .sorted(comparator)
                    .collect(Collectors.toList());
            saveProxiesToJson(goodProxies);

            System.out.println("--------");
            goodProxies.forEach(System.out::println);
            main.onComplete(goodProxies);
        }

        private void saveProxiesToJson(List<ProxyItem> goodProxies) {

            try (FileWriter fw = new FileWriter(FILE_FOR_PROXIES)) {
                fw.write(new GsonBuilder().setPrettyPrinting().create().toJson(goodProxies));
            } catch (Exception ignored) {}
        }

    }
}
