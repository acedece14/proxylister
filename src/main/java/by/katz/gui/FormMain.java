package by.katz.gui;

import by.katz.Controller;
import by.katz.MyPacServer;
import by.katz.proxy.ProxyItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import lombok.extern.java.Log;

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

import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

@Log
public class FormMain
    extends JFrame {

    private static final java.lang.reflect.Type TYPE_LIST_PROXY = new TypeToken<List<ProxyItem>>() {}.getType();
    private JPanel pnlMain;
    private JButton btnStartCollect;
    private JLabel lblStatus;
    private JTable tableData;
    private JTextPane txtSites;
    private JButton btnLoadSites;
    private JButton btnSaveSites;
    private JTabbedPane tabbedPane;
    private Controller controller;


    public FormMain(Controller controller) {
        this.controller = controller;

        $$$setupUI$$$();
        btnStartCollect.addActionListener(a -> btnStartScanClick());
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

    private void btnStartScanClick() {
        clearTable();
        controller.startScan();
    }

    private void clearTable() {
        DefaultTableModel model = (DefaultTableModel) tableData.getModel();
        if (model.getRowCount() > 0)
            while (model.getRowCount() > 0)
                model.removeRow(0);
    }

    private void initDataInTable() {
        try {
            FileReader fileReader = new FileReader("proxies_good.json");
            List<ProxyItem> goodProxies = new Gson().fromJson(fileReader, TYPE_LIST_PROXY);
            setNewTableData(goodProxies);
        } catch (FileNotFoundException e) { lblStatus.setText(e.getLocalizedMessage()); }
    }


    public void setNewTableData(List<ProxyItem> goodProxies) {

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

    private void initTable() {
        tableData.setSelectionMode(SINGLE_SELECTION);
        tableData.setShowGrid(true);
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
            if (tableData.getModel().getRowCount() <= 0)
                return;
            String data = tableData.getValueAt(tableData.getSelectedRow(), 1).toString();


            MyPacServer.getInstance().setProxy(data.split(":")[0], data.split(":")[1]);
            StringSelection stringSelection = new StringSelection(data);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            lblStatus.setText("Copied: " + data);
            log.info("text copied");
        });
    }


    public void setStatus(String text) { lblStatus.setText(text); }

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
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new BorderLayout(0, 0));
        tabbedPane.addTab("SItes to check", panel2);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel3, BorderLayout.CENTER);
        txtSites = new JTextPane();
        panel3.add(txtSites, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        btnLoadSites = new JButton();
        btnLoadSites.setText("Load");
        panel3.add(btnLoadSites, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnSaveSites = new JButton();
        btnSaveSites.setText("Save");
        panel3.add(btnSaveSites, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new BorderLayout(0, 0));
        tabbedPane.addTab("Magic", panel4);
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