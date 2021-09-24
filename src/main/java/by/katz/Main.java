package by.katz;

import com.formdev.flatlaf.FlatDarculaLaf;
import lombok.extern.java.Log;

@Log
public class Main {

    public static void main(String[] a) {
        log.info("Start...");
        // ui
        FlatDarculaLaf.setup();
        Settings.getInstance().loadSettings();
        new Controller();
        MyPacServer proxy = MyPacServer.getInstance();
    }

}
