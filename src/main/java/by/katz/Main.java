package by.katz;

public class Main {

    public static void main(String[] a) {
        new Main();
    }

    private Main() {
        System.out.println("Start...");
        Settings.getInstance().loadSettings();
        Settings settings = Settings.getInstance();
        new FormMain();
    }

}
