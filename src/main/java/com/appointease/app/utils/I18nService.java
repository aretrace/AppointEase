package com.appointease.app.utils;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * A service interface for internationalization.
 */
public interface I18nService {
    /**
     * Gets the resource bundle for the given exhibit name.
     *
     * @param exhibitName The exhibit name.
     * @return The resource bundle.
     */
    static ResourceBundle getL10n(String exhibitName) {
        ResourceBundle rb;
        try {
            rb = ResourceBundle.getBundle(exhibitName, Locale.getDefault());
        } catch (Exception e) {
            e.printStackTrace();
            rb = ResourceBundle.getBundle(exhibitName, new Locale("en"));
        }
        assert rb != null;
        return rb;
    }

}
