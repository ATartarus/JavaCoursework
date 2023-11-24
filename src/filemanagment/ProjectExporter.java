package filemanagment;

import components.managedTable.ManagedTable;
import components.managedTable.ManagedTableModel;
import containers.Writable;
import entity.Data;
import org.apache.poi.xwpf.usermodel.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProjectExporter {
    private ProjectExporter() {}
    private static String getPlaceholder(String text) { return "${" + text + "}"; }
    private static String getText(JComponent component) {
        String result = null;
        if (component instanceof JTextField textField) {
            result = textField.getText();
        }
        else if (component instanceof JComboBox<?> comboBox) {
            result = (String) comboBox.getSelectedItem();
        }
        return result;
    }

    private static void createTable(XWPFDocument doc, ManagedTable table) {
        ManagedTableModel tableModel = (ManagedTableModel) table.getModel();
        XWPFTable wordTable = doc.createTable();
        XWPFTableRow row = wordTable.getRow(0);
        row.getCell(0).setText(tableModel.getColumnName(0));
        for (int i = 1; i < tableModel.getColumnCount(); i++) {
            row.addNewTableCell().setText(tableModel.getColumnName(i));
        }

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            row = wordTable.createRow();
            for (int j = 0; j < tableModel.getColumnCount(); j++) {
                Object val = tableModel.getValueAt(i, j);
                String text;
                if (val instanceof Data data) {
                    text = data.getText();
                } else {
                    text = val.toString();
                }
                row.getCell(j).setText(text);
            }
        }
    }

    private static ArrayList<String> getComponentKeys(String parText) {
        ArrayList<String> componentKeys = new ArrayList<>();
        int left = parText.indexOf("${");
        while (left != -1) {
            int right = parText.indexOf("}");
            if (right > left) {
                componentKeys.add(parText.substring(left + 2, right));
            }
            parText = parText.substring(right + 1);
            left = parText.indexOf("${");
        }
        return componentKeys;
    }
    private static void extractParagraphs(XWPFDocument source, XWPFDocument target, int pos) {
        List<XWPFParagraph> paragraphs = source.getParagraphs();
        while (pos < paragraphs.size()) {
            copyParagraph(source, target, pos);
            source.removeBodyElement(pos);
        }
    }

    private static void copyParagraph(XWPFDocument source, XWPFDocument target, int pos) {
        XWPFParagraph copyPar = source.getParagraphs().get(pos);
        XWPFParagraph newPar = target.createParagraph();
        newPar.getCTP().setPPr(copyPar.getCTP().getPPr());
        for (XWPFRun copyRun : copyPar.getRuns()) {
            XWPFRun newRun = newPar.createRun();
            newRun.getCTR().setRPr(copyRun.getCTR().getRPr());
            String tmpText = copyRun.getText(0);
            if (tmpText == null || tmpText.isEmpty()) {
                newRun.addTab();
            } else {
                newRun.setText(tmpText);
            }
        }
    }


    public static void export(ProjectData projectData, String outputPath) {
        String templatePath = projectData.getFolderPath() + "/template.docx";
        try (FileInputStream input = new FileInputStream(templatePath);
             XWPFDocument templateDoc = new XWPFDocument(input);
             XWPFDocument holderDoc = new XWPFDocument()
        ) {
            Writable header = projectData.getContainer("header");
            if (header == null) throw new IllegalArgumentException("No item with 'header' key");
            HashMap<String, JComponent> headerComponents = header.getComponentMap();
            JComponent tableObject = projectData.findComponent("body", "table");
            if (!(tableObject instanceof ManagedTable table)) {
                throw new IllegalArgumentException("tableObject not ManagedTable instance");
            }

            extractParagraphs(templateDoc, holderDoc, 0);
            List<XWPFParagraph> paragraphs = holderDoc.getParagraphs();
            outer:
            for (int i = 0; i < paragraphs.size(); i++) {
                XWPFParagraph paragraph = paragraphs.get(i);
                ArrayList<String> componentKeys = getComponentKeys(paragraph.getText());
                if (componentKeys.isEmpty()) {
                    copyParagraph(holderDoc, templateDoc, i);
                }
                else {
                    String paragraphText = paragraph.getText();
                    for (String key : componentKeys) {
                        if (key.equals("table")) {
                            createTable(templateDoc, table);
                            continue outer;
                        }
                        else if (key.equals("was") || key.equals("!was")) {
                            ManagedTableModel model = (ManagedTableModel) table.getModel();
                            int notAppeared = model.countNotAppeared();
                            paragraphText = paragraphText.replace(
                                    getPlaceholder(key),
                                    Integer.toString(key.equals("was") ?
                                            model.getRowCount() - notAppeared : notAppeared)
                            );
                        }
                        else {
                            paragraphText = paragraphText.replace(getPlaceholder(key), getText(headerComponents.get(key)));
                        }
                    }
                    XWPFParagraph newParagraph = templateDoc.createParagraph();
                    newParagraph.getCTP().setPPr(paragraph.getCTP().getPPr());
                    XWPFRun newRun = newParagraph.createRun();
                    newRun.getCTR().setRPr(paragraph.getRuns().get(0).getCTR().getRPr());
                    newRun.setText(paragraphText);
                }
            }
            try (FileOutputStream output = new FileOutputStream(outputPath)) {
                templateDoc.write(output);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static String showFileChooser(Component parent, String defaultFileName) {
        JFileChooser fileChooser = new JFileChooser() {
            @Override
            public void approveSelection() {
                if (getSelectedFile().exists()) {
                    int answer = JOptionPane.showConfirmDialog(
                            this,
                            "Rewrite file?",
                            "File already exists",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    );
                    if (answer == JOptionPane.CANCEL_OPTION) {
                        return;
                    }
                }
                super.approveSelection();
            }
        };
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                String fileName = f.getName();
                return fileName.substring(fileName.indexOf(".")).equals(".docx");
            }

            @Override
            public String getDescription() {
                return "Docx files (.docx)";
            }
        });
        fileChooser.setSelectedFile(new File(defaultFileName));

        int chooseResult = fileChooser.showSaveDialog(parent);
        if (chooseResult == JFileChooser.APPROVE_OPTION) {
            String selectedPath = fileChooser.getSelectedFile().getPath();
            int pivot = selectedPath.indexOf(".");
            return pivot == -1 ? selectedPath + ".docx" : selectedPath;
        }
        else {
            return null;
        }
    }
}
