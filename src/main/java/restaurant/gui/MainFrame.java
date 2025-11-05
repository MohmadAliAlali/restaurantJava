package restaurant.gui;

import restaurant.model.*;
import restaurant.model.MenuItem;
import restaurant.database.DatabaseManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {
    private List<MenuItem> menuItems;
    public static String user ;
    private List<Order> orders;
    private JPanel mainPanel;
    private JTabbedPane tabbedPane;
    private boolean isLoggedIn = false;

    public MainFrame() {
        if (!showLoginDialog()) {
            System.exit(0);
        }
        initializeData();
        initializeUI();
    }

    private void initializeData() {
        menuItems = DatabaseManager.loadMenuItems();
        orders = DatabaseManager.loadOrders();

    }

    private void initializeUI() {
        setTitle("DashBord restaurant");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setLocationRelativeTo(null);

        mainPanel = new JPanel(new BorderLayout());
        tabbedPane = new JTabbedPane();

        tabbedPane.addTab("New Order", new NewOrderPanel(this));
        tabbedPane.addTab("Order", new CurrentOrdersPanel(this));
        tabbedPane.addTab("Menu Manager", new MenuManagementPanel(this));
        tabbedPane.addTab("Reports", new ReportsPanel(this));

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);


        setVisible(true);
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void addOrder(Order order) {
        DatabaseManager.saveOrderMain(order);
        orders = DatabaseManager.loadOrders();
        refreshPanels();
    }

    public void updateMenuItems() {
        DatabaseManager.saveMenuItems(menuItems);
        refreshPanels();
    }

    public void refreshPanels() {
        for (Component comp : tabbedPane.getComponents()) {
            if (comp instanceof JPanel) {
                ((JPanel) comp).revalidate();
                ((JPanel) comp).repaint();
            }
        }
    }

    private boolean showLoginDialog() {
        LoginDialog loginDialog = new LoginDialog(this);
        loginDialog.setVisible(true);
        user = loginDialog.getUser();
        isLoggedIn = loginDialog.isSucceeded();
        return isLoggedIn;
    }


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("hello error 2");
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
