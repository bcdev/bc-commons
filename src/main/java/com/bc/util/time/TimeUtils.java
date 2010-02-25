package com.bc.util.time;

import com.bc.util.string.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TimeUtils {

    public static final String ISO8601_YMDHMS_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final Date INVALID_DATE = new Date(0);

    public static boolean isSameDay(Date date_1, Date date_2) {
        if (date_1 == null || date_2 == null) {
            return false;
        }

        final Calendar cal_1 = GregorianCalendar.getInstance();
        cal_1.setTime(date_1);
        final Calendar cal_2 = GregorianCalendar.getInstance();
        cal_2.setTime(date_2);

        //noinspection RedundantIfStatement
        if (cal_1.get(Calendar.YEAR) == cal_2.get(Calendar.YEAR)
                && cal_1.get(Calendar.MONTH) == cal_2.get(Calendar.MONTH)
                && cal_1.get(Calendar.DAY_OF_MONTH) == cal_2.get(Calendar.DAY_OF_MONTH)) {
            return true;

        }
        return false;
    }

    public static boolean isSameSecond(Date date_1, Date date_2) {
        if (date_1 == null || date_2 == null) {
            return false;
        }

        final Calendar cal_1 = GregorianCalendar.getInstance();
        cal_1.setTime(date_1);
        final Calendar cal_2 = GregorianCalendar.getInstance();
        cal_2.setTime(date_2);

        //noinspection RedundantIfStatement
        if (cal_1.get(Calendar.YEAR) == cal_2.get(Calendar.YEAR)
                && cal_1.get(Calendar.MONTH) == cal_2.get(Calendar.MONTH)
                && cal_1.get(Calendar.DAY_OF_MONTH) == cal_2.get(Calendar.DAY_OF_MONTH)
                && cal_1.get(Calendar.HOUR_OF_DAY) == cal_2.get(Calendar.HOUR_OF_DAY)
                && cal_1.get(Calendar.MINUTE) == cal_2.get(Calendar.MINUTE)
                && cal_1.get(Calendar.SECOND) == cal_2.get(Calendar.SECOND)) {
            return true;
        }

        return false;
    }

    /**
     * Parses the given text value as UTC date value. The method recognizes the format the ISO 8601 date/time format
     * <code>YYYY-MM-DD [hh:mm:ss.S]</code>.
     *
     * @param dateString the text to be parsed, must not be null or empty
     * @return a date object, never null
     * @throws java.text.ParseException o invalid pattern format
     */
    public static Date parseISO8601Date(String dateString) throws ParseException {
        return parseDateByPattern(dateString, ISO8601_PATTERNS);
    }

    public static Date parseISO8601Date(String dateString, boolean isUTC) throws ParseException {
        return parseDateByPattern(dateString, ISO8601_PATTERNS, isUTC);
    }

    public static Date parseISO8601StartDate(String dateString) throws ParseException {
        final Date date = parseDateByPattern(dateString, ISO8601_PATTERNS, false);
        final Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date parseISO8601EndDate(String dateString) throws ParseException {
        final Date date = parseDateByPattern(dateString, ISO8601_PATTERNS, false);
        final Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static boolean isValidISO8601String(String dateString) {
        boolean result = true;
        try {
            parseISO8601Date(dateString);
        } catch (ParseException e) {
            result = false;
        } catch (IllegalArgumentException e) {
            result = false;
        }
        return result;
    }

    /**
     * Formats the given UTC date value using the ISO 8601 format <code>YYYY-MM-DD [hh:mm:ss.S]</code>.
     *
     * @param date the date to be formatted, must not be null
     * @return a text string, never null
     */
    public static String formatISO8601Date(Date date) {
        return formatISO8601Date(date, true);
    }

    public static String formatISO8601Date(Date date, boolean isUtc) {
        if (date == null) {
            throw new IllegalArgumentException("argument date is null");
        }
        Calendar calendar;
        if (isUtc) {
            calendar = createUTCCalendar();
        } else {
            calendar = GregorianCalendar.getInstance();
            calendar.clear();
        }
        calendar.setTime(date);

        final String pattern = getPattern(calendar);
        final DateFormat format = new SimpleDateFormat(pattern);
        format.setTimeZone(calendar.getTimeZone());
        return format.format(date);
    }

    public static String getPattern(final Calendar calendar) {
        final String pattern;
        if (calendar.get(Calendar.MILLISECOND) != 0) {
            pattern = ISO8601_YMDHMSM_PATTERN;
        } else if (calendar.get(Calendar.SECOND) != 0) {
            pattern = ISO8601_YMDHMS_PATTERN;
        } else if (calendar.get(Calendar.HOUR_OF_DAY) != 0 || calendar.get(Calendar.MINUTE) != 0) {
            pattern = ISO8601_YMDHM_PATTERN;
        } else {
            pattern = ISO8601_YMD_PATTERN;
        }
        return pattern;
    }

    public static Calendar createUTCCalendar() {
        final Calendar instance = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        instance.clear();
        return instance;
    }

    /**
     * Parses the given text value as ftp logfile date value. The method recognizes the format used by ftp logfiles
     * <code>dd/MMM/yyyy [:hh:mm:ss.S]</code>.
     *
     * @param dateString the text to be parsed, must not be null or empty
     * @return a date object, never null
     * @throws ParseException
     */
    public static Date parseFtpLogDate(String dateString) throws ParseException {
        final String[] patterns = {
                LOG_YMDHMS_PATTERN, LOG_YMDHM_PATTERN, LOG_YMD_PATTERN
        };

        return parseDateByPattern(dateString, patterns);
    }

    public static Date parseHdfEosDateString(String hdfEosDate) throws ParseException {
        final String[] patterns = {
                HDFEOS_YMDHMSM_PATTERN, HDFEOS_YMD_PATTERN
        };
        return parseDateByPattern(hdfEosDate, patterns, false);
    }

    public static Date parseProductDate(String dateString) throws ParseException {
        final String[] patterns = {
                PRODUCT_PATTERN
        };
        return parseDateByPattern(dateString, patterns, true);
    }

    ///////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ///////////////////////////////////////////////////////////////////////////

    private static final String ISO8601_YMDHMSM_PATTERN = "yyyy-MM-dd HH:mm:ss.S";
    private static final String ISO8601_YMDHM_PATTERN = "yyyy-MM-dd HH:mm";
    private static final String ISO8601_YMD_PATTERN = "yyyy-MM-dd";
    private static final String[] ISO8601_PATTERNS = new String[]{
            ISO8601_YMDHMSM_PATTERN, ISO8601_YMDHMS_PATTERN, ISO8601_YMDHM_PATTERN, ISO8601_YMD_PATTERN
    };

    private static final String LOG_YMDHMS_PATTERN = "dd/MMM/yyyy:HH:mm:ss";
    private static final String LOG_YMDHM_PATTERN = "dd/MMM/yyyy:HH:mm";
    private static final String LOG_YMD_PATTERN = "dd/MMM/yyyy";

    private static final String HDFEOS_YMDHMSM_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.S";
    private static final String HDFEOS_YMD_PATTERN = "yyyy-MM-dd";

    private static final String PRODUCT_PATTERN = "dd-MMM-yyyy HH:mm:ss";

    private static final int YEAR_2K_THRESHOLD = 100;
    private static final int YEAR_2K = 2000;

    private static Date parseDateByPattern(String dateString, final String[] patterns) throws ParseException {
        return parseDateByPattern(dateString, patterns, true);
    }

    private static Date parseDateByPattern(String dateString, final String[] patterns, boolean isUTC) throws ParseException {
        if (StringUtils.isEmpty(dateString)) {
            throw new IllegalArgumentException("argument dateString is null");
        }
        final Calendar calendar;
        if (isUTC) {
            calendar = createUTCCalendar();
        } else {
            calendar = GregorianCalendar.getInstance();
        }
        Date result = null;
        ParseException lastError = null;

        for (int i = 0; i < patterns.length; i++) {
            final SimpleDateFormat format = new SimpleDateFormat(patterns[i], Locale.ENGLISH);
            format.setTimeZone(calendar.getTimeZone());
            try {
                result = format.parse(dateString);
                break;
            } catch (ParseException e) {
                lastError = e;
            }
        }
        if (result == null) {
            throw lastError;
        }
        calendar.clear();
        calendar.setTime(result);
        if (calendar.get(Calendar.YEAR) < YEAR_2K_THRESHOLD) {
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + YEAR_2K);
        }
        result = calendar.getTime();

        return result;
    }
}
