package com.appointease.app.utils.exceptions;

import com.appointease.app.utils.I18nService;

import java.util.ResourceBundle;

/**
 * Exception thrown when a SQL statement has no rows affected.
 */
public class SQLNoRowsAffectedException extends Exception {

    private static final ResourceBundle rb = I18nService.getL10n("ExceptionMessages");

    public SQLNoRowsAffectedException() {
        super(rb.getString("SQLNoRowsAffectedException"));
    }

    public SQLNoRowsAffectedException(String message) {
        super(message);
    }
}
