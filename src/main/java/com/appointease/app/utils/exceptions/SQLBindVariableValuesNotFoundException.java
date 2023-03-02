package com.appointease.app.utils.exceptions;

import com.appointease.app.utils.I18nService;

import java.util.ResourceBundle;
/**
 * Exception thrown when a SQL statement has bind variables but no values are provided.
 */
public class SQLBindVariableValuesNotFoundException extends Exception {
    private static final ResourceBundle rb = I18nService.getL10n("ExceptionMessages");

    public SQLBindVariableValuesNotFoundException() {
        super(rb.getString("SQLBindVariableValuesNotFoundException"));
    }

    public SQLBindVariableValuesNotFoundException(String message) {
        super(message);
    }
}

