package com.liquibase.application.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.Period;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.util.Pair;
import reactor.core.publisher.Flux;

/**
 * Utility class with methods working with {@link Date}s.
 */
public class DateTimeUtil {
    /**
     * Time format with Hours:Minutes.
     */
    public static final String HH_MM = "HH:mm";

    /**
     * Date format with dayOfMonth-Month-year.
     */
    public static final String DD_MM_YYYY = "dd-MM-yyyy";

    /**
     * Common text format for a date.
     */
    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    /**
     * Default flex value, which is used for fixed time drivers, with no flex explicitly specified.
     */
    public static final LocalTime DEFAULT_FLEX = LocalTime.of(0, 14);

    /**
     * How many calendar days in a calendar week (i.e. 7)
     */
    public static final int DAYS_IN_WEEK = 7;

    private DateTimeUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Generator of {@link Stream} of days (in form of {@link LocalDate}s) between given two (both ends inclusive).
     *
     * @param from first day
     * @param to   last day
     * @return Flux of days between given two
     */
    public static Stream<LocalDate> allDaysBetween(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("from can't be later than to");
        }

        return Stream.iterate(from, to::isAfter, date -> date.plusDays(1));
    }

    /**
     * Calculates a count of days between given two, inclusive.
     * E.g. if <code>from</code> is equal to <code>to</code> the answer will be 1.
     *
     * @param from from date
     * @param to   to date
     * @return count of days between given dates
     */
    public static int countAllDaysBetween(LocalDate from, LocalDate to) {
        return (int) ChronoUnit.DAYS.between(from, to) + 1;
    }

    /**
     * Counts a number of weeks and days between two dates.
     *
     * @param earlier starting date
     * @param later   ending date
     * @return an array, where the first element is a number of weeks, and a second element - of days
     */
    public static int[] weeksAndDaysBetween(Date earlier, Date later) {
        int daysBetween = (int) ChronoUnit.DAYS.between(earlier.toInstant(), later.toInstant());
        int weeks = daysBetween / DAYS_IN_WEEK;
        int days = daysBetween % DAYS_IN_WEEK;

        return new int[] {weeks, days};
    }

    public static DateFormat getDayFormat() {
        return new SimpleDateFormat(YYYY_MM_DD);
    }

    /**
     * Returns a previous day relatively to provided date.
     *
     * @param date provided date
     * @return previous day
     */
    public static Date previousDay(Date date) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return calendar.getTime();
    }

    /**
     * Calculates start date of the specified Tesco financial week in the specified year.
     *
     * @param year            year of interest
     * @param tescoWeekNumber Tesco financial week number
     * @return start date of the specified week
     */
    public static LocalDate getStartDateOfTescoWeek(int year, int tescoWeekNumber) {
        return getStartDateOfFirstTescoWeek(year).plusWeeks((long) tescoWeekNumber - 1);
    }

    /**
     * Calculates Tesco financial week number based on the provided start date.
     *
     * @param startDate start date of interest
     * @return Tesco financial week
     * @throws IllegalArgumentException if the provided date is not a start date of a Tesco week
     */
    public static int getTescoWeekByStartDate(LocalDate startDate) {
        LocalDate startDateOfFirstWeek = getStartDateOfFirstTescoWeek(startDate.getYear());
        if (startDateOfFirstWeek.isAfter(startDate)) {
            startDateOfFirstWeek = getStartDateOfFirstTescoWeek(startDate.getYear() - 1);
        }

        int weeksBetween = (int) Math.abs(ChronoUnit.WEEKS.between(startDate, startDateOfFirstWeek));
        if (!startDateOfFirstWeek.plusWeeks(weeksBetween).equals(startDate)) {
            throw new IllegalArgumentException("Provided date is not a start date of Tesco week in the specified year");
        }
        return weeksBetween + 1;
    }

    /**
     * Calculates Tesco financial week number based on the provided day in this week.
     *
     * @param day day, for which we want to find Tesco week
     * @return Tesco financial week
     */
    public static int getTescoWeekByDayInWeek(LocalDate day) {
        final LocalDate weekStartDate = day.getDayOfWeek().equals(DayOfWeek.SUNDAY) ? day
                : day.minusDays(day.getDayOfWeek().getValue());
        return getTescoWeekByStartDate(weekStartDate);
    }

    /**
     * The first Tesco financial week starts with 1 day after the last Saturday in February.
     *
     * @param year year of interest
     * @return start date of the first Tesco financial week
     */
    private static LocalDate getStartDateOfFirstTescoWeek(int year) {
        LocalDate day = LocalDate.of(year, Month.MARCH, 1).minusDays(1);
        while (!day.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
            day = day.minusDays(1);
        }
        return day.plusDays(1);
    }

    /**
     * Calculates earliest and latest times based on base time and flex values.
     *
     * @param baseTime base time
     * @param flex     flexibility value
     * @return Pair of earliest and latest times (in this order)
     */
    public static Pair<LocalTime, LocalTime> calculateEarliestAndLatestTimes(LocalTime baseTime, LocalTime flex) {
        if (flex == null) {
            // if flex is null then we need to add 14 minutes to these drivers base start time calculation
            return Pair.of(baseTime, baseTime.plusMinutes(14));
        }
        long flexSeconds = ChronoUnit.SECONDS.between(LocalTime.of(0, 0), flex);
        return Pair.of(baseTime.minusSeconds(flexSeconds), baseTime.plusSeconds(flexSeconds));
    }

    /**
     * Calculates base time and flex based on earliest and latest times.
     *
     * @param earliestTime earliest time
     * @param latestTime   latest time
     * @return Pair of base time and flex (in this order)
     */
    public static Pair<LocalTime, LocalTime> calculateBaseTimeAndFlex(LocalTime earliestTime, LocalTime latestTime) {
        long flexSeconds = ChronoUnit.SECONDS.between(earliestTime, latestTime) / 2;
        return Pair.of(latestTime.minusSeconds(flexSeconds), LocalTime.of(0, 0).plusSeconds(flexSeconds));
    }

    public static String toString(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern(YYYY_MM_DD));
    }

    public static LocalTime parseLocalTime(String text) {
        return LocalTime.parse(text, DateTimeFormatter.ofPattern(HH_MM));
    }

    /**
     * Checks fiven dates if they are in same year. Used to find out if a booking is in same year.
     *
     * @param startDate start date of booking
     * @param endDate   end date of booking
     * @return
     */
    public static boolean checkIfBookingDatesInDifferentBookingYear(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return false;
        }
        Integer startDateTescoYear = getTescoBookingYear(startDate);
        Integer endDateTescoYear = getTescoBookingYear(endDate);
        return !startDateTescoYear.equals(endDateTescoYear);
    }

    /**
     * Returns Tesco booking year for given date.
     *
     * @param date BookingDate
     * @return TescoBookingYear
     */
    public static Integer getTescoBookingYear(LocalDate date) {
        return date.isAfter(LocalDate.of(date.getYear(), 3, 31)) ? date.getYear() : date.getYear() - 1;
    }

    /**
     * Returns Tesco financial year for the given date.
     *
     * @param date Financial date
     * @return Tesco financial year
     */
    public static Integer getTescoFinancialYear(LocalDate date) {
        LocalDate startDateOfFirstTescoWeek = getStartDateOfFirstTescoWeek(date.getYear());
        return date.isAfter(startDateOfFirstTescoWeek.minusDays(1)) ? date.getYear() : date.getYear() - 1;
    }

    /**
     * Calculates Holiday Budget financial year start date.
     * Tesco financial year begins on the Sunday following the last Saturday in February.
     */
    public static LocalDate getHolidayBudgetFinancialStartDate(Integer year) {
        return calculateHolidayBudgetFinancialYear(year).plusDays(1);
    }

    /**
     * Calculates Holiday Budget financial year's end date, which is the last Saturday of February.
     */
    public static LocalDate getHolidayBudgetFinancialEndDate(Integer year) {
        return calculateHolidayBudgetFinancialYear(year + 1);
    }

    /**
     * Validates if given currentDate is between the Tesco financial start date and end date
     */
    public static boolean isCurrentDateInTescoFinancialYear(LocalDate currentDate, LocalDate finYearStartDate,
            LocalDate finYearEndDate) {
        return (currentDate.isAfter(finYearStartDate) || currentDate.equals(finYearStartDate)) &&
                (currentDate.isBefore(finYearEndDate) || currentDate.equals(finYearEndDate));
    }

    private static LocalDate calculateHolidayBudgetFinancialYear(Integer year) {
        YearMonth ym = YearMonth.of(year, Month.FEBRUARY);
        LocalDate endDate = ym.atEndOfMonth();
        DayOfWeek day = endDate.getDayOfWeek();
        int lastDay = day.getValue();

        if (lastDay < 6) {
            endDate = endDate.minusDays(lastDay + 1L);
        } else if (lastDay > 6) {
            endDate = endDate.minusDays(1L);
        }

        return endDate;
    }

    /**
     * @param calendarYear
     * @return
     */
    public static int getYearDiffWithCurrentDate(int calendarYear) {
        LocalDate currentDate = LocalDate.now(); // current date
        LocalDate givenDate = LocalDate.of(calendarYear, 1, 1); // given year's date
        Period diff = Period.between(givenDate, currentDate); // difference between two dates
        return diff.getYears();
    }

    /**
     * Transforms dates period into the list of TescoWeek objects, representing Tesco financial year and week.
     *
     * @param startDate start of the period
     * @param endDate   end of the period
     * @return Flux of TescoWeek objects
     */
    public static Flux<TescoWeek> getTescoWeeks(LocalDate startDate, LocalDate endDate) {
        LocalDate startDateOfStartWeek = DateTimeUtil.getStartDateOfTescoWeek(
                DateTimeUtil.getTescoFinancialYear(startDate), DateTimeUtil.getTescoWeekByDayInWeek(startDate));

        List<TescoWeek> tescoWeeks = new ArrayList<>();
        for (LocalDate d = startDateOfStartWeek; d.isBefore(endDate.plusDays(1)); d = d.plusDays(7)) {
            tescoWeeks.add(new TescoWeek(DateTimeUtil.getTescoFinancialYear(d),
                    DateTimeUtil.getTescoWeekByStartDate(d)));
        }

        return Flux.fromIterable(tescoWeeks);
    }

    /**
     * Tesco financial week representation.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TescoWeek {

        private int financialYear;

        private int weekNumber;
    }
}
