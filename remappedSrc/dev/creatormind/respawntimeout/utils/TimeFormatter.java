package dev.creatormind.respawntimeout.utils;

import java.util.concurrent.TimeUnit;


public class TimeFormatter {

    /**
     * Transforms the given time input into an HH:mm:ss string format.
     * @param time The time to transform.
     * @param unit The unit of time that the given time is in.
     * @return The formatted string.
     */
    public static String format(long time, TimeUnit unit) {
        final long hours = unit.toHours(time);
        final long minutes = unit.toMinutes(time) - TimeUnit.HOURS.toMinutes(hours);
        final long seconds = unit.toSeconds(time) - TimeUnit.MINUTES.toSeconds(minutes) - TimeUnit.HOURS.toSeconds(hours);

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

}
