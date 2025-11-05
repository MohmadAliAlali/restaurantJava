package restaurant.model;

import javax.swing.*;
import java.awt.*;
import java.io.File;


public class MenuItem {
    private String name;
    private String description;
    private double price;
    private String category;
    private String imagePath;
    private ImageIcon image;

    public MenuItem(String name, String description, double price, String category, String imagePath) {
        this.name = name;
        this.description = description;
        setPrice(price);
        this.category = category;
        this.imagePath = imagePath;
        loadImage();
    }

    private void loadImage() {
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                ImageIcon originalIcon = new ImageIcon(imagePath);
                Image scaledImage = originalIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                this.image = new ImageIcon(scaledImage);
            } catch (Exception e) {
                throw new RuntimeException("Error loading image: " + e.getMessage());
            }
        }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.price = price;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public ImageIcon getImage() { return image; }

    @Override
    public String toString() {
        return name + " - " + price + " S.P";
    }
}