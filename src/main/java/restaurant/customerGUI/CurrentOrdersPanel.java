package restaurant.customerGUI;


import restaurant.database.DataBaseCustomer;
import restaurant.model.*;
import restaurant.model.MenuItem;
import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class CurrentOrdersPanel extends JPanel {
    private CustomerMain mainFrame;
    private JTable ordersTable;
    private JTextArea orderDetailsArea;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusCombo;
    private JLabel totalLabel;

    public CurrentOrdersPanel(CustomerMain mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(10, 10));
        initializeComponents();
    }

    private void initializeComponents() {
        initializeOrdersTable();
        initializeOrderDetailsArea();
        initializeControlPanel();
        addComponentsToPanel();
    }

    private void initializeOrdersTable() {
        String[] columnNames = {"ID", "TYPE ORDER", "NAME CUSTOMER","NAME ORDER", "CUSTOMER PHONE", "STATUS"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        ordersTable = new JTable(tableModel);
        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScrollPane = new JScrollPane(ordersTable);
    }
    private void initializeOrderDetailsArea() {
        JPanel detailsPanel = new JPanel(new BorderLayout());
        orderDetailsArea = new JTextArea(10, 40);
        orderDetailsArea.setEditable(false);
        detailsPanel.add(new JScrollPane(orderDetailsArea), BorderLayout.CENTER);

        totalLabel = new JLabel("Total: 0.00");
        detailsPanel.add(totalLabel, BorderLayout.SOUTH);
    }


    private void initializeControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("Update List");
        controlPanel.add(new JLabel("Status:"));
        controlPanel.add(refreshButton);
    }


    private void addComponentsToPanel() {
        add(new JScrollPane(ordersTable), BorderLayout.CENTER);
        add(new JPanel(new BorderLayout()) {{
            add(new JScrollPane(orderDetailsArea), BorderLayout.CENTER);
            add(totalLabel, BorderLayout.SOUTH);
        }}, BorderLayout.EAST);
        JButton button =new JButton("Update status");
        add(new JPanel(new FlowLayout(FlowLayout.RIGHT)) {{

            add(button);
        }}, BorderLayout.SOUTH);
        button.addActionListener(e -> refreshOrders());
        ordersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateOrderDetails();
            }
        });


    }

    private void updateOrderDetails() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow != -1) {
            String orderId = ordersTable.getValueAt(selectedRow, 0).toString();
            List<Order> orders = mainFrame.getOrders();
            Order order = null;
            for (Order o : orders) {
                if (o.getOrderId().equals(orderId)) {
                    order = o;
                    break;
                }
            }

            if (order != null) {
                StringBuilder details = new StringBuilder();
                details.append("Order Details:\n\n");
                details.append("ID: ").append(order.getOrderId()).append("\n");
                details.append("Name Customer: ").append(order.getCustomerName()).append("\n");
                details.append("Name Order: ").append(order.getOrderUserName()).append("\n");
                details.append("Phone number: ").append(order.getCustomerPhone()).append("\n");
                details.append("Order Type: ").append(order.getOrderType()).append("\n\n");
                details.append("items:\n");

                double total = 0;
                for (MenuItem item : order.getItems().keySet()) {
                    int quantity = order.getItems().get(item);
                    double itemTotal = item.getPrice() * quantity;
                    total += itemTotal;
                    details.append(String.format("%s x%d = %.2f\n",
                            item.getName(), quantity, itemTotal));
                }

                details.append("\nTotal: ").append(String.format("%.2f", total));
                if (order.getTip() > 0) {
                    details.append("\ntips: ").append(String.format("%.2f", order.getTip()));
                    details.append("\ntotal: ").append(String.format("%.2f", total + order.getTip()));
                }

                orderDetailsArea.setText(details.toString());
                totalLabel.setText(String.format("total: %.2f", total + order.getTip()));
            } else {
                orderDetailsArea.setText("cant get data.");
                totalLabel.setText("total: 0.00");
            }
        }
    }



    private void refreshOrders() {
        tableModel.setRowCount(0);

        List<Order> orders = DataBaseCustomer.loadOrders();
        for (Order order : orders) {
            Object[] row = {
                    order.getOrderId(),
                    order.getOrderType(),
                    order.getCustomerName(),
                    order.getOrderUserName(),
                    order.getCustomerPhone(),
                    order.getStatus()
            };
            tableModel.addRow(row);
        }

        orderDetailsArea.setText("");
        totalLabel.setText("total: 0.00");
    }
}
