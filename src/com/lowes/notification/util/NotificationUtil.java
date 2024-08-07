package com.lowes.notification.util;

import org.apache.logging.log4j.Logger;
import wt.log4j.LogR;
import wt.org.WTUser;
import wt.util.WTException;

import java.util.Objects;

/**
 * Utility methods used here for custom notifications
 */
public class NotificationUtil {

    private static final Logger LOGGER = LogR.getLogger(NotificationUtil.class.getName());

    /**
     * To hide the public implicit one
     */
    private NotificationUtil() {
    }

    /**
     * @param user WTUser
     * @return String
     * @throws WTException WTException
     */
    public static String formatUser(WTUser user) throws WTException {

        LOGGER.debug("formatUser- Start");
        String formatString = "";
        if (Objects.isNull(user)) {
            return formatString;
        }
        formatString = user.getFullName() + " (" + user.getEMail() + ")";

        LOGGER.debug("formatUser-  formatString {} ", formatString);
        LOGGER.debug("formatUser- End");
        return formatString;
    }
}
