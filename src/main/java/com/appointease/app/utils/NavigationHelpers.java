package com.appointease.app.utils;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


// Pseudo implementations in respective controller classes are needed because of:
// https://bugs.java.com/bugdatabase/view_bug.do?bug_id=JDK-8259916
public interface NavigationHelpers {

    /**
     * Navigates to the given screen.
     *
     * @param event the event
     * @param screenName the screen name
     * @param headerTitle the header title
     * @throws IOException the io exception
     */
    static void navigateToScreen(ActionEvent event, String screenName, String headerTitle) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(NavigationHelpers.class.getResource("/com/appointease/app/view/" + screenName + ".fxml"));
        Parent screen = fxmlLoader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(screen);
        stage.setScene(scene);
        stage.setTitle(headerTitle);
        stage.show();
    }

    /**
     * Navigates to the canonical screen.
     *
     * @param event the event
     * @throws IOException the io exception
     */
    default void toCanonicalScreenHandler(ActionEvent event) throws IOException {
        navigateToScreen(event, "canonical-screen", "THE Screen");
    }

    /**
     * Navigates to the test screen.
     *
     * @param event the event
     * @throws IOException the io exception
     */
    default void toTestScreenHandler(ActionEvent event) throws IOException {
        navigateToScreen(event, "test-screen", "Test Screen");
    }

    /**
     * Navigates to the appointment screen.
     *
     * @param event the event
     * @throws IOException the io exception
     */
    default void toAppointmentScreenHandler(ActionEvent event) throws IOException {
        navigateToScreen(event, "appointment-screen", "Appointment Screen");
    }

    /**
     * Navigates to the customer screen.
     *
     * @param event the event
     * @throws IOException the io exception
     */
    default void toCustomerScreenHandler(ActionEvent event) throws IOException {
        navigateToScreen(event, "customer-screen", "Customer Screen");
    }

    /**
     * Navigates to the report screen.
     *
     * @param event the event
     * @throws IOException the io exception
     */
    default void toReportScreenHandler(ActionEvent event) throws IOException {
        navigateToScreen(event, "report-screen", "Report Screen");
    }

}