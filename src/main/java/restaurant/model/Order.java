package restaurant.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Order implements Serializable {
    private String orderId;
    private String customerName;
    private String orderUserType;
    private String orderUserName;
    private String customerPhone;
    private String orderType;
    private Map<MenuItem, Integer> items;
    private double totalAmount;
    private double tip;
    private String status;
    private LocalDateTime orderTime;
    private static int orderCounter = 1;

    public Order(String customerName,String customerPhone) {
        this.orderId = "ORD-" + orderCounter++;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.orderUserType ="ADMIN";
        this.orderUserName = "";
        this.orderType = "in restaurant order";
        this.items = new HashMap<>();
        this.totalAmount = 0.0;
        this.tip = 0.0;
        this.status = "PENDING";
        this.orderTime = LocalDateTime.now();
    }

    public void addItem(MenuItem item, int quantity) {
        if (item != null && quantity > 0) {
            int currentQuantity = items.getOrDefault(item, 0);
            items.put(item, currentQuantity + quantity);
            calculateTotal();
        }
    }


    private void calculateTotal() {
        totalAmount = 0;
        for (Map.Entry<MenuItem, Integer> entry : items.entrySet()) {
            totalAmount += entry.getKey().getPrice() * entry.getValue();
        }
    }
    public void setTotalAmount(double totalAmount) {
        if (tip >= 0) {
            this.totalAmount = totalAmount;
        } else {
            throw new IllegalArgumentException("Tip cannot be negative");
        }
    }
    public String getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setID(String ID) { this.orderId = ID; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) {
        if (customerPhone.matches("[0-9]{10}")) {
            this.customerPhone = customerPhone;
        } else {
            throw new IllegalArgumentException("Invalid phone number");
        }
    }
    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }
    public Map<MenuItem, Integer> getItems() { return items; }
    public double getTotalAmount() { return totalAmount; }
    public double getTip() { return tip; }
    public void setTip(double tip) {
        if (tip >= 0) {
            this.tip = tip;
        } else {
            throw new IllegalArgumentException("Tip cannot be negative");
        }
    }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getOrderTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return orderTime.format(formatter);
    }
    public void setUserName(String status) { this.customerName = status; }
    public String getUserName() {
        return customerName;
    }
    public String getOrderUserName() {
        return orderUserName;
    }
    public void setOrderUserName(String s) {
        this.orderUserName = s;
    }
    public void setUserType(String status) { this.orderUserType = status; }
    public String getUserType() {
        return orderUserType;
    }
}
