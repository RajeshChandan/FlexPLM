package com.lowes.notification.exceptions;

/**
 * Common exception for notifications
 */
public class NotificationRuntimeException extends Exception {

    public NotificationRuntimeException(Exception e) {
        super(e);
    }
}
