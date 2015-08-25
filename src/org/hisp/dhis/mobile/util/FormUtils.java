/*
 * Copyright (c) 2004-2012, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.mobile.util;

import java.util.Calendar;

/**
 * @author Nguyen Kim Lai
 * 
 * @version FormUtils.java 1:52:26 PM Mar 12, 2013 $
 */
public class FormUtils
{

    public static boolean isBoolean( String value )
    {
        return value.equals( "true" ) || value.equals( "false" );
    }

    public static boolean isDate( String value )
    {
        boolean valid = false;
        try
        {
            Calendar cal = Calendar.getInstance();
            cal.set( Calendar.DATE, Integer.parseInt( value.substring( 8, 10 ) ) );
            cal.set( Calendar.MONTH, Integer.parseInt( value.substring( 5, 7 ) ) - 1 );
            cal.set( Calendar.YEAR, Integer.parseInt( value.substring( 0, 4 ) ) );
            cal.getTime();
            valid = true;
            return valid;
        }
        catch ( Exception ex )
        {
            return valid;
        }
    }

    public static boolean isStandardDateFormat( String value )
    {
        boolean valid = false;
        try
        {
            Calendar cal = Calendar.getInstance();
            cal.set( Calendar.DATE, Integer.parseInt( value.substring( 0, 2 ) ) );
            cal.set( Calendar.MONTH, Integer.parseInt( value.substring( 3, 5 ) ) - 1 );
            cal.set( Calendar.YEAR, Integer.parseInt( value.substring( 6, 10 ) ) );

            cal.getTime();
            valid = true;
            return valid;
        }
        catch ( Exception ex )
        {
            return valid;
        }
    }

    public static boolean isNumber( String value )
    {
        try
        {
            Double.parseDouble( value );
        }
        catch ( NumberFormatException e )
        {
            return false;
        }

        return true;
    }

    public static boolean isInteger( String value )
    {
        try
        {
            Integer.parseInt( value );
        }
        catch ( NumberFormatException e )
        {
            return false;
        }

        return true;
    }

    public static boolean isPositiveInteger( String value )
    {
        return valueHigher( value, 0 );
    }

    public static boolean valueHigher( String value, int max )
    {
        int integerValue;

        try
        {
            integerValue = Integer.parseInt( value );

            if ( integerValue > max )
            {
                return true;
            }
        }
        catch ( NumberFormatException ignored )
        {
        }

        return false;
    }

    public static boolean isNegativeInteger( String value )
    {
        return valueLower( value, 0 );
    }

    public static boolean valueLower( String value, int min )
    {
        int integerValue;

        try
        {
            integerValue = Integer.parseInt( value );

            if ( integerValue < min )
            {
                return true;
            }
        }
        catch ( NumberFormatException ignored )
        {
        }

        return false;
    }
}
