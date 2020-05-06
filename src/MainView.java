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

    private JComboBox<String> sourceSelection;
    private GraphTreeModel graphModel;
    private JTree topologyTree;
    private HashSet<String> expandedNode = new HashSet<>();

    private Button addNodeBtn;
    private Button addLinkBtn;
    private Button removeBtn;
    private Button loadBtn;
    private Button saveBtn;
    private Button clearBtn;

    private JTextArea statusArea;

    private DijkstraAlgorithm dijkstra;
    private int computeStep = 0;
    private boolean computing = false;

    public MainView() {
        super();
        setTitle("LSR Simulator");

        Icon fileIcon = new ImageIcon(MainView.class.getResource("file.png"));
        Icon folderOpenIcon = new ImageIcon(MainView.class.getResource("folder_open.png"));
        Icon folderCloseIcon = new ImageIcon(MainView.class.getResource("folder_close.png"));

        UIManager.put("Tree.closedIcon", folderCloseIcon);
        UIManager.put("Tree.openIcon", folderOpenIcon);
        UIManager.put("Tree.leafIcon", fileIcon);

        addComponents();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    }

    public void init() {
        setVisible(true);
    }

    private void addComponents() {
        JPanel controlBar = buildControlBar();
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
        graphPanel.setPreferredSize(new Dimension(130, 300));
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
        JPanel graphIOPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel upperPanel = new JPanel();
        upperPanel.setLayout(new BoxLayout(upperPanel, BoxLayout.PAGE_AXIS));
        JPanel bottomPanel = new JPanel(new BorderLayout());
        statusArea = new JTextArea(6, 28);
        statusArea.setEditable(false);
        statusArea.setFont(new Font("monospaced", Font.PLAIN, 12));
        JLabel statusLbl = new JLabel("Status");
        statusLbl.setBorder(new EmptyBorder(0, 0, 3, 0));
        JScrollPane statusPane = new JScrollPane(
                statusArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        statusPane.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
        statusPane.setPreferredSize(new Dimension(400, 100));
        bottomPanel.add(statusLbl, BorderLayout.NORTH);
        bottomPanel.add(statusPane, BorderLayout.CENTER);
        loadBtn = new Button("Load File...");
        saveBtn = new Button("Save File...");
        clearBtn = new Button("Clear Topology");
        Button clearMsgBtn = new Button("Clear Status");
        loadBtn.addActionListener(this::onLoadFileClicked);
        saveBtn.addActionListener(this::onSaveFileClicked);
        clearBtn.addActionListener(this::onClearClicked);
        clearMsgBtn.addActionListener(this::onClearMsgClicked);
        graphIOPanel.add(loadBtn);
        graphIOPanel.add(saveBtn);
        graphIOPanel.add(clearBtn);
        graphIOPanel.add(clearMsgBtn);
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
        if (computing) return;
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
                        "Link Existed " + srcNodeName + " <> " + destNodeName,
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
            if (graphModel.addNode(nodeName.getText())) {
                for (int i = 0; i < topologyTree.getRowCount(); i++) {
                    Object object = topologyTree.getPathForRow(i).getLastPathComponent();
                    if (object instanceof GraphTreeModel.Node && nodeName.getText().equals(
                            ((GraphTreeModel.Node) object).name
                    )) {
                        topologyTree.setSelectionRow(i);
                        topologyTree.scrollRowToVisible(i);
                    }
                }
            } else {
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
        dijkstra = null;
        setComputing(false);
        statusArea.setText(statusArea.getText() + "===== RESET ===== \n\n");
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

    private void onClearClicked(ActionEvent actionEvent) {
        int confirmResult = JOptionPane.showConfirmDialog(
                null,
                "This will remove all links and nodes, continue?",
                "Clear All",
                JOptionPane.YES_NO_OPTION);
        if (confirmResult != JOptionPane.YES_OPTION) return;
        graphModel.clearGraph();
    }

    private void setComputing(boolean enabled) {
        if (enabled) {
            addNodeBtn.setEnabled(false);
            addLinkBtn.setEnabled(false);
            removeBtn.setEnabled(false);
            loadBtn.setEnabled(false);
            saveBtn.setEnabled(false);
            clearBtn.setEnabled(false);
            sourceSelection.setEnabled(false);
        } else {
            topologyTree.clearSelection();
            loadBtn.setEnabled(true);
            saveBtn.setEnabled(true);
            clearBtn.setEnabled(true);
            addNodeBtn.setEnabled(true);
            sourceSelection.setEnabled(true);
        }
        computing = enabled;
    }

    private void refreshSourceSelection() {
        sourceSelection.setModel(new DefaultComboBoxModel<>(
                ((GraphTreeModel.Root) graphModel.getRoot()).nodes
        ));
    }

    private void onSingleStepClicked(ActionEvent e) {
        if (!computing) {
            if (!tryInitCompute()) return;
            computeStep = 0;
            setComputing(true);
        }
        Iterator<VisitedNodeInfo> iterator = dijkstra.iterator();
        if (iterator.hasNext()) {
            VisitedNodeInfo info = iterator.next();
            StringBuilder status = new StringBuilder(statusArea.getText());
            status.append(String.format("Single Step %d:\n    [Visiting node: %s]\n",
                    computeStep,
                    info.getNewVisitedNode()
            ));
            for (String node : info.getNewDiscoverNodes().toArray(new String[0])) {
                status.append(String.format("    > Found %s: Path: %s Cost: %d\n",
                        node,
                        String.join(" > ", info.getChain(node)),
                        info.distance(node)
                ));
            }
            status.append("\n");
            statusArea.setText(status.toString());
            computeStep++;
        } else {
            printFinalResult();
        }
    }

    private void onComputeAllClicked(ActionEvent e) {
        if (!computing) {
            if (!tryInitCompute()) return;
        }
        for (VisitedNodeInfo info : dijkstra) {
            // For side effects only
            assert true;
        }
        printFinalResult();
    }

    private boolean tryInitCompute() {
        String sourceNode = (String) sourceSelection.getSelectedItem();
        boolean invalid = false;
        if (sourceNode != null) {
            try {
                dijkstra = new DijkstraAlgorithm(graphModel.getGraph(), sourceNode);
            } catch (IllegalArgumentException ex) {
                invalid = true;
            }
        } else {
            invalid = true;
        }
        if (invalid) {
            JOptionPane.showMessageDialog(
                    null,
                    "Please select a valid source node",
                    "Invalid Source Node",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }
        return true;
    }

    private void onClearMsgClicked(ActionEvent actionEvent) {
        statusArea.setText("");
    }

    private void printFinalResult() {
        VisitedNodeInfo info = dijkstra.getFinalResult();
        StringBuilder status = new StringBuilder(statusArea.getText());
        status.append("=================\n");
        status.append("  Summary Table  \n");
        status.append("=================\n");
        status.append(String.format("Source %s:\n", info.getSourceNode()));
        for (String node : info.getAllVisitedNodes()) {
            if (node.equals(info.getSourceNode())) continue;
            status.append(String.format("    %s: Path: %s Cost: %d\n",
                    node,
                    String.join(" > ", info.getChain(node)),
                    info.distance(node)
            ));
        }
        status.append("\n");
        statusArea.setText(status.toString());
        if (computing) setComputing(false);
        JOptionPane.showMessageDialog(
                null,
                "All paths computed!",
                "Simulation completed",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

}
