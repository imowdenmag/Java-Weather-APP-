package weatherapp.util;

import java.time.LocalTime;

/**
 * Determines the current time-of-day period for dynamic background theming.
 *
 * <p>Time periods are defined as:
 * <ul>
 *   <li>MORNING   05:00 - 11:59</li>
 *   <li>AFTERNOON 12:00 - 16:59</li>
 *   <li>EVENING   17:00 - 19:59</li>
 *   <li>NIGHT     20:00 - 04:59</li>
 * </ul>
 * </p>
 *
 * @author Owden Magnusen
 */
public final class TimeOfDayUtil {

    // Boundary hours for each period
    private static final int MORNING_START   = 5;
    private static final int AFTERNOON_START = 12;
    private static final int EVENING_START   = 17;
    private static final int NIGHT_START     = 20;

    private TimeOfDayUtil() {}

    /**
     * Represents the four time-of-day periods used for dynamic backgrounds.
     */
    public enum Period {
        MORNING,
        AFTERNOON,
        EVENING,
        NIGHT
    }

    /**
     * Returns the current time-of-day period based on system clock.
     *
     * @return the {@link Period} that corresponds to the current local time
     */
    public static Period getCurrentPeriod() {
        return getPeriodForTime(LocalTime.now());
    }

    /**
     * Returns the time-of-day period for a given {@link LocalTime}.
     * Separated from {@link #getCurrentPeriod()} to allow unit testing.
     *
     * @param time the time to evaluate
     * @return corresponding {@link Period}
     */
    public static Period getPeriodForTime(LocalTime time) {
        int hour = time.getHour();

        if (hour >= MORNING_START && hour < AFTERNOON_START) {
            return Period.MORNING;
        } else if (hour >= AFTERNOON_START && hour < EVENING_START) {
            return Period.AFTERNOON;
        } else if (hour >= EVENING_START && hour < NIGHT_START) {
            return Period.EVENING;
        } else {
            return Period.NIGHT;
        }
    }

    /**
     * Returns a human-friendly label for a given period.
     *
     * @param period the time-of-day period
     * @return label string e.g. "Morning", "Night"
     */
    public static String periodLabel(Period period) {
        switch (period) {
            case MORNING:   return "Morning";
            case AFTERNOON: return "Afternoon";
            case EVENING:   return "Evening";
            default:        return "Night";
        }
    }
}
