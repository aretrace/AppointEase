package com.appointease.app.utils;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;

/**
 * A helper interface for JavaFX tables.
 */
public interface TableHelpers {
    /**
     * Creates a table view with the given data list.
     *
     * By using a lambda expression to set cell value factory's,
     * the code becomes more concise and easier to read compared to using a Callback interface.
     *
     * @param table the table view
     * @param dataList the data list
     * @param resizePolicy the resize policy
     * @param <T> the type of the data list
     */
    default <T> void tabularFabricator(TableView<T> table, ObservableList<T> dataList, Callback<TableView.ResizeFeatures, Boolean> resizePolicy) {
        if (dataList.size() == 0) {
            // TODO: handle empty data guard clause
            return;
        }
        int i = 0;
        RecordComponent[] recordComponentArray = dataList.get(0).getClass().getRecordComponents();
        for (RecordComponent rc : recordComponentArray) {
            String columnName = rc.getName()
                                  .replaceFirst("^[a-z]", rc.getName().substring(0, 1).toUpperCase())
                                  .replaceAll("(?<=[a-z])(?=[A-Z])", " ");
            TableColumn<T, Object> col = new TableColumn<>(columnName);
            int cellColumnIndex = i;
            col.setCellValueFactory(dataObject -> {
                var cellData = dataObject.getValue().getClass().getRecordComponents()[cellColumnIndex];
                // TODO: name this [ cellData.getAccessor().invoke(dataObject.getValue())) ]
                try {
                    if (cellData.getType().isRecord()) {
                        var nestedRecordObject = cellData.getType();
                        return new SimpleObjectProperty<>(nestedRecordObject.getRecordComponents()[0].getAccessor()
                                .invoke(cellData.getAccessor().invoke(dataObject.getValue())));
                    }
                    return new SimpleObjectProperty<>(cellData.getAccessor().invoke(dataObject.getValue()));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null, e.getMessage());
                    throw new RuntimeException(e);
                }
            });
            table.getColumns().add(col);
            i++;
        }
        table.setItems(dataList);
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        table.setColumnResizePolicy(resizePolicy);
    }
}
