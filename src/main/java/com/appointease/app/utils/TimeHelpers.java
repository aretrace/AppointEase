package com.appointease.app.utils;

import java.sql.Timestamp;
import java.time.*;

/**
 * A helper interface for time conversions.
 */
public interface TimeHelpers {
    /**
     * Converts a timestamp from the system time zone to UTC.
     *
     * @param timestamp the timestamp to convert
     * @return the converted timestamp
     */
    static Timestamp fromDBtoSysTZ(Timestamp timestamp) {
        Instant utcDateTime  = timestamp.toInstant();
        LocalDateTime systemDateTime = LocalDateTime.ofInstant(utcDateTime, ZoneId.systemDefault());
        return Timestamp.valueOf(systemDateTime);
    }
}