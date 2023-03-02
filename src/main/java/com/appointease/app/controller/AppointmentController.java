package com.appointease.app.controller;

import com.appointease.app.model.Appointment;
import com.appointease.app.model.Contact;
import com.appointease.app.model.Customer;
import com.appointease.app.model.User;
import com.appointease.app.utils.BindingHelpers;
import com.appointease.app.utils.DialogHelpers;
import com.appointease.app.utils.NavigationHelpers;
import com.appointease.app.utils.TableHelpers;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;
import java.util.ResourceBundle;


/**
 * Controller for the Appointment screen.
 */
public class AppointmentController implements Initializable, NavigationHelpers, BindingHelpers, TableHelpers {

    @FXML
    private TableView<Appointment> table;
    @FXML
    private ToggleGroup tableViewToggleGroup;
    @FXML
    private RadioButton viewThisMonthRadioButton;
    @FXML
    private RadioButton viewThisWeekRadioButton;
    @FXML
    private RadioButton viewAllRadioButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button clearSelectionButton;
    @FXML
    private Pane fromPane;
    @FXML
    private TextField appointmentIdField;
    @FXML
    private ComboBox<Contact> contactNameComboBox;
    @FXML
    private TextField titleTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private TextField locationTextField;
    @FXML
    private ComboBox<Customer> customerIdComboBox;
    @FXML
    private TextField typeTextField;
    @FXML
    private TextField userIdTextField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField startTimeHourTextField;
    @FXML
    private TextField startTimeMinuteTextField;
    @FXML
    private TextField endTimeMinuteTextField;
    @FXML
    private TextField endTimeHourTextField;
    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button toReportViewButton;
    private final User thisUser = User.selectIdDB(LoginController.currentUser.userId());
    protected static Appointment selectedAppointment;

    static {
        appointmentWithinAQuarterHourAlert();
    }

    /**
     * Gets the selected customer from the table.
     */
    private void getSelectedAppointment() {
        AppointmentController.selectedAppointment = table.getSelectionModel().getSelectedItem();
    }

    /**
     * Verifies existence of a user id.
     *
     * @return true if the user id exists, false otherwise.
     */
    private boolean isUserIdVerified() {
        for (User user : User.selectAllDB()) {
            if (user.userId() == Integer.parseInt(userIdTextField.getText())) {
                return true;
            }
        }
        DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null,
                "User does not exist.");
        return false;
    }

    /**
     * Navigation pseudo implementation (see NavigationHelpers interface).
     *
     * @param event An event.
     * @throws IOException An exception.
     */
    @Override
    @FXML
    public void toCustomerScreenHandler(ActionEvent event) throws IOException {
        NavigationHelpers.super.toCustomerScreenHandler(event);
    }

    /**
     * Navigation pseudo implementation (see NavigationHelpers interface).
     *
     * @param event An event.
     * @throws IOException An exception.
     */
    public void toReportScreenHandler(ActionEvent event) throws IOException {
        NavigationHelpers.super.toReportScreenHandler(event);
    }

    /**
     * Determines if there is an appointment within 15 minutes.
     */
    private static void appointmentWithinAQuarterHourAlert() {
        boolean isFound = false;
        for (Appointment a : Appointment.selectAllDB()) {
            if ((a.start().toLocalDateTime().isAfter(LocalDateTime.now())
                ||
                a.start().toLocalDateTime().isEqual(LocalDateTime.now()))
                &&
                (a.start().toLocalDateTime().isBefore(LocalDateTime.now().plusMinutes(15))
                ||
                a.start().toLocalDateTime().isEqual(LocalDateTime.now().plusMinutes(15))))
            {
                isFound = true;
                DialogHelpers.displayDialog(Alert.AlertType.INFORMATION, null, null,
                        "You have an appointment with id " + a.appointmentId()
                                + " on " + a.start().toLocalDateTime().getMonth()
                                + " " + a.start().toLocalDateTime().getDayOfMonth()
                                + " " + " at " + a.start().toLocalDateTime().getHour()
                                + ":" + a.start().toLocalDateTime().getMinute());
            }
        }
        if (!isFound) {
            DialogHelpers.displayDialog(Alert.AlertType.INFORMATION, null, null,
                    "You have no appointments within the next 15 minutes.");
        }
    }

    /**
     * Clears the selection in the table.
     */
    private void clearForm() {
        appointmentIdField.clear();
        contactNameComboBox.getSelectionModel().clearSelection();
        titleTextField.clear();
        descriptionTextField.clear();
        locationTextField.clear();
        customerIdComboBox.getSelectionModel().clearSelection();
        typeTextField.clear();
        userIdTextField.textProperty().setValue(String.valueOf(thisUser.userId()));
        datePicker.setValue(null);
        startTimeHourTextField.clear();
        startTimeMinuteTextField.clear();
        endTimeHourTextField.clear();
        endTimeMinuteTextField.clear();
    }

    /**
     * Sets up the combo boxes.
     *
     * By using a lambda expression to set cell factory's,
     * the code becomes more concise and easier to read compared to using an anonymous inner class or a separate method.
     */
    private void setUpComboBoxes() {
        contactNameComboBox.setItems(Contact.selectAllDB());
        class contactCell extends ListCell<Contact> {
            @Override
            protected void updateItem(Contact contact, boolean empty) {
                super.updateItem(contact, empty);
                if (empty || contact == null) {
                    setText(null);
                } else {
                    setText(contact.contactName());
                }
            }
        }
        contactNameComboBox.setCellFactory(listView -> new contactCell());
        contactNameComboBox.setButtonCell(new contactCell());

        customerIdComboBox.setItems(Customer.selectAllDB());
        class customerCell extends ListCell<Customer> {
            @Override
            protected void updateItem(Customer customer, boolean empty) {
                super.updateItem(customer, empty);
                if (empty || customer == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(customer.customerId()));
                }
            }
        }
        customerIdComboBox.setCellFactory(listView -> new customerCell());
        customerIdComboBox.setButtonCell(new customerCell());
    }

    /**
     * Sets up the table and form.
     *
     * The lambda expression also allows for capturing the values of variables in the enclosing scope,
     * making it easier to access necessary data in the listener function.
     */
    private void setUpTableAndForm() {
        tabularFabricator(table, Appointment.selectAllDB(), TableView.UNCONSTRAINED_RESIZE_POLICY);
        userIdTextField.textProperty().setValue(String.valueOf(thisUser.userId()));
        table.getColumns().get(11).setText("User_ID");
        table.getColumns().get(12).setText("Customer_ID");
        table.getColumns().get(13).setText("Contact_ID");
        table.getSelectionModel().selectedItemProperty().addListener((observableValue, prevSelectedAppointment, currSelectedAppointment) -> {
            if (currSelectedAppointment != null) {
                appointmentIdField.setText(String.valueOf(currSelectedAppointment.appointmentId()));
                contactNameComboBox.getSelectionModel().select(currSelectedAppointment.contact());
                titleTextField.setText(currSelectedAppointment.title());
                descriptionTextField.setText(currSelectedAppointment.description());
                locationTextField.setText(currSelectedAppointment.location());
                customerIdComboBox.getSelectionModel().select(currSelectedAppointment.customer());
                typeTextField.setText(currSelectedAppointment.type());
                userIdTextField.setText(String.valueOf(currSelectedAppointment.user().userId()));
                datePicker.setValue(currSelectedAppointment.start().toLocalDateTime().toLocalDate());
                startTimeHourTextField.setText(String.valueOf(currSelectedAppointment.start().toLocalDateTime().toLocalTime().getHour()));
                startTimeMinuteTextField.setText(String.valueOf(currSelectedAppointment.start().toLocalDateTime().toLocalTime().getMinute()));
                endTimeHourTextField.setText(String.valueOf(currSelectedAppointment.end().toLocalDateTime().toLocalTime().getHour()));
                endTimeMinuteTextField.setText(String.valueOf(currSelectedAppointment.end().toLocalDateTime().toLocalTime().getMinute()));
            } else {
                clearForm();
            }
        });
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
            vacantTextFieldDependencies(
                titleTextField,
                descriptionTextField,
                locationTextField,
                typeTextField,
                userIdTextField,
                startTimeHourTextField,
                startTimeMinuteTextField,
                endTimeHourTextField,
                endTimeMinuteTextField
            )
            .or(
            vacantComboBoxDependencies(contactNameComboBox, customerIdComboBox))
            .or(
            vacantDatePickerDependencies(datePicker))
            .or(
            table.getSelectionModel().selectedItemProperty().isNotNull())
        );
        updateButton.disableProperty().bind(
            vacantTextFieldDependencies(
                titleTextField,
                descriptionTextField,
                locationTextField,
                typeTextField,
                userIdTextField,
                startTimeHourTextField,
                startTimeMinuteTextField,
                endTimeHourTextField,
                endTimeMinuteTextField
            )
            .or(
            vacantComboBoxDependencies(contactNameComboBox, customerIdComboBox))
            .or(
            vacantDatePickerDependencies(datePicker))
            .or(
            table.getSelectionModel().selectedItemProperty().isNull())
        );
        toReportViewButton.disableProperty().bind(Bindings.createBooleanBinding(() ->
            table.getItems().isEmpty(),
            table.itemsProperty()
        ));
    }

    /**
     * Sets up the radio buttons and the table items respectively.
     *
     * Using a lambda expression for filtering allows for a functional programming style,
     * making it easier to express complex operations on collections in a compact and readable manner.
     */
    @FXML
    private void tableViewFilterHandler() {
        ObservableList<Appointment> allAppointments = Appointment.selectAllDB();
        if (viewAllRadioButton.isSelected()) {
            table.setItems(allAppointments);
            table.getSelectionModel().clearSelection();
        }
        if (viewThisMonthRadioButton.isSelected()) {
            table.setItems(allAppointments.filtered(appointment -> {
                return appointment.start().toLocalDateTime().getMonth() == LocalDate.now().getMonth();
            }));
            table.getSelectionModel().clearSelection();
        }
        if (viewThisWeekRadioButton.isSelected()) {
            table.setItems(allAppointments.filtered(appointment -> {
                LocalDate appointmentDate = appointment.start().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate now = LocalDate.now();
                DayOfWeek nowDay = now.getDayOfWeek();
                LocalDate weekStart;
                if (appointmentDate.getMonth() == now.getMonth()) {
                    if (nowDay == DayOfWeek.SUNDAY) {
                        weekStart = now;
                    } else {
                        weekStart = now.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));
                    }
                    return appointmentDate.isAfter(weekStart);
                }
                return false;
            }));
        }
        table.getSelectionModel().clearSelection();
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
     * Validates the DateTime made from the user input.
     *
     * @return An Optional Pair of LocalDateTime objects.
     */
    private Optional<Pair<LocalDateTime, LocalDateTime>> dateTimeValidation() {
        LocalDate date = datePicker.getValue();
        LocalTime startTime = null;
        LocalTime endTime = null;
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;
        ZonedDateTime startSystemZonedDateTime = null;
        ZonedDateTime endSystemZonedDateTime = null;
        ZonedDateTime startEstZonedDateTime = null;
        ZonedDateTime endEstZonedDateTime = null;
        try {
            startTime = LocalTime.of(Integer.parseInt(startTimeHourTextField.getText().strip()),
                    Integer.parseInt(startTimeMinuteTextField.getText().strip()));
            endTime = LocalTime.of(Integer.parseInt(endTimeHourTextField.getText().strip()),
                    Integer.parseInt(endTimeMinuteTextField.getText().strip()));
            startDateTime = LocalDateTime.of(date, startTime);
            endDateTime = LocalDateTime.of(date, endTime);
        } catch (Exception e) {
            DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null, "Invalid: " + e.getMessage());
            return Optional.empty();
        }

        if (startDateTime.isAfter(endDateTime)) {
            DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null, "Start must be before end.");
            return Optional.empty();
        }

        startSystemZonedDateTime = ZonedDateTime.of(startDateTime, ZoneId.systemDefault());
        endSystemZonedDateTime = ZonedDateTime.of(endDateTime, ZoneId.systemDefault());
        startEstZonedDateTime = startSystemZonedDateTime.withZoneSameInstant(ZoneId.of("America/New_York"));
        endEstZonedDateTime = endSystemZonedDateTime.withZoneSameInstant(ZoneId.of("America/New_York"));

        if (startEstZonedDateTime.isBefore(
                ZonedDateTime.of(
                startEstZonedDateTime.toLocalDate(), LocalTime.of(8, 0), ZoneId.of("America/New_York")))
                ||
            endEstZonedDateTime.isAfter(
                ZonedDateTime.of(
                startEstZonedDateTime.toLocalDate(), LocalTime.of(22, 0), ZoneId.of("America/New_York")))) {
            DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null,
                    "Appointment must be between 8:00 AM and 10:00 PM EST of the same day.");
            return Optional.empty();
        }

        LocalDateTime formStart = startDateTime;
        LocalDateTime formEnd = endDateTime;

        int formAppointmentId = -1;

        if (!appointmentIdField.textProperty().isEmpty().get()) {
            formAppointmentId = Integer.parseInt(appointmentIdField.textProperty().get());
        }

        for (Appointment appointment : Appointment.selectAllDB()) {

            LocalDateTime dbStart = appointment.start().toLocalDateTime();
            LocalDateTime dbEnd = appointment.end().toLocalDateTime();

            if (appointment.customer().customerId() == customerIdComboBox.getValue().customerId()) {
                if (appointment.appointmentId() == formAppointmentId) {
                    continue;
                }
                if ((dbStart.isAfter(formStart) || dbStart.isEqual(formStart))
                    &&
                    (dbStart.isBefore(formEnd)))
                {
                    DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null,
                            "Appointment conflicts with another appointment. 1");
                    return Optional.empty();
                }
                if ((dbEnd.isAfter(formStart))
                    &&
                    (dbEnd.isBefore(formEnd) || dbEnd.isEqual(formEnd)))
                {
                    DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null,
                            "Appointment conflicts with another appointment. 2");
                    return Optional.empty();
                }
                if ((dbStart.isBefore(formStart) || dbStart.isEqual(formStart))
                    &&
                    (dbEnd.isAfter(formEnd) || dbEnd.isEqual(formEnd)))
                {
                    DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null,
                            "Appointment conflicts with another appointment. 3");
                    return Optional.empty();
                }
            }
        }
        return Optional.of(new Pair<>(startDateTime, endDateTime));
    }

    /**
     * Adds an appointment.
     *
     * Compared to using traditional imperative code with multiple variables and intermediate operations,
     * a lambda expression is a more concise way to express series of transformations on a value.
     */
    @FXML
    private void addButtonHandler() {
        if (!isUserIdVerified()) {
            return;
        }

        if (dateTimeValidation().isEmpty()) {
            return;
        }

        Timestamp startTimestamp = dateTimeValidation().map(Pair::getKey)
                .map(Timestamp::valueOf).orElseThrow();
        Timestamp endTimestamp = dateTimeValidation().map(Pair::getValue)
                .map(Timestamp::valueOf).orElseThrow();

        Appointment addedAppointment = Appointment.insertDB(
            titleTextField.getText().strip(),
            descriptionTextField.getText().strip(),
            locationTextField.getText().strip(),
            typeTextField.getText().strip(),
            startTimestamp,
            endTimestamp,
            new Timestamp(System.currentTimeMillis()),
            thisUser.userName(),
            customerIdComboBox.getSelectionModel().getSelectedItem().customerId(),
            thisUser.userId(),
            contactNameComboBox.getSelectionModel().getSelectedItem().contactId()
        );
        if (addedAppointment != null) {
            table.getItems().add(addedAppointment);
            clearForm();
            table.getSelectionModel().clearSelection();
        } else {
            DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null,
                    "Failed to add appointment.");
        }
    }

    /**
     * Updates a appointment.
     *
     * Compared to using traditional imperative code with multiple variables and intermediate operations,
     * a lambda expression is a more concise way to express series of transformations on a value.
     */
    @FXML
    private void updateButtonHandler() {
        if (!isUserIdVerified()) {
            return;
        }

        if (dateTimeValidation().isEmpty()) {
            return;
        }

        Timestamp startTimestamp = dateTimeValidation().map(Pair::getKey)
                .map(Timestamp::valueOf).orElseThrow();
        Timestamp endTimestamp = dateTimeValidation().map(Pair::getValue)
                .map(Timestamp::valueOf).orElseThrow();

        Appointment updatedAppointment = Appointment.updateDB(
            table.getSelectionModel().getSelectedItem().appointmentId(),
            titleTextField.getText().strip(),
            descriptionTextField.getText().strip(),
            locationTextField.getText().strip(),
            typeTextField.getText().strip(),
            startTimestamp,
            endTimestamp,
            new Timestamp(System.currentTimeMillis()),
            thisUser.userName(),
            customerIdComboBox.getSelectionModel().getSelectedItem().customerId(),
            thisUser.userId(),
            contactNameComboBox.getSelectionModel().getSelectedItem().contactId()
        );
        if (updatedAppointment != null) {
            tableViewFilterHandler();
            clearForm();
            table.getSelectionModel().clearSelection();
        } else {
            DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null,
                    "Unable to update appointment.");
        }
    }

    /**
     * Deletes the selected customer.
     */
    @FXML
    private void deleteButtonHandler() {
        getSelectedAppointment();
        var result = DialogHelpers.displayConfirmationDialog(null, null,
                "Are you sure you want to delete this appointment?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean isAppointmentDeleted = Appointment.deleteDB(selectedAppointment.appointmentId());
            if (isAppointmentDeleted) {
                tableViewFilterHandler();
                String appointmentTitle = AppointmentController.selectedAppointment.title();
                DialogHelpers.displayDialog(Alert.AlertType.INFORMATION, null, null,
                        "Appointment '" + appointmentTitle + "' has been deleted.");
                table.getItems().remove(AppointmentController.selectedAppointment);
                table.getSelectionModel().clearSelection();
            } else {
                DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null,
                        "Failed to delete appointment.");
                table.setItems(Appointment.selectAllDB());
            }
        }
    }
}

