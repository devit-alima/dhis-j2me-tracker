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

import org.hisp.dhis.mobile.model.ProgramStageDataElement;

/**
 * @author Nguyen Kim Lai
 * 
 * @version TrackingUtils.java 2:16:55 PM Mar 12, 2013 $
 */
public class TrackingUtils
{
    static String type;

    static String numberType;

    static String value;

    public static String getTypeViolation( ProgramStageDataElement dataElement )
    {
        value = dataElement.getValue();

        if ( value.equals( "" ) || value.equals( "Please Select" ) || value.equals( "Option" ) )
        {
            if ( dataElement.isCompulsory() )
            {
                return "is_required";
            }
        }
        else
        {
            type = dataElement.getType();
            numberType = dataElement.getNumberType();
            if ( type.equals( ProgramStageDataElement.VALUE_TYPE_STRING ) )
            {
            }
            else if ( type.equals( ProgramStageDataElement.VALUE_TYPE_BOOL ) )
            {
                if ( !FormUtils.isBoolean( value ) )
                {
                    return "is_invalid_boolean";
                }
            }
            else if ( type.equals( ProgramStageDataElement.VALUE_TYPE_DATE ) )
            {
                // if ( !FormUtils.isDate( value ) )
                // {
                // return "is_invalid_date";
                // }

                // Support standard date format
                if ( !FormUtils.isStandardDateFormat( value ) )
                {
                    return "is_invalid_date";
                }
            }
            else if ( type.equals( ProgramStageDataElement.VALUE_TYPE_INT )
                && numberType.equals( ProgramStageDataElement.VALUE_TYPE_NUMBER ) )
            {
                if ( !FormUtils.isNumber( value ) )
                {
                    return "is_invalid_number";
                }
            }
            else if ( type.equals( ProgramStageDataElement.VALUE_TYPE_INT )
                && numberType.equals( ProgramStageDataElement.VALUE_TYPE_INT ) )
            {
                if ( !FormUtils.isInteger( value ) )
                {
                    return "is_invalid_integer";
                }
            }
            else if ( type.equals( ProgramStageDataElement.VALUE_TYPE_INT )
                && numberType.equals( ProgramStageDataElement.VALUE_TYPE_POSITIVE_INT ) )
            {
                if ( !FormUtils.isPositiveInteger( value ) )
                {
                    return "is_invalid_positive_integer";
                }
            }
            else if ( type.equals( ProgramStageDataElement.VALUE_TYPE_INT )
                && numberType.equals( ProgramStageDataElement.VALUE_TYPE_NEGATIVE_INT ) )
            {
                if ( !FormUtils.isNegativeInteger( value ) )
                {
                    return "is_invalid_negative_integer";
                }
            }
        }
        return null;
    }

    public static String getUrlForSelectionAll( String url )
    {
        String result;
        char c = '/';
        String backup = url.substring( url.lastIndexOf( c ) );
        result = url.substring( 0, url.lastIndexOf( c ) );
        result = result.substring( 0, result.lastIndexOf( c ) + 1 );
        result = result + "0" + backup;

        backup = null;
        url = null;

        return result;
    }

}
