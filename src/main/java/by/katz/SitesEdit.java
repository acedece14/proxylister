package by.katz;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

class SitesEdit {

    static void load(JTextPane txtSites) {
        List<String> sites = Settings.getInstance().getSitesToCheck();
        String tmp = "";
        if (sites != null)
            for (String s : sites)
                tmp += s + "\n";
        tmp = tmp.trim();
        txtSites.setText(tmp);
    }

    static void save(JTextPane txtSites) {
        txtSites.setText(txtSites.getText().replaceAll("\r", "\n"));
        String[] tmp = txtSites.getText().split("\n");
        List<String> sites = Arrays.asList(tmp);
        Settings.getInstance().updateSites(sites);
    }
}
