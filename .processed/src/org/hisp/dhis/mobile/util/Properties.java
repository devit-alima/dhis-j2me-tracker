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

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import org.hisp.dhis.mobile.ui.Text;

public class Properties
{

    public static Hashtable load( String resourcePath )
        throws IOException
    {
        Class clazz = Runtime.getRuntime().getClass();
        InputStream is = clazz.getResourceAsStream( resourcePath );
        if ( is == null )
        {
            // FIXME
            throw new IOException( Text.RESOURCE_FILE() + " " + resourcePath + " " + Text.DOES_NOT_EXIST() );
        }
        return parsePropertiesFile( is );
    }

    public static Hashtable parsePropertiesFile( InputStream is )
        throws IOException
    {
        Hashtable textTable = new Hashtable();
        int charRead = 0;
        StringBuffer messageKey = new StringBuffer();
        StringBuffer messageText = new StringBuffer();
        boolean readingKey = true;

        while ( (charRead = is.read()) != -1 )
        {
            if ( charRead == '=' )
            {
                readingKey = false;
            }
            else if ( charRead == '\r' || charRead == '\n' )
            {
                is.read();
                if ( messageKey.length() > 0 )
                {
                    textTable.put( messageKey.toString(), convertString( messageText.toString() ) );
                    messageKey = new StringBuffer();
                }
                messageText = new StringBuffer();
                readingKey = true;
            }
            else if ( charRead == '#' )
            {
                while ( is.read() != '\n' )
                {
                }
            }
            else
            {
                if ( readingKey )
                    messageKey.append( (char) charRead );
                else
                    messageText.append( (char) charRead );
            }
        }
        return textTable;
    }

    private static String convertString( String theString )
    {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer( len );

        for ( int x = 0; x < len; )
        {
            aChar = theString.charAt( x++ );
            if ( aChar == '\\' )
            {
                aChar = theString.charAt( x++ );
                if ( aChar == 'u' )
                {
                    int value = 0;
                    for ( int i = 0; i < 4; i++ )
                    {
                        aChar = theString.charAt( x++ );
                        switch ( aChar )
                        {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            value = (value << 4) + aChar - '0';
                            break;
                        case 'a':
                        case 'b':
                        case 'c':
                        case 'd':
                        case 'e':
                        case 'f':
                            value = (value << 4) + 10 + aChar - 'a';
                            break;
                        case 'A':
                        case 'B':
                        case 'C':
                        case 'D':
                        case 'E':
                        case 'F':
                            value = (value << 4) + 10 + aChar - 'A';
                            break;
                        default:
                            return "NO_STRING";
                        }
                    }
                    outBuffer.append( (char) value );
                }
                else
                {
                    if ( aChar == 't' )
                        aChar = '\t';
                    else if ( aChar == 'r' )
                        aChar = '\r';
                    else if ( aChar == 'n' )
                        aChar = '\n';
                    else if ( aChar == 'f' )
                        aChar = '\f';
                    outBuffer.append( aChar );
                }
            }
            else
            {
                outBuffer.append( aChar );
            }
        }
        return outBuffer.toString();
    }

}
