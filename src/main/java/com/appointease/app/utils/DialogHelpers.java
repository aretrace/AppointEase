package com.appointease.app.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * A helper interface for JavaFX dialogs.
 */
public interface DialogHelpers {
    /**
     * Shows a dialog.
     *
     * @param alertType the alert type
     * @param title the title
     * @param headerText the header text of the dialog
     * @param contentText the content text of the dialog
     */
    static void displayDialog(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    /**
     * Shows a confirmation dialog.
     * Use as follows:
     * <pre>
     * result = displayConfirmationDialog(...)
     * if (result.get() == ButtonType.OK){
     *    // ... user chose OK
     * } else {
     *   // ... user chose CANCEL or closed the dialog
     * }
     * </pre>
     * @param title the title
     * @param headerText the header text of the dialog
     * @param contentText the content text of the dialog
     */
    static Optional<ButtonType> displayConfirmationDialog(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        return alert.showAndWait();
    }
}
