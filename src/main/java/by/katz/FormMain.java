package by.katz;

import by.katz.proxy.ProxyCollector;
import by.katz.proxy.ProxyItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FormMain
        extends JFrame
        implements ICallback {

    private static final java.lang.reflect.Type TYPE_LIST_PROXY = new TypeToken<List<ProxyItem>>() {}.getType();
    private boolean running = false;
    private JPanel pnlMain;
    private JButton btnStartCollect;
    private JLabel lblStatus;
    private JTable tableData;
    private JTextPane txtSites;
    private JButton btnLoadSites;
    private JButton btnSaveSites;
    private JTabbedPane tabbedPane;
    private JPanel tabSites;


    FormMain() {

        $$$setupUI$$$();
        btnStartCollect.addActionListener(a -> btnStartClick());
        btnLoadSites.addActionListener(a -> SitesEdit.load(txtSites));
        btnSaveSites.addActionListener(a -> SitesEdit.save(txtSites));

        try {
            final InputStream stream = this.getClass().getClassLoader().getResourceAsStream("proxy.png");
            if (stream != null)
                setIconImage(ImageIO.read(stream));
        } catch (IOException ignored) { }
        SitesEdit.load(txtSites);

        initTable();
        setTitle("Proxylister");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(tabbedPane);
        pack();
        setSize(380, 500);
        setLocationRelativeTo(null);
        setVisible(true);
        initDataInTable();
    }

    private void btnStartClick() {


        clearTable();
        if (!running) {
            running = true;

            new Thread(() -> {
                lblStatus.setText("Update starting...");
                new ProxyCollector(this);
            }).start();
        } else lblStatus.setText("Already started");
    }

    private void clearTable() {
        DefaultTableModel model = (DefaultTableModel) tableData.getModel();
        while (model.getRowCount() > 0)
            model.removeRow(0);
    }

    private void initDataInTable() {
        try {
            FileReader fileReader = new FileReader("proxies_good.json");
            List<ProxyItem> goodProxies = new Gson().fromJson(fileReader, TYPE_LIST_PROXY);
            onComplete(goodProxies);
        } catch (FileNotFoundException e) { lblStatus.setText(e.getLocalizedMessage()); }
    }

    private void initTable() {
        final DefaultTableModel model = (DefaultTableModel) tableData.getModel();
        model.addColumn("Time");
        model.addColumn("Address");
        model.addColumn("Type");
        model.addColumn("C");
        final TableColumnModel columnModel = tableData.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);
        columnModel.getColumn(1).setPreferredWidth(180);
        columnModel.getColumn(2).setPreferredWidth(40);
        columnModel.getColumn(3).setPreferredWidth(30);


        tableData.getSelectionModel().addListSelectionListener(event -> {
            String data = tableData.getValueAt(tableData.getSelectedRow(), 1).toString();
            StringSelection stringSelection = new StringSelection(data);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            lblStatus.setText("Copied: " + data);
            System.out.println("text copied");
        });
    }

    @Override public void onComplete(List<ProxyItem> goodProxies) {
        running = false;
        lblStatus.setText("Loaded!");
        DefaultTableModel model = (DefaultTableModel) tableData.getModel();

        for (ProxyItem p : goodProxies) {
            Object[] row = new Object[]{
                    p.getResponseTime(),
                    p.getHost() + ":" + p.getPort(),
                    p.getType(),
                    p.getCountry(),
            };
            model.addRow(row);
        }
    }

    @Override public void onGet(int count) {
        lblStatus.setText("Get " + count + ", now checking...");
    }

    @Override public void onError(String error) {
        lblStatus.setText("Error: " + error);
        running = false;
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setTabLayoutPolicy(0);
        tabbedPane.setTabPlacement(1);
        tabbedPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), null, TitledBorder.RIGHT, TitledBorder.BOTTOM, this.$$$getFont$$$(null, -1, -1, tabbedPane.getFont()), new Color(-4473925)));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        tabbedPane.addTab("Proxy lists", panel1);
        pnlMain = new JPanel();
        pnlMain.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(pnlMain, BorderLayout.CENTER);
        lblStatus = new JLabel();
        lblStatus.setText("Started");
        pnlMain.add(lblStatus, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnStartCollect = new JButton();
        btnStartCollect.setText("Update list");
        pnlMain.add(btnStartCollect, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        pnlMain.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tableData = new JTable();
        scrollPane1.setViewportView(tableData);
        tabSites = new JPanel();
        tabSites.setLayout(new BorderLayout(0, 0));
        tabbedPane.addTab("SItes to check", tabSites);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        tabSites.add(panel2, BorderLayout.CENTER);
        txtSites = new JTextPane();
        panel2.add(txtSites, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        btnLoadSites = new JButton();
        btnLoadSites.setText("Load");
        panel2.add(btnLoadSites, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnSaveSites = new JButton();
        btnSaveSites.setText("Save");
        panel2.add(btnSaveSites, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new BorderLayout(0, 0));
        tabbedPane.addTab("Magic", panel3);
        tabbedPane.setEnabledAt(2, false);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {resultName = currentFont.getName();} else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {resultName = fontName;} else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() { return tabbedPane; }

}