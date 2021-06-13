package by.katz.proxy;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ProxyCollector {

    private final static Type TYPE_HASHMAP = new TypeToken<List<ProxyItem>>() {}.getType();
    private static final String URL_PROXIES = "https://raw.githubusercontent.com/fate0/proxylist/master/proxy.list";

    public ProxyCollector(IProxiesCallback callback) {

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
            callback.onGetProxiesList(proxies.size());

            // sort by resp time
            proxies.sort(Comparator.comparing(ProxyItem::getResponseTime));
            // some viebons
            proxies = proxies.stream()
                    .filter(p -> p.getType().equals("https") || p.getType().equals("http"))
                    .limit(500)
                    .collect(Collectors.toList());
            // send to check
            new ProxyChecker(proxies, callback);
        } catch (IOException e) { callback.onError("Cant collect " + e.getLocalizedMessage());}
    }

    private static String getParsedData(String json) {
        // magic
        json = "[" + json + "]";
        json = json.replaceAll("}", "},");
        json = json.replace("},]", "}]");
        return json;
    }

}
