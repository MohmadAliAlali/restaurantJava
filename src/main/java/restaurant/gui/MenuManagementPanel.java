package restaurant.gui;

import restaurant.database.DatabaseManager;
import restaurant.model.MenuItem;
import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;

public class MenuManagementPanel extends JPanel {
    private MainFrame mainFrame;
    private JTable menuTable;
    private DefaultTableModel tableModel;
    private JTextField nameField;
    private JTextField descField;
    private JTextField priceField;
    private JTextField categoryField;
    private JTextField imagePathField;

    public MenuManagementPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(10, 10));
        initializeComponents();
    }

    private void initializeComponents() {
        String[] columns = {"Name", "Description", "Price", "Category"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        menuTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(menuTable);

        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add / Edit item"));

        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Description:"));
        descField = new JTextField();
        inputPanel.add(descField);

        inputPanel.add(new JLabel("Price:"));
        priceField = new JTextField();
        inputPanel.add(priceField);

        inputPanel.add(new JLabel("Category:"));
        categoryField = new JTextField();
        inputPanel.add(categoryField);

        inputPanel.add(new JLabel("Path of image:"));
        imagePathField = new JTextField();
        inputPanel.add(imagePathField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("Add");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton browseButton = new JButton("Display");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        inputPanel.add(buttonPanel);
        inputPanel.add(browseButton);

        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
        addButton.addActionListener(e -> addMenuItem());
        editButton.addActionListener(e -> editMenuItem());
        deleteButton.addActionListener(e -> deleteMenuItem());
        menuTable.getSelectionModel().addListSelectionListener(e -> selectMenuItem());
        browseButton.addActionListener(e -> imagePathField.setText(selectImage()));
        refreshMenuTable();
    }

    private void addMenuItem() {
        try {
            String name = nameField.getText().trim();
            String description = descField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            String category = categoryField.getText().trim();
            String imagePath = imagePathField.getText().trim();

            if (name.isEmpty() || description.isEmpty() || category.isEmpty() || imagePath.isEmpty()) {
                throw new IllegalArgumentException("all field is reqoierd");
            }

            MenuItem newItem = new MenuItem(name, description, price, category, imagePath);
            mainFrame.getMenuItems().add(newItem);
            mainFrame.updateMenuItems();
            refreshMenuTable();
            clearFields();

            JOptionPane.showMessageDialog(this,
                "success add item",
                "OK",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "set valid Price",
                "error",
                JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                e.getMessage(),
                "error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private String selectImage() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            return selectedFile.getAbsolutePath();
        }
        return "";
    }

    private void editMenuItem() {
        int selectedRow = menuTable.getSelectedRow();
        if (selectedRow != -1) {
            try {
                String name = nameField.getText().trim();
                String description = descField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                String category = categoryField.getText().trim();
                String imagePath = imagePathField.getText().trim();

                if (name.isEmpty() || description.isEmpty() || category.isEmpty() || imagePath.isEmpty()) {
                    throw new IllegalArgumentException("جميع الحقول مطلوبة.");
                }

                MenuItem item = mainFrame.getMenuItems().get(selectedRow);
                item.setName(name);
                item.setDescription(description);
                item.setPrice(price);
                item.setCategory(category);
                item.setImagePath(imagePath);

                DatabaseManager.saveMenuItems(mainFrame.getMenuItems());

                refreshMenuTable();
                clearFields();

                JOptionPane.showMessageDialog(this,
                        "تم تحديث البيانات بنجاح.",
                        "تمت العملية",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "يرجى إدخال سعر صالح.",
                        "خطأ",
                        JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this,
                        e.getMessage(),
                        "خطأ",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "يرجى اختيار عنصر للتعديل.",
                    "خطأ",
                    JOptionPane.WARNING_MESSAGE);
        }
    }


    private void deleteMenuItem() {
        int selectedRow = menuTable.getSelectedRow();
        if (selectedRow != -1) {
            int confirmation = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this item?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmation == JOptionPane.YES_OPTION) {
                mainFrame.getMenuItems().remove(selectedRow);
                DatabaseManager.saveMenuItems(mainFrame.getMenuItems());
                refreshMenuTable();

                JOptionPane.showMessageDialog(
                        this,
                        "Item deleted successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select an item to delete.",
                    "Error",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }


    private void selectMenuItem() {
        int selectedRow = menuTable.getSelectedRow();
        if (selectedRow != -1) {
            MenuItem item = mainFrame.getMenuItems().get(selectedRow);
            nameField.setText(item.getName());
            descField.setText(item.getDescription());
            priceField.setText(String.valueOf(item.getPrice()));
            categoryField.setText(item.getCategory());
            imagePathField.setText(item.getImagePath());
        }
    }

    private void clearFields() {
        nameField.setText("");
        descField.setText("");
        priceField.setText("");
        categoryField.setText("");
        imagePathField.setText("");
    }

    private void refreshMenuTable() {
        tableModel.setRowCount(0);
        for (MenuItem item : mainFrame.getMenuItems()) {
            Object[] row = {
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.getCategory()
            };
            tableModel.addRow(row);
        }
    }
}
