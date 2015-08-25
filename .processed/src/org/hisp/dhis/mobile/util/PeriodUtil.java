package org.hisp.dhis.mobile.util;

/*
 * Copyright (c) 2004-2011, University of Oslo All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met: * Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer. * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or other materials provided with the
 * distribution. * Neither the name of the HISP project nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class PeriodUtil
{

    public static final int DAILY = 1;

    public static final int WEEKLY = 2;

    public static final int MONTHLY = 3;

    public static final int QUARTERLY = 4;

    public static final int SIX_MONTHLY = 5;

    public static final int YEARLY = 6;

    public static final int TWO_YEARLY = 7;

    public static Vector generateWeeklyPeriods()
    {
        Vector weeks = new Vector();
        Calendar cal = Calendar.getInstance();

        int year = cal.get( Calendar.YEAR );
        int month = cal.get( Calendar.MONTH ) + 1;
        int day = cal.get( Calendar.DATE );

        int a, b, c, s, e, f, g, d, n;
        int week;

        if ( month <= 2 )
        {
            a = year - 1;
            b = a / 4 - a / 100 + a / 400;
            c = (a - 1) / 4 - (a - 1) / 100 + (a - 1) / 400;
            s = b - c;
            e = 0;
            f = day - 1 + 31 * (month - 1);
        }
        else
        {
            a = year;
            b = a / 4 - a / 100 + a / 400;
            c = (a - 1) / 4 - (a - 1) / 100 + (a - 1) / 400;
            s = b - c;
            e = s + 1;
            f = day + (153 * (month - 3) + 2) / 5 + 58 + s;
        }

        g = (a + b) % 7;
        d = (f + g - e) % 7;
        n = f + 3 - d;

        if ( n < 0 )
        {
            week = 53 - (g - s) / 5;
            year = year - 1;
        }
        else if ( n > 364 + s )
        {
            week = 1;
            year = year + 1;
        }
        else
        {
            week = n / 7 + 1;
        }

        // Display only 12 previous periods including the current one

        // formatting week "Week WW YYYY"
        for ( int i = 0; i < 4; i++ )
        {
            if ( week < 10 )
            {
                weeks.addElement( "Wk 0" + week + " " + year );
            }
            else
            {
                weeks.addElement( "Wk " + week + " " + year );
            }

            week = week - 1;

            if ( week <= 0 )
            {
                week = 52;
                year = year - 1;
            }
        }

        return weeks;
    }

    public static String weekOfYear( Date date )
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime( date );

        int year = cal.get( Calendar.YEAR );
        int month = cal.get( Calendar.MONTH ) + 1;
        int day = cal.get( Calendar.DATE );

        int a, b, c, s, e, f, g, d, n;
        int week;

        if ( month <= 2 )
        {
            a = year - 1;
            b = a / 4 - a / 100 + a / 400;
            c = (a - 1) / 4 - (a - 1) / 100 + (a - 1) / 400;
            s = b - c;
            e = 0;
            f = day - 1 + 31 * (month - 1);
        }
        else
        {
            a = year;
            b = a / 4 - a / 100 + a / 400;
            c = (a - 1) / 4 - (a - 1) / 100 + (a - 1) / 400;
            s = b - c;
            e = s + 1;
            f = day + (153 * (month - 3) + 2) / 5 + 58 + s;
        }

        g = (a + b) % 7;
        d = (f + g - e) % 7;
        n = f + 3 - d;

        if ( n < 0 )
        {
            week = 53 - (g - s) / 5;
            year = year - 1;
        }
        else if ( n > 364 + s )
        {
            week = 1;
            year = year + 1;
        }
        else
        {
            week = n / 7 + 1;
        }

        if ( week < 10 )
        {
            return "0" + week + " " + year;
        }
        else
        {
            return week + " " + year;
        }
    }

    public static Vector generateQuaterlyPeriods()
    {
        Vector quarters = new Vector();
        Calendar cal = Calendar.getInstance();
        String[] quatersStr = { "Jan to Mar", "Apr to Jun", "Jul to Sep", "Oct to Dec" };

        if ( cal.get( Calendar.MONTH ) >= 0 && cal.get( Calendar.MONTH ) <= 2 )
        {
            quarters.addElement( quatersStr[0] + " " + cal.get( Calendar.YEAR ) );
        }
        else if ( cal.get( Calendar.MONTH ) >= 3 && cal.get( Calendar.MONTH ) <= 5 )
        {
            quarters.addElement( quatersStr[1] + " " + cal.get( Calendar.YEAR ) );
            quarters.addElement( quatersStr[0] + " " + cal.get( Calendar.YEAR ) );
        }
        else if ( cal.get( Calendar.MONTH ) >= 6 && cal.get( Calendar.MONTH ) <= 8 )
        {
            quarters.addElement( quatersStr[2] + " " + cal.get( Calendar.YEAR ) );
            quarters.addElement( quatersStr[1] + " " + cal.get( Calendar.YEAR ) );
            quarters.addElement( quatersStr[0] + " " + cal.get( Calendar.YEAR ) );
        }
        else if ( cal.get( Calendar.MONTH ) >= 9 && cal.get( Calendar.MONTH ) <= 11 )
        {
            quarters.addElement( quatersStr[3] + " " + cal.get( Calendar.YEAR ) );
            quarters.addElement( quatersStr[2] + " " + cal.get( Calendar.YEAR ) );
            quarters.addElement( quatersStr[1] + " " + cal.get( Calendar.YEAR ) );
            quarters.addElement( quatersStr[0] + " " + cal.get( Calendar.YEAR ) );
        }
        return quarters;
    }

    public static Vector generateMonthlyPeriods()
    {
        Vector months = new Vector();
        Calendar cal = Calendar.getInstance();

        String[] monthNames = { "January", "February", "March", "April", "May", "June", "July", "August", "September",
            "October", "November", "December" };

        // Display only 12 previous periods including the current one
        cal.set( Calendar.MONTH, cal.get( Calendar.MONTH ) );

        for ( int i = 0; i < 11; i++ )
        {
            if ( cal.get( Calendar.MONTH ) < 0 )
            {
                cal.set( Calendar.MONTH, 11 );
                cal.set( Calendar.YEAR, cal.get( Calendar.YEAR ) - 1 );
            }
            months.addElement( monthNames[cal.get( Calendar.MONTH )] + " " + cal.get( Calendar.YEAR ) );
            cal.set( Calendar.MONTH, cal.get( Calendar.MONTH ) - 1 );
        }

        return months;
    }

    public static Vector generateYearlyPeriods()
    {
        Vector years = new Vector();
        Calendar cal = Calendar.getInstance();

        // Display only 12 previous periods including the current one
        cal.set( Calendar.YEAR, cal.get( Calendar.YEAR ) );

        for ( int i = 0; i < 2; i++ )
        {
            years.addElement( Integer.toString( cal.get( Calendar.YEAR ) ) );
            cal.set( Calendar.YEAR, cal.get( Calendar.YEAR ) - 1 );
        }

        return years;
    }

    public static String formatDate( Date date )
    {
        StringBuffer formattedPeriod = new StringBuffer();
        Calendar cal = Calendar.getInstance();
        cal.setTime( date );
        int yr = cal.get( Calendar.YEAR );
        int mnth = (cal.get( Calendar.MONTH ) + 1) % 13;
        int d = cal.get( Calendar.DATE );

        formattedPeriod.append( yr );
        formattedPeriod.append( "-" );

        if ( mnth < 10 )
        {
            formattedPeriod.append( 0 );
            formattedPeriod.append( mnth );
        }
        else
        {
            formattedPeriod.append( mnth );
        }

        formattedPeriod.append( "-" );

        if ( d < 10 )
        {
            formattedPeriod.append( 0 );
            formattedPeriod.append( d );
        }
        else
        {
            formattedPeriod.append( d );
        }

        return formattedPeriod.toString();
    }

    public static Date stringToDate( String string )
    {
        String token[] = split( string, '-' );
        Calendar cal = Calendar.getInstance();

        cal.set( Calendar.YEAR, Integer.parseInt( token[0] ) );

        if ( String.valueOf( token[2].charAt( 0 ) ).equals( "0" ) )
        {
            cal.set( Calendar.DATE, Integer.parseInt( String.valueOf( token[2].charAt( 1 ) ) ) );

        }
        else
        {
            cal.set( Calendar.DATE, Integer.parseInt( token[2] ) );

        }

        if ( String.valueOf( token[1].charAt( 0 ) ).equals( "0" ) )
        {
            cal.set( Calendar.MONTH, Integer.parseInt( String.valueOf( token[1].charAt( 1 ) ) ) - 1 );
        }
        else
        {
            cal.set( Calendar.MONTH, Integer.parseInt( token[1] ) - 1);
        }
        return cal.getTime();
    }

    public static String[] split( String str, char separatorChar )
    {
        if ( str == null )
        {
            return null;
        }
        int len = str.length();
        if ( len == 0 )
        {
            return null;
        }
        Vector list = new Vector();
        int i = 0;
        int start = 0;
        boolean match = false;
        while ( i < len )
        {
            if ( str.charAt( i ) == separatorChar )
            {
                if ( match )
                {
                    list.addElement( str.substring( start, i ).trim() );
                    match = false;
                }
                start = ++i;
                continue;
            }
            match = true;
            i++;
        }
        if ( match )
        {
            list.addElement( str.substring( start, i ).trim() );
        }
        String[] arr = new String[list.size()];
        list.copyInto( arr );
        return arr;
    }

    // Return String to display on form from the PeriodName in database (03-03
    // 2010 to Week 03 2010)
    public static String displayWeeklyPeriod( String week )
    {
        StringBuffer displayPeriod = new StringBuffer();
        displayPeriod.append( "Wk " );
        if ( week.lastIndexOf( ' ' ) > 0 )
        {
            displayPeriod.append( week.substring( 0, 2 ) );
            displayPeriod.append( " " + week.substring( week.lastIndexOf( ' ' ) + 1 ) );
        }
        else
        {
            displayPeriod.append( week.substring( 0, 2 ) );
            displayPeriod.append( " " + week.substring( week.lastIndexOf( '-' ) + 1 ) );
        }

        return displayPeriod.toString();
    }

    public static String formatWeeklyPeriod( String week )
    {
        week = week.substring( 3, week.length() );
        StringBuffer formattedPeriod = new StringBuffer();

        String w = week.substring( 0, week.lastIndexOf( ' ' ) );
        String y = week.substring( week.indexOf( " " ) + 1, week.length() );

        formattedPeriod.append( w );
        formattedPeriod.append( "-" );
        formattedPeriod.append( y );

        return formattedPeriod.toString();
    }

    // Return String to display on form from the PeriodName in database (03-03
    // 2010 to Week 03 2010)
    public static String displayMonthlyPeriod( String week )
    {
        String[] monthNames = { "January", "February", "March", "April", "May", "June", "July", "August", "September",
            "October", "November", "December" };
        StringBuffer displayPeriod = new StringBuffer();

        displayPeriod.append( monthNames[Integer.parseInt( week.substring( 0, week.indexOf( '-' ) ) )] + " " );
        displayPeriod.append( week.substring( week.lastIndexOf( '-' ) + 1 ) );
        return displayPeriod.toString();
    }

    public static String formatMonthlyPeriod( String month )
    {
        String[] monthNames = { "January", "February", "March", "April", "May", "June", "July", "August", "September",
            "October", "November", "December" };

        StringBuffer formattedPeriod = new StringBuffer();

        String m = month.substring( 0, month.indexOf( " " ) );
        String y = month.substring( month.indexOf( " " ) + 1, month.length() );

        int mnth = -1;
        for ( int i = 0; i < monthNames.length; i++ )
        {
            if ( monthNames[i].equals( m ) )
            {
                mnth = i;
                break;
            }
        }

        formattedPeriod.append( mnth );
        formattedPeriod.append( "-" );
        formattedPeriod.append( y );

        return formattedPeriod.toString();
    }

    public static String formatMonthlyPeriodForSMS( String monthStr )
    {
        String[] tokens = PeriodUtil.split( monthStr, '-' );
        StringBuffer formattedPeriod = new StringBuffer();
        formattedPeriod.append( tokens[1] );
        formattedPeriod.append( "-" );

        int monthNumber = Integer.parseInt( tokens[0] ) + 1;
        if ( monthNumber < 10 )
        {
            formattedPeriod.append( "0" + monthNumber );
        }
        else
        {
            formattedPeriod.append( monthNumber );
        }

        formattedPeriod.append( "-" );
        formattedPeriod.append( "01" );
        return formattedPeriod.toString();
    }

    public static boolean isExpired( Date currentDate, Date expireDate )
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime( currentDate );

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime( expireDate );

        if ( cal.before( cal2 ) || cal.equals( cal2 ) )
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public static String dateToString( Date date )
    {
        int y,m,d;
        Calendar c = Calendar.getInstance();
        String dateString = null;
        c.setTime( date );
        y = c.get( Calendar.YEAR );
        m = c.get( Calendar.MONTH )+1;
        d = c.get( Calendar.DATE );
        dateString = (y < 10 ? "0" : "") + y + "-" + (m < 10 ? "0" : "") + m + "-" + (d < 10 ? "0" : "") + d;
        c = null;
        return dateString;
    }
    
    public static boolean isDateValid( String date )
    {
        try
        {
            stringToDate( date );
            return true;
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            return false;
        }
    }
}
