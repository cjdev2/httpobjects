package org.httpobjects;

import org.junit.Assert;
import org.junit.Test;
import org.httpobjects.DateTimeRFC6265.*;

public class DateTimeRFC6265Test {

    @Test
    public void canBeConstructedFromParts(){
        // given

        final String input = "Thu, 01 Jan 1970 00:00:01 GMT";

        // when
        DateTimeRFC6265 result = new DateTimeRFC6265(DayOfWeek.THURSDAY, 1, MonthOfYear.JANUARY, 1970, 21, 33, 34, "PST");

        // then
        Assert.assertEquals(DateTimeRFC6265.DayOfWeek.THURSDAY, result.dayOfWeek);
        Assert.assertEquals(1, result.dayOfMonth);
        Assert.assertEquals(DateTimeRFC6265.MonthOfYear.JANUARY, result.monthOfYear);
        Assert.assertEquals(1970, result.year);
        Assert.assertEquals(21, result.hours);
        Assert.assertEquals(33, result.minutes);
        Assert.assertEquals(34, result.seconds);
        Assert.assertEquals("PST", result.timezone);
        Assert.assertEquals("Thu, 01 Jan 1970 21:33:34 PST", result.toString());
    }

    @Test
    public void parsesDaysOfWeek(){
        // given

        final String input = "Thu, 01 Jan 1970 00:00:01 GMT";

        // when
        DateTimeRFC6265 result = new DateTimeRFC6265(input);

        // then
        Assert.assertEquals(DateTimeRFC6265.DayOfWeek.THURSDAY, result.dayOfWeek);
    }


    @Test
    public void parsesDayOfMonth(){
        // given

        final String input = "Thu, 01 Jan 1970 00:00:01 GMT";

        // when
        DateTimeRFC6265 result = new DateTimeRFC6265(input);

        // then
        Assert.assertEquals(1, result.dayOfMonth);
    }

    @Test
    public void parsesMonthOfYear(){
        // given

        final String input = "Thu, 01 Jan 1970 00:00:01 GMT";

        // when
        DateTimeRFC6265 result = new DateTimeRFC6265(input);

        // then
        Assert.assertEquals(DateTimeRFC6265.MonthOfYear.JANUARY, result.monthOfYear);
    }


    @Test
    public void parsesYear(){
        // given

        final String input = "Thu, 01 Jan 1970 00:00:01 GMT";

        // when
        DateTimeRFC6265 result = new DateTimeRFC6265(input);

        // then
        Assert.assertEquals(1970, result.year);
    }

    @Test
    public void parsesTwoDigitYearsBefore1970(){
        // given

        final String input = "Thu, 01 Jan 69 00:00:01 GMT";

        // when
        DateTimeRFC6265 result = new DateTimeRFC6265(input);

        // then
        Assert.assertEquals(2069, result.year);
    }


    @Test
    public void parsesTwoDigitYearsAfter1969(){
        // given

        final String input = "Thu, 01 Jan 70 00:00:01 GMT";

        // when
        DateTimeRFC6265 result = new DateTimeRFC6265(input);

        // then
        Assert.assertEquals(1970, result.year);
    }


    @Test
    public void parsesHours(){
        // given

        final String input = "Tue, 15 Jan 2013 21:47:38 GMT";

        // when
        DateTimeRFC6265 result = new DateTimeRFC6265(input);

        // then
        Assert.assertEquals(21, result.hours);
    }

    @Test
    public void parsesMinutes(){
        // given

        final String input = "Tue, 15 Jan 2013 21:47:38 GMT";

        // when
        DateTimeRFC6265 result = new DateTimeRFC6265(input);

        // then
        Assert.assertEquals(47, result.minutes);
    }

    @Test
    public void parsesSeconds(){
        // given

        final String input = "Tue, 15 Jan 2013 21:47:38 GMT";

        // when
        DateTimeRFC6265 result = new DateTimeRFC6265(input);

        // then
        Assert.assertEquals(47, result.minutes);
    }

    @Test
    public void parsesTimezone(){
        // given

        final String input = "Tue, 15 Jan 2013 21:47:38 GMT";

        // when
        DateTimeRFC6265 result = new DateTimeRFC6265(input);

        // then
        Assert.assertEquals("GMT", result.timezone);
    }

    @Test
    public void inequality(){
        // given

        DateTimeRFC6265 a = new DateTimeRFC6265("Tue, 15 Jan 2013 21:47:38 GMT");
        DateTimeRFC6265 b = new DateTimeRFC6265("Thu, 01 Jan 1970 00:00:01 GMT");

        // when
        boolean result = a.equals(b);

        // then
        Assert.assertEquals(false, result);
    }

    @Test
    public void equality(){
        // given
        DateTimeRFC6265 a = new DateTimeRFC6265("Tue, 15 Jan 2013 21:47:38 GMT");
        DateTimeRFC6265 b = new DateTimeRFC6265("Tue, 15 Jan 2013 21:47:38 GMT");

        // when
        boolean result = a.equals(b);

        // then
        Assert.assertEquals(true, result);
    }

    @Test
    public void calculatesHashCode(){
        // given
        DateTimeRFC6265 value = new DateTimeRFC6265("Tue, 15 Jan 2013 21:47:38 GMT");

        // when
        int result = value.hashCode();

        // then
        Assert.assertEquals("Tue, 15 Jan 2013 21:47:38 GMT".hashCode(), result);
    }


    @Test
    public void generatesSpecToString(){
        // given
        DateTimeRFC6265 value = new DateTimeRFC6265("Tue, 15 Jan 2013 21:47:38 GMT");

        // when
        String result = value.toString();

        // then
        Assert.assertEquals("Tue, 15 Jan 2013 21:47:38 GMT", result);
    }

    @Test
    public void acceptsHyphenatedForm(){
        // given

        final String input = "Thu, 01-Jan-70 21:33:01 GMT";

        // when
        DateTimeRFC6265 result = new DateTimeRFC6265(input);

        // then
        Assert.assertEquals(DateTimeRFC6265.DayOfWeek.THURSDAY, result.dayOfWeek);
        Assert.assertEquals(1, result.dayOfMonth);
        Assert.assertEquals(DateTimeRFC6265.MonthOfYear.JANUARY, result.monthOfYear);
        Assert.assertEquals(1970, result.year);
        Assert.assertEquals(21, result.hours);
        Assert.assertEquals(33, result.minutes);
        Assert.assertEquals(1, result.seconds);
        Assert.assertEquals("GMT", result.timezone);
    }

    @Test
    public void throwsADescriptiveExceptionWhenParsingFails(){
        // given

        final String input = "january seventh two-thousand and 80";

        // when
        Exception err;
        try{
            new DateTimeRFC6265(input);
            err = null;
        }catch(Exception e){
            err = e;
        }

        // then
        Assert.assertNotNull("Should have failed", err);
        Assert.assertEquals(RuntimeException.class, err.getClass());
        Assert.assertEquals("Not a date I understand: 'january seventh two-thousand and 80'", err.getMessage());
    }
}
