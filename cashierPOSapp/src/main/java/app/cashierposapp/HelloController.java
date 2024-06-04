package app.cashierposapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class HelloController {

    @FXML
    private ComboBox<String> ClothingComboBox, outdoorComboBox, FoodComboBox, indoorComboBox;
    @FXML
    private DatePicker SalesHistoryDate, SetCartpurchaseDate, fromDate, toDate;
    @FXML
    private ListView<String> cartListView, SaleHistoryList;
    @FXML
    private TextField temporarytotal, Taxtotal, overalltotal, Indooritemsquantity, outsideitemsquantity, Fooditemsquantity, Clothingitemsquantity;
    @FXML
    private TableView<Product> productTableView;
    @FXML
    private TableColumn<Product, String> itemNameColumn;
    @FXML
    private TableColumn<Product, Integer> quantityColumn;
    @FXML
    private TableColumn<Product, Double> retailPriceColumn;
    @FXML
    private PieChart productPieChart;
    @FXML
    private Button addButton, editButton, deleteButton, checkoutButton, Showsalehistory, showButton, resetCart;

    private final ObservableList<String> cartItems = FXCollections.observableArrayList();
    private final ObservableList<Product> products = FXCollections.observableArrayList(
            new Product("Indoor Item 1", 10.0, 100),
            new Product("Indoor Item 2", 15.0, 80),
            new Product("Outdoor Item 1", 20.0, 50),
            new Product("Outdoor Item 2", 25.0, 60),
            new Product("Food Item 1", 5.0, 200),
            new Product("Food Item 2", 8.0, 150),
            new Product("Clothing Item 1", 30.0, 70),
            new Product("Clothing Item 2", 35.0, 40)
    );

    @FXML
    public void initialize() {
        // Initialize ComboBoxes
        indoorComboBox.setItems(FXCollections.observableArrayList("Indoor Item 1", "Indoor Item 2"));
        outdoorComboBox.setItems(FXCollections.observableArrayList("Outdoor Item 1", "Outdoor Item 2"));
        FoodComboBox.setItems(FXCollections.observableArrayList("Food Item 1", "Food Item 2"));
        ClothingComboBox.setItems(FXCollections.observableArrayList("Clothing Item 1", "Clothing Item 2"));

        // Initialize Cart ListView
        cartListView.setItems(cartItems);

        // Initialize Product Table
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        retailPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        productTableView.setItems(products);
    }

    @FXML
    private void addToCart(ActionEvent event) {
        String item = getSelectedItem();
        if (item != null) {
            int quantity = getQuantity();
            if (quantity > 0) {
                cartItems.add(item + " x" + quantity);
                updateTotals();
            }
        }
    }

    @FXML
    private void editQuantity(ActionEvent event) {
        String selectedItem = cartListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            cartItems.remove(selectedItem);
            addToCart(event);
        }
    }

    @FXML
    private void deleteFromCart(ActionEvent event) {
        String selectedItem = cartListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            cartItems.remove(selectedItem);
            updateTotals();
        }
    }

    @FXML
    private void checkOut(ActionEvent event) {
        double total = calculateTotal();
        double tax = total * 0.1; // 10% tax
        double overallTotal = total + tax;

        temporarytotal.setText(String.valueOf(total));
        Taxtotal.setText(String.valueOf(tax));
        overalltotal.setText(String.valueOf(overallTotal));

        Stage checkoutStage = new Stage();
        checkoutStage.initModality(Modality.APPLICATION_MODAL);
        checkoutStage.setTitle("Checkout");

        Label totalLabel = new Label("Total: " + overallTotal);
        TextField moneyField = new TextField();
        moneyField.setPromptText("Enter your money amount");
        Button generateReceiptButton = new Button("Generate Receipt");
        Label changeLabel = new Label();

        generateReceiptButton.setOnAction(e -> {
            double moneyAmount = Double.parseDouble(moneyField.getText());
            double change = moneyAmount - overallTotal;
            changeLabel.setText("Change: " + change);

            recordPurchase(overallTotal);
            generateReceipt(overallTotal, moneyAmount, change);
            cartItems.clear();
            updateTotals();
        });

        VBox layout = new VBox(10, totalLabel, moneyField, generateReceiptButton, changeLabel);
        Scene scene = new Scene(layout, 300, 200);
        checkoutStage.setScene(scene);
        checkoutStage.showAndWait();
    }

    @FXML
    private void showStatistics(ActionEvent event) {
        LocalDate from = fromDate.getValue();
        LocalDate to = toDate.getValue();
        if (from != null && to != null && !from.isAfter(to)) {
            List<Product> filteredProducts = products.stream()
                    .filter(product -> product.getDate().isAfter(from.minusDays(1)) && product.getDate().isBefore(to.plusDays(1)))
                    .collect(Collectors.toList());

            productTableView.setItems(FXCollections.observableArrayList(filteredProducts));
            productPieChart.setData(filteredProducts.stream()
                    .map(product -> new PieChart.Data(product.getName(), product.getQuantity()))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList)));
        }
    }

    private String getSelectedItem() {
        if (indoorComboBox.getSelectionModel().getSelectedItem() != null) {
            return indoorComboBox.getSelectionModel().getSelectedItem();
        } else if (outdoorComboBox.getSelectionModel().getSelectedItem() != null) {
            return outdoorComboBox.getSelectionModel().getSelectedItem();
        } else if (FoodComboBox.getSelectionModel().getSelectedItem() != null) {
            return FoodComboBox.getSelectionModel().getSelectedItem();
        } else if (ClothingComboBox.getSelectionModel().getSelectedItem() != null) {
            return ClothingComboBox.getSelectionModel().getSelectedItem();
        }
        return null;
    }

    private int getQuantity() {
        try {
            if (Indooritemsquantity.getText() != null && !Indooritemsquantity.getText().isEmpty()) {
                return Integer.parseInt(Indooritemsquantity.getText());
            } else if (outsideitemsquantity.getText() != null && !outsideitemsquantity.getText().isEmpty()) {
                return Integer.parseInt(outsideitemsquantity.getText());
            } else if (Fooditemsquantity.getText() != null && !Fooditemsquantity.getText().isEmpty()) {
                return Integer.parseInt(Fooditemsquantity.getText());
            } else if (Clothingitemsquantity.getText() != null && !Clothingitemsquantity.getText().isEmpty()) {
                return Integer.parseInt(Clothingitemsquantity.getText());
            }
        } catch (NumberFormatException e) {
            // Handle invalid number format
        }
        return 0;
    }

    private void updateTotals() {
        double total = calculateTotal();
        double tax = total * 0.1; // 10% tax
        double overallTotal = total + tax;

        temporarytotal.setText(String.valueOf(total));
        Taxtotal.setText(String.valueOf(tax));
        overalltotal.setText(String.valueOf(overallTotal));
    }

    private double calculateTotal() {
        double total = 0.0;
        for (String item : cartItems) {
            String[] parts = item.split(" x");
            String itemName = parts[0];
            int quantity = Integer.parseInt(parts[1]);
            total += products.stream()
                    .filter(product -> product.getName().equals(itemName))
                    .mapToDouble(product -> product.getPrice() * quantity)
                    .sum();
        }
        return total;
    }

    private void recordPurchase(double total) {
        LocalDate purchaseDate = SetCartpurchaseDate.getValue();
        if (purchaseDate != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("purchase_history.csv", true))) {
                writer.write(purchaseDate + "," + total + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void generateReceipt(double total, double moneyAmount, double change) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("receipt.txt"))) {
            writer.write("Receipt\n");
            writer.write("--------------\n");
            for (String item : cartItems) {
                writer.write(item + "\n");
            }
            writer.write("--------------\n");
            writer.write("Total: " + total + "\n");
            writer.write("Paid: " + moneyAmount + "\n");
            writer.write("Change: " + change + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
