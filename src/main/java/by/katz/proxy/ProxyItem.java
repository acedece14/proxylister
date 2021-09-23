package by.katz.proxy;

import by.katz.Settings;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.List;

@Getter
public class ProxyItem {
    /*
        private static final String[] URL_STRINGS = {
                "http://lurkmore.to",
                //"https://www.tut.by",
        };*/
    public static final int MAX_TIMEOUT = Settings.getInstance().getTimeToCheck();

    @Setter
    @SerializedName("response_time") private Double responseTime;
    @SerializedName("port") private Integer port;
    @SerializedName("anonymity") private String anonymity;
    @SerializedName("host") private String host;
    @SerializedName("export_address") private List<String> exportAddress = null;
    @SerializedName("from") private String from;
    @SerializedName("country") private String country;
    @SerializedName("type") private String type;

    @Override public String toString() {
        return String.format("Proxy: resp= %-10s\t%s:%-20d\t'%-15s'\t'%s'}",
            responseTime, host, port, type, country);
    }

    boolean checkProxy() {
        final List<String> sitesToCheck = Settings.getInstance().getSitesToCheck();
        for (String url : sitesToCheck) {
            if (!checkProxyOnUrl(url))
                return false;
        }
        return true;
    }

    private boolean checkProxyOnUrl(String urlString) {
        final long startTime = System.currentTimeMillis();
        try {
            Proxy webProxy;
            if (type.equals("https") || type.equals("http"))
                webProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
            else if (type.contains("sock"))
                webProxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(host, port));
            else webProxy = new Proxy(Proxy.Type.DIRECT, new InetSocketAddress(host, port));

            final Document res = Jsoup.connect(urlString)
                .followRedirects(false)
                .proxy(webProxy)
                .timeout(MAX_TIMEOUT)
                .get();
            setResponseTime((double) (System.currentTimeMillis() - startTime));
            return res.head().toString().contains(new URL(urlString).getHost());
        } catch (IOException ignored) { setResponseTime(-1.0);}
        return false;
    }
}
