package com.appointease.app.model;

import com.appointease.app.utils.DialogHelpers;
import com.appointease.app.utils.JDBC;
import com.appointease.app.utils.exceptions.SQLNoRowsAffectedException;
import com.appointease.app.utils.exceptions.SQLNoRowsFoundException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.*;

/**
 * A data carrier class that contains database CRUD operations as static methods for the first_level_divisions table.
 * A row returned from the table is packaged as a FirstLevelDivision data-transfer-like object.
 * @param divisionId
 * @param divisionName
 * @param createDate
 * @param createdBy
 * @param lastUpdate
 * @param lastUpdateBy
 * @param country
 */
public record FirstLevelDivision(int divisionId, String divisionName, Timestamp createDate, String createdBy, Timestamp lastUpdate, String lastUpdateBy, Country country) {
    /**
     * Gets all rows from first_level_divisions table.
     *
     * @return Observable List of FirstLevelDivision objects.
     */
    public static ObservableList<FirstLevelDivision> selectAllDB() {
        ObservableList<FirstLevelDivision> divisionList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM first_level_divisions";
        try {
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new SQLNoRowsFoundException();
            } else {
                do {
                    divisionList.add(new FirstLevelDivision(
                        rs.getInt("Division_ID"),
                        rs.getString("Division"),
                        rs.getTimestamp("Create_Date"),
                        rs.getString("Created_By"),
                        rs.getTimestamp("Last_Update"),
                        rs.getString("Last_Updated_By"),
                        Country.selectIdDB(rs.getInt("Country_ID"))
                    ));
                } while (rs.next());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (SQLNoRowsFoundException e) {
            DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null, e.getMessage());
        }
        return divisionList;
    }

    /**
     * Gets row for a specific division ID.
     *
     * @param divisionId Division ID.
     * @return FirstLevelDivision object.
     */
    public static FirstLevelDivision selectIdDB(int divisionId) {
        String sql = "SELECT * FROM first_level_divisions WHERE Division_ID = ?";
        FirstLevelDivision division = null;
        try {
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
            ps.setInt(1, divisionId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new SQLNoRowsFoundException();
            }
            division = new FirstLevelDivision(
                    rs.getInt("Division_ID"),
                    rs.getString("Division"),
                    rs.getTimestamp("Create_Date"),
                    rs.getString("Created_By"),
                    rs.getTimestamp("Last_Update"),
                    rs.getString("Last_Updated_By"),
                    Country.selectIdDB(rs.getInt("Country_ID"))
            );
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (SQLNoRowsFoundException e) {
            DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null, e.getMessage());
        }
        return division;
    }


    /**
     * Gets row for a specific division name.
     *
     * @param divisionName Division name.
     * @return FirstLevelDivision object.
     */
    public static ObservableList<FirstLevelDivision> selectNameDB(String divisionName) {
        ObservableList<FirstLevelDivision> divisionList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM first_level_divisions WHERE Division = ?";
        try {
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
            ps.setString(1, divisionName);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new SQLNoRowsFoundException();
            } else {
                do {
                    divisionList.add(new FirstLevelDivision(
                        rs.getInt("Division_ID"),
                        rs.getString("Division"),
                        rs.getTimestamp("Create_Date"),
                        rs.getString("Created_By"),
                        rs.getTimestamp("Last_Update"),
                        rs.getString("Last_Updated_By"),
                        Country.selectIdDB(rs.getInt("Country_ID"))
                    ));
                } while (rs.next());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (SQLNoRowsFoundException e) {
            DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null, e.getMessage());
        }
        return divisionList;
    }

    /**
     * Inserts a new row into the first_level_divisions table.
     * Sets the Last Update as the create date and sets the Last Update By as the created by.
     *
     * @param divisionName Country name.
     * @param createDate Create date.
     * @param createdBy Created by.
     * @param countryId Country ID.
     * @return FirstLevelDivision object.
     */
    public static FirstLevelDivision insertDB(String divisionName, Timestamp createDate, String createdBy, int countryId) {
        String sql = "INSERT INTO first_level_divisions (Division, Create_Date, Created_By, Last_Update, Last_Updated_By, Country_ID) VALUES (?, ?, ?, ?, ?, ?)";
        int rowsAffected = 0;
        FirstLevelDivision division = null;
        try {
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, divisionName);
            ps.setTimestamp(2, createDate);
            ps.setString(3, createdBy);
            ps.setTimestamp(4, createDate);
            ps.setString(5, createdBy);
            ps.setInt(6, countryId);
            rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLNoRowsAffectedException();
            }
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            division = selectIdDB(rs.getInt(1));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (SQLNoRowsAffectedException e) {
            DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null, e.getMessage());
        }
        return division;
    }

    /**
     * Updates a row in the first_level_divisions table.
     *
     * @param divisionId Division ID.
     * @param newDivisionName New Division name.
     * @param lastUpdate Last update.
     * @param lastUpdateBy Last update by.
     * @param newCountryId New Country ID.
     * @return FirstLevelDivision object.
     */
    public static FirstLevelDivision updateDB(int divisionId, String newDivisionName, Timestamp lastUpdate, String lastUpdateBy, int newCountryId) {
        String sql = "UPDATE first_level_divisions SET Division = ?, Last_Update = ?, Last_Updated_By = ?, Country_ID = ? WHERE Division_ID = ?";
        int rowsAffected = 0;
        FirstLevelDivision division = null;
        try {
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
            ps.setString(1, newDivisionName);
            ps.setTimestamp(2, lastUpdate);
            ps.setString(3, lastUpdateBy);
            ps.setInt(4, divisionId);
            ps.setInt(5, newCountryId);
            rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLNoRowsAffectedException();
            }
            division = selectIdDB(divisionId);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (SQLNoRowsAffectedException e) {
            DialogHelpers.displayDialog(Alert.AlertType.ERROR, null, null, e.getMessage());
        }
        return division;
    }

    /**
     * Deletes a row from the first_level_divisions table.
     *
     * @param divisionId Division ID.
     * @return True if row was deleted, false if not.
     */
    public static boolean deleteDB(int divisionId) {
        String sql = "DELETE FROM first_level_divisions WHERE Division_ID = ?";
        int rowsAffected = 0;
        try {
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
            ps.setInt(1, divisionId);
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
