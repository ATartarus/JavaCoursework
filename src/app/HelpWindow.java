package app;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class HelpWindow extends JFrame {
    private final JTree tree;
    private final JEditorPane infoPane;
    public HelpWindow() {
        super("Help");
        setSize(600, 400);
        setMinimumSize(new Dimension(400, 200));

        try (InputStream input = getClass().getResourceAsStream("/images/help_window_icon.png")) {
            if (input != null) {
                ImageIcon icon = new ImageIcon(ImageIO.read(input));
                setIconImage(icon.getImage());
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        tree = new JTree();
        infoPane = new JEditorPane();
        JPanel treePane = new JPanel(new BorderLayout());
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treePane, new JScrollPane(infoPane));
        setContentPane(splitPane);

        infoPane.setEditable(false);
        infoPane.setMinimumSize(new Dimension(300, 0));
        treePane.setMinimumSize(new Dimension(100, 0));
        treePane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        treePane.setBackground(Color.white);
        treePane.add(tree, BorderLayout.WEST);

        populateTree();

        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (selectedNode != null) {
                if (selectedNode.isLeaf()) {
                    Leaf leaf = (Leaf) selectedNode.getUserObject();
                    displayInfo(leaf.url);
                }
                else {
                    displayInfo(getClass().getResource("/help/html/defaultHelp.html"));
                }
            }
        });

        tree.expandRow(0);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void populateTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Помощь");
        tree.setModel(new DefaultTreeModel(root));
        DefaultMutableTreeNode menuHelpNode = new DefaultMutableTreeNode(
                new Leaf("Меню", "menuHelp.html")
        );
        DefaultMutableTreeNode workspaceHelpNode = new DefaultMutableTreeNode(
                new Leaf("Рабочая область", "workspaceHelp.html")
        );
        root.add(menuHelpNode);
        root.add(workspaceHelpNode);
    }

    private void displayInfo(URL url) {
        try {
            Document doc = infoPane.getDocument();
            doc.putProperty(Document.StreamDescriptionProperty, null);
            infoPane.setPage(url);
        } catch (IOException e) {
            infoPane.setText("File not found");
        }
    }

    private static class Leaf {
        public final String name;
        public final URL url;
        public Leaf(String name, String url) {
            this.name = name;
            this.url = getClass().getResource("/help/html/" + url);
            if (this.url == null) {
                System.err.println("Help file " + url + " not found");
            }
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
