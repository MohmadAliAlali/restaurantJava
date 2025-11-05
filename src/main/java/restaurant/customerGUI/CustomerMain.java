package restaurant.customerGUI;

import restaurant.database.DataBaseCustomer;
import restaurant.model.MenuItem;
import restaurant.model.Order;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerMain extends JFrame {
    private List<MenuItem> menuItems;
    public static String user ;
    private List<Order> orders;
    private JPanel mainPanel;
    private JTabbedPane tabbedPane;
    private boolean isLoggedIn = false;

    public CustomerMain() {
        if (!showLoginDialog()) {
            System.exit(0);
        }
        initializeData();
        initializeUI();
    }

    private void initializeData() {
        menuItems = DataBaseCustomer.loadMenuItems();
        orders = DataBaseCustomer.loadOrders();
    }

    private void initializeUI() {
        setTitle("User restaurant");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        mainPanel = new JPanel(new BorderLayout());
        tabbedPane = new JTabbedPane();

        tabbedPane.addTab("New Order", new NewOrderPanel(this));
        tabbedPane.addTab("Order", new CurrentOrdersPanel(this));


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
        DataBaseCustomer.saveOrder(order);
        orders = DataBaseCustomer.loadOrders();
        refreshPanels();
    }



    public void updateMenuItems() {
        DataBaseCustomer.saveMenuItems(menuItems);
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
        System.out.println("hello");
        LoginDialogCustomer loginDialog = new LoginDialogCustomer(this);
        loginDialog.setVisible(true);
        user = loginDialog.getUser();
        isLoggedIn = loginDialog.isSucceeded();
        return isLoggedIn;
    }

    private void showRegisterDialog() {
        RegisterDialogCustomer registerDialog = new RegisterDialogCustomer(this);
        registerDialog.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("hello error");
        }

        SwingUtilities.invokeLater(() -> {
            restaurant.customerGUI.CustomerMain frame = new restaurant.customerGUI.CustomerMain ();
            frame.setVisible(true);
        });
    }
}
