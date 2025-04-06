package com.library.frontend;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.library.backend.UserService;
import com.library.backend.UserRepository;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) {
        TextField nameField = new TextField();
        Button greetButton = new Button("Enter");
        Label messageLabel = new Label();

        // Existing logic for greeting using the backend service
        UserService userService = new UserService();
        greetButton.setOnAction(e -> {
            String enteredName = nameField.getText();
            messageLabel.setText(userService.greetUser(enteredName));
        });

        // New components to load and display data from the database
        Button loadDbButton = new Button("Load DB Info");
        Label dbLabel = new Label();

        loadDbButton.setOnAction(e -> {
            UserRepository repository = new UserRepository();
            String dbName = repository.getFirstUserName();
            if (dbName != null) {
                dbLabel.setText("DB user: " + dbName);
            } else {
                dbLabel.setText("No user found in DB.");
            }
        });

        VBox root = new VBox(10, nameField, greetButton, messageLabel, loadDbButton, dbLabel);
        stage.setScene(new Scene(root, 300, 250));
        stage.setTitle("Simple JavaFX Greeting");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}