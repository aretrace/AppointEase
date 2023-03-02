package com.appointease.app.controller;

import com.appointease.app.model.User;
import com.appointease.app.utils.BindingHelpers;
import com.appointease.app.utils.DialogHelpers;
import com.appointease.app.utils.I18nService;
import com.appointease.app.utils.NavigationHelpers;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.time.ZoneId;
import java.util.ResourceBundle;


/**
 * Controller for the login screen.
 */
public class LoginController implements Initializable, NavigationHelpers, BindingHelpers {
    public Button toTestViewLabel;
    @FXML
    private Label heroTitle;
    @FXML
    private Label timeZoneLabel;
    @FXML
    private Label userTimeZone;
    @FXML
    private Label usernameLabel;
    @FXML
    private TextField usernameField;
    @FXML
    private Label passwordLabel;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button enterButton;
    protected static User currentUser;

    /**
     * Gets localization based on the user's system language.
     */
    ResourceBundle rb = I18nService.getL10n("LoginScreen");

    /**
     * Navigation pseudo implementation (see NavigationHelpers interface).
     *
     * @param event An event.
     * @throws IOException An exception.
     */
    @Override
    @FXML
    public void toAppointmentScreenHandler(ActionEvent event) throws IOException {
        NavigationHelpers.super.toAppointmentScreenHandler(event);
    }

    /**
     * Sets up the login screen.
     */
    private void setUpScene() {
        timeZoneLabel.setText(rb.getString("timeZoneLabel"));
        userTimeZone.setText(ZoneId.systemDefault().toString());
        heroTitle.setText(rb.getString("heroTitle"));
        usernameLabel.setText(rb.getString("usernameLabel"));
        usernameField.setPromptText("admin");
        passwordLabel.setText(rb.getString("passwordLabel"));
        passwordField.setPromptText("admin");
        enterButton.setText(rb.getString("enterButton"));
//        enterButton.disableProperty().bind(
//            vacantTextFieldDependencies(usernameField, passwordField)
//        );
    }

    /**
     * Initializes the controller class.
     *
     * @param url A URL.
     * @param resourceBundle A resource bundle.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUpScene();
    }

    /**
     * Handles the enter button click event for the login form.
     *
     * @param event An action event.
     */
    @FXML
    public void enterButtonHandler(ActionEvent event) throws IOException {
        if (usernameField.getText().strip().isEmpty() || passwordField.getText().strip().isEmpty()) {
            return;
        }
        File file = new File("login_activity.txt");
        FileWriter fileWriter = new FileWriter(file, true);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.print("Attempted login by " + usernameField.getText() + " on " + java.time.LocalDate.now()
                + " at " + java.time.LocalTime.now() + " in timezone " + ZoneId.systemDefault());
        ObservableList<User> ul = User.selectAllDB();
        for (User u : ul) {
            if (u.userName().equals(usernameField.getText().strip()) && u.password().equals(passwordField.getText().strip())) {
                LoginController.currentUser = u;
                printWriter.print(" was successful.");
                printWriter.println(" timestamp: " + java.time.Instant.now() + " UTC");
                printWriter.close();
                fileWriter.close();
                toAppointmentScreenHandler(event);
                return;
            }
        }
        printWriter.print(" was unsuccessful.");
        printWriter.println(" timestamp: " + java.time.Instant.now() + " UTC");
        printWriter.close();
        fileWriter.close();
        DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null, rb.getString("loginFailure"));
    }
}
