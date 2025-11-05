package restaurant.gui;

import restaurant.model.MenuItem;
import restaurant.model.Order;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class NewOrderPanel extends JPanel {
    private final MainFrame mainFrame;
    private DefaultListModel<MenuItem> menuListModel;
    private DefaultListModel<String> orderListModel;
    private JList<MenuItem> menuList;
    private JList<String> orderList;
    private JComboBox<String> orderTypeCombo;
    private JTextField customerNameField;
    private JTextField customerPhoneField;
    private JTextField tipField;
    private JSpinner quantitySpinner;
    private final Map<MenuItem, Integer> orderItems;
    private JLabel selectedItemImageLabel;

    public NewOrderPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.orderItems = new HashMap<>();
        setLayout(new BorderLayout(10, 10));
        initializeComponents();
    }

    private void initializeComponents() {
        JPanel menuPanel = createMenuPanel();
        JPanel orderPanel = createOrderPanel();
        JPanel customerPanel = createCustomerInfoPanel();
        JPanel submitPanel = createSubmitPanel();
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        centerPanel.add(menuPanel);
        centerPanel.add(orderPanel);

        add(centerPanel, BorderLayout.CENTER);
        add(customerPanel, BorderLayout.NORTH);
        add(submitPanel, BorderLayout.SOUTH);
    }

    private JPanel createMenuPanel() {
        JPanel menuPanel = new JPanel(new BorderLayout(5, 5));
        menuPanel.setBorder(BorderFactory.createTitledBorder("List of Food"));

        menuListModel = new DefaultListModel<>();
        mainFrame.getMenuItems().forEach(menuListModel::addElement);
        menuList = new JList<>(menuListModel);
        menuList.setCellRenderer(new MenuItemRenderer());
        menuList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane menuScrollPane = new JScrollPane(menuList);

        selectedItemImageLabel = new JLabel();
        selectedItemImageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        quantityPanel.add(new JLabel("Quantity:"));
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        quantityPanel.add(quantitySpinner);

        JButton addButton = new JButton("Add order >");
        JButton refreshButton = new JButton("refresh menu");
        quantityPanel.add(addButton);
        quantityPanel.add(refreshButton);

        menuPanel.add(menuScrollPane, BorderLayout.CENTER);
        menuPanel.add(quantityPanel, BorderLayout.SOUTH);

        menuList.addListSelectionListener(e -> updateSelectedItemImage());
        addButton.addActionListener(e -> addSelectedItemToOrder());
        refreshButton.addActionListener(e -> refreshMenuList());

        return menuPanel;
    }

    private void refreshMenuList() {
        menuListModel.clear();
        if (mainFrame.getMenuItems() != null && !mainFrame.getMenuItems().isEmpty()) {
            mainFrame.getMenuItems().forEach(menuListModel::addElement);
        } else {
            JOptionPane.showMessageDialog(this,
                    "No items found in the menu.",
                    "Menu Refresh",
                    JOptionPane.WARNING_MESSAGE);
        }
        menuList.updateUI();
    }

    private JPanel createOrderPanel() {
        JPanel orderPanel = new JPanel(new BorderLayout(5, 5));
        orderPanel.setBorder(BorderFactory.createTitledBorder("this order"));

        orderListModel = new DefaultListModel<>();
        orderList = new JList<>(orderListModel);
        JScrollPane orderScrollPane = new JScrollPane(orderList);

        JButton removeButton = new JButton("delete from order");
        orderPanel.add(orderScrollPane, BorderLayout.CENTER);
        orderPanel.add(removeButton, BorderLayout.SOUTH);

        removeButton.addActionListener(e -> removeSelectedItemFromOrder());

        return orderPanel;
    }

    private JPanel createCustomerInfoPanel() {
        JPanel customerPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        customerPanel.setBorder(BorderFactory.createTitledBorder("customer data"));

        customerPanel.add(new JLabel("name:"));
        customerNameField = new JTextField();
        customerPanel.add(customerNameField);

        customerPanel.add(new JLabel("phone number:"));
        customerPhoneField = new JTextField();
        customerPanel.add(customerPhoneField);

        customerPanel.add(new JLabel("type order:"));
        orderTypeCombo = new JComboBox<>(new String[]{"in restaurant order", "delivery", "special order"});
        customerPanel.add(orderTypeCombo);

        customerPanel.add(new JLabel("tips:"));
        tipField = new JTextField("5.0");
        customerPanel.add(tipField);

        return customerPanel;
    }

    private JPanel createSubmitPanel() {
        JPanel submitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton submitButton = new JButton("submit");
        submitPanel.add(submitButton);

        submitButton.addActionListener(e -> submitOrder());

        return submitPanel;
    }

    private void updateSelectedItemImage() {
        MenuItem selected = menuList.getSelectedValue();
        if (selected != null && selected.getImage() != null) {
            ImageIcon originalIcon = selected.getImage();
            Image scaledImage = originalIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            selectedItemImageLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            selectedItemImageLabel.setIcon(null);
        }
    }

    private void addSelectedItemToOrder() {
        MenuItem selected = menuList.getSelectedValue();
        if (selected != null) {
            int quantity = (int) quantitySpinner.getValue();
            orderItems.merge(selected, quantity, Integer::sum);
            updateOrderList();
        }
    }

    private void removeSelectedItemFromOrder() {
        int selectedIndex = orderList.getSelectedIndex();
        if (selectedIndex != -1) {
            String selectedString = orderList.getSelectedValue();
            MenuItem item = findMenuItemByOrderString(selectedString);
            if (item != null) {
                orderItems.remove(item);
                updateOrderList();
            }
        }
    }

    private MenuItem findMenuItemByOrderString(String orderString) {
        String itemName = orderString.split(" x")[0];
        return mainFrame.getMenuItems().stream()
                .filter(item -> orderString.startsWith(item.getName()))
                .findFirst()
                .orElse(null);
    }

    private void updateOrderList() {
        orderListModel.clear();
        double total = 0;
        for (Map.Entry<MenuItem, Integer> entry : orderItems.entrySet()) {
            MenuItem item = entry.getKey();
            int quantity = entry.getValue();
            double itemTotal = item.getPrice() * quantity;
            total += itemTotal;
            orderListModel.addElement(String.format("%s x%d = %.2f S.P",
                item.getName(), quantity, itemTotal));
        }
        orderListModel.addElement(String.format("total: %.2f S.p", total));
    }

    private void submitOrder() {
        if (validateOrder()) {
            Order order = new Order(
                    customerNameField.getText(),
                    customerPhoneField.getText()
            );
            String type = (String) orderTypeCombo.getSelectedItem();
            order.setOrderType(type);

            for (Map.Entry<MenuItem, Integer> entry : orderItems.entrySet()) {
                MenuItem item = entry.getKey();
                int quantity = entry.getValue();
                order.addItem(item, quantity);
            }

            try {
                double tip = Double.parseDouble(tipField.getText());
                order.setTip(tip);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                    "the tip is error is set as 0.",
                    "error",
                    JOptionPane.WARNING_MESSAGE);
            }

            mainFrame.addOrder(order);
            clearForm();
            JOptionPane.showMessageDialog(this,
                "order create success!",
                "OK",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private boolean validateOrder() {
        if (orderItems.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please add at least one item to the order.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (customerNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter the customer's name.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String phoneNumber = customerPhoneField.getText().trim();
        if (phoneNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter the customer's phone number.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!phoneNumber.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this,
                    "Phone number must be exactly 10 digits.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void clearForm() {
        orderItems.clear();
        orderListModel.clear();
        customerNameField.setText("");
        customerPhoneField.setText("");
        tipField.setText("0.0");
        orderTypeCombo.setSelectedIndex(0);
        quantitySpinner.setValue(1);
    }

    private class MenuItemRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {

            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            panel.setBackground(Color.WHITE);

            if (value instanceof MenuItem) {
                MenuItem item = (MenuItem) value;

                JLabel imageLabel = new JLabel();
                if (item.getImage() != null) {
                    ImageIcon icon = item.getImage();
                    Image scaledImage = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(scaledImage));
                }
                imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                imageLabel.setPreferredSize(new Dimension(80, 80));

                JLabel textLabel = new JLabel(String.format("<html><b>%s</b><br>%.2f S.P</html>",
                        item.getName(), item.getPrice()));
                textLabel.setHorizontalAlignment(SwingConstants.CENTER);

                panel.add(imageLabel, BorderLayout.WEST);
                panel.add(textLabel, BorderLayout.CENTER);

                if (isSelected) {
                    panel.setBackground(list.getSelectionBackground());
                    panel.setForeground(list.getSelectionForeground());
                } else {
                    panel.setBackground(list.getBackground());
                    panel.setForeground(list.getForeground());
                }
            }

            return panel;
        }
    }
}
