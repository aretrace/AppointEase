package com.appointease.app.controller;

import com.appointease.app.model.*;
import com.appointease.app.utils.BindingHelpers;
import com.appointease.app.utils.DialogHelpers;
import com.appointease.app.utils.NavigationHelpers;
import com.appointease.app.utils.TableHelpers;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ResourceBundle;


/**
 * Controller for the Customer screen.
 */
public class CustomerController implements Initializable, NavigationHelpers, BindingHelpers, TableHelpers {

    @FXML
    private TableView<Customer> table;
    @FXML
    private Button deleteButton;
    @FXML
    private Button clearSelectionButton;
    @FXML
    private Pane fromPane;
    @FXML
    private TextField customerIdField;
    @FXML
    private TextField customerNameTextField;
    @FXML
    private TextField addressTextField;
    @FXML
    private TextField postalCodeTextField;
    @FXML
    private TextField phoneTextField;
    @FXML
    private ComboBox<Country> countryComboBox;
    @FXML
    private ComboBox<FirstLevelDivision> firstLevelDivisionComboBox;
    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    private final User thisUser = User.selectIdDB(LoginController.currentUser.userId());
    protected static Customer selectedCustomer;

    /**
     * Gets the selected customer from the table.
     */
    private void getSelectedCustomer() {
        CustomerController.selectedCustomer = table.getSelectionModel().getSelectedItem();
    }

    /**
     * Navigation pseudo implementation (see NavigationHelpers interface).
     *
     * @param event An event.
     * @throws IOException An exception.
     */
    public void toAppointmentScreenHandler(ActionEvent event) throws IOException {
        NavigationHelpers.super.toAppointmentScreenHandler(event);
    }

    /**
     * Clears the selection in the table.
     */
    private void clearForm() {
        customerIdField.clear();
        customerNameTextField.clear();
        addressTextField.clear();
        postalCodeTextField.clear();
        phoneTextField.clear();
        countryComboBox.getSelectionModel().clearSelection();
        firstLevelDivisionComboBox.getSelectionModel().clearSelection();
    }

    /**
     * Filters the first level division combo box items based on the selected country.
     *
     * Using a lambda expression for filtering allows for a functional programming style,
     * making it easier to express complex operations on collections in a compact and readable manner.
     *
     * @return The filtered list of first level divisions.
     */
    private FilteredList<FirstLevelDivision> filteredFirstLevelDivisionComboBoxItems() {
        return FirstLevelDivision.selectAllDB()
                .filtered(firstLevelDivision -> firstLevelDivision.country()
                        .countryId() == countryComboBox.getSelectionModel().getSelectedItem().countryId());
    }

    /**
     * Sets up the combo boxes.
     *
     * By using a lambda expression to set cell factory's,
     * the code becomes more concise and easier to read compared to using an anonymous inner class or a separate method.
     */
    private void setUpComboBoxes() {
        countryComboBox.setItems(Country.selectAllDB());
        class countryCell extends ListCell<Country> {
            @Override
            protected void updateItem(Country country, boolean empty) {
                super.updateItem(country, empty);
                if (empty || country == null) {
                    setText(null);
                } else {
                    setText(country.countryName());
                }
            }
        }
        countryComboBox.setCellFactory(listView -> new countryCell());
        countryComboBox.setButtonCell(new countryCell());
        firstLevelDivisionComboBox.setItems(FirstLevelDivision.selectAllDB());
        class firstLevelDivisionCell extends ListCell<FirstLevelDivision> {
            @Override
            protected void updateItem(FirstLevelDivision firstLevelDivision, boolean empty) {
                super.updateItem(firstLevelDivision, empty);
                if (empty || firstLevelDivision == null) {
                    setText(null);
                } else {
                    setText(firstLevelDivision.divisionName());
                }
            }
        }
        firstLevelDivisionComboBox.setCellFactory(listView -> new firstLevelDivisionCell());
        firstLevelDivisionComboBox.setButtonCell(new firstLevelDivisionCell());
        countryComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, prevSelectedCountry, currSelectedCountry) -> {
            if (currSelectedCountry != null) {
                firstLevelDivisionComboBox.setItems(
                    filteredFirstLevelDivisionComboBoxItems()
                );
            }
        });
    }

    /**
     * Sets up the table and form.
     *
     * The lambda expression also allows for capturing the values of variables in the enclosing scope,
     * making it easier to access necessary data in the listener function.
     */
    private void setUpTableAndForm() {
        tabularFabricator(table, Customer.selectAllDB(), TableView.UNCONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().selectedItemProperty().addListener((observableValue, prevSelectedCustomer, currSelectedCustomer) -> {
            if (currSelectedCustomer != null) {
                customerIdField.setText(String.valueOf(currSelectedCustomer.customerId()));
                customerNameTextField.setText(currSelectedCustomer.customerName());
                addressTextField.setText(currSelectedCustomer.address());
                postalCodeTextField.setText(currSelectedCustomer.postalCode());
                phoneTextField.setText(currSelectedCustomer.phone());
                countryComboBox.getSelectionModel().select(currSelectedCustomer.division().country());
                firstLevelDivisionComboBox.setItems(
                    filteredFirstLevelDivisionComboBoxItems()
                );
                firstLevelDivisionComboBox.getSelectionModel().select(currSelectedCustomer.division());
            } else {
                clearForm();
            }
        });
        table.setItems(Customer.selectAllDB());
    }

    /**
     * Establishes logical user-flow based bindings for CRUD operations.
     *
     * Using a lambda expression to bind disableProperty and visibleProperty allows for a declarative and reactive programming style,
     * making it easier to manage the relationships between properties in a clear and concise manner.
     */
    private void bindingEnactments() {
        deleteButton.visibleProperty().bind(table.getSelectionModel().selectedItemProperty().isNotNull());
        clearSelectionButton.visibleProperty().bind(table.getSelectionModel().selectedItemProperty().isNotNull());
        addButton.disableProperty().bind(
            vacantTextFieldDependencies(customerNameTextField, addressTextField, postalCodeTextField, phoneTextField)
            .or(
            vacantComboBoxDependencies(countryComboBox, firstLevelDivisionComboBox))
            .or(
            table.getSelectionModel().selectedItemProperty().isNotNull())
        );
        updateButton.disableProperty().bind(
            vacantTextFieldDependencies(customerNameTextField, addressTextField, postalCodeTextField, phoneTextField)
            .or(
            vacantComboBoxDependencies(countryComboBox, firstLevelDivisionComboBox))
            .or(
            table.getSelectionModel().selectedItemProperty().isNull())
        );
        firstLevelDivisionComboBox.disableProperty().bind(countryComboBox.getSelectionModel().selectedItemProperty().isNull());
    }

    /**
     * Initializes the controller class.
     *
     * @param url A URL.
     * @param resourceBundle A resource bundle.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUpTableAndForm();
        setUpComboBoxes();
        bindingEnactments();
    }

    /**
     * Clears the selection.
     */
    @FXML
    private void clearSelectionButtonHandler() {
        table.getSelectionModel().clearSelection();
    }

    /**
     * Adds a customer.
     */
    @FXML
    private void addButtonHandler(ActionEvent event) {
        Customer addedCustomer = Customer.insertDB(
            customerNameTextField.getText().strip(),
            addressTextField.getText().strip(),
            postalCodeTextField.getText().strip(),
            phoneTextField.getText().strip(),
            new Timestamp(System.currentTimeMillis()),
            thisUser.userName(),
            firstLevelDivisionComboBox.getSelectionModel().getSelectedItem().divisionId()
        );
        if (addedCustomer != null) {
            table.getItems().add(addedCustomer);
            clearForm();
            table.getSelectionModel().clearSelection();
        } else {
            DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null,
                    "Unable to add customer.");
        }
    }

    /**
     * Updates a customer.
     */
    @FXML
    private void updateButtonHandler() {
        Customer updatedCustomer = Customer.updateDB(
            table.getSelectionModel().getSelectedItem().customerId(),
            customerNameTextField.getText().strip(),
            addressTextField.getText().strip(),
            postalCodeTextField.getText().strip(),
            phoneTextField.getText().strip(),
            new Timestamp(System.currentTimeMillis()),
            thisUser.userName(),
            firstLevelDivisionComboBox.getSelectionModel().getSelectedItem().divisionId()
        );
        if (updatedCustomer != null) {
            table.getItems().set(table.getSelectionModel().getSelectedIndex(), updatedCustomer);
            clearForm();
            table.getSelectionModel().clearSelection();
        } else {
            DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null,
                    "Unable to update customer.");
        }
    }

    /**
     * Deletes the selected customer.
     */
    @FXML
    private void deleteButtonHandler() {
        getSelectedCustomer();
        var result = DialogHelpers.displayConfirmationDialog(null, null,
                "Are you sure you want to delete this customer?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            for (Appointment appointment : Appointment.selectAllDB()) {
                if (appointment.customer().customerId() == CustomerController.selectedCustomer.customerId()) {
                    boolean isAppointmentDeleted = Appointment.deleteDB(appointment.appointmentId());
                    if (!isAppointmentDeleted) {
                        DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null,
                                "Unable to delete associated appointment(s).");
                        return;
                    }
                }
            }
            boolean isCustomerDeleted = Customer.deleteDB(CustomerController.selectedCustomer.customerId());
            if (isCustomerDeleted) {
                String id = String.valueOf(CustomerController.selectedCustomer.customerId());
                DialogHelpers.displayDialog(Alert.AlertType.INFORMATION, null, null,
                        "Customer #" + id + " has been deleted successfully.");
                table.getItems().remove(CustomerController.selectedCustomer);
                table.getSelectionModel().clearSelection();
            } else {
                DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null,
                        "Unable to delete customer.");

                table.setItems(Customer.selectAllDB());
            }
        }
    }
}

