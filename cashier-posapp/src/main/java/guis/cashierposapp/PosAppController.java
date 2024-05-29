package guis.cashierposapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PosAppController {

    @FXML
    private Label welcomeText;

    @FXML
    private ComboBox<String> productComboBox;

    @FXML
    private TextField quantityTextField;

    @FXML
    private ListView<String> cartListView;

    private List<Product> products;
    private double totalPurchase;
    private DecimalFormat decimalFormat;

    public void initialize() {
    
        products = new ArrayList<>();
        products.add(new Product("1", "T-Shirt", 10, 15.0));
        products.add(new Product("2", "Pants", 8, 25.0));
        products.add(new Product("3", "Shoes", 5, 50.0));

        decimalFormat = new DecimalFormat("0.00");

        welcomeText.setText("Welcome!");

        for (Product product : products) {
            productComboBox.getItems().add(product.getName());
        }

        totalPurchase = 0.0;
    }

    @FXML
    void addToCart(ActionEvent event) {
        int selectedIndex = productComboBox.getSelectionModel().getSelectedIndex();
        Product selectedProduct = products.get(selectedIndex);
        int quantity = Integer.parseInt(quantityTextField.getText());

        if (quantity <= selectedProduct.getQuantityStock()) {
            selectedProduct.purchase(quantity);
            totalPurchase += selectedProduct.getPrice() * quantity;

            String cartItem = "Product ID: " + selectedProduct.getId() + ", Quantity: " + quantity + ", Quantity Left: " +
                    selectedProduct.getQuantityStock();
            cartListView.getItems().add(cartItem);

            quantityTextField.clear();
        } else {
            showAlert("Insufficient stock for the selected product.");
        }
    }

    @FXML
    void editQuantity(ActionEvent event) {
        int selectedIndex = cartListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            String selectedItem = cartListView.getSelectionModel().getSelectedItem();
            String[] parts = selectedItem.split(",");
            String productId = parts[0].split(": ")[1].trim();

            for (Product product : products) {
                if (product.getId().equals(productId)) {
                    int originalQuantity = Integer.parseInt(parts[1].split(": ")[1].trim());
                    int newQuantity = Integer.parseInt(showInputDialog("Enter new quantity:"));

                    if (newQuantity <= product.getQuantityStock() + originalQuantity) {
                        totalPurchase -= product.getPrice() * originalQuantity;
                        totalPurchase += product.getPrice() * newQuantity;

                        product.setQuantityStock(product.getQuantityStock() + (originalQuantity - newQuantity));
                        cartListView.getItems().set(selectedIndex, "Product ID: " + product.getId() + ", Quantity: " + newQuantity + ", Quantity Left: " +
                                product.getQuantityStock());
                        return;
                    } else {
                        showAlert("Insufficient stock for the selected product.");
                        return;
                    }
                }
            }
        }
    }

    @FXML
    void deleteFromCart(ActionEvent event) {
        int selectedIndex = cartListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            String selectedItem = cartListView.getSelectionModel().getSelectedItem();
            String[] parts = selectedItem.split(",");
            String productId = parts[0].split(": ")[1].trim();

            for (Product product : products) {
                if (product.getId().equals(productId)) {
                    int originalQuantity = Integer.parseInt(parts[1].split(": ")[1].trim());

                    totalPurchase -= product.getPrice() * originalQuantity;
                    product.setQuantityStock(product.getQuantityStock() + originalQuantity);
                    cartListView.getItems().remove(selectedIndex);
                    return;
                }
            }
        }
    }

    @FXML
    void checkOut(ActionEvent event) {
        String cashStr = showInputDialog("Total Purchase: ₱" + totalPurchase + "\nEnter cash amount:");
        double cash = Double.parseDouble(cashStr);

        double change = cash - totalPurchase;
        String receiptMessage = "Total Purchase: ₱" + totalPurchase + "\nCash: ₱" + cash + "\nChange: ₱" + change;

        showAlert(receiptMessage);
        generateReceipt();
        savePurchaseDetails();
        resetCart();
    }

    private void generateReceipt() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String receiptFileName = "receipt_" + dateFormat.format(new Date()) + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(receiptFileName))) {
            writer.write("Receipt for Purchase\n");
            writer.write("====================\n\n");
            writer.write("Date and Time: " + new Date() + "\n\n");
            writer.write("Items Purchased:\n");
            for (String item : cartListView.getItems()) {
                writer.write(item + "\n");
            }
            writer.write("Total Purchase: ₱" + totalPurchase + "\n");

            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void savePurchaseDetails() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String csvFileName = "purchase_details_" + dateFormat.format(new Date()) + ".csv";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFileName))) {
            writer.write("Product ID,Product Name,Quantity Purchased,Price per Piece,Total Amount\n");

            for (Product product : products) {
                String line = product.getId() + "," + product.getName() + "," + product.getQuantitySold() + "," + product.getPrice() + ","
                        + (product.getPrice() * product.getQuantitySold());
                writer.write(line + "\n");
            }

            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resetCart() {
        cartListView.getItems().clear();
        totalPurchase = 0.0;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String showInputDialog(String message) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input Dialog");
        dialog.setHeaderText(null);
        dialog.setContentText(message);
        return dialog.showAndWait().orElse("");
    }
}
