package guis.cashierposapp;

public class Product {
    private String id;
    private String name;
    private int quantityStock;
    private double price;
    private int quantitySold;

    public Product(String id, String name, int quantityStock, double price) {
        this.id = id;
        this.name = name;
        this.quantityStock = quantityStock;
        this.price = price;
        this.quantitySold = 0;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantityStock() {
        return quantityStock;
    }

    public void setQuantityStock(int quantityStock) {
        this.quantityStock = quantityStock;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public void purchase(int quantity) {
        if (quantity <= quantityStock) {
            quantitySold += quantity;
            quantityStock -= quantity;
        } else {
            System.out.println("Insufficient stock for the selected product.");
        }
    }
}
