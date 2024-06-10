package app.cashierposapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;

import java.io.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class AppController {

    @FXML
    private DatePicker SalesHistoryDate, SetCartpurchaseDate, fromDate, toDate;
    @FXML
    private ListView<String> cartListView, SaleHistoryList, productListView;
    @FXML
    private ComboBox<String> ItemsComboBox;
    @FXML
    private TextField temporarytotal, Taxtotal, overalltotal, Indooritemsquantity;
    @FXML
    private PieChart productPieChart;
    @FXML
    private Button addButton, editButton, deleteButton, checkoutButton, resetCart, Showsalehistory, showButton;

    private final ObservableList<String> cartItems = FXCollections.observableArrayList();
    private final ObservableList<String> salesHistory = FXCollections.observableArrayList();
    private final ObservableList<String> availableItems = FXCollections.observableArrayList("Item1", "Item2", "Item3");
    private final Map<String, Integer> itemPrices = new HashMap<>();
    private final double taxRate = 0.10;

    public void initialize() {
        ItemsComboBox.setItems(availableItems);
        cartListView.setItems(cartItems);
        SaleHistoryList.setItems(salesHistory);
        productListView.setItems(FXCollections.observableArrayList());

        itemPrices.put("Item1", 10);
        itemPrices.put("Item2", 20);
        itemPrices.put("Item3", 30);

        // Make ItemsComboBox searchable
        makeComboBoxSearchable(ItemsComboBox);

        // Custom cell factory for productListView
        productListView.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                }
            }
        });
    }

    private void makeComboBoxSearchable(ComboBox<String> comboBox) {
        comboBox.setEditable(true);
        comboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            ObservableList<String> filteredItems = FXCollections.observableArrayList();
            for (String item : availableItems) {
                if (item.toLowerCase().contains(newValue.toLowerCase())) {
                    filteredItems.add(item);
                }
            }
            comboBox.setItems(filteredItems);
            comboBox.show();
        });
        comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            comboBox.getEditor().setText(newValue);
        });
    }

    @FXML
    private void addToCart() {
        if (SetCartpurchaseDate.getValue() == null) {
            showAlert("Set purchase date before adding items.");
            return;
        }

        String item = ItemsComboBox.getValue();
        String quantityStr = Indooritemsquantity.getText();
        if (item != null && !quantityStr.isEmpty()) {
            try {
                int quantity = Integer.parseInt(quantityStr);
                cartItems.add(item + " x " + quantity);
                updateTotals();
                // Reset the search function of the ComboBox
                ItemsComboBox.getEditor().clear();
                ItemsComboBox.setItems(availableItems);
            } catch (NumberFormatException e) {
                showAlert("Invalid quantity. Please enter a number.");
            }
        } else {
            showAlert("Select an item and enter quantity.");
        }
    }

    @FXML
    private void editQuantity() {
        if (SetCartpurchaseDate.getValue() == null) {
            showAlert("Set purchase date before editing items.");
            return;
        }

        String selectedItem = cartListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            String newQuantityStr = Indooritemsquantity.getText();
            try {
                int newQuantity = Integer.parseInt(newQuantityStr);
                cartItems.set(cartListView.getSelectionModel().getSelectedIndex(), selectedItem.split(" x ")[0] + " x " + newQuantity);
                updateTotals();
            } catch (NumberFormatException e) {
                showAlert("Invalid quantity. Please enter a number.");
            }
        } else {
            showAlert("Select an item to edit.");
        }
    }

    @FXML
    private void deleteFromCart() {
        if (SetCartpurchaseDate.getValue() == null) {
            showAlert("Set purchase date before deleting items.");
            return;
        }

        String selectedItem = cartListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            cartItems.remove(selectedItem);
            updateTotals();
        } else {
            showAlert("Select an item to delete.");
        }
    }

    @FXML
    private void resetCart() {
        cartItems.clear();
        temporarytotal.clear();
        Taxtotal.clear();
        overalltotal.clear();
    }

    @FXML
    private void checkOut() {
        if (cartItems.isEmpty()) {
            showAlert("Cart is empty.");
            return;
        }

        double total = Double.parseDouble(overalltotal.getText());
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Checkout");
        dialog.setHeaderText("Total: $" + total);
        dialog.setContentText("Enter amount received:");

        dialog.showAndWait().ifPresent(amountStr -> {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount >= total) {
                    double change = amount - total;
                    showAlert("Change: $" + change);
                    generateReceipt();
                    saveSalesToCSV();
                    resetCart();
                } else {
                    showAlert("Insufficient amount. Please enter a valid amount.");
                }
            } catch (NumberFormatException e) {
                showAlert("Invalid amount. Please enter a number.");
            }
        });
    }

    @FXML
    private void showSaleHistory() {
        salesHistory.clear();
        LocalDate date = SalesHistoryDate.getValue();
        if (date != null) {
            File file = new File("sales_history.csv");
            if (file.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (LocalDate.parse(parts[0]).isEqual(date)) {
                            salesHistory.add(parts[1] + " x " + parts[2] + " - $" + parts[3]);
                        }
                    }
                } catch (IOException e) {
                    showAlert("Error reading sales history.");
                }
            } else {
                showAlert("No sales history found.");
            }
        } else {
            showAlert("Select a date to view sales history.");
        }
    }

    @FXML
    private void showStatistics() {
        productListView.getItems().clear();
        productPieChart.getData().clear();

        LocalDate from = fromDate.getValue();
        LocalDate to = toDate.getValue();

        if (from != null && to != null) {
            Map<String, Integer> productSales = new HashMap<>();
            File file = new File("sales_history.csv");

            if (file.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] parts = line.split(",");
                        LocalDate date = LocalDate.parse(parts[0]);
                        if (!date.isBefore(from) && !date.isAfter(to)) {
                            String item = parts[1];
                            int quantity = Integer.parseInt(parts[2]);
                            int price = Integer.parseInt(parts[3]);
                            int totalPrice = Integer.parseInt(parts[4]); // Parse total price
                            productSales.put(item, productSales.getOrDefault(item, 0) + quantity);

                            String row = String.format("Date: %s | Item: %s | Quantity: %d | Retail Price: %d | Total: %d",
                                    parts[0], item, quantity, price, totalPrice);
                            productListView.getItems().add(row);
                        }
                    }
                } catch (IOException e) {
                    showAlert("Error reading sales history.");
                }

                for (Map.Entry<String, Integer> entry : productSales.entrySet()) {
                    productPieChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
                }
            } else {
                showAlert("No sales history found.");
            }
        } else {
            showAlert("Select a valid date range.");
        }
    }

    private void updateTotals() {
        double subtotal = 0;
        for (String item : cartItems) {
            String[] parts = item.split(" x ");
            String itemName = parts[0];
            int quantity = Integer.parseInt(parts[1]);
            subtotal += itemPrices.get(itemName) * quantity;
        }

        double tax = subtotal * taxRate;
        double total = subtotal + tax;

        temporarytotal.setText(String.valueOf(subtotal));
        Taxtotal.setText(String.valueOf(tax));
        overalltotal.setText(String.valueOf(total));
    }

    private void generateReceipt() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("receipt.txt"))) {
            writer.write("Receipt:\n");
            for (String item : cartItems) {
                writer.write(item + "\n");
            }
            writer.write("\nSubtotal: $" + temporarytotal.getText());
            writer.write("\nTax: $" + Taxtotal.getText());
            writer.write("\nTotal: $" + overalltotal.getText());
        } catch (IOException e) {
            showAlert("Error generating receipt.");
        }
    }

    private void saveSalesToCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("sales_history.csv", true))) {
            for (String item : cartItems) {
                String[] parts = item.split(" x ");
                String itemName = parts[0];
                int quantity = Integer.parseInt(parts[1]);
                int price = itemPrices.get(itemName);
                int totalPrice = quantity * price;
                writer.write(LocalDate.now() + "," + itemName + "," + quantity + "," + price + "," + totalPrice);
                writer.newLine();
            }
        } catch (IOException e) {
            showAlert("Error saving sales history.");
        }
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
