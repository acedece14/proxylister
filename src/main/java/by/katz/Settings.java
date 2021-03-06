package by.katz;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Settings {

    private static File SETTINGS_FILE = new File("pr_ch_settings.json");

    private static Settings instance;

    public static Settings getInstance() {
        return instance == null ? instance = new Settings() : instance;
    }

    @Getter
    private List<String> sitesToCheck = Arrays.asList("http://example.com", "https://example.net");
    @Getter
    private int timeToCheck = 3000;
    @Getter
    private int threadCount = 50;
    @Getter
    private int pacServerPort = 8765;

    private Settings() { }

    void loadSettings() {
        if (!SETTINGS_FILE.exists())
            saveSettings();

        try (FileReader fileReader = new FileReader(SETTINGS_FILE)) {
            instance = new Gson().fromJson(fileReader, Settings.class);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(2);
        }
    }

    private void saveSettings() {
        try (FileWriter fileWriter = new FileWriter(SETTINGS_FILE)) {
            fileWriter.write(new GsonBuilder().setPrettyPrinting().create().toJson(this));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void updateSites(List<String> sites) {
        sitesToCheck = sites;
        saveSettings();
    }

}
