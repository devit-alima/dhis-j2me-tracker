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
package org.hisp.dhis.mobile.connection.task;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Vector;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.ui.Text;

/**
 * @author Nguyen Kim Lai
 * 
 * @version FindPatientTask.java 11:11:10 AM Jan 5, 2013 $
 */

public class FindPatientTask
    extends AbstractTask
{
    private static final String CLASS_TAG = "FindPatientTask";

    private String keyWord;

    private NameBasedMIDlet nameBasedMIDlet;

    public FindPatientTask( String keyWord )
    {
        this.keyWord = keyWord;
        this.nameBasedMIDlet = (NameBasedMIDlet) ConnectionManager.getDhisMIDlet();
    }

    public void run()
    {
        LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Starting FindPatientTask..." );
        DataInputStream inputStream = null;
        // Patient patient = new Patient();
        try
        {
            inputStream = this.downloadUncompress( keyWord, "name" );
            if ( inputStream != null )
            {
                String message = this.readMessage( inputStream );
                Vector patientInfos = new Vector();
                while ( message.length() > 0 )
                {
                    patientInfos.addElement( message.substring( 0, message.indexOf( "$" ) ) );
                    message = message.substring( message.indexOf( "$" ) + 1, message.length() );
                }
                nameBasedMIDlet.getPersonListView().setPatientInfos( patientInfos );
                patientInfos = null;
                nameBasedMIDlet.getPersonListView().showView();
            }
            inputStream = null;
            System.gc();
        }
        catch ( IOException e )
        {
            LogMan.log( CLASS_TAG, e );
            e.printStackTrace();

            String message = null;
            String searchString = keyWord.substring( keyWord.lastIndexOf( ':' ) + 1 );

            if ( e.getMessage().equalsIgnoreCase( "NO_BENEFICIARY_FOUND" ) )
            {
                message = Text.NO_PERSON_FOUND() + ": " + searchString;
                nameBasedMIDlet.getNoMatchingView().prepareView();
                nameBasedMIDlet.getNoMatchingView().getTxtNotification().setText( message );
                nameBasedMIDlet.getNoMatchingView().getMainForm().show();
            }
            else if ( e.getMessage().equalsIgnoreCase( Text.HTTP_ERROR() )
                || e.getMessage().equalsIgnoreCase( "TCP open" ) )
            {
                message = "Your internet connection is currently unavailable. The search result you are about to see are from your local database. Click Next to continue or Back to go to the previous screen.";
                nameBasedMIDlet.getWarningView().prepareView();
                nameBasedMIDlet.getWarningView().setKeyword( searchString );
                nameBasedMIDlet.getWarningView().getTxtNotification().setText( message );
                nameBasedMIDlet.getWarningView().getMainForm().show();
            }
            else if ( e.getMessage().equalsIgnoreCase( "NO_PROGRAM_BELONG_ORGUNIT" ) )
            {
                nameBasedMIDlet.getAlertBoxView( "Program not belong to Org Unit", "Warning" ).showView();
                nameBasedMIDlet.getFindBeneficiaryView().showView();
            }

            else
            {
                message = e.getMessage();
                nameBasedMIDlet.getAlertBoxView( message, "Warning" ).showView();
                nameBasedMIDlet.getFindBeneficiaryView().showView();
                // Vector patientInfos = new Vector();
                // while ( message.length() > 0 )
                // {
                // patientInfos.addElement( message.substring( 0,
                // message.indexOf( "$" ) ) );
                // message = message.substring( message.indexOf( "$" ) + 1,
                // message.length() );
                // }
                // nameBasedMIDlet.getPersonListView().setPatientInfos(
                // patientInfos );
                // patientInfos = null;
                // nameBasedMIDlet.getPersonListView().showView();
            }
        }
    }

    protected String readMessage( DataInputStream dis )
    {
        LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "reading message..." );
        StringBuffer responseMessage = new StringBuffer();
        try
        {
            int ch;
            while ( (ch = dis.read()) != -1 )
            {
                responseMessage.append( (char) ch );
            }
        }
        catch ( IOException e )
        {
            e.printStackTrace();
            LogMan.log( CLASS_TAG, e );
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
                LogMan.log( CLASS_TAG, ioe );
            }
        }
        return responseMessage.toString();
    }

}
