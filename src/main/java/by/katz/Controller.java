package by.katz;

import by.katz.gui.FormMain;
import by.katz.proxy.IProxiesCallback;
import by.katz.proxy.ProxyCollector;
import by.katz.proxy.ProxyItem;

import java.util.List;

public class Controller
        implements IProxiesCallback {

    private FormMain formMain;

    private boolean running = false;
    private int checkCounter = 0;
    private int checkTotal = 0;
    private int validFound = 0;
    private long startTime;


    public Controller() {
        formMain = new FormMain(this);
    }

    public void logStatus(String text) { formMain.setStatus(text); }

    public void startScan() {
        if (!running) {
            running = true;
            checkCounter = 0;
            validFound = 0;
            startTime = System.currentTimeMillis();
            new Thread(() -> {
                logStatus("Update starting...");
                new ProxyCollector(this);
            }).start();
        } else logStatus("Already started");
    }

    @Override public void onCompleteProxiesCheck(List<ProxyItem> goodProxies) {
        running = false;

        int totalSecs = (int) ((System.currentTimeMillis() - startTime) / 1000);
        int hours = totalSecs / 3600;
        int minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;

        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        logStatus("Complete in " + timeString);
        formMain.setNewTableData(goodProxies);
    }


    @Override public void onGetProxiesList(int count) {
        checkTotal = count;
        logStatus("Get " + count + ", now checking...");
    }

    @Override public void onError(String error) {
        logStatus("Error: " + error);
        running = false;
    }


    @Override synchronized public void onOneProxyCheck() {
        int percent = 100 * checkCounter / checkTotal;
        logStatus(String.format("Check status: %d/%d (%d%%) valid: %d",
                checkCounter++, checkTotal, percent, validFound));
    }

    @Override synchronized public void onOneFullValidProxyFound() {
        validFound++;
    }
}
