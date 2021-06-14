package by.katz;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static by.katz.Utils.readFromStream;

public class MyPacServer {

    private static final InetSocketAddress pacServerAddress = new InetSocketAddress(Settings.getInstance().getPacServerPort());
    private static final int N_THREADS = 100;
    private static MyPacServer instance = new MyPacServer();

    private String proxyHost = "localhost";
    private String proxyPort = "0";
    private HttpServer httpServer;
    private ExecutorService httpThreadPool;


    public static MyPacServer getInstance() {
        if (instance == null)
            instance = new MyPacServer();
        return instance;
    }

    public void setProxy(String proxyHost, String proxyPort) {
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
    }

    private MyPacServer() {
        try {
            startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startServer() throws IOException {
        httpServer = HttpServer.create(pacServerAddress, 0);
        httpServer.createContext("/test", httpExchange -> {
            final String readed = readFromStream(httpExchange.getRequestBody());
            final String strResponse = "This is the response: " + readed;
            httpExchange.sendResponseHeaders(200, strResponse.length());
            final OutputStream os = httpExchange.getResponseBody();
            os.write(strResponse.getBytes());
            os.close();
        });
        httpThreadPool = Executors.newFixedThreadPool(N_THREADS);
        httpServer.createContext("/", new PacReqHandler());
        httpServer.setExecutor(httpThreadPool);
        httpServer.start();
    }

    @SuppressWarnings("unused")
    private void stopServer() {
        httpServer.stop(1);
        httpThreadPool.shutdownNow();
    }

    private class PacReqHandler implements HttpHandler {

        @Override public void handle(HttpExchange httpExchange) throws IOException {
            System.out.println("request handler");
            final String response = getResponse()
                    .replace("%SERVER%", proxyHost)
                    .replace("%PORT%", proxyPort);
            httpExchange.sendResponseHeaders(200, response.length());
            final OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    private static String getResponse() throws IOException {
        final InputStream stream = MyPacServer.class
                .getClassLoader()
                .getResourceAsStream("pacResponse.txt");
        return stream != null ? Utils.readFromStream(stream) : "";
    }
}