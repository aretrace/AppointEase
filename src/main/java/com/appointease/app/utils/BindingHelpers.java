package com.appointease.app.utils;

import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.control.*;

import java.util.stream.Stream;

import static javafx.beans.binding.Bindings.createBooleanBinding;

/**
 * Helper interface for binding properties of JavaFX controls.
 */
public interface BindingHelpers {
    /**
     * A function that takes an arbitrary number of TextField arguments to
     * represent dependencies between the text fields and the state of whether
     * any of them contain an empty or blank value.
     *
     * Using a lambda expression for filtering allows for a functional programming style,
     * making it easier to express complex operations on collections in a compact and readable manner.
     *
     * @param textFields array of text fields to bind to.
     * @return BooleanBinding object.
     */
    default BooleanBinding vacantTextFieldDependencies(TextField... textFields) {
        return createBooleanBinding(() ->
            Stream.of(textFields).anyMatch((textField) -> textField.textProperty().get().isBlank()),
            Stream.of(textFields).map(TextInputControl::textProperty).toArray(Observable[]::new)
        );
    }

    /**
     * A function that takes an arbitrary number of ComboBox arguments to
     * represent dependencies between the combo boxes and the state of whether
     * any of them contain an empty or blank value.
     *
     * Using a lambda expression for filtering allows for a functional programming style,
     * making it easier to express complex operations on collections in a compact and readable manner.
     *
     * @param comboBoxes array of combo boxes to bind to.
     * @return BooleanBinding object.
     */
    default BooleanBinding vacantComboBoxDependencies(ComboBox... comboBoxes) {
        return createBooleanBinding(() ->
            Stream.of(comboBoxes).anyMatch((comboBox) -> comboBox.valueProperty().get() == null),
            Stream.of(comboBoxes).map(ComboBox::valueProperty).toArray(Observable[]::new)
        );
    }

    /**
     * A function that takes an arbitrary number of DatePicker arguments to
     * represent dependencies between the DatePickers and the state of whether
     * any of them contain an empty or blank value.
     *
     * Using a lambda expression for filtering allows for a functional programming style,
     * making it easier to express complex operations on collections in a compact and readable manner.
     *
     * @param DatePicker array of DatePickers to bind to.
     * @return BooleanBinding object.
     */
    default BooleanBinding vacantDatePickerDependencies(DatePicker... datePickers) {
        return createBooleanBinding(() ->
            Stream.of(datePickers).anyMatch((datePicker) -> datePicker.valueProperty().get() == null),
            Stream.of(datePickers).map(DatePicker::valueProperty).toArray(Observable[]::new)
        );
    }
}
