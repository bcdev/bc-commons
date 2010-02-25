package com.bc.util.time;

import junit.framework.TestCase;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@SuppressWarnings({"MagicNumber"})
public class TimeUtilsTest extends TestCase {

    public void testIsSameDay() {
        Date date_1 = getDate(2003, 8, 12, 9, 33, 55);
        Date date_2 = getDate(2003, 8, 12, 10, 22, 8);

        assertTrue(TimeUtils.isSameDay(date_1, date_2));
        assertTrue(TimeUtils.isSameDay(date_2, date_1));

        assertFalse(TimeUtils.isSameDay(date_1, null));
        assertFalse(TimeUtils.isSameDay(null, date_2));

        date_2 = getDate(2003, 8, 13, 10, 22, 8);
        assertFalse(TimeUtils.isSameDay(date_1, date_2));
        assertFalse(TimeUtils.isSameDay(date_2, date_1));

        date_2 = getDate(2003, 9, 12, 10, 22, 8);
        assertFalse(TimeUtils.isSameDay(date_1, date_2));
        assertFalse(TimeUtils.isSameDay(date_2, date_1));

        date_2 = getDate(2004, 8, 12, 10, 22, 8);
        assertFalse(TimeUtils.isSameDay(date_1, date_2));
        assertFalse(TimeUtils.isSameDay(date_2, date_1));
    }

    public void testIsSameSecond() {
        Date date_1 = getDate(2003, 8, 12, 10, 22, 8);
        Date date_2 = getDate(2003, 8, 12, 10, 22, 8);

        assertTrue(TimeUtils.isSameSecond(date_1, date_2));
        assertTrue(TimeUtils.isSameSecond(date_2, date_1));

        assertFalse(TimeUtils.isSameSecond(date_1, null));
        assertFalse(TimeUtils.isSameSecond(null, date_2));

        date_2 = getDate(2003, 8, 12, 10, 22, 7);
        assertFalse(TimeUtils.isSameSecond(date_1, date_2));
        assertFalse(TimeUtils.isSameSecond(date_2, date_1));

        date_2 = getDate(2003, 8, 12, 10, 22, 9);
        assertFalse(TimeUtils.isSameSecond(date_1, date_2));
        assertFalse(TimeUtils.isSameSecond(date_2, date_1));

        date_2 = getDate(2003, 8, 12, 10, 25, 9);
        assertFalse(TimeUtils.isSameSecond(date_1, date_2));
        assertFalse(TimeUtils.isSameSecond(date_2, date_1));
    }

    public void testParseISO8601DateInvalidArguments() throws ParseException {
        try {
            TimeUtils.parseISO8601Date(null);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }

        try {
            TimeUtils.parseISO8601Date("");
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testParseExpectedISODateString() throws ParseException {
        Date resDate = TimeUtils.parseISO8601Date("2003-04-18");
        assertNotNull(resDate);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(resDate);
        assertEquals(18, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(4 - 1, calendar.get(Calendar.MONTH));
        assertEquals(2003, calendar.get(Calendar.YEAR));
    }

    public void testLosslessISO8601Conversion() throws ParseException {
        assertEquals("2001-01-01", TimeUtils.formatISO8601Date(TimeUtils.parseISO8601Date("2001-1-1")));
        assertEquals("2001-08-21 14:28", TimeUtils.formatISO8601Date(TimeUtils.parseISO8601Date("2001-8-21 14:28")));
        assertEquals("2001-05-01 04:36:24.432",
                TimeUtils.formatISO8601Date(TimeUtils.parseISO8601Date("2001-5-1 4:36:24.432")));
        assertEquals("2001-11-23 17:00:03",
                TimeUtils.formatISO8601Date(TimeUtils.parseISO8601Date("2001-11-23 17:0:3")));
    }

    public void testUnparseableIsoDates() {
        assertUnparsableIsoDateString("bert + bibo");
        assertUnparsableIsoDateString("1.1.2003");
        assertUnparsableIsoDateString("01/01/01");
        assertUnparsableIsoDateString("1.22.05");
        assertUnparsableIsoDateString("1987.08");
        assertUnparsableIsoDateString("8-SEP-98");
    }

    public void testValidISO8601DateStrings() throws ParseException {
        assertIsoDateEquals(2001, 1, 1, "2001-1-1");
        assertIsoDateEquals(2004, 3, 1, "2004-3-1");
        assertIsoDateEquals(2004, 3, 1, "4-3-1");
        assertIsoDateEquals(2003, 3, 11, 13, 16, 0, 0, "2003-03-11 13:16");
        assertIsoDateEquals(2003, 3, 11, 13, 16, 54, 0, "2003-03-11 13:16:54");
        assertIsoDateEquals(2003, 3, 11, 13, 16, 54, 432, "2003-03-11 13:16:54.432");
    }

    public void testIsValidISO8601DateString() {
        assertTrue(TimeUtils.isValidISO8601String("2002-2-2"));
        assertTrue(TimeUtils.isValidISO8601String("5-4-2"));
        assertTrue(TimeUtils.isValidISO8601String("2003-03-11 13:16"));

        assertFalse(TimeUtils.isValidISO8601String(null));
        assertFalse(TimeUtils.isValidISO8601String(""));
        assertFalse(TimeUtils.isValidISO8601String("Hallo"));
        assertFalse(TimeUtils.isValidISO8601String("Du.Da-Hinten"));
    }

    public void testParseFtpLogDateInvalidArguments() throws ParseException {
        try {
            TimeUtils.parseFtpLogDate(null);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }

        try {
            TimeUtils.parseFtpLogDate("");
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testParseExpectedFtpLogDateString() throws ParseException {
        Date resDate = TimeUtils.parseFtpLogDate("01/Feb/2003");
        assertNotNull(resDate);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(resDate);
        assertEquals(1, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(2 - 1, calendar.get(Calendar.MONTH));
        assertEquals(2003, calendar.get(Calendar.YEAR));
    }

    public void testUnparseableFtpDates() {
        assertUnparsableFtpDateString("nasenmann");
        assertUnparsableFtpDateString("7-NOV-32");
        assertUnparsableFtpDateString("1945.08.31");
        assertUnparsableFtpDateString("5.22.02");
        assertUnparsableFtpDateString("1.1.2003");
    }

    public void testValidFtpDateStrings() throws ParseException {
        assertFtpDateEquals(2002, 1, 1, "01/Jan/2002");
        assertFtpDateEquals(2003, 2, 2, "02/Feb/2003");
        assertFtpDateEquals(2004, 3, 4, "04/Mar/2004");
        assertFtpDateEquals(2005, 4, 6, 21, 7, 23, 0, "06/Apr/2005:21:07:23");
        assertFtpDateEquals(2006, 5, 8, 22, 8, 24, 0, "08/May/2006:22:08:24");
    }

    public void testParseHdfEosDateString() throws ParseException {
        assertHdfEosDateEquals(2003, 2, 21, "2003-02-21");
        assertHdfEosDateEquals(2005, 11, 2, "2005-11-02");
        assertHdfEosDateEquals(2002, 2, 21, 10, 22, 34, 78, "2002-02-21T10:22:34.078");
        assertHdfEosDateEquals(2003, 3, 22, 11, 23, 45, 67, "2003-03-22T11:23:45.067");

        assertUnparsableHdfEosDateString("hu-hu-erTereps");
        assertUnparsableHdfEosDateString("2003/03/22Q11:23:45.067");
    }

    public void testParseProductDate() throws ParseException {
        String dateString_1 = "05-APR-2005 10:28:59";
        String dateString_2 = "11-NOV-2004 11:16:32";

        Date date = TimeUtils.parseProductDate(dateString_1);
        assertDateEquals(2005, 4, 5, 10, 28, 59, 0, date);

        date = TimeUtils.parseProductDate(dateString_2);
        assertDateEquals(2004, 11, 11, 11, 16, 32, 0, date);
    }

    public void testParseISO8601Date_NotUTC() throws ParseException {
        Date date = TimeUtils.parseISO8601Date("2005-07-21 08:09:10.234", false);
        assertDateEquals(2005, 7, 21, 6, 9, 10, 234, date);

        date = TimeUtils.parseISO8601Date("2004-11-16 22:23:24.338", false);
        assertDateEquals(2004, 11, 16, 21, 23, 24, 338, date);
    }

    public void testParseISO8601StartDate() throws ParseException {
        Date date = TimeUtils.parseISO8601StartDate("2003-05-19 11:05:23.334");
        assertDateEquals(2003, 5, 19, 0, 0, 0, 0, date, false);

        date = TimeUtils.parseISO8601StartDate("2004-11-02 22:18:23.334");
        assertDateEquals(2004, 11, 2, 0, 0, 0, 0, date, false);
    }

    public void testParseISO8601EndDate() throws ParseException {
        Date date = TimeUtils.parseISO8601EndDate("2003-05-19 11:05:23.334");
        assertDateEquals(2003, 5, 19, 23, 59, 59, 0, date, false);

        date = TimeUtils.parseISO8601EndDate("2004-11-02 22:18:23.334");
        assertDateEquals(2004, 11, 2, 23, 59, 59, 0, date, false);
    }

    public void testInvalidDateEquals() {
        final Date invalid = new Date(0);
        assertTrue(invalid.equals(TimeUtils.INVALID_DATE));
        assertTrue(TimeUtils.INVALID_DATE.equals(invalid));

        final Date valid = new Date(1);
        assertFalse(valid.equals(TimeUtils.INVALID_DATE));
        assertFalse(TimeUtils.INVALID_DATE.equals(valid));
    }

    ///////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ///////////////////////////////////////////////////////////////////////////

    private static Date getDate(int year, int month, int day, int hour, int minute, int second) {
        Calendar cal = GregorianCalendar.getInstance();

        cal.set(year, month, day, hour, minute, second);

        return cal.getTime();
    }

    @SuppressWarnings({"EmptyCatchBlock"})
    private void assertUnparsableIsoDateString(final String dateString) {
        try {
            assertNull(TimeUtils.parseISO8601Date(dateString));
            fail();
        } catch (ParseException expected) {
        }
    }

    private void assertIsoDateEquals(int year, int month, int day, String dateString) throws ParseException {
        Date resDate = TimeUtils.parseISO8601Date(dateString);
        assertDateEquals(year, month, day, 0, 0, 0, 0, resDate);
    }

    private void assertIsoDateEquals(int year, int month, int day, int hour, int min, int sec, int millis, String dateString) throws ParseException {
        Date resDate = TimeUtils.parseISO8601Date(dateString);
        assertDateEquals(year, month, day, hour, min, sec, millis, resDate);
    }

    private void assertFtpDateEquals(int year, int month, int day, String dateString) throws ParseException {
        Date resDate = TimeUtils.parseFtpLogDate(dateString);
        assertDateEquals(year, month, day, 0, 0, 0, 0, resDate);
    }

    private void assertFtpDateEquals(int year, int month, int day, int hour, int min, int sec, int millis, String dateString) throws ParseException {
        Date resDate = TimeUtils.parseFtpLogDate(dateString);
        assertDateEquals(year, month, day, hour, min, sec, millis, resDate);
    }

    private void assertHdfEosDateEquals(int year, int month, int day, String dateString) throws ParseException {
        Date resDate = TimeUtils.parseHdfEosDateString(dateString);
        assertDateEquals(year, month, day, 0, 0, 0, 0, resDate, false);
    }

    private void assertHdfEosDateEquals(int year, int month, int day, int hour, int min, int sec, int millis, String dateString) throws ParseException {
        Date resDate = TimeUtils.parseHdfEosDateString(dateString);
        assertDateEquals(year, month, day, hour, min, sec, millis, resDate, false);
    }

    @SuppressWarnings({"EmptyCatchBlock"})
    private void assertUnparsableHdfEosDateString(final String dateString) {
        try {
            assertNull(TimeUtils.parseHdfEosDateString(dateString));
            fail();
        } catch (ParseException expected) {
        }
    }


    private void assertDateEquals(int year, int month, int day, int hour, int min, int sec, int millis,
                                  Date testDate) {
        assertDateEquals(year, month, day, hour, min, sec, millis, testDate, true);
    }

    private void assertDateEquals(int year, int month, int day, int hour, int min, int sec, int millis,
                                  Date testDate, boolean isUtc) {

        assertNotNull(testDate);
        Calendar calendar;
        if (isUtc) {
            calendar = TimeUtils.createUTCCalendar();
        } else {
            calendar = GregorianCalendar.getInstance();
        }
        calendar.setTime(testDate);
        assertEquals(year, calendar.get(Calendar.YEAR));
        assertEquals(month - 1, calendar.get(Calendar.MONTH));
        assertEquals(day, calendar.get(Calendar.DATE));
        assertEquals(hour, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(min, calendar.get(Calendar.MINUTE));
        assertEquals(sec, calendar.get(Calendar.SECOND));
        assertEquals(millis, calendar.get(Calendar.MILLISECOND));
    }


    @SuppressWarnings({"EmptyCatchBlock"})
    private void assertUnparsableFtpDateString(final String dateString) {
        try {
            assertNull(TimeUtils.parseFtpLogDate(dateString));
            fail();
        } catch (ParseException expected) {
        }
    }
}
