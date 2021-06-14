package by.katz;

import com.formdev.flatlaf.FlatDarculaLaf;

public class Main {

    public static void main(String[] a) {
        System.out.println("Start...");
        // ui
        FlatDarculaLaf.setup();

        Settings.getInstance().loadSettings();

        new Controller();

        MyPacServer proxy = MyPacServer.getInstance();

       // proxy.setProxy("178.63.17.151", "3128");
    }

}
