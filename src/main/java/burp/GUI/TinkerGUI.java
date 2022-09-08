package burp.GUI;

import burp.*;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import javafx.scene.control.CheckBox;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.util.HashMap;
import java.util.Objects;

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
    private JCheckBox a404CheckBox;
    private JCheckBox a403CheckBox;
    private JCheckBox a401CheckBox;
    private JCheckBox a500CheckBox;
    private JButton linkclearButton;
    private JCheckBox GETCheckBox;
    private JCheckBox POSTCheckBox;
    private JCheckBox PUTCheckBox;
    private JCheckBox HEADCheckBox;
    private JScrollPane linkScroll;
    private JScrollPane infoScroll;
    private JButton infoclearButton;
    private JComboBox FiltercomboBox;
    private JButton copyButton;


    private IBurpExtenderCallbacks callbacks;
    private IMessageEditor requestTextEditor;
    private IMessageEditor responseTextEditor;
    private IHttpRequestResponse currentlyDisplayedItem;
    public HashMap<Integer, repeaterTableData> repeatertableDataMap = new HashMap<Integer, repeaterTableData>();
    public HashMap<Integer, linkTableData> linktableDataMap = new HashMap<Integer, linkTableData>();
    DefaultTableModel repeaterdataModel;
    DefaultTableModel infodataModel;

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
                repeatertableDataMap.clear();
                repeaterdataModel.fireTableDataChanged();
                repeaterTable.updateUI();
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
        repeaterPane.addComponentListener(new ComponentAdapter() {
        });
        autoCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    UIcon.UIStatHandle(UIStatController.UICheckBoxType.Auto, autoCheckBox.isSelected());
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        });
        withCookieCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    UIcon.UIStatHandle(UIStatController.UICheckBoxType.WithCookie, withCookieCheckBox.isSelected());
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        });
        a404CheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    UIcon.UIStatHandle(UIStatController.UICheckBoxType.Code404, a404CheckBox.isSelected());
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        });
        a403CheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    UIcon.UIStatHandle(UIStatController.UICheckBoxType.Code403, a403CheckBox.isSelected());
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        });
        a401CheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    UIcon.UIStatHandle(UIStatController.UICheckBoxType.Code401, a401CheckBox.isSelected());
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        });
        a500CheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    UIcon.UIStatHandle(UIStatController.UICheckBoxType.Code500, a500CheckBox.isSelected());
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        });
        GETCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    UIcon.UIStatHandle(UIStatController.UICheckBoxType.MethodGET, GETCheckBox.isSelected());
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        });
        POSTCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    UIcon.UIStatHandle(UIStatController.UICheckBoxType.MethodPost, POSTCheckBox.isSelected());
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        });
        PUTCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    UIcon.UIStatHandle(UIStatController.UICheckBoxType.MethodPut, PUTCheckBox.isSelected());
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        });
        HEADCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    UIcon.UIStatHandle(UIStatController.UICheckBoxType.MethodHead, HEADCheckBox.isSelected());
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
    }


    public void initUIStats() throws InterruptedException {
        UIcon.UIStatHandle(UIStatController.UICheckBoxType.Auto, autoCheckBox.isSelected());
        UIcon.UIStatHandle(UIStatController.UICheckBoxType.WithCookie, withCookieCheckBox.isSelected());
        UIcon.UIStatHandle(UIStatController.UICheckBoxType.Code404, a404CheckBox.isSelected());
        UIcon.UIStatHandle(UIStatController.UICheckBoxType.Code403, a403CheckBox.isSelected());
        UIcon.UIStatHandle(UIStatController.UICheckBoxType.Code401, a401CheckBox.isSelected());
        UIcon.UIStatHandle(UIStatController.UICheckBoxType.Code500, a500CheckBox.isSelected());
        UIcon.UIStatHandle(UIStatController.UICheckBoxType.MethodGET, GETCheckBox.isSelected());
        UIcon.UIStatHandle(UIStatController.UICheckBoxType.MethodPost, POSTCheckBox.isSelected());
        UIcon.UIStatHandle(UIStatController.UICheckBoxType.MethodPut, PUTCheckBox.isSelected());
        UIcon.UIStatHandle(UIStatController.UICheckBoxType.MethodHead, HEADCheckBox.isSelected());
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
        String[] title = new String[]{"Pos", "Domain", "URL", "Status", "Length"};
        repeaterdataModel = new DefaultTableModel(0, 5);
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
                requestTextEditor.setMessage(repeatertableDataMap.get(rowIdx).requestResponse.getRequest(), true);
                responseTextEditor.setMessage(repeatertableDataMap.get(rowIdx).requestResponse.getResponse(), false);
                currentlyDisplayedItem = repeatertableDataMap.get(rowIdx).requestResponse;
                //repeaterTable.getSelectedRow();
                //requestTextEditor.setMessage(tableDataMap.get(repeaterTable.getSelectedRow()).requestResponse.getRequest(), true);
                //responseTextEditor.setMessage(tableDataMap.get(repeaterTable.getSelectedRow()).requestResponse.getResponse(), false);
            }
        });

    }

    public void updateLinkTable(linkTableData data) {
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
    }

    public void updateRepeaterTable(repeaterTableData data) {
        repeatertableDataMap.put(repeaterTable.getRowCount(), data);
        repeaterdataModel.addRow(new String[]{String.valueOf(repeaterTable.getRowCount()), data.domain, data.link, String.valueOf(data.status), String.valueOf(data.lens)});
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
        panel1.setLayout(new GridLayoutManager(5, 6, new Insets(0, 0, 0, 0), -1, -1));
        repeaterPane.addTab("Tinker Menu", panel1);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
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
        label2.setText("Authored by L4ml3da V1.1 release");
        panel2.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 80, false));
        final JLabel label3 = new JLabel();
        label3.setText("Link Repeater");
        panel1.add(label3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, -1), null, 3, false));
        autoCheckBox = new JCheckBox();
        autoCheckBox.setSelected(true);
        autoCheckBox.setText("Auto");
        panel1.add(autoCheckBox, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(1, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        withCookieCheckBox = new JCheckBox();
        withCookieCheckBox.setText("WithCookie");
        panel1.add(withCookieCheckBox, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Filter status code");
        panel1.add(label4, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), null, null, 2, false));
        a404CheckBox = new JCheckBox();
        a404CheckBox.setText("404");
        panel1.add(a404CheckBox, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a403CheckBox = new JCheckBox();
        a403CheckBox.setText("403");
        panel1.add(a403CheckBox, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a401CheckBox = new JCheckBox();
        a401CheckBox.setText("401");
        panel1.add(a401CheckBox, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a500CheckBox = new JCheckBox();
        a500CheckBox.setText("500");
        panel1.add(a500CheckBox, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel1.add(spacer2, new GridConstraints(2, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Http Method");
        panel1.add(label5, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 3, false));
        GETCheckBox = new JCheckBox();
        GETCheckBox.setSelected(true);
        GETCheckBox.setText("GET");
        panel1.add(GETCheckBox, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        POSTCheckBox = new JCheckBox();
        POSTCheckBox.setText("POST");
        panel1.add(POSTCheckBox, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        PUTCheckBox = new JCheckBox();
        PUTCheckBox.setText("PUT");
        panel1.add(PUTCheckBox, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        HEADCheckBox = new JCheckBox();
        HEADCheckBox.setText("HEAD");
        panel1.add(HEADCheckBox, new GridConstraints(3, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel1.add(spacer3, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        linkRepeaterPanel = new JPanel();
        linkRepeaterPanel.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        repeaterPane.addTab("Link Repeater", linkRepeaterPanel);
        repeaterScroll = new JScrollPane();
        linkRepeaterPanel.add(repeaterScroll, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(-1, 521), null, 0, false));
        repeaterTable = new JTable();
        repeaterTable.setAutoscrolls(true);
        repeaterScroll.setViewportView(repeaterTable);
        reqrespSplit = new JSplitPane();
        reqrespSplit.setDividerSize(10);
        reqrespSplit.setResizeWeight(0.5);
        linkRepeaterPanel.add(reqrespSplit, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(-1, 400), null, 0, false));
        tabbedPane1 = new JTabbedPane();
        reqrespSplit.setLeftComponent(tabbedPane1);
        tabbedPane2 = new JTabbedPane();
        reqrespSplit.setRightComponent(tabbedPane2);
        linkclearButton = new JButton();
        linkclearButton.setText("Clear");
        linkRepeaterPanel.add(linkclearButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        final Spacer spacer4 = new Spacer();
        linkRepeaterPanel.add(spacer4, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(2, 4, new Insets(0, 0, 0, 0), -1, -1));
        repeaterPane.addTab("Sensitive Info", panel4);
        linkScroll = new JScrollPane();
        panel4.add(linkScroll, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        linkTable = new JTable();
        linkScroll.setViewportView(linkTable);
        infoScroll = new JScrollPane();
        panel4.add(infoScroll, new GridConstraints(1, 1, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        infoArea = new JTextArea();
        infoScroll.setViewportView(infoArea);
        infoclearButton = new JButton();
        infoclearButton.setText("Clear");
        panel4.add(infoclearButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        final JLabel label6 = new JLabel();
        label6.setText("Filter");
        panel4.add(label6, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
        panel4.add(FiltercomboBox, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        copyButton = new JButton();
        copyButton.setText("Copy");
        panel4.add(copyButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
