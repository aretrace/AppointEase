package com.appointease.app.model;

// Redefine some one-to-many relationship between SomeTable1 and SomeTable2 to be one-to-one, I think.

import com.appointease.app.utils.DialogHelpers;
import com.appointease.app.utils.JDBC;
import com.appointease.app.utils.exceptions.SQLNoRowsAffectedException;
import com.appointease.app.utils.exceptions.SQLNoRowsFoundException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.*;

/**
 * A data carrier class that contains database CRUD operations as static methods for the customers table.
 * A row returned from the table is packaged as a Customer data-transfer-like object.
 * @param customerId
 * @param customerName
 * @param address
 * @param postalCode
 * @param phone
 * @param createDate
 * @param createdBy
 * @param lastUpdate
 * @param lastUpdateBy
 * @param division
 */
public record Customer(int customerId, String customerName, String address, String postalCode, String phone, Timestamp createDate, String createdBy, Timestamp lastUpdate, String lastUpdateBy, FirstLevelDivision division) {
    /**
     * Gets all rows from customers table.
     *
     * @return Observable List of Customer objects.
     */
    public static ObservableList<Customer> selectAllDB() {
        ObservableList<Customer> customerList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM customers";
        try {
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new SQLNoRowsFoundException();
            } else {
                do {
                    Customer customer = new Customer(
                        rs.getInt("Customer_ID"),
                        rs.getString("Customer_Name"),
                        rs.getString("Address"),
                        rs.getString("Postal_Code"),
                        rs.getString("Phone"),
                        rs.getTimestamp("Create_Date"),
                        rs.getString("Created_By"),
                        rs.getTimestamp("Last_Update"),
                        rs.getString("Last_Updated_By"),
                        FirstLevelDivision.selectIdDB(rs.getInt("Division_ID"))
                    );
                    customerList.add(customer);
                } while (rs.next());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (SQLNoRowsFoundException e) {
            DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null, e.getMessage());
        }
        return customerList;
    }

    /**
     * Gets row for a specific customer ID.
     *
     * @param customerId Customer ID.
     * @return Customer object.
     */
    public static Customer selectIdDB(int customerId) {
        String sql = "SELECT * FROM customers WHERE Customer_ID = ?";
        Customer customer = null;
        try {
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new SQLNoRowsFoundException();
            } else {
                customer = new Customer(
                    rs.getInt("Customer_ID"),
                    rs.getString("Customer_Name"),
                    rs.getString("Address"),
                    rs.getString("Postal_Code"),
                    rs.getString("Phone"),
                    rs.getTimestamp("Create_Date"),
                    rs.getString("Created_By"),
                    rs.getTimestamp("Last_Update"),
                    rs.getString("Last_Updated_By"),
                    FirstLevelDivision.selectIdDB(rs.getInt("Division_ID"))
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (SQLNoRowsFoundException e) {
            DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null, e.getMessage());
        }
        return customer;
    }

    /**
     * Gets row for a specific customer name.
     *
     * @param customerName Customer name.
     * @return Observable List of Customer objects.
     */
    public static ObservableList<Customer> selectNameDB(String customerName) {
        ObservableList<Customer> customerList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM customers WHERE Customer_Name = ?";
        try {
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
            ps.setString(1, customerName);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new SQLNoRowsFoundException();
            } else {
                do {
                    Customer customer = new Customer(
                        rs.getInt("Customer_ID"),
                        rs.getString("Customer_Name"),
                        rs.getString("Address"),
                        rs.getString("Postal_Code"),
                        rs.getString("Phone"),
                        rs.getTimestamp("Create_Date"),
                        rs.getString("Created_By"),
                        rs.getTimestamp("Last_Update"),
                        rs.getString("Last_Updated_By"),
                        FirstLevelDivision.selectIdDB(rs.getInt("Division_ID"))
                    );
                    customerList.add(customer);
                } while (rs.next());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (SQLNoRowsFoundException e) {
            DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null, e.getMessage());
        }
        return customerList;
    }

    /**
     * Inserts a new row into customers table.
     * Sets the Last Update as the create date and sets the Last Update By as the created by.
     *
     * @param customerName Customer name.
     * @param address Customer address.
     * @param postalCode Customer postal code.
     * @param phone Customer phone number.
     * @param createDate Customer create date.
     * @param createdBy User who created the record.
     * @param divisionId Customer division ID.
     * @return Customer object.
     */
    public static Customer insertDB(String customerName, String address, String postalCode, String phone, Timestamp createDate, String createdBy, int divisionId) {
       String sql = "INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
       int rowsAffected = 0;
       Customer customer = null;
        try {
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, customerName);
            ps.setString(2, address);
            ps.setString(3, postalCode);
            ps.setString(4, phone);
            ps.setTimestamp(5, createDate);
            ps.setString(6, createdBy);
            ps.setTimestamp(7, createDate);
            ps.setString(8, createdBy);
            ps.setInt(9, divisionId);
            rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLNoRowsAffectedException();
            }
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            customer = Customer.selectIdDB(rs.getInt(1));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (SQLNoRowsAffectedException e) {
            DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null, e.getMessage());
        }
        return customer;
    }

    /**
     * Updates a row in the customers table.
     *
     * @param customerId Customer ID.
     * @param newCustomerName New Customer name.
     * @param newAddress New Customer address.
     * @param newPostalCode New Customer postal code.
     * @param newPhone New Customer phone number.
     * @param lastUpdate Last update.
     * @param lastUpdateBy Last update by.
     * @param newDivisionId New Customer division ID.
     * @return Customer object.
     */
    public static Customer updateDB(int customerId, String newCustomerName, String newAddress, String newPostalCode, String newPhone, Timestamp lastUpdate, String lastUpdateBy, int newDivisionId) {
        String sql = "UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Last_Update = ?, Last_Updated_By = ?, Division_ID = ? WHERE Customer_ID = ?";
        int rowsAffected = 0;
        Customer customer = null;
        try {
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
            ps.setString(1, newCustomerName);
            ps.setString(2, newAddress);
            ps.setString(3, newPostalCode);
            ps.setString(4, newPhone);
            ps.setTimestamp(5, lastUpdate);
            ps.setString(6, lastUpdateBy);
            ps.setInt(7, newDivisionId);
            ps.setInt(8, customerId);
            rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLNoRowsAffectedException();
            }
            customer = Customer.selectIdDB(customerId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (SQLNoRowsAffectedException e) {
            DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null, e.getMessage());
        }
        return customer;
    }

    /**
     * Deletes a row from the customers table.
     *
     * @param customerId Customer ID.
     * @return True if row was deleted, false if not.
     */
    public static boolean deleteDB(int customerId) {
        String sql = "DELETE FROM customers WHERE Customer_ID = ?";
        int rowsAffected = 0;
        try {
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
            ps.setInt(1, customerId);
            rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLNoRowsAffectedException();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (SQLNoRowsAffectedException e) {
            DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null, e.getMessage());
        }
        return rowsAffected > 0;
    }

}



















