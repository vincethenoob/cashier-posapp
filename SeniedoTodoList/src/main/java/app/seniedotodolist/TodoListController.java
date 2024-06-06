package app.seniedotodolist;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TodoListController {

    @FXML
    private ListView<String> todoListView;

    @FXML
    private TextField todoTextField;

    @FXML
    private Button addButton;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    private ObservableList<String> todoList;

    @FXML
    public void initialize() {
        todoList = FXCollections.observableArrayList();
        todoListView.setItems(todoList);
    }

    @FXML
    private void handleAdd() {
        String newTask = todoTextField.getText();
        if (!newTask.isEmpty()) {
            todoList.add(newTask);
            todoTextField.clear();
        } else {
            showAlert("Input Error", "Task cannot be empty.");
        }
    }

    @FXML
    private void handleEdit() {
        String selectedTask = todoListView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            int selectedIndex = todoListView.getSelectionModel().getSelectedIndex();
            String newTask = todoTextField.getText();
            if (!newTask.isEmpty()) {
                todoList.set(selectedIndex, newTask);
                todoTextField.clear();
            } else {
                showAlert("Input Error", "Task cannot be empty.");
            }
        } else {
            showAlert("Selection Error", "No task selected.");
        }
    }

    @FXML
    private void handleDelete() {
        String selectedTask = todoListView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            todoList.remove(selectedTask);
        } else {
            showAlert("Selection Error", "No task selected.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
