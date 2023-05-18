package burp.GUI;

import burp.*;
import burp.core.httpTester;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TinkerGUI implements IMessageEditorController {
    private JPanel rootPanel;
    private JTabbedPane repeaterPane;
    private JTable repeaterTable;
    private JPanel linkRepeaterPanel;
    private JScrollPane repeaterScroll;
    private JTabbedPane tabbedPane1;
    private JTabbedPane tabbedPane2;
    private JSplitPane reqrespSplit;
    private JTextArea infoArea;
    private JTable linkTable;
    private JCheckBox autoCheckBox;
    private JCheckBox withCookieCheckBox;
    private JButton linkclearButton;
    private JCheckBox GETCheckBox;
    private JCheckBox POSTCheckBox;
    private JCheckBox PUTCheckBox;
    private JCheckBox HEADCheckBox;
    private JScrollPane linkScroll;
    private JScrollPane infoScroll;
    private JButton repeatButton;
    private JComboBox FiltercomboBox;
    private JButton copyButton;
    private JButton applyButton;
    private JTextField filterSuffixTextField;
    private JTextField filterCodeTextField;
    private JTextField filterURLTextField;
    private JButton infoclearButton;
    private JTextField filterMIMETextField;
    private JTextField rootDirectoryTextField;
    private JLabel repeatStatLabel;


    private IBurpExtenderCallbacks callbacks;
    private IMessageEditor requestTextEditor;
    private IMessageEditor responseTextEditor;
    private IHttpRequestResponse currentlyDisplayedItem;
    public HashMap<Integer, repeaterTableData> repeatertableDataMap = new HashMap<Integer, repeaterTableData>();
    public HashMap<Integer, linkTableData> linktableDataMap = new HashMap<Integer, linkTableData>();
    DefaultTableModel repeaterdataModel;
    DefaultTableModel infodataModel;
    private final ReadWriteLock repeaterTableLock = new ReentrantReadWriteLock();

    public UIStatController UIcon;

    public TinkerGUI(IBurpExtenderCallbacks callbacks) throws InterruptedException {
        this.callbacks = callbacks;
        $$$setupUI$$$();
        initRepeaterPane();
        initSensitiveInfoPane();

        UIcon = new UIStatController();
        initUIStats();


        linkclearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                requestTextEditor.setMessage(new byte[]{}, true);
                responseTextEditor.setMessage(new byte[]{}, false);
                repeaterdataModel.setRowCount(0);
                repeaterTableClear();
                //repeaterdataModel.fireTableDataChanged();
                repeaterTable.updateUI();
            }
        });
        repeatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                repeatButton.setEnabled(false);
                repeatStatLabel.setText("working");
                repeatStatLabel.setForeground(Color.RED);
                Thread thread = new Thread(() -> {
                    httpTester ht = null;
                    ArrayList<repeaterTableData> rdata;
                    String rootDir = rootDirectoryTextField.getText();
                    String[] rootDirList = rootDir.replaceAll("\\s+", "").split(",");
                    ArrayList<String> testApi;
                    int[] selectedRows = linkTable.getSelectedRows();
                    for (int i = selectedRows.length - 1; i >= 0; i--) {
                        testApi = linktableDataMap.get(selectedRows[i]).getAPIList();
                        callbacks.printOutput("select row repeat " + selectedRows[i]);
                        callbacks.printOutput("select row js " + linktableDataMap.get(selectedRows[i]).dataFromURL);
                        callbacks.printOutput("test root list length " + rootDirList.length);
                        ArrayList<String> realApi = new ArrayList<>();
                        if (rootDir.length() != 0 && rootDirList.length != 0) {
                            for (String dir : rootDirList) {
                                for (int n = 0; n < testApi.size(); n++) {
                                    realApi.add(dir + "/" + testApi.get(n));
                                }
                            }
                        } else {
                            realApi.addAll(testApi);
                        }
                        ht = linktableDataMap.get(selectedRows[i]).getHt();
                        ht.configHttp(UIcon.filterCode, UIcon.supportMethod, UIcon.filterBlackMIME, UIcon.withCookie);
                        for (String api : realApi) {
                            callbacks.printOutput("repeat test api " + api);
                            try {
                                rdata = ht.testLinkReq(api);
                                for (repeaterTableData res : rdata) {
                                    updateRepeaterTable(res);
                                }
                            } catch (MalformedURLException | InterruptedException malformedURLException) {
                                malformedURLException.printStackTrace();
                            }
                        }
                    }
                    SwingUtilities.invokeLater(() -> {
                                repeatButton.setEnabled(true);
                                repeatStatLabel.setText("IDLE");
                                repeatStatLabel.setForeground(Color.GREEN);
                            }
                    );
                });
                thread.start();
            }
        });

        repeaterPane.addComponentListener(new ComponentAdapter() {
        });
        autoCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    UIcon.UIConfigHandle(UIStatController.UIConfigEnum.Auto, autoCheckBox.isSelected(), "");
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        });
        withCookieCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    UIcon.UIConfigHandle(UIStatController.UIConfigEnum.WithCookie, withCookieCheckBox.isSelected(), "");
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        });
        GETCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    UIcon.UIConfigHandle(UIStatController.UIConfigEnum.MethodGET, GETCheckBox.isSelected(), "");
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        });
        POSTCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    UIcon.UIConfigHandle(UIStatController.UIConfigEnum.MethodPost, POSTCheckBox.isSelected(), "");
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        });
        PUTCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    UIcon.UIConfigHandle(UIStatController.UIConfigEnum.MethodPut, PUTCheckBox.isSelected(), "");
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        });
        HEADCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    UIcon.UIConfigHandle(UIStatController.UIConfigEnum.MethodHead, HEADCheckBox.isSelected(), "");
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        });
        FiltercomboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int rowIdx = Integer.parseInt(linkTable.getValueAt(linkTable.getSelectedRow(), 0).toString());
                String item = Objects.requireNonNull(FiltercomboBox.getSelectedItem()).toString();
                infoArea.setText(linktableDataMap.get(rowIdx).getTableTextData(item));
            }
        });
        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StringSelection selection = new StringSelection(infoArea.getText());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);
            }
        });

        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    UIcon.UIConfigHandle(UIStatController.UIConfigEnum.BlackMIME, false, filterMIMETextField.getText());
                    UIcon.UIConfigHandle(UIStatController.UIConfigEnum.BlackSuffix, false, filterSuffixTextField.getText());
                    UIcon.UIConfigHandle(UIStatController.UIConfigEnum.BlackURL, false, filterURLTextField.getText());
                    if (!UIcon.UIConfigHandle(UIStatController.UIConfigEnum.StatusCode, false, filterCodeTextField.getText())) {
                        showCustomDialog("Error", "filter code config error");
                    } else {
                        showCustomDialog("Success", "config saved");
                    }
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        });
        infoclearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                infodataModel.setRowCount(0);
                linktableDataMap.clear();
                infoArea.setText("");
                infodataModel.fireTableDataChanged();
                linkTable.updateUI();
            }
        });
    }

    public void showCustomDialog(String title, String msg) {
        Window window = SwingUtilities.windowForComponent(this.$$$getRootComponent$$$());
        JDialog dialog = new JDialog(window, title, Dialog.ModalityType.APPLICATION_MODAL);

        JLabel label = new JLabel(msg);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 16));

        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        JPanel dialogPanel = new JPanel(new BorderLayout());
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dialogPanel.add(label, BorderLayout.CENTER);
        dialogPanel.add(okButton, BorderLayout.SOUTH);

        dialog.setContentPane(dialogPanel);
        dialog.setSize(200, 150);
        dialog.setLocationRelativeTo(this.$$$getRootComponent$$$());
        dialog.setVisible(true);
    }

    public void initUIStats() throws InterruptedException {
        UIcon.UIConfigHandle(UIStatController.UIConfigEnum.Auto, autoCheckBox.isSelected(), "");
        UIcon.UIConfigHandle(UIStatController.UIConfigEnum.WithCookie, withCookieCheckBox.isSelected(), "");
        UIcon.UIConfigHandle(UIStatController.UIConfigEnum.MethodGET, GETCheckBox.isSelected(), "");
        UIcon.UIConfigHandle(UIStatController.UIConfigEnum.MethodPost, POSTCheckBox.isSelected(), "");
        UIcon.UIConfigHandle(UIStatController.UIConfigEnum.MethodPut, PUTCheckBox.isSelected(), "");
        UIcon.UIConfigHandle(UIStatController.UIConfigEnum.MethodHead, HEADCheckBox.isSelected(), "");
        UIcon.UIConfigHandle(UIStatController.UIConfigEnum.StatusCode, false, filterCodeTextField.getText());
        UIcon.UIConfigHandle(UIStatController.UIConfigEnum.BlackSuffix, false, filterSuffixTextField.getText());
        UIcon.UIConfigHandle(UIStatController.UIConfigEnum.BlackURL, false, filterURLTextField.getText());
        UIcon.UIConfigHandle(UIStatController.UIConfigEnum.BlackMIME, false, filterMIMETextField.getText());
    }

    public void initSensitiveInfoPane() {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        linkTable.setDefaultRenderer(Object.class, centerRenderer);
        String[] title = new String[]{"Pos", "From", "Api", "Url", "IDCard", "Phone", "Email", "InterIP", "Domain"};

        infodataModel = new DefaultTableModel(0, 4);
        infodataModel.setColumnIdentifiers(title);

        infoArea.setEditable(false);

        linkTable.setModel(infodataModel);

        linkTable.getColumnModel().getColumn(0).setPreferredWidth(10);
        /*
        linkTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        linkTable.getColumnModel().getColumn(2).setPreferredWidth(10);
        linkTable.getColumnModel().getColumn(3).setPreferredWidth(10);
         */

        linkTable.setAutoCreateRowSorter(true);

        linkTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int rowIdx = Integer.parseInt(linkTable.getValueAt(linkTable.getSelectedRow(), 0).toString());
                String item = Objects.requireNonNull(FiltercomboBox.getSelectedItem()).toString();
                infoArea.setText(linktableDataMap.get(rowIdx).getTableTextData(item));
            }
        });

    }


    public void initRepeaterPane() {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        repeaterTable.setDefaultRenderer(Object.class, centerRenderer);
        String[] title = new String[]{"Pos", "Domain", "URL", "Status", "Length", "MIME"};
        repeaterdataModel = new DefaultTableModel(0, 6);
        repeaterdataModel.setColumnIdentifiers(title);

        repeaterTable.setModel(repeaterdataModel);
        repeaterTable.getColumnModel().getColumn(0).setPreferredWidth(10);
        repeaterTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        repeaterTable.getColumnModel().getColumn(3).setPreferredWidth(10);
        repeaterTable.getColumnModel().getColumn(4).setPreferredWidth(10);
        repeaterTable.setAutoCreateRowSorter(true);

        requestTextEditor = this.callbacks.createMessageEditor(this, false);
        responseTextEditor = this.callbacks.createMessageEditor(this, false);
        tabbedPane1.addTab("Request", requestTextEditor.getComponent());
        tabbedPane2.addTab("Response", responseTextEditor.getComponent());
        repeaterTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int rowIdx = Integer.parseInt(repeaterTable.getValueAt(repeaterTable.getSelectedRow(), 0).toString());
                requestTextEditor.setMessage(repeaterTableGet(rowIdx).requestResponse.getRequest(), true);
                responseTextEditor.setMessage(repeaterTableGet(rowIdx).requestResponse.getResponse(), false);
                currentlyDisplayedItem = repeaterTableGet(rowIdx).requestResponse;
                //repeaterTable.getSelectedRow();
                //requestTextEditor.setMessage(tableDataMap.get(repeaterTable.getSelectedRow()).requestResponse.getRequest(), true);
                //responseTextEditor.setMessage(tableDataMap.get(repeaterTable.getSelectedRow()).requestResponse.getResponse(), false);
            }
        });

    }

    public void updateLinkTable(linkTableData data) {
        infoclearButton.setEnabled(false);
        linktableDataMap.put(linkTable.getRowCount(), data);
        infodataModel.addRow(new String[]{
                String.valueOf(linkTable.getRowCount()),
                data.dataFromURL,
                String.valueOf(data.getTableDataNum("Api")),
                String.valueOf(data.getTableDataNum("Url")),
                String.valueOf(data.getTableDataNum("IDCard")),
                String.valueOf(data.getTableDataNum("Phone")),
                String.valueOf(data.getTableDataNum("Email")),
                String.valueOf(data.getTableDataNum("InterIP")),
                String.valueOf(data.getTableDataNum("Domain"))
        });
        infoclearButton.setEnabled(true);
    }

    public void repeaterTableClear() {
        repeaterTableLock.writeLock().lock();
        try {
            repeatertableDataMap.clear();
        } finally {
            repeaterTableLock.writeLock().unlock();
        }
    }

    public void repeaterTablePut(repeaterTableData v) {
        repeaterTableLock.writeLock().lock();
        try {
            repeatertableDataMap.put(repeaterTable.getRowCount(), v);
            repeaterdataModel.addRow(new String[]{String.valueOf(repeaterTable.getRowCount()), v.domain, v.link, String.valueOf(v.status), String.valueOf(v.lens), v.mimeType});
        } finally {
            repeaterTableLock.writeLock().unlock();
        }
    }

    public repeaterTableData repeaterTableGet(Integer k) {
        repeaterTableLock.readLock().lock();
        try {
            return repeatertableDataMap.get(k);
        } finally {
            repeaterTableLock.readLock().unlock();
        }
    }

    public void updateRepeaterTable(repeaterTableData data) {
        linkclearButton.setEnabled(false);
        repeaterTablePut(data);
        linkclearButton.setEnabled(true);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        rootPanel = new JPanel();
        rootPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        repeaterPane = new JTabbedPane();
        rootPanel.add(repeaterPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(10, 10, new Insets(0, 0, 0, 0), -1, -1));
        repeaterPane.addTab("Tinker Menu", panel1);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 10, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JSeparator separator1 = new JSeparator();
        panel2.add(separator1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$("Droid Sans Mono", Font.BOLD, 36, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setText("Tinker");
        panel3.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        Font label2Font = this.$$$getFont$$$("Calibri", Font.ITALIC, 16, label2.getFont());
        if (label2Font != null) label2.setFont(label2Font);
        label2.setText("Authored by L4ml3da V1.3.0 release");
        panel2.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 80, false));
        final JLabel label3 = new JLabel();
        label3.setText("Link Repeater");
        panel1.add(label3, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, -1), null, 3, false));
        autoCheckBox = new JCheckBox();
        autoCheckBox.setSelected(true);
        autoCheckBox.setText("Auto");
        panel1.add(autoCheckBox, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(1, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel1.add(spacer2, new GridConstraints(3, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel1.add(spacer3, new GridConstraints(9, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        filterSuffixTextField = new JTextField();
        filterSuffixTextField.setText("png,jpg,gif,css,js,ico,svg,eot,woff,woff2,ttf,vue");
        panel1.add(filterSuffixTextField, new GridConstraints(4, 2, 1, 7, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        withCookieCheckBox = new JCheckBox();
        withCookieCheckBox.setText("WithCookie");
        panel1.add(withCookieCheckBox, new GridConstraints(1, 3, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        filterURLTextField = new JTextField();
        filterURLTextField.setText("www.w3.org,localhost");
        panel1.add(filterURLTextField, new GridConstraints(6, 2, 1, 7, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        filterCodeTextField = new JTextField();
        filterCodeTextField.setText("400,404,405,502,503,504");
        panel1.add(filterCodeTextField, new GridConstraints(3, 2, 1, 7, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel1.add(spacer4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Filter status code");
        panel1.add(label4, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), null, null, 2, false));
        final JLabel label5 = new JLabel();
        label5.setText("Filter black suffix");
        panel1.add(label5, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Filter black URL");
        panel1.add(label6, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Filter MIME");
        panel1.add(label7, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        filterMIMETextField = new JTextField();
        filterMIMETextField.setText("");
        panel1.add(filterMIMETextField, new GridConstraints(5, 2, 1, 7, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("Http Method");
        panel1.add(label8, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 3, false));
        GETCheckBox = new JCheckBox();
        GETCheckBox.setSelected(true);
        GETCheckBox.setText("GET");
        panel1.add(GETCheckBox, new GridConstraints(2, 2, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        POSTCheckBox = new JCheckBox();
        POSTCheckBox.setSelected(true);
        POSTCheckBox.setText("POST");
        panel1.add(POSTCheckBox, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        PUTCheckBox = new JCheckBox();
        PUTCheckBox.setText("PUT");
        panel1.add(PUTCheckBox, new GridConstraints(2, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        HEADCheckBox = new JCheckBox();
        HEADCheckBox.setText("HEAD");
        panel1.add(HEADCheckBox, new GridConstraints(2, 6, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("Root Directory");
        panel1.add(label9, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        rootDirectoryTextField = new JTextField();
        rootDirectoryTextField.setText("");
        panel1.add(rootDirectoryTextField, new GridConstraints(7, 2, 1, 7, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        applyButton = new JButton();
        applyButton.setText("Apply");
        panel1.add(applyButton, new GridConstraints(8, 4, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        linkRepeaterPanel = new JPanel();
        linkRepeaterPanel.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        repeaterPane.addTab("Link Repeater", linkRepeaterPanel);
        repeaterScroll = new JScrollPane();
        linkRepeaterPanel.add(repeaterScroll, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(-1, 300), null, 0, false));
        repeaterTable = new JTable();
        repeaterTable.setAutoscrolls(true);
        repeaterTable.setPreferredScrollableViewportSize(new Dimension(450, 300));
        repeaterScroll.setViewportView(repeaterTable);
        reqrespSplit = new JSplitPane();
        reqrespSplit.setDividerSize(10);
        reqrespSplit.setResizeWeight(0.5);
        linkRepeaterPanel.add(reqrespSplit, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(-1, 400), null, 0, false));
        tabbedPane1 = new JTabbedPane();
        reqrespSplit.setLeftComponent(tabbedPane1);
        tabbedPane2 = new JTabbedPane();
        reqrespSplit.setRightComponent(tabbedPane2);
        linkclearButton = new JButton();
        linkclearButton.setText("Clear");
        linkRepeaterPanel.add(linkclearButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        final Spacer spacer5 = new Spacer();
        linkRepeaterPanel.add(spacer5, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(3, 8, new Insets(0, 0, 0, 0), -1, -1));
        repeaterPane.addTab("Sensitive Info", panel4);
        linkScroll = new JScrollPane();
        panel4.add(linkScroll, new GridConstraints(1, 0, 1, 4, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 600), new Dimension(-1, 600), 0, false));
        linkTable = new JTable();
        linkScroll.setViewportView(linkTable);
        infoScroll = new JScrollPane();
        panel4.add(infoScroll, new GridConstraints(1, 4, 1, 4, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 600), new Dimension(-1, 600), 0, false));
        infoArea = new JTextArea();
        infoArea.setColumns(50);
        infoArea.setRows(50);
        infoScroll.setViewportView(infoArea);
        repeatButton = new JButton();
        repeatButton.setText("Repeat");
        panel4.add(repeatButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        final JLabel label10 = new JLabel();
        label10.setText("Filter");
        panel4.add(label10, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        FiltercomboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("Api");
        defaultComboBoxModel1.addElement("Url");
        defaultComboBoxModel1.addElement("IDCard");
        defaultComboBoxModel1.addElement("Phone");
        defaultComboBoxModel1.addElement("Email");
        defaultComboBoxModel1.addElement("InterIP");
        defaultComboBoxModel1.addElement("Domain");
        FiltercomboBox.setModel(defaultComboBoxModel1);
        panel4.add(FiltercomboBox, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        copyButton = new JButton();
        copyButton.setText("Copy");
        panel4.add(copyButton, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer6 = new Spacer();
        panel4.add(spacer6, new GridConstraints(2, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer7 = new Spacer();
        panel4.add(spacer7, new GridConstraints(2, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer8 = new Spacer();
        panel4.add(spacer8, new GridConstraints(2, 6, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer9 = new Spacer();
        panel4.add(spacer9, new GridConstraints(0, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        infoclearButton = new JButton();
        infoclearButton.setText("Clear");
        panel4.add(infoclearButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer10 = new Spacer();
        panel4.add(spacer10, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        repeatStatLabel = new JLabel();
        repeatStatLabel.setForeground(new Color(-15287512));
        repeatStatLabel.setText("IDLE");
        panel4.add(repeatStatLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here

    }

    @Override
    public byte[] getRequest() {
        return currentlyDisplayedItem.getRequest();
    }

    @Override
    public byte[] getResponse() {
        return currentlyDisplayedItem.getResponse();
    }

    @Override
    public IHttpService getHttpService() {
        return currentlyDisplayedItem.getHttpService();
    }
}
