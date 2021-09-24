package by.katz.proxy;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.java.Log;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Log
public class ProxyCollector {

    private final static Type TYPE_HASHMAP = new TypeToken<List<ProxyItem>>() {}.getType();
    private static final String URL_PROXIES = "https://raw.githubusercontent.com/fate0/proxylist/master/proxy.list";
    public static final int MAX_PROXYLIST_SIZE = 500;

    public ProxyCollector(IProxiesCallback callback) {

        try {
            // getting data
            String json = Jsoup.connect(URL_PROXIES)
                .ignoreContentType(true)
                .followRedirects(false)
                .get()
                .body()
                .text();
            json = getParsedData(json);

            // convert json2objs
            List<ProxyItem> proxies = new Gson().fromJson(json, TYPE_HASHMAP);

            log.info("Total proxies in list: " + proxies.size());
            callback.onGetProxiesList(proxies.size());

            // some viebons
            proxies = proxies.stream()
                .sorted(Comparator.comparing(ProxyItem::getResponseTime))
                // .filter(p -> p.getType().equals("https") || p.getType().equals("http"))
                .limit(MAX_PROXYLIST_SIZE)
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
