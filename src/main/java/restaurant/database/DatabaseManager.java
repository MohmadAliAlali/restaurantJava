package restaurant.database;

import restaurant.customerGUI.CustomerMain;
import restaurant.gui.MainFrame;
import restaurant.model.MenuItem;
import restaurant.model.User;
import restaurant.model.Order;
import javax.swing.*;
import java.io.*;
import java.util.*;


public class DatabaseManager {
    private static final String DATA_DIRECTORY = "restaurant_data";
    private static final String MENU_FILE = DATA_DIRECTORY + "/menu.txt";
    private static final String ORDERS_FILE = DATA_DIRECTORY + "/orders.txt";
    private static final String USERS_FILE = DATA_DIRECTORY + "/users.txt";
    private static final String IMAGES_DIRECTORY = DATA_DIRECTORY + "/images";

    static {
        new File(DATA_DIRECTORY).mkdirs();
        new File(IMAGES_DIRECTORY).mkdirs();
    }

    public static void saveUser(User newUser) {
        List<User> users = loadUsers();

        for (User user : users) {
            if (user.getUsername().equals(newUser.getUsername())) {
                JOptionPane.showMessageDialog(null,
                        "user already register",
                        "error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        users.add(newUser);

        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE))) {
            for (User user : users) {
                writer.println(String.format("%s|%s|%s",
                        user.getUsername(),
                        user.getPassword(),
                        user.getRole()
                ));
            }
            JOptionPane.showMessageDialog(null,
                    "register success",
                    "message",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            System.err.println("error when user save: " + e.getMessage());
        }
    }



    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        File file = new File(USERS_FILE);

        if (!file.exists()) {
            return users;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 3) {
                    users.add(new User(parts[0], parts[1], parts[2]));
                }
            }
        } catch (IOException e) {
            System.err.println("error when read users: " + e.getMessage());
        }
        return users;
    }

    public static User authenticateUser(String username, String password) {
        List<User> users = loadUsers();
        return users.stream()
                .filter(user -> user.getUsername().equals(username) &&
                        user.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    public static void saveMenuItems(List<MenuItem> items) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(MENU_FILE))) {
            for (MenuItem item : items) {
                writer.println(String.format("%s|%s|%.2f|%s|%s",
                        item.getName(),
                        item.getDescription(),
                        item.getPrice(),
                        item.getCategory(),
                        item.getImagePath() != null ? item.getImagePath() : ""
                ));
            }
        } catch (IOException e) {
            System.err.println("error on menu save: " + e.getMessage());
        }
    }

    public static List<MenuItem> loadMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        File file = new File(MENU_FILE);

        if (!file.exists()) {
            return items;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 4) {
                    String imagePath = parts.length > 4 ? parts[4] : "";
                    items.add(new MenuItem(
                            parts[0],
                            parts[1],
                            Double.parseDouble(parts[2]),
                            parts[3],
                            imagePath
                    ));
                }
            }
        } catch (IOException e) {
            System.err.println("error in read menu data: " + e.getMessage());
        }
        return items;
    }

    public static void saveOrder(Order order) {
        List<Order> orders = loadOrders();
        orders.add(order);
        saveOrders(orders);
    }

    public static List<Order> loadOrders() {
        List<Order> orders = new ArrayList<>();
        File file = new File(ORDERS_FILE);

        if (!file.exists()) {
            return orders;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            Order currentOrder = null;
            Map<MenuItem, Integer> currentItems = new HashMap<>();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                switch (parts[0]) {
                    case "ORDER":
                        if (currentOrder != null) {
                            for (Map.Entry<MenuItem, Integer> entry : currentItems.entrySet()) {
                                currentOrder.addItem(entry.getKey(), entry.getValue());

                            }
                            orders.add(currentOrder);
                        }

                        currentOrder = new Order(parts[2], parts[5]);
                        currentOrder.setID(parts[1]);
                        currentOrder.setOrderUserName(parts[4]);
                        currentOrder.setUserType(parts[3]);
                        currentOrder.setCustomerPhone(parts[5]);
                        currentOrder.setOrderType(parts[6]);
                        currentOrder.setTotalAmount(Double.parseDouble(parts[7]));
                        currentOrder.setTip(Double.parseDouble(parts[8]));
                        currentOrder.setStatus(parts[9]);
                        currentItems = new HashMap<>();
                        break;

                    case "ITEM":
                        if (currentOrder != null && parts.length >= 5) {
                            String imagePath = parts.length > 5 ? parts[5] : "";
                            MenuItem item = new MenuItem(
                                    parts[1],
                                    parts[2],
                                    Double.parseDouble(parts[3]),
                                    parts[4],
                                    imagePath
                            );
                            currentItems.put(item, currentItems.getOrDefault(item, 0) + 1);
                        }
                        break;

                    case "END_ORDER":
                        if (currentOrder != null) {
                            for (Map.Entry<MenuItem, Integer> entry : currentItems.entrySet()) {
                                currentOrder.addItem(entry.getKey(), entry.getValue());
                            }
                            orders.add(currentOrder);
                            currentOrder = null;
                            currentItems = new HashMap<>();
                        }
                        break;

                    default:
                        System.err.println("Unknown line type: " + parts[0]);
                        break;
                }
            }

            if (currentOrder != null) {
                for (Map.Entry<MenuItem, Integer> entry : currentItems.entrySet()) {
                    currentOrder.addItem(entry.getKey(), entry.getValue());
                }
                orders.add(currentOrder);
            }
        } catch (IOException e) {
            System.err.println("error in read order: " + e.getMessage());
        }
        return orders;
    }
    public static void saveOrderMain(Order newOrder) {
        File file = new File(ORDERS_FILE);
        int lineCount = 0;
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                while (reader.readLine() != null) {
                    lineCount++;
                }
            } catch (IOException e) {
                System.err.println("Error reading file for line count: " + e.getMessage());
            }
        }

        String orderID = "ORD-" + (lineCount);
        newOrder.setID(orderID);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write("ORDER|" + newOrder.getOrderId() + "|" + MainFrame.user + "|ADMIN|" +
                    newOrder.getCustomerName() + "|" + newOrder.getCustomerPhone() + "|" +
                    newOrder.getOrderType() + "|" + newOrder.getTotalAmount()+ "|" + newOrder.getTip() + "|" + newOrder.getStatus());
            writer.newLine();

            for (MenuItem item : newOrder.getItems().keySet()) {
                writer.write("ITEM|" + item.getName() + "|" + item.getDescription() + "|" +
                        item.getPrice() + "|" + item.getCategory() + "|" + item.getImagePath());
                writer.newLine();
            }

            writer.write("END_ORDER");
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error saving order: " + e.getMessage());
        }
    }

    public static void saveOrders(List<Order> orders) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ORDERS_FILE))) {
            for (Order order : orders) {
                writer.println(String.format("ORDER|%s|%s|%s|%s|%s|%s|%.2f|%.2f|%s|%s",
                        order.getOrderId(),
                        order.getUserName(),
                        order.getUserType(),
                        order.getCustomerName(),
                        order.getCustomerPhone(),
                        order.getOrderType(),
                        order.getTotalAmount(),
                        order.getTip(),
                        order.getStatus(),
                        order.getOrderTime()
                ));

                for (Map.Entry<MenuItem, Integer> entry : order.getItems().entrySet()) {
                    MenuItem item = entry.getKey();
                    writer.println(String.format("ITEM|%s|%s|%.2f|%s|%s",
                            item.getName(),
                            item.getDescription(),
                            item.getPrice(),
                            item.getCategory(),
                            item.getImagePath() != null ? item.getImagePath() : ""
                    ));
                }

                writer.println("END_ORDER");
            }
        } catch (IOException e) {
            System.err.println("error in save order: " + e.getMessage());
        }
    }

    public static Order getOrderById(String orderId) {
        return loadOrders().stream()
                .filter(order -> order.getOrderId().equals(orderId))
                .findFirst()
                .orElse(null);
    }

    public static void updateOrderStatus(String orderId, String newStatus) {
        List<Order> orders = loadOrders();
        orders.stream()
                .filter(order -> order.getOrderId().equals(orderId))
                .findFirst()
                .ifPresent(order -> {
                    order.setStatus(newStatus);
                    saveOrders(orders);
                });
    }

    public static String saveImage(File sourceFile, String itemName) {
        String targetFileName = itemName.replaceAll("[^a-zA-Z0-9]", "_") + "_" +
                System.currentTimeMillis() + ".jpg";
        File targetFile = new File(IMAGES_DIRECTORY, targetFileName);

        try (FileInputStream fis = new FileInputStream(sourceFile);
             FileOutputStream fos = new FileOutputStream(targetFile)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }

            return targetFile.getAbsolutePath();
        } catch (IOException e) {
            System.err.println("error in save photo: " + e.getMessage());
            return null;
        }
    }
}
