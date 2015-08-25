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
import org.hisp.dhis.mobile.model.Program;
import org.hisp.dhis.mobile.model.ProgramStage;
import org.hisp.dhis.mobile.ui.Text;

/**
 * @author Nguyen Kim Lai
 * 
 * @version GetAllAnonymousProgramTask.java 2:56:09 PM Mar 22, 2013 $
 */
public class GetProgramsTask
    extends AbstractTask
{
    public static final String CLASS_TAG = "GetProgramsTask";
    
    public static final String ANONYMOUS_TYPE = "3";

    public static final String MULTI_EVENT_TYPE = "1";

    private String programType;

    private int targetScreen;

    private NameBasedMIDlet nameBasedMIDlet;

    public GetProgramsTask( String programType, int targetScreen )
    {
        this.programType = programType;
        this.targetScreen = targetScreen;
    }

    public void run()
    {
        LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Starting GetProgramsTask..." );
        this.nameBasedMIDlet = (NameBasedMIDlet) ConnectionManager.getDhisMIDlet();
        DataInputStream dataInputStream = null;
        try
        {
            dataInputStream = this.download( programType, "programType" );
            Program program = new Program();
            program.deSerialize( dataInputStream );

            // Anonymous is 3
            if ( programType.equals( ANONYMOUS_TYPE ) )
            {
                ProgramStage programStage = (ProgramStage) program.getProgramStages().elementAt( 0 );

                nameBasedMIDlet.getTrackingDataEntryView().setProgramStage( programStage );
                nameBasedMIDlet.getTrackingDataEntryView().setTitle( program.getName() );
                nameBasedMIDlet.getTrackingDataEntryView().showView();
                programStage = null;
            }
            // Multiple events with registration is 1, or lost to follow up
            // events
            else
            {
                Vector programInfos = new Vector();
                programInfos.addElement( program.getId() + "/" + program.getName() );
                nameBasedMIDlet.getProgramSelectView().setProgramInfos( programInfos );
                programInfos = null;
                nameBasedMIDlet.getProgramSelectView().setProgramType( programType );
                nameBasedMIDlet.getProgramSelectView().setTargetScreen( targetScreen );
                nameBasedMIDlet.getProgramSelectView().showView();
            }

            dataInputStream = null;
            programType = null;
            program = null;
            System.gc();
        }
        catch ( IOException e )
        {
            LogMan.log( CLASS_TAG, e );
            e.printStackTrace();
            String message = null;

            message = e.getMessage();
            if ( e.getMessage().equalsIgnoreCase( Text.HTTP_ERROR() ) )
            {
                nameBasedMIDlet.getAlertBoxView( "Internet is not available, Please try again later.", "Alert" )
                    .showView();
                nameBasedMIDlet.getTrackingMainMenuView().showView();
            }
            else if ( message.equalsIgnoreCase( "NO_PROGRAM_FOUND" ) )
            {
                nameBasedMIDlet.getAlertBoxView(
                    "No program found for " + nameBasedMIDlet.getCurrentOrgUnit().getName(), "Opps" ).showView();
                nameBasedMIDlet.getOrgUnitSelectView().setProgramType( programType );
                // nameBasedMIDlet.getOrgUnitSelectView().showView();
                ConnectionManager.setUrl( nameBasedMIDlet.getCurrentOrgUnit().getGetVariesInfoUrl() );
                nameBasedMIDlet.getPersonRegistrationView().prepareView();
                nameBasedMIDlet.getPersonRegistrationView().showView();
            }
            else
            {
                Vector programInfos = new Vector();
                while ( message.length() > 0 )
                {
                    programInfos.addElement( message.substring( 0, message.indexOf( "$" ) ) );
                    message = message.substring( message.indexOf( "$" ) + 1, message.length() );
                }

                // Anonymous is 3
                if ( programType.equals( "3" ) )
                {
                    nameBasedMIDlet.getAnonymousProgramListView().setAnonymousProgramInfos( programInfos );
                    nameBasedMIDlet.getAnonymousProgramListView().showView();
                }
                // Multiple events with registration is 1, or lost to follow up
                // events
                else
                {
                    nameBasedMIDlet.getProgramSelectView().setProgramInfos( programInfos );
                    nameBasedMIDlet.getProgramSelectView().setProgramType( programType );
                    nameBasedMIDlet.getProgramSelectView().setTargetScreen( targetScreen );
                    nameBasedMIDlet.getProgramSelectView().showView();
                }

                message = null;
                programInfos = null;
            }
            programType = null;
            nameBasedMIDlet = null;
            System.gc();
        }
    }

}
