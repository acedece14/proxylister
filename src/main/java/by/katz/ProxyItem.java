package by.katz;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.List;

@Getter
public class ProxyItem {

    private static final String[] URL_STRINGS = {
            "http://lurkmore.to",
            //"https://www.tut.by",
    };
    public static final int MAX_TIMEOUT = 3000;

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
        for (String url : URL_STRINGS) {
            if (!checkProxyOnUrl(url))
                return false;
        }
        return true;
    }

    boolean checkProxyOnUrl(String urlString) {
        try {
            if (type.equals("https") || type.equals("http")) {
                long startTime = System.currentTimeMillis();
                final URL weburl = new URL(urlString);
                final Proxy webProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
                HttpURLConnection.setFollowRedirects(false);
                final HttpURLConnection con = (HttpURLConnection) weburl.openConnection(webProxy);
                con.setConnectTimeout(MAX_TIMEOUT);
                con.setReadTimeout(MAX_TIMEOUT);
                if (con.getInputStream() == null)
                    return false;
                final InputStream stream = con.getInputStream();

                int len;
                if ((len = stream.available()) > 1000) {
                    byte[] readedBytes = new byte[len];
                    int res = stream.read(readedBytes, 0, len);
                    con.disconnect();
                    setResponseTime((double) (System.currentTimeMillis() - startTime));
                    String readed = new String(readedBytes);

                    return getResponseTime() <= MAX_TIMEOUT;
                } else con.disconnect();
            }
        } catch (IOException ignored) { }
        return false;
    }
}
