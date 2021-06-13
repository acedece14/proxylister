package by.katz;

import com.formdev.flatlaf.FlatDarculaLaf;

public class Main {

    public static void main(String[] a) {
        new Main();
    }

    private Main() {
        System.out.println("Start...");

        // ui
        FlatDarculaLaf.setup();

        Settings.getInstance().loadSettings();
        new FormMain();
    }

}
