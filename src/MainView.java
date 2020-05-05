import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;

public class MainView extends JFrame {

    private JPanel controlBar;
    private JPanel graphIOPanel;
    private JComboBox<String> sourceSelection;
    private GraphTreeModel graphModel;
    private JTree topologyTree;
    private HashSet<String> expandedNode = new HashSet<>();

    private Button addNodeBtn;
    private Button addLinkBtn;
    private Button removeBtn;

    public MainView() {
        super();
        setTitle("LSR Simulator");
        addComponents();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public void init() {
        setVisible(true);
    }

    private void addComponents() {
        controlBar = buildControlBar();
        this.add(buildGraphEditorPanel(), BorderLayout.WEST);
        this.add(buildGraphOptionPanel(), BorderLayout.CENTER);
        this.add(controlBar, BorderLayout.SOUTH);
        pack();
    }

    private JPanel buildGraphEditorPanel() {
        JPanel graphPanel = new JPanel(new BorderLayout());
        JPanel editorPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        graphPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        /* The graph editor*/
        graphModel = new GraphTreeModel(new Graph());
        topologyTree = new JTree(graphModel);
        topologyTree.setEditable(false);
        topologyTree.setRootVisible(false);
        topologyTree.addTreeSelectionListener(this::onSelectTree);

        JLabel topologyLbl = new JLabel("Topology:");
        topologyLbl.setBorder(new EmptyBorder(0, 0, 3, 0));
        editorPanel.add(topologyLbl, BorderLayout.NORTH);
        JScrollPane topologyPane = new JScrollPane(
                topologyTree,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        topologyPane.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
        editorPanel.add(topologyPane, BorderLayout.CENTER);

        /* The buttons */
        addNodeBtn = new Button("Add Node");
        addLinkBtn = new Button("Add Link");
        removeBtn = new Button("Remove Selected");
        addLinkBtn.setEnabled(false);
        removeBtn.setEnabled(false);
        addNodeBtn.addActionListener(this::onAddNodeClicked);
        addLinkBtn.addActionListener(this::onAddLinkClicked);
        removeBtn.addActionListener(this::onRemoveClicked);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.5;
        constraints.gridx = 0;
        constraints.gridy = 1;
        buttonPanel.add(addNodeBtn, constraints);
        constraints.gridx = 1;
        constraints.gridy = 1;
        buttonPanel.add(addLinkBtn, constraints);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.weightx = 0;
        buttonPanel.add(removeBtn, constraints);
        graphModel.addTreeModelListener(new TreeModelListener() {
            @Override
            public void treeNodesChanged(TreeModelEvent e) {
            }

            @Override
            public void treeNodesInserted(TreeModelEvent e) {
            }

            @Override
            public void treeNodesRemoved(TreeModelEvent e) {
            }

            public void treeStructureChanged(TreeModelEvent e) {
                addLinkBtn.setEnabled(false);
                removeBtn.setEnabled(false);
                refreshSourceSelection();
                restoreTreeExpansion();
            }
        });
        topologyTree.addTreeExpansionListener(new TreeExpansionListener() {
            @Override
            public void treeExpanded(TreeExpansionEvent event) {
                Object object = event.getPath().getLastPathComponent();
                if (object instanceof GraphTreeModel.Node) {
                    expandedNode.add(((GraphTreeModel.Node) object).name);
                }
            }

            @Override
            public void treeCollapsed(TreeExpansionEvent event) {
                Object object = event.getPath().getLastPathComponent();
                if (object instanceof GraphTreeModel.Node) {
                    expandedNode.remove(((GraphTreeModel.Node) object).name);
                }
            }
        });

        /* Bindings */
        graphPanel.add(editorPanel, BorderLayout.CENTER);
        graphPanel.add(buttonPanel, BorderLayout.SOUTH);
        graphPanel.setPreferredSize(new Dimension(130, 250));
        return graphPanel;
    }

    private void restoreTreeExpansion() {
        for (int i = 0; i < topologyTree.getRowCount(); i++) {
            Object object = topologyTree.getPathForRow(i).getLastPathComponent();
            if (object instanceof GraphTreeModel.Node && expandedNode.contains(((GraphTreeModel.Node) object).name)) {
                topologyTree.expandRow(i);
            }
        }
    }

    private JPanel buildGraphOptionPanel() {
        JPanel optionPanel = new JPanel(new BorderLayout());
        graphIOPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel upperPanel = new JPanel();
        upperPanel.setLayout(new BoxLayout(upperPanel, BoxLayout.PAGE_AXIS));
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JTextArea status = new JTextArea(6, 28);
        status.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
        status.setEditable(false);
        JLabel statusLbl = new JLabel("Status");
        statusLbl.setBorder(new EmptyBorder(0, 0, 3, 0));
        bottomPanel.add(statusLbl, BorderLayout.NORTH);
        bottomPanel.add(status, BorderLayout.CENTER);
        Button loadBtn = new Button("Load File...");
        Button saveBtn = new Button("Save File...");
        loadBtn.addActionListener(this::onLoadFileClicked);
        saveBtn.addActionListener(this::onSaveFileClicked);
        graphIOPanel.add(loadBtn);
        graphIOPanel.add(saveBtn);
        upperPanel.add(graphIOPanel);
        upperPanel.setBorder(new EmptyBorder(19, 0, 0, 0));
        optionPanel.add(upperPanel, BorderLayout.NORTH);
        optionPanel.add(bottomPanel, BorderLayout.CENTER);
        optionPanel.setBorder(new EmptyBorder(0, 0, 5, 5));
        return optionPanel;
    }

    private JPanel buildControlBar() {
        /* Panel layouts */
        JPanel controlBar = new JPanel(new BorderLayout());
        controlBar.setBackground(Color.WHITE);
        JPanel leftBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftBar.setBackground(Color.WHITE);
        JPanel rightBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightBar.setBackground(Color.WHITE);

        /* Left control bar */
        sourceSelection = new JComboBox<>();
        leftBar.add(new JLabel("Select Source: "));
        leftBar.add(sourceSelection);

        /* Right control bar */
        Button singleStepBtn = new Button("Single Step");
        singleStepBtn.addActionListener(this::onSingleStepClicked);
        Button computeAllBtn = new Button("Compute All");
        computeAllBtn.addActionListener(this::onComputeAllClicked);
        Button resetBtn = new Button("Reset");
        resetBtn.addActionListener(this::onResetClicked);
        rightBar.add(singleStepBtn);
        rightBar.add(computeAllBtn);
        rightBar.add(resetBtn);

        /* Bindings */
        controlBar.add(leftBar, BorderLayout.WEST);
        controlBar.add(rightBar, BorderLayout.EAST);
        return controlBar;
    }

    private void onSelectTree(TreeSelectionEvent e) {
        addLinkBtn.setEnabled(false);
        if (e.getPath().getLastPathComponent() instanceof GraphTreeModel.Node) addLinkBtn.setEnabled(true);
        if (!(e.getPath().getLastPathComponent() instanceof GraphTreeModel.Root)) removeBtn.setEnabled(true);
    }

    private void onSaveFileClicked(ActionEvent e) {
        FileDialog fd = new FileDialog(this, "Save Game To...", FileDialog.SAVE);
        fd.setFile("myroute.lsa");
        fd.setVisible(true);
        String filename = fd.getFile();
        if (filename == null) return;
        if (graphModel.saveFile(fd.getDirectory() + filename)) {
            JOptionPane.showMessageDialog(
                    null,
                    "File saved!",
                    "Save Completed",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    "File could not be saved at this location.",
                    "Save Failed",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void onRemoveClicked(ActionEvent e) {
        TreePath path = topologyTree.getSelectionPath();
        if (path == null) return;
        Object selected = path.getLastPathComponent();
        boolean successful = false;
        if (selected instanceof GraphTreeModel.Node) {
            expandedNode.remove(path.toString());
            successful = graphModel.removeNode(((GraphTreeModel.Node) selected).name);
        } else if (selected instanceof GraphTreeModel.Edge) {
            expandedNode.remove(path.toString());
            GraphTreeModel.Edge edge = (GraphTreeModel.Edge) selected;
            successful = graphModel.removeLink(edge.srcNodeName, edge.destNodeName);
        } else {
            // No operation
            successful = true;
        }
        if (!successful) {
            JOptionPane.showMessageDialog(
                    null,
                    "The selected component cannot be removed",
                    "Remove Selected",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void onAddLinkClicked(ActionEvent e) {
        TreePath path = topologyTree.getSelectionPath();
        if (path == null) return;
        Object selected = path.getLastPathComponent();
        if (!(selected instanceof GraphTreeModel.Node)) return;
        String srcNodeName = ((GraphTreeModel.Node) selected).name;
        List<String> nodes = new ArrayList<>(
                Arrays.asList(((GraphTreeModel.Root) graphModel.getRoot()).nodes)
        );
        nodes.remove(srcNodeName);
        if (nodes.isEmpty()) {
            JOptionPane.showMessageDialog(
                    null,
                    "No nodes available to link!",
                    "Create Link",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        JComboBox<String> destNodeSelection = new JComboBox<>(nodes.toArray(new String[0]));
        JSpinner distanceSelection = new JSpinner(
                new SpinnerNumberModel(1, 0, Integer.MAX_VALUE, 1)
        );
        final JComponent[] inputs = new JComponent[]{
                new JLabel("Connect \"" + srcNodeName + "\" with: "),
                destNodeSelection,
                new JLabel("At a distance of:"),
                distanceSelection
        };
        String destNodeName;
        int distance;
        while (true) {
            int result = JOptionPane.showConfirmDialog(
                    null,
                    inputs,
                    "Create Link",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null);
            if (!(result == JOptionPane.OK_OPTION)) return;
            destNodeName = (String) destNodeSelection.getSelectedItem();
            distance = ((Number) distanceSelection.getValue()).intValue();
            if (graphModel.hasLink(srcNodeName, destNodeName)) {
                int confirmResult = JOptionPane.showConfirmDialog(
                        null,
                        "Overwrite existing link " + srcNodeName + " <> "
                                + destNodeName + "\nwith a distance of " + distance + "?",
                        "Link Existed "+ srcNodeName + " <> "+ destNodeName,
                        JOptionPane.YES_NO_OPTION);
                if (confirmResult != JOptionPane.YES_OPTION) continue;
            }
            break;
        }
        if (destNodeName == null) return;
        if (graphModel.addLink(srcNodeName, destNodeName, distance)) {
            expandedNode.add(srcNodeName);
            expandedNode.add(destNodeName);
            restoreTreeExpansion();
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    "The specified link could not be created",
                    "Create Link",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void onAddNodeClicked(ActionEvent e) {
        JTextField nodeName = new JTextField();
        final JComponent[] inputs = new JComponent[]{
                new JLabel("Node name:"),
                nodeName,
        };
        int result = JOptionPane.showConfirmDialog(
                null,
                inputs,
                "Add Node",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null);
        if (result == JOptionPane.OK_OPTION && !nodeName.getText().isEmpty()) {
            if (!graphModel.addNode(nodeName.getText())) {
                JOptionPane.showMessageDialog(
                        null,
                        "The specified node has already existed",
                        "Add Node",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        }
    }

    private void onResetClicked(ActionEvent e) {
        restoreTreeExpansion();
    }

    private void onLoadFileClicked(ActionEvent e) {
        FileDialog fd = new FileDialog(this, "Load File From...", FileDialog.LOAD);
        fd.setVisible(true);
        String filename = fd.getFile();
        if (filename == null) return;
        if (graphModel.loadFile(fd.getDirectory() + filename)) {
            for (int i = 0; i < topologyTree.getRowCount(); i++) {
                topologyTree.expandRow(i);
            }
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    "File is invalid!",
                    "Load Failed",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void refreshSourceSelection() {
        sourceSelection.setModel(new DefaultComboBoxModel<>(
                ((GraphTreeModel.Root) graphModel.getRoot()).nodes
        ));
    }

    private void onComputeAllClicked(ActionEvent e) {
    }

    private void onSingleStepClicked(ActionEvent e) {
    }

}
