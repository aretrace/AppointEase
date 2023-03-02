package com.appointease.app.model;

import com.appointease.app.utils.DialogHelpers;
import com.appointease.app.utils.JDBC;
import com.appointease.app.utils.TimeHelpers;
import com.appointease.app.utils.exceptions.SQLNoRowsAffectedException;
import com.appointease.app.utils.exceptions.SQLNoRowsFoundException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.*;

/**
 * A data carrier class that contains database CRUD operations as static methods for the appointments table.
 * A row returned from the table is packaged as a Appointment data-transfer-like object.
 * @param appointmentId
 * @param title
 * @param description
 * @param location
 * @param type
 * @param start
 * @param end
 * @param createDate
 * @param createdBy
 * @param lastUpdate
 * @param lastUpdatedBy
 * @param customer
 * @param user
 * @param contact
 */
public record Appointment(int appointmentId, String title, String description, String location, String type, Timestamp start, Timestamp end, Timestamp createDate, String createdBy, Timestamp lastUpdate, String lastUpdatedBy, Customer customer, User user, Contact contact) {
    /**
     * Gets all rows from appointments table
     *
     * @return Observable List of Appointment objects
     */
    public static ObservableList<Appointment> selectAllDB() {
        ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM appointments";
        try {
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new SQLNoRowsFoundException();
            } else {
                do {
                    Appointment appointment = new Appointment(
                        rs.getInt("Appointment_ID"),
                        rs.getString("Title"),
                        rs.getString("Description"),
                        rs.getString("Location"),
                        rs.getString("Type"),
                        TimeHelpers.fromDBtoSysTZ(rs.getTimestamp("Start")),
                        TimeHelpers.fromDBtoSysTZ(rs.getTimestamp("End")),
                        rs.getTimestamp("Create_Date"),
                        rs.getString("Created_By"),
                        rs.getTimestamp("Last_Update"),
                        rs.getString("Last_Updated_By"),
                        Customer.selectIdDB(rs.getInt("Customer_ID")),
                        User.selectIdDB(rs.getInt("User_ID")),
                        Contact.selectIdDB(rs.getInt("Contact_ID"))
                    );
                    appointmentList.add(appointment);
                } while (rs.next());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (SQLNoRowsFoundException e) {
            DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null, e.getMessage());
        }
        return appointmentList;
    }

    /**
     * Gets row for a specific appointment ID
     *
     * @param appointmentId Appointment ID
     * @return Appointment object
     */
    public static Appointment selectIdDB(int appointmentId) {
        Appointment appointment = null;
        String sql = "SELECT * FROM appointments WHERE Appointment_ID = ?";
        try {
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
            ps.setInt(1, appointmentId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new SQLNoRowsFoundException();
            }
            appointment = new Appointment(
                rs.getInt("Appointment_ID"),
                rs.getString("Title"),
                rs.getString("Description"),
                rs.getString("Location"),
                rs.getString("Type"),
                TimeHelpers.fromDBtoSysTZ(rs.getTimestamp("Start")),
                TimeHelpers.fromDBtoSysTZ(rs.getTimestamp("End")),
                rs.getTimestamp("Create_Date"),
                rs.getString("Created_By"),
                rs.getTimestamp("Last_Update"),
                rs.getString("Last_Updated_By"),
                Customer.selectIdDB(rs.getInt("Customer_ID")),
                User.selectIdDB(rs.getInt("User_ID")),
                Contact.selectIdDB(rs.getInt("Contact_ID"))
            );
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (SQLNoRowsFoundException e) {
            DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null, e.getMessage());
        }
        return appointment;
    }

    // TODO: some method to get appointments by title or type or location etc if necessary

    /**
     * Inserts a new row into appointments table.
     *
     * @param title Appointment title.
     * @param description Appointment description.
     * @param location Appointment location.
     * @param type Appointment type.
     * @param start Appointment start time.
     * @param end Appointment end time.
     * @param createDate Appointment create date.
     * @param createdBy Appointment created by.
     * @param customerId Appointment customer ID.
     * @param userId Appointment user ID.
     * @param contactId Appointment contact ID.
     * @return Appointment object
     */
    public static Appointment insertDB(String title, String description, String location, String type, Timestamp start, Timestamp end, Timestamp createDate, String createdBy, int customerId, int userId, int contactId) {
        String sql = "INSERT INTO appointments (Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int rowsAffected = 0;
        Appointment appointment = null;
        try {
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, location);
            ps.setString(4, type);
            ps.setTimestamp(5, start);
            ps.setTimestamp(6, end);
            ps.setTimestamp(7, createDate);
            ps.setString(8, createdBy);
            ps.setTimestamp(9, createDate);
            ps.setString(10, createdBy);
            ps.setInt(11, customerId);
            ps.setInt(12, userId);
            ps.setInt(13, contactId);
            rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLNoRowsAffectedException();
            }
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            appointment = selectIdDB(rs.getInt(1));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (SQLNoRowsAffectedException e) {
            DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null, e.getMessage());
        }
        return appointment;
    }

    /**
     * Updates an existing row in appointments table.
     *
     * @param appointmentId Appointment ID.
     * @param newTitle New appointment title.
     * @param newDescription New appointment description.
     * @param newLocation New appointment location.
     * @param newType New appointment type.
     * @param newStart New appointment start time.
     * @param newEnd New appointment end time.
     * @param lastUpdate Last update time.
     * @param lastUpdatedBy Last updated by.
     * @param newCustomerId New appointment customer ID.
     * @param newUserId New appointment user ID.
     * @param newContactId New appointment contact ID.
     * @return Appointment object.
     */
    public static Appointment updateDB(int appointmentId, String newTitle, String newDescription, String newLocation, String newType, Timestamp newStart, Timestamp newEnd, Timestamp lastUpdate, String lastUpdatedBy, int newCustomerId, int newUserId, int newContactId) {
        String sql = "UPDATE appointments SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Last_Update = ?, Last_Updated_By = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?";
        int rowsAffected = 0;
        Appointment appointment = null;
        try {
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
            ps.setString(1, newTitle);
            ps.setString(2, newDescription);
            ps.setString(3, newLocation);
            ps.setString(4, newType);
            ps.setTimestamp(5, newStart);
            ps.setTimestamp(6, newEnd);
            ps.setTimestamp(7, lastUpdate);
            ps.setString(8, lastUpdatedBy);
            ps.setInt(9, newCustomerId);
            ps.setInt(10, newUserId);
            ps.setInt(11, newContactId);
            ps.setInt(12, appointmentId);
            rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLNoRowsAffectedException();
            }
            appointment = selectIdDB(appointmentId);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (SQLNoRowsAffectedException e) {
            DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null, e.getMessage());
        }
        return appointment;
    }

    /**
     * Deletes an existing row in appointments table.
     *
     * @param appointmentId Appointment ID.
     * @return True if successful, false if not.
     */
    public static boolean deleteDB(int appointmentId) {
        String sql = "DELETE FROM appointments WHERE Appointment_ID = ?";
        int rowsAffected = 0;
        try {
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
            ps.setInt(1, appointmentId);
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
