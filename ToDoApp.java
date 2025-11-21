import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;

public class ToDoApp extends JFrame implements ActionListener {
    private JTextField taskField;
    private JButton addBtn, deleteBtn, clearBtn, saveBtn, loadBtn;
    private DefaultListModel<String> listModel;
    private JList<String> taskList;
    private static final String SAVE_FILE = "tasks.txt";

    public ToDoApp() {
        super("To-Do App");

        // Top panel: input + add button
        JPanel topPanel = new JPanel(new BorderLayout(6, 6));
        taskField = new JTextField();
        addBtn = new JButton("Add");
        topPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        topPanel.add(taskField, BorderLayout.CENTER);
        topPanel.add(addBtn, BorderLayout.EAST);

        // Center: list inside scroll pane
        listModel = new DefaultListModel<>();
        taskList = new JList<>(listModel);
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(taskList);

        // Bottom: buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        deleteBtn = new JButton("Delete Selected");
        clearBtn = new JButton("Clear All");
        saveBtn = new JButton("Save");
        loadBtn = new JButton("Load");
        bottomPanel.add(deleteBtn);
        bottomPanel.add(clearBtn);
        bottomPanel.add(saveBtn);
        bottomPanel.add(loadBtn);

        // Add components to frame
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Wire listeners
        addBtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        clearBtn.addActionListener(this);
        saveBtn.addActionListener(this);
        loadBtn.addActionListener(this);

        // Allow pressing Enter to add task
        taskField.addActionListener(e -> addTaskFromField());

        // Default window settings
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 450);
        setLocationRelativeTo(null); // center
        setVisible(true);
    }

    // Add task when Add button or Enter pressed
    private void addTaskFromField() {
        String text = taskField.getText().trim();
        if (text.isEmpty()) {
            // short, clear feedback
            JOptionPane.showMessageDialog(this, "Enter a task first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        listModel.addElement(text);
        taskField.setText("");
        taskField.requestFocus();
    }

    // Save tasks to a simple text file (one task per line)
    private void saveTasks() {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(SAVE_FILE))) {
            for (int i = 0; i < listModel.size(); i++) {
                bw.write(listModel.get(i));
                bw.newLine();
            }
            JOptionPane.showMessageDialog(this, "Tasks saved to " + SAVE_FILE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving tasks: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Load tasks from file (replaces current list)
    private void loadTasks() {
        Path path = Paths.get(SAVE_FILE);
        if (!Files.exists(path)) {
            JOptionPane.showMessageDialog(this, SAVE_FILE + " not found. Nothing to load.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try (BufferedReader br = Files.newBufferedReader(path)) {
            listModel.clear();
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) listModel.addElement(line);
            }
            JOptionPane.showMessageDialog(this, "Tasks loaded from " + SAVE_FILE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error loading tasks: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Delete selected task
    private void deleteSelected() {
        int idx = taskList.getSelectedIndex();
        if (idx == -1) {
            JOptionPane.showMessageDialog(this, "Select a task to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        listModel.remove(idx);
    }

    // Clear all tasks with confirmation
    private void clearAll() {
        if (listModel.isEmpty()) return;
        int confirm = JOptionPane.showConfirmDialog(this, "Clear all tasks?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            listModel.clear();
        }
    }

    // Handle button clicks
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == addBtn) {
            addTaskFromField();
        } else if (src == deleteBtn) {
            deleteSelected();
        } else if (src == clearBtn) {
            clearAll();
        } else if (src == saveBtn) {
            saveTasks();
        } else if (src == loadBtn) {
            loadTasks();
        }
    }

    // Run on EDT
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ToDoApp());
    }
}

