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
import java.sql.Timestamp;


/**
 * A data carrier class that contains database CRUD operations as static methods for the users table.
 * A row returned from the table is packaged as a User data-transfer-like object.
 * @param userId
 * @param userName
 * @param password
 * @param createDate
 * @param createdBy
 * @param lastUpdate
 * @param lastUpdateBy
 */
public record User(int userId, String userName, String password, Timestamp createDate, String createdBy, Timestamp lastUpdate, String lastUpdateBy) {
    /**
     * Gets all rows from users table.
     *
     * @return Observable List of User objects.
     */
    public static ObservableList<User> selectAllDB() {
        ObservableList<User> userList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM users";
        try {
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new SQLNoRowsFoundException();
            }
            do {
                User user = new User(
                    rs.getInt("User_ID"),
                    rs.getString("User_Name"),
                    rs.getString("Password"),
                    rs.getTimestamp("Create_Date"),
                    rs.getString("Created_By"),
                    rs.getTimestamp("Last_Update"),
                    rs.getString("Last_Updated_By")
                );
                userList.add(user);
            } while (rs.next());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (SQLNoRowsFoundException e) {
            DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null, e.getMessage());
        }
        return userList;
    }

    /**
     * Gets row for a specific user ID.
     *
     * @param userId User ID.
     * @return User object.
     */
    public static User selectIdDB(int userId) {
        String sql = "SELECT * FROM users WHERE User_ID = ?";
        User user = null;
        try {
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new SQLNoRowsFoundException();
            }
            user = new User(
                rs.getInt("User_ID"),
                rs.getString("User_Name"),
                rs.getString("Password"),
                rs.getTimestamp("Create_Date"),
                rs.getString("Created_By"),
                rs.getTimestamp("Last_Update"),
                rs.getString("Last_Updated_By")
            );
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (SQLNoRowsFoundException e) {
            DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null, e.getMessage());
        }
        return user;
    }

    /**
     * Gets row for a specific username.
     *
     * @param userName User name.
     * @return Observable List of User objects.
     */
    public static ObservableList<User> selectNameDB(String userName) {
        ObservableList<User> userList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM users WHERE User_Name = ?";
        try {
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new SQLNoRowsFoundException();
            } else {
                do {
                    User user = new User(
                        rs.getInt("User_ID"),
                        rs.getString("User_Name"),
                        rs.getString("Password"),
                        rs.getTimestamp("Create_Date"),
                        rs.getString("Created_By"),
                        rs.getTimestamp("Last_Update"),
                        rs.getString("Last_Updated_By")
                    );
                    userList.add(user);
                } while (rs.next());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (SQLNoRowsFoundException e) {
            DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null, e.getMessage());
        }
        return userList;
    }

    /**
     * Inserts a new row into users table.
     *
     * @param userName User name.
     * @param password User password.
     * @param createDate Create date.
     * @param createdBy Created by.
     * @return User object.
     */
    public static User insertDB(String userName, String password, Timestamp createDate, String createdBy) {
        String sql = "INSERT INTO users (User_Name, Password, Create_Date, Created_By, Last_Update, Last_Updated_By) VALUES (?, ?, ?, ?, ?, ?)";
        int rowsAffected = 0;
        User user = null;
        try {
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, userName);
            ps.setString(2, password);
            ps.setTimestamp(3, createDate);
            ps.setString(4, createdBy);
            ps.setTimestamp(5, createDate);
            ps.setString(6, createdBy);
            rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLNoRowsAffectedException();
            }
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            user = selectIdDB(rs.getInt(1));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (SQLNoRowsAffectedException e) {
            DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null, e.getMessage());
        }
        return user;
    }

    /**
     * Updates a row in users table.
     *
     * @param userId User ID.
     * @param newUserName New username.
     * @param newPassword New user password.
     * @param lastUpdate Last update.
     * @param lastUpdateBy Last updated by.
     * @return User object.
     */
    public static User updateDB(int userId, String newUserName, String newPassword, Timestamp lastUpdate, String lastUpdateBy) {
        String sql = "UPDATE users SET User_Name = ?, Password = ?, Last_Update = ?, Last_Updated_By = ? WHERE User_ID = ?";
        int rowsAffected = 0;
        User user = null;
        try {
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
            ps.setString(1, newUserName);
            ps.setString(2, newPassword);
            ps.setTimestamp(3, lastUpdate);
            ps.setString(4, lastUpdateBy);
            ps.setInt(5, userId);
            rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLNoRowsAffectedException();
            }
            user = selectIdDB(userId);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (SQLNoRowsAffectedException e) {
            DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null, e.getMessage());
        }
        return user;
    }

    /**
     * Deletes a row from users table.
     *
     * @param userId User ID.
     * @return True if successful, false if not.
     */
    public static boolean deleteDB(int userId) {
        String sql = "DELETE FROM users WHERE User_ID = ?";
        int rowsAffected = 0;
        try {
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
            ps.setInt(1, userId);
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
