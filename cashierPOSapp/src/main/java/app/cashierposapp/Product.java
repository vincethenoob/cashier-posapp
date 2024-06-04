package app.cashierposapp;

import java.time.LocalDate;

public class Product {

    private final String name;
    private final double price;
    private final int quantity;
    private final LocalDate date;

    public Product(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.date = LocalDate.now();
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDate getDate() {
        return date;
    }
}
