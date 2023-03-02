package com.appointease.app.model;

import com.appointease.app.utils.DialogHelpers;
import com.appointease.app.utils.JDBC;
import com.appointease.app.utils.exceptions.SQLBindVariableValuesNotFoundException;
import com.appointease.app.utils.exceptions.SQLNoRowsAffectedException;
import com.appointease.app.utils.exceptions.SQLNoRowsFoundException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.*;


/**
 * A data carrier class that contains database CRUD operations as static methods for the countries table.
 * A row returned from the table is packaged as a Country data-transfer-like object.
 * @param countryId
 * @param countryName
 * @param createDate
 * @param createdBy
 * @param lastUpdate
 * @param lastUpdateBy
 */
public record Country(int countryId, String countryName, Timestamp createDate, String createdBy, Timestamp lastUpdate, String lastUpdateBy)  {
    /**
     * Gets all rows from countries table.
     *
     * @return Observable List of Country objects.
     */
    public static ObservableList<Country> selectAllDB() {
        ObservableList<Country> countryList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM countries";
        try {
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new SQLNoRowsFoundException();
            } else {
                do {
                    Country country = new Country(
                        rs.getInt("Country_ID"),
                        rs.getString("Country"),
                        rs.getTimestamp("Create_Date"),
                        rs.getString("Created_By"),
                        rs.getTimestamp("Last_Update"),
                        rs.getString("Last_Updated_By")
                    );
                    countryList.add(country);
                } while (rs.next());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (SQLNoRowsFoundException e) {
            DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null, e.getMessage());
        }
        return countryList;
    }

    /**
     * Gets row for a specific country ID.
     *
     * @param countryId Country ID.
     * @return Country object.
     */
    public static Country selectIdDB(int countryId) {
        String sql = "SELECT * FROM countries WHERE Country_ID = ?";
        Country country = null;
        try {
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
            ps.setInt(1, countryId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new SQLBindVariableValuesNotFoundException();
            }
            country = new Country(
                    rs.getInt("Country_ID"),
                    rs.getString("Country"),
                    rs.getTimestamp("Create_Date"),
                    rs.getString("Created_By"),
                    rs.getTimestamp("Last_Update"),
                    rs.getString("Last_Updated_By")
            );
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (SQLBindVariableValuesNotFoundException e) {
            DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null, e.getMessage());
        }
        return country;
    }

    /**
     * Gets row for a specific country name.
     *
     *
     * @param countryName Country name.
     * @return Country object.
     */
    public static Country selectNameDB(String countryName) {
        String sql = "SELECT * FROM countries WHERE Country = ?";
        Country country = null;
        try {
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
            ps.setString(1, countryName);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new SQLBindVariableValuesNotFoundException();
            }
            country = new Country(
                    rs.getInt("Country_ID"),
                    rs.getString("Country"),
                    rs.getTimestamp("Create_Date"),
                    rs.getString("Created_By"),
                    rs.getTimestamp("Last_Update"),
                    rs.getString("Last_Updated_By")
            );
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (SQLBindVariableValuesNotFoundException e) {
            DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null, e.getMessage());
        }
        return country;
    }

    /**
     * Inserts a new row into the countries table.
     * Sets the Last Update as the create date and sets the Last Update By as the created by.
     *
     * @param countryName Country name.
     * @param createDate Create date.
     * @param createdBy Created by.
     * @return Country object.
     */
    public static Country insertDB(String countryName, Timestamp createDate, String createdBy) {
        String sql = "INSERT INTO countries (Country, Create_Date, Created_By, Last_Update, Last_Updated_By) VALUES (?, ?, ?, ?, ?)";
        int rowsAffected = 0;
        Country country = null;
        try {
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, countryName);
            ps.setTimestamp(2, createDate);
            ps.setString(3, createdBy);
            ps.setTimestamp(4, createDate);
            ps.setString(5, createdBy);
            rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLNoRowsAffectedException();
            }
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            country = selectIdDB(rs.getInt(1));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (SQLNoRowsAffectedException e) {
            DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null, e.getMessage());
        }
        return country;
    }

    /**
     * Updates a row in the countries table.
     *
     * @param countryId Country ID.
     * @param newCountryName New Country name.
     * @param lastUpdate Last update.
     * @param lastUpdateBy Last update by.
     * @return Country object.
     */
    public static Country updateDB(int countryId, String newCountryName, Timestamp lastUpdate, String lastUpdateBy) {
        String sql = "UPDATE countries SET Country = ?, Last_Update = ?, Last_Updated_By = ? WHERE Country_ID = ?";
        int rowsAffected = 0;
        Country country = null;
        try {
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
            ps.setString(1, newCountryName);
            ps.setTimestamp(2, lastUpdate);
            ps.setString(3, lastUpdateBy);
            ps.setInt(4, countryId);
            rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLNoRowsAffectedException();
            }
            country = selectIdDB(countryId);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (SQLNoRowsAffectedException e) {
            DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null, e.getMessage());
        }
        return country;
    }

    /**
     * Deletes a row from the countries table.
     *
     * @param countryId Country ID.
     * @return True if row was deleted, false if not.
     */
    public static boolean deleteDB(int countryId) {
        String sql = "DELETE FROM countries WHERE Country_ID = ?";
        int rowsAffected = 0;
        try {
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
            ps.setInt(1, countryId);
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

