package com.appointease.app.model;

import com.appointease.app.utils.DialogHelpers;
import com.appointease.app.utils.JDBC;
import com.appointease.app.utils.exceptions.SQLNoRowsAffectedException;
import com.appointease.app.utils.exceptions.SQLNoRowsFoundException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * A data carrier class that contains database CRUD operations as static methods for the countries table.
 * A row returned from the table is packaged as a Contact data-transfer-like object.
 * @param contactId
 * @param contactName
 * @param email
 */
public record Contact(int contactId, String contactName, String email) {

    /**
     * Gets all rows from contacts table.
     *
     * @return Observable List of Contact objects.
     */
    public static ObservableList<Contact> selectAllDB() {
        ObservableList<Contact> contactList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM contacts";
        try {
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new SQLNoRowsFoundException();
            } else {
                do {
                    contactList.add(new Contact(
                        rs.getInt("Contact_ID"),
                        rs.getString("Contact_Name"),
                        rs.getString("Email")
                    ));
                } while (rs.next());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (SQLNoRowsFoundException e) {
            DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null, e.getMessage());
        }
        return contactList;
    }

    /**
     * Gets a row from contacts table by ID.
     *
     * @param contactId Contact ID.
     * @return Contact object.
     */
    public static Contact selectIdDB(int contactId) {
        String sql = "SELECT * FROM contacts WHERE Contact_ID = ?";
        Contact contact = null;
        try {
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
            ps.setInt(1, contactId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new SQLNoRowsFoundException();
            }
            contact = new Contact(
                rs.getInt("Contact_ID"),
                rs.getString("Contact_Name"),
                rs.getString("Email")
            );
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (SQLNoRowsFoundException e) {
            DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null, e.getMessage());
        }
        return contact;
    }

    /**
     * Gets row for a specific contact name.
     *
     * @param contactName Contact name.
     * @return Contact object.
     */
    public static ObservableList<Contact> selectNameDB(String contactName) {
        ObservableList<Contact> contactList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM contacts WHERE Contact_Name = ?";
        try {
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
            ps.setString(1, contactName);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new SQLNoRowsFoundException();
            } else {
                do {
                    Contact contact = new Contact(
                        rs.getInt("Contact_ID"),
                        rs.getString("Contact_Name"),
                        rs.getString("Email")
                    );
                    contactList.add(contact);
                } while (rs.next());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (SQLNoRowsFoundException e) {
            DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null, e.getMessage());
        }
        return contactList;
    }

    /**
     * Inserts a row into contacts table.
     *
     * @param contactName Contact name.
     * @param email Contact email.
     * @return Contact object.
     */
    public static Contact insertDB(String contactName, String email) {
        String sql = "INSERT INTO contacts (Contact_Name, Email) VALUES (?, ?)";
        int rowsAffected = 0;
        Contact contact = null;
        try {
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, contactName);
            ps.setString(2, email);
            rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLNoRowsAffectedException();
            }
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            contact = selectIdDB(rs.getInt(1));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (SQLNoRowsAffectedException e) {
            DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null, e.getMessage());
        }
        return contact;
    }

    /**
     * Updates a row in contacts table.
     *
     * @param contactId Contact ID.
     * @param contactName The new Contact name.
     * @return Contact object.
     */
    public static Contact updateDB(int contactId, String contactName, String email) {
        String sql = "UPDATE contacts SET Contact_Name = ?, Email = ? WHERE Contact_ID = ?";
        int rowsAffected = 0;
        Contact contact = null;
        try {
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
            ps.setString(1, contactName);
            ps.setString(2, email);
            ps.setInt(3, contactId);
            rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLNoRowsAffectedException();
            }
            contact = selectIdDB(contactId);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (SQLNoRowsAffectedException e) {
            DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null, e.getMessage());
        }
        return contact;
    }

    /**
     * Deletes a row from contacts table.
     *
     * @param contactId Contact ID.
     * @return True if row was deleted, false if not.
     */
    public static boolean deleteDB(int contactId) {
        String sql = "DELETE FROM contacts WHERE Contact_ID = ?";
        int rowsAffected = 0;
        try {
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
            ps.setInt(1, contactId);
            rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLNoRowsAffectedException();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (SQLNoRowsAffectedException e) {
            DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null, e.getMessage());
        }
        return rowsAffected != 0;
    }

}

