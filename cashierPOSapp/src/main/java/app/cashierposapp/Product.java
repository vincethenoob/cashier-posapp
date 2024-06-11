package app.cashierposapp;

import java.time.LocalDate;

public class Product {

    private LocalDate date;
    private String itemName;
    private int quantity;
    private double retailPrice;

    public Product(LocalDate date, String itemName, int quantity, double retailPrice) {
        this.date = date;
        this.itemName = itemName;
        this.quantity = quantity;
        this.retailPrice = retailPrice;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantity() { return quantity;}

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(double retailPrice) {
        this.retailPrice = retailPrice;
    }
}
