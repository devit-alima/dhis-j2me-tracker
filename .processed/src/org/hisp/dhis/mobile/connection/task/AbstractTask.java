package org.hisp.dhis.mobile.connection.task;

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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.io.HttpConnection;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.ui.Text;

import com.jcraft.jzlib.ZInputStream;

public abstract class AbstractTask
    implements Runnable
{
    private static final String CLASS_TAG = "AbstractTask";

    /**
     * 
     * @return
     * @throws IOException
     */
    protected DataInputStream download()
        throws IOException
    {
        LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Downloading server response..." );
        
        HttpConnection hcon = null;
        try
        {
            int redirectTimes = 0;
            boolean redirect;
            do
            {
                redirect = false;
                hcon = ConnectionManager.createConnection();
                int status = hcon.getResponseCode();

                LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Response Code: " + status );

                switch ( status )
                {
                case HttpConnection.HTTP_OK:
                    return getDecompressedStream( new DataInputStream( hcon.openInputStream() ) );
                case HttpConnection.HTTP_SEE_OTHER:
                case HttpConnection.HTTP_TEMP_REDIRECT:
                case HttpConnection.HTTP_MOVED_TEMP:
                case HttpConnection.HTTP_MOVED_PERM:
                    ConnectionManager.setUrl( hcon.getHeaderField( "location" ) );
                    if ( hcon != null )
                        hcon.close();
                    hcon = null;
                    redirectTimes++;
                    redirect = true;
                    break;
                case HttpConnection.HTTP_INTERNAL_ERROR:
                    hcon.close();
                    throw new IOException( Text.SERVER_ERROR() );
                case HttpConnection.HTTP_UNAUTHORIZED:
                    hcon.close();
                    throw new IOException( Text.INVALID_USER_PASS() );
                case HttpConnection.HTTP_NOT_FOUND:
                    hcon.close();
                    throw new IOException( Text.CONN_NOT_FOUND() );
                default:
                    hcon.close();
                    throw new IOException( Text.RESPONSE_STATUS_NOT_OK() + " " + status );
                }

            }
            while ( redirect == true && redirectTimes < 5 );

            if ( redirectTimes == 5 )
            {
                throw new IOException( Text.TOO_MUCH_REDIRECT() );
            }
        }
        finally
        {
            try
            {
                if ( hcon != null )
                    hcon.close();
                System.gc();
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }
        return null;

    }

    /**
     * 
     * @param additionalProperty
     * @param additionalPropertyName
     * @return
     * @throws IOException
     */
    protected DataInputStream download( String additionalProperty, String additionalPropertyName )
        throws IOException
    {
        LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Downloading server response..." );
        
        HttpConnection hcon = null;
        try
        {
            int redirectTimes = 0;
            DataInputStream inputStream = null;
            boolean redirect;
            do
            {
                redirect = false;
                hcon = ConnectionManager.createConnection();
                hcon.setRequestProperty( additionalPropertyName, additionalProperty );
                int status = hcon.getResponseCode();

                LogMan.log( LogMan.DEV, "Network," + CLASS_TAG, "Response Code: " + status );

                switch ( status )
                {
                case HttpConnection.HTTP_OK:
                    inputStream = new DataInputStream( hcon.openInputStream() );
                    if ( inputStream != null )
                    {
                        return getDecompressedStream( inputStream );
                    }
                case HttpConnection.HTTP_SEE_OTHER:
                case HttpConnection.HTTP_TEMP_REDIRECT:
                case HttpConnection.HTTP_MOVED_TEMP:
                case HttpConnection.HTTP_MOVED_PERM:
                    ConnectionManager.setUrl( hcon.getHeaderField( "location" ) );
                    if ( hcon != null )
                        hcon.close();
                    hcon = null;
                    redirectTimes++;
                    redirect = true;
                    break;
                case HttpConnection.HTTP_CONFLICT:
                    handleError( "Problem with sent data", hcon );
                case HttpConnection.HTTP_INTERNAL_ERROR:
                    hcon.close();
                    throw new IOException( Text.SERVER_ERROR() );
                case HttpConnection.HTTP_UNAUTHORIZED:
                    hcon.close();
                    throw new IOException( Text.INVALID_USER_PASS() );
                case HttpConnection.HTTP_NOT_FOUND:
                    hcon.close();
                    throw new IOException( Text.CONN_NOT_FOUND() );
                default:
                    hcon.close();
                }

            }
            while ( redirect == true && redirectTimes < 5 );

            if ( redirectTimes == 5 )
            {
                throw new IOException( Text.TOO_MUCH_REDIRECT() );
            }
        }
        finally
        {
            try
            {
                if ( hcon != null )
                    hcon.close();
                System.gc();
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }
        return null;

    }

    /**
     * 
     * @param additionalProperty
     * @param additionalPropertyName
     * @return
     * @throws IOException
     */
    protected DataInputStream downloadUncompress( String additionalProperty, String additionalPropertyName )
        throws IOException
    {
        LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Downloading uncompressed server response..." );
        
        HttpConnection hcon = null;
        try
        {
            int redirectTimes = 0;
            DataInputStream inputStream = null;
            boolean redirect;
            do
            {
                redirect = false;
                hcon = ConnectionManager.createConnection();
                hcon.setRequestProperty( additionalPropertyName, additionalProperty );
                int status = hcon.getResponseCode();

                LogMan.log( LogMan.DEV, "Network," + CLASS_TAG, "Response Code: " + status );

                switch ( status )
                {
                case HttpConnection.HTTP_OK:
                    inputStream = new DataInputStream( hcon.openInputStream() );
                    if ( inputStream != null )
                    {
                        return inputStream;
                    }
                case HttpConnection.HTTP_SEE_OTHER:
                case HttpConnection.HTTP_TEMP_REDIRECT:
                case HttpConnection.HTTP_MOVED_TEMP:
                case HttpConnection.HTTP_MOVED_PERM:
                    ConnectionManager.setUrl( hcon.getHeaderField( "location" ) );
                    if ( hcon != null )
                        hcon.close();
                    hcon = null;
                    redirectTimes++;
                    redirect = true;
                    break;
                case HttpConnection.HTTP_CONFLICT:
                    handleError( "Problem with sent data", hcon );
                case HttpConnection.HTTP_INTERNAL_ERROR:
                    hcon.close();
                    throw new IOException( Text.SERVER_ERROR() );
                case HttpConnection.HTTP_UNAUTHORIZED:
                    hcon.close();
                    throw new IOException( Text.INVALID_USER_PASS() );
                case HttpConnection.HTTP_NOT_FOUND:
                    hcon.close();
                    throw new IOException( Text.CONN_NOT_FOUND() );
                default:
                    hcon.close();
                }

            }
            while ( redirect == true && redirectTimes < 5 );

            if ( redirectTimes == 5 )
            {
                throw new IOException( Text.TOO_MUCH_REDIRECT() );
            }
        }
        finally
        {
            try
            {
                if ( hcon != null )
                    hcon.close();
                System.gc();
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }
        return null;

    }

    /**
     * 
     * @param request_body
     * @param additionalProperty
     * @param additionalPropertyName
     * @return
     * @throws IOException
     */
    protected DataInputStream upload( byte[] request_body, String additionalProperty, String additionalPropertyName )
        throws IOException
    {
        LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Uploading..." );
        HttpConnection hcon = null;
        DataOutputStream dos = null;
        try
        {
            int redirectTimes = 0;
            boolean redirect;
            do
            {
                redirect = false;
                hcon = ConnectionManager.createConnection();
                hcon.setRequestMethod( HttpConnection.POST );
                hcon.setRequestProperty( "Content-Length", "" + request_body.toString().length() );
                hcon.setRequestProperty( additionalPropertyName, additionalProperty );
                dos = hcon.openDataOutputStream();

                for ( int i = 0; i < request_body.length; i++ )
                {
                    dos.writeByte( request_body[i] );
                }
                dos.flush();

                int status = hcon.getResponseCode();
                LogMan.log( LogMan.DEV, "Network," + CLASS_TAG, "Response Code: " + status );
                
                switch ( status )
                {
                case HttpConnection.HTTP_OK:
                    return new DataInputStream( hcon.openInputStream() );
                case HttpConnection.HTTP_NO_CONTENT:
                    // Went fine!
                    return null;
                case HttpConnection.HTTP_TEMP_REDIRECT:
                case HttpConnection.HTTP_MOVED_TEMP:
                case HttpConnection.HTTP_MOVED_PERM:
                    ConnectionManager.setUrl( hcon.getHeaderField( "location" ) );
                    if ( hcon != null )
                        hcon.close();
                    redirectTimes++;
                    redirect = true;
                    break;
                case HttpConnection.HTTP_CONFLICT:
                    handleError( Text.SENT_DATA_PROBLEM(), hcon );
                case HttpConnection.HTTP_INTERNAL_ERROR:
                    handleError( Text.SERVER_ERROR(), hcon );
                default:
                    hcon.close();
                    handleError( Text.SERVER_NOT_FOUND(), hcon );
                }
            }
            while ( redirect == true && redirectTimes < 5 );

            if ( redirectTimes == 5 )
            {
                throw new IOException( Text.TOO_MUCH_REDIRECT() );
            }
        }
        finally
        {
            try
            {
                if ( hcon != null )
                    hcon.close();
                if ( dos != null )
                    dos.close();
            }
            catch ( IOException ioe )
            {
                ioe.printStackTrace();
            }
        }
        return null;
    }

    protected DataInputStream upload( byte[] request_body )
        throws IOException
    {
        LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Uploading..." );
        HttpConnection hcon = null;
        DataOutputStream dos = null;
        try
        {
            int redirectTimes = 0;
            boolean redirect;
            do
            {
                redirect = false;
                hcon = ConnectionManager.createConnection();
                hcon.setRequestMethod( HttpConnection.POST );
                hcon.setRequestProperty( "Content-Length", "" + request_body.toString().length() );
                dos = hcon.openDataOutputStream();

                for ( int i = 0; i < request_body.length; i++ )
                {
                    dos.writeByte( request_body[i] );
                }
                dos.flush();

                int status = hcon.getResponseCode();
                LogMan.log( LogMan.DEV, "Network," + CLASS_TAG, "Response Code: " + status );
                
                switch ( status )
                {
                case HttpConnection.HTTP_OK:
                    return new DataInputStream( hcon.openInputStream() );
                case HttpConnection.HTTP_NO_CONTENT:
                    // Went fine!
                    return null;
                case HttpConnection.HTTP_TEMP_REDIRECT:
                case HttpConnection.HTTP_MOVED_TEMP:
                case HttpConnection.HTTP_MOVED_PERM:
                    ConnectionManager.setUrl( hcon.getHeaderField( "location" ) );
                    if ( hcon != null )
                        hcon.close();
                    redirectTimes++;
                    redirect = true;
                    break;
                case HttpConnection.HTTP_CONFLICT:
                    handleError( Text.SENT_DATA_PROBLEM(), hcon );
                case HttpConnection.HTTP_INTERNAL_ERROR:
                    handleError( Text.SERVER_ERROR(), hcon );
                default:
                    hcon.close();
                    handleError( Text.SERVER_NOT_FOUND(), hcon );
                }
            }
            while ( redirect == true && redirectTimes < 5 );

            if ( redirectTimes == 5 )
            {
                throw new IOException( Text.TOO_MUCH_REDIRECT() );
            }
        }
        finally
        {
            try
            {
                if ( hcon != null )
                    hcon.close();
                if ( dos != null )
                    dos.close();
            }
            catch ( IOException ioe )
            {
                ioe.printStackTrace();
            }
        }
        return null;
    }

    protected DataInputStream getDecompressedStream( DataInputStream dis )
    {
        LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Getting decompressed stream..." );
        return new DataInputStream( new ZInputStream( dis ) );
    }

    /**
     * 
     * @param message
     * @param hcon
     * @throws IOException
     */
    private void handleError( String message, HttpConnection hcon )
        throws IOException
    {
        StringBuffer responseMessage = new StringBuffer();

        DataInputStream dis = null;
        try
        {
            dis = new DataInputStream( hcon.openInputStream() );
            int ch;
            while ( (ch = dis.read()) != -1 )
            {
                responseMessage.append( (char) ch );
            }
        }
        catch ( IOException e )
        {
            e.printStackTrace();
            LogMan.log( "Network," + CLASS_TAG, e );
        }
        finally
        {
            try
            {
                if ( dis != null )
                    dis.close();
            }
            catch ( IOException ioe )
            {
                ioe.printStackTrace();
                LogMan.log( "Network," + CLASS_TAG, ioe );
            }
        }

        message += ", " + hcon.getResponseCode();
        LogMan.log( LogMan.ERROR, "Network," + CLASS_TAG, message );

        if ( responseMessage.length() > 0 )
        {
            throw new IOException( responseMessage.toString() );
        }
        else
        {
            throw new IOException( message );
        }

    }

    /**
     * 
     * @param dis
     * @return
     */
    protected String readMessage( DataInputStream dis )
    {
        LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Reading response message..." );
        StringBuffer responseMessage = new StringBuffer();
        String message = "";
        try
        {
            int ch;
            while ( (ch = dis.read()) != -1 )
            {
                responseMessage.append( (char) ch );
            }
            message = responseMessage.toString();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
            LogMan.log( "Network," + CLASS_TAG, e );
        }
        finally
        {
            try
            {
                if ( dis != null )
                    dis.close();
            }
            catch ( IOException ioe )
            {
                ioe.printStackTrace();
                LogMan.log( "Network," + CLASS_TAG, ioe );
            }
        }
        return message;
    }

    /**
     * 
     * @param request_body
     * @param additionalProperty
     * @param additionalPropertyName
     * @return
     * @throws IOException
     */
    protected DataInputStream download( byte[] request_body, String additionalProperty, String additionalPropertyName )
        throws IOException
    {
        LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Downloading server response..." );

        HttpConnection hcon = null;
        DataOutputStream dos = null;
        try
        {
            int redirectTimes = 0;
            DataInputStream inputStream = null;
            boolean redirect;
            do
            {
                redirect = false;
                hcon = ConnectionManager.createConnection();
                hcon.setRequestProperty( additionalPropertyName, additionalProperty );
                hcon.setRequestProperty( "Content-Length", "" + request_body.toString().length() );
                hcon.setRequestMethod( HttpConnection.POST );

                dos = hcon.openDataOutputStream();
                for ( int i = 0; i < request_body.length; i++ )
                {
                    dos.writeByte( request_body[i] );
                }
                dos.flush();

                int status = hcon.getResponseCode();

                LogMan.log( LogMan.DEV, "Network," + CLASS_TAG, "Response Code: " + status );

                switch ( status )
                {
                case HttpConnection.HTTP_OK:
                    inputStream = new DataInputStream( hcon.openInputStream() );
                    if ( inputStream != null )
                    {
                        return getDecompressedStream( inputStream );
                    }
                case HttpConnection.HTTP_SEE_OTHER:
                case HttpConnection.HTTP_TEMP_REDIRECT:
                case HttpConnection.HTTP_MOVED_TEMP:
                case HttpConnection.HTTP_MOVED_PERM:
                    ConnectionManager.setUrl( hcon.getHeaderField( "location" ) );
                    if ( hcon != null )
                        hcon.close();
                    hcon = null;
                    redirectTimes++;
                    redirect = true;
                    break;
                case HttpConnection.HTTP_CONFLICT:
                    handleError( "Problem with sent data", hcon );
                case HttpConnection.HTTP_INTERNAL_ERROR:
                    hcon.close();
                    throw new IOException( Text.SERVER_ERROR() );
                case HttpConnection.HTTP_UNAUTHORIZED:
                    hcon.close();
                    throw new IOException( Text.INVALID_USER_PASS() );
                case HttpConnection.HTTP_NOT_FOUND:
                    hcon.close();
                    throw new IOException( Text.CONN_NOT_FOUND() );
                default:
                    hcon.close();
                }

            }
            while ( redirect == true && redirectTimes < 5 );

            if ( redirectTimes == 5 )
            {
                throw new IOException( Text.TOO_MUCH_REDIRECT() );
            }
        }
        finally
        {
            try
            {
                if ( hcon != null )
                    hcon.close();
                if ( hcon != null )
                    dos.close();
                System.gc();
            }
            catch ( IOException e )
            {
                e.printStackTrace();
                LogMan.log( "Network," + CLASS_TAG, e );
            }
        }
        return null;

    }

}
