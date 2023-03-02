package com.appointease.app.controller;

import com.appointease.app.model.Appointment;
import com.appointease.app.model.Contact;
import com.appointease.app.utils.BindingHelpers;
import com.appointease.app.utils.NavigationHelpers;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Controller for the Reports screen.
 */
public class ReportController implements Initializable, NavigationHelpers, BindingHelpers {

    @FXML
    private TextArea a;
    @FXML
    private TextArea b;
    @FXML
    private TextArea c;

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
     * Sets up the three reports.
     *
     * By using a lambda expression for filtering makes easier to express complex operations compared to using traditional imperative code.
     * Multiple variables declarations and intermediate operations can be avoided by using a lambda expression to express series of transformations on a value.
     */
    private void setUpReports() {
        HashMap <String, Integer> typesMap = new HashMap<>();
        HashMap <String, Integer> monthsMap = new HashMap<>();
        for (Appointment appointment : Appointment.selectAllDB()) {
            if (typesMap.containsKey(appointment.type())) {
                typesMap.put(appointment.type(), typesMap.get(appointment.type()) + 1);
            } else {
                typesMap.put(appointment.type(), 1);
            }
            if (monthsMap.containsKey(appointment.start().toLocalDateTime().getMonth().toString())) {
                monthsMap.put(appointment.start().toLocalDateTime().getMonth().toString(),
                        monthsMap.get(appointment.start().toLocalDateTime().getMonth().toString()) + 1);
            } else {
                monthsMap.put(appointment.start().toLocalDateTime().getMonth().toString(), 1);
            }
        }
        a.appendText("=== By Type ===\n");
        for (String type : typesMap.keySet()) {
            a.appendText(type + ": " + typesMap.get(type) + "\n");
        }
        a.appendText("=== By Month ===\n");
        for (String month : monthsMap.keySet()) {
            a.appendText(month + ": " + monthsMap.get(month) + "\n");
        }
        Set<Contact> contactsWITHAppointmentsSet = Appointment.selectAllDB().stream().map(Appointment::contact)
                                                   .collect(Collectors.toSet());
        List<Contact> contactsWithNOAppointmentList = Contact.selectAllDB().stream()
                  .filter(contact -> !contactsWITHAppointmentsSet.contains(contact))
                  .toList();
        for (Contact contact : contactsWITHAppointmentsSet) {
            b.appendText("=== " + contact.contactName() + " [ID " + contact.contactId() + "]" + " ===\n");
            for (Appointment appointment : Appointment.selectAllDB()) {
                if (appointment.contact().contactId() == contact.contactId()) {
                    b.appendText(
                        "id: " +
                          appointment.appointmentId() + ", title: "
                        + appointment.title() + ", type: "
                        + appointment.type() + ", description: "
                        + appointment.description() + ", start date: "
                        + appointment.start().toLocalDateTime().toLocalDate().toString() + ", Customer ID: "
                        + appointment.customer().customerId() + "\n"
                    );
                }
            }
        }
        for (Contact contact : contactsWithNOAppointmentList) {
            b.appendText("=== " + contact.contactName() + " [ID " + contact.contactId() + "]" + " ===\n");
            b.appendText("No appointments.\n");
        }
        var thisYearAppointments = Appointment.selectAllDB().filtered(appointment ->
                appointment.start().toLocalDateTime().getYear() == LocalDate.now().getYear());
        c.appendText(LocalDate.now().getYear() + ": " + thisYearAppointments.size() + "\n");
        a.setScrollTop(Double.MAX_VALUE);
        b.setScrollTop(Double.MAX_VALUE);
        c.setScrollTop(Double.MAX_VALUE);
    }

    /**
     * Initializes the controller class.
     *
     * @param url A URL.
     * @param resourceBundle A resource bundle.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUpReports();
    }
}
