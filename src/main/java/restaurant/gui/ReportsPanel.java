package restaurant.gui;

import restaurant.model.*;
import restaurant.model.MenuItem;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ReportsPanel extends JPanel {
    private MainFrame mainFrame;
    private JTextArea reportArea;
    private JComboBox<String> reportTypeCombo;

    public ReportsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(10, 10));
        initializeComponents();
    }

    private void initializeComponents() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        reportTypeCombo = new JComboBox<>(new String[]{
            "Daily Order",
            "Daily Revenue",
            "Regular Customers"
        });
        
        JButton generateButton = new JButton("Create Reports");
        controlPanel.add(reportTypeCombo);
        controlPanel.add(generateButton);

        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(reportArea);

        add(controlPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        generateButton.addActionListener(e -> generateReport());
    }

    private void generateReport() {
        String reportType = (String) reportTypeCombo.getSelectedItem();
        StringBuilder report = new StringBuilder();
        
        report.append("=== ").append(reportType).append(" ===\n\n");
        
        switch (reportType) {
            case "Daily Order":
                generateDailyOrdersReport(report);
                break;
            case "Daily Revenue":
                generateDailyRevenueReport(report);
                break;
            case "Regular Customers":
                generateFrequentCustomersReport(report);
                break;
        }
        
        reportArea.setText(report.toString());
    }

    private void generateDailyOrdersReport(StringBuilder report) {
        List<Order> orders = mainFrame.getOrders();
        
        report.append("Total Number of Orders: ").append(orders.size()).append("\n\n");
        report.append("Details order:\n");
        
        for (Order order : orders) {
            report.append("ID: ").append(order.getOrderId()).append("\n");
            report.append("User Type: ").append(order.getUserType()).append("\n");
            report.append("Name Creator: ").append(order.getUserName()).append("\n");
            report.append("Customer name: ").append(order.getCustomerName()).append("\n");
            report.append("Order type: ").append(order.getOrderType()).append("\n");
            report.append("Status: ").append(order.getStatus()).append("\n");
            report.append("Prise: ").append(order.getTotalAmount()).append(" S.P\n");
            report.append("Tip: ").append(order.getTip()).append(" S.P\n");
            report.append("------------------\n");
        }
    }


    private void generateDailyRevenueReport(StringBuilder report) {
        double totalRevenue = mainFrame.getOrders().stream()
            .mapToDouble(order -> order.getTotalAmount() + order.getTip())
            .sum();
            
        double totalTips = mainFrame.getOrders().stream()
            .mapToDouble(Order::getTip)
            .sum();
        
        report.append("Total Revenue: ").append(String.format("%.2f", totalRevenue)).append(" S.P\n");
        report.append("Total tips: ").append(String.format("%.2f", totalTips)).append(" S.P\n");
        report.append("Net Sales: ").append(String.format("%.2f", totalRevenue - totalTips)).append(" S.p\n\n");
        
        report.append("Details Revenue by type of order:\n");
        Map<String, Double> revenueByType = mainFrame.getOrders().stream()
            .collect(Collectors.groupingBy(
                Order::getOrderType,
                Collectors.summingDouble(order -> order.getTotalAmount() + order.getTip())
            ));
            
        revenueByType.forEach((type, amount) -> {
            report.append(type).append(": ").append(String.format("%.2f", amount)).append(" S.P\n");
        });
    }

    private void generateFrequentCustomersReport(StringBuilder report) {
        Map<String, Integer> customerVisits = new HashMap<>();
        Map<String, Double> customerSpending = new HashMap<>();
        
        for (Order order : mainFrame.getOrders()) {
            customerVisits.merge(order.getCustomerName(), 1, Integer::sum);
            customerSpending.merge(order.getCustomerName(), 
                order.getTotalAmount() + order.getTip(), Double::sum);
        }
        
        report.append("Most Frequent Customers:\n\n");
        
        customerVisits.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(5)
            .forEach(entry -> {
                report.append(entry.getKey()).append("\n");
                report.append("Number of Visits: ").append(entry.getValue()).append("\n");
                report.append("Total Expenditure: ")
                      .append(String.format("%.2f", customerSpending.get(entry.getKey())))
                      .append(" S.P\n");
                report.append("------------------\n");
            });
    }
}
