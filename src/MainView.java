import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainView extends JFrame {

    private JPanel controlBar;
    private JPanel graphControlPanel;
    private JPanel graphIOPanel;
    private JComboBox<String> sourceSelection;
    private JTree topologyTree;

    public MainView() {
        super();
        setTitle("LSR Simulator");
        addComponents();
    }

    public void init() {
        setVisible(true);
    }

    private void addComponents() {
        controlBar = buildControlBar();
        this.add(buildGraphPanel(), BorderLayout.WEST);
        this.add(buildGraphOptionPanel(), BorderLayout.CENTER);
        this.add(controlBar, BorderLayout.SOUTH);
        pack();
    }

    private JPanel buildGraphPanel() {
        JPanel graphPanel = new JPanel(new BorderLayout());
        graphPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        topologyTree = new JTree();
        JLabel topologyLbl = new JLabel("Imported graph:");
        topologyLbl.setBorder(new EmptyBorder(0, 0, 3, 0));
        graphPanel.add(topologyLbl, BorderLayout.NORTH);
        JScrollPane topologyPane = new JScrollPane(
                topologyTree,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        topologyPane.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
        graphPanel.add(topologyPane, BorderLayout.CENTER);
        graphPanel.setPreferredSize(new Dimension(130, 250));
        return graphPanel;
    }

    private JPanel buildGraphOptionPanel() {
        JPanel optionPanel = new JPanel(new BorderLayout());
        graphIOPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        graphControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
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
        Button loadBtn = new Button("Load File");
        Button saveBtn = new Button("Save File");
        Button addNodeBtn = new Button("Add Node");
        Button addLinkBtn = new Button("Add Link");
        Button removeBtn = new Button("Remove Selected");
        loadBtn.addActionListener(this::onLoadFileClicked);
        saveBtn.addActionListener(this::onSaveFileClicked);
        addNodeBtn.addActionListener(this::onAddNodeClicked);
        addLinkBtn.addActionListener(this::onAddLinkClicked);
        removeBtn.addActionListener(this::onRemoveClicked);
        graphIOPanel.add(loadBtn);
        graphIOPanel.add(saveBtn);
        graphControlPanel.add(addNodeBtn);
        graphControlPanel.add(addLinkBtn);
        graphControlPanel.add(removeBtn);
        upperPanel.add(graphIOPanel);
        upperPanel.add(graphControlPanel);
        upperPanel.setBorder(new EmptyBorder(19,0,0,0));
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

    private void onSaveFileClicked(ActionEvent e) {
    }

    private void onAddLinkClicked(ActionEvent e) {
    }

    private void onRemoveClicked(ActionEvent e) {
    }

    private void onAddNodeClicked(ActionEvent e) {
    }

    private void onResetClicked(ActionEvent e) {
    }

    private void onLoadFileClicked(ActionEvent e) {
    }

    private void onComputeAllClicked(ActionEvent e) {
    }

    private void onSingleStepClicked(ActionEvent e) {
    }
}
