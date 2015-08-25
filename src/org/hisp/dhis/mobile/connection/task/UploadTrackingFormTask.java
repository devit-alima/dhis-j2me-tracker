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
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.Patient;
import org.hisp.dhis.mobile.model.Program;
import org.hisp.dhis.mobile.model.ProgramInstance;
import org.hisp.dhis.mobile.model.ProgramStage;
import org.hisp.dhis.mobile.model.Section;
import org.hisp.dhis.mobile.recordstore.ProgramRecordStore;
import org.hisp.dhis.mobile.ui.Text;
import org.hisp.dhis.mobile.util.SerializationUtil;

/**
 * @author Nguyen Kim Lai
 * 
 * @version UpdateTrackingFormTask.java 10:46:09 AM Feb 21, 2013 $
 */
public class UploadTrackingFormTask
    extends AbstractTask
{
    private static final String CLASS_TAG = "UploadTrackingFormTask";

    private static final String PROGRAM_STAGE_UPLOADED = "program_stage_uploaded";

    private static final String PROGRAM_STAGE_SECTION_UPLOADED = "program_stage_section_uploaded";

    private static final String ANONYMOUS_PROGRAM_UPLOADED = "anonymous_program_uploaded";

    private static final String SINGLE_EVENT_UPLOADED = "single_event_uploaded";

    private Patient patient;

    private ProgramStage programStage;

    private ProgramStage uploadProgramStage;

    private Section section;

    public void run()
    {
        LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Starting UploadTrackingFormTask..." );
        NameBasedMIDlet nameBasedMIDlet = (NameBasedMIDlet) ConnectionManager.getDhisMIDlet();
        try
        {
            DataInputStream messageStream = this.upload( SerializationUtil.serialize( uploadProgramStage ) );
            String message = this.readMessage( messageStream );

            if ( message.startsWith( PROGRAM_STAGE_UPLOADED ) )
            {
                programStage.setCompleted( uploadProgramStage.isCompleted() );

                // for ( int i = 0; i < patient.getPrograms().size(); i++ )
                // {
                // if ( ((Program) patient.getPrograms().elementAt( i
                // )).getIsCompleted() != 1 )
                // {
                // if ( ((Program) patient.getPrograms().elementAt( i
                // )).getProgramStages().size() > 1 )
                // {
                // for ( int j = 0; j < ((Program)
                // patient.getPrograms().elementAt( i )).getProgramStages()
                // .size(); j++ )
                // {
                // final ProgramStage programStage = (ProgramStage) ((Program)
                // patient.getPrograms()
                // .elementAt( i )).getProgramStages().elementAt( j );
                // if ( programStage.getId() == this.programStage.getId() )
                // {
                // ((ProgramStage) ((Program) patient.getPrograms().elementAt( i
                // )).getProgramStages()
                // .elementAt( j )).setCompleted(
                // this.uploadProgramStage.isCompleted() );
                // }
                // }
                // }
                // }
                // }

                nameBasedMIDlet.getTrackingDataEntryView().setSection( null );
//                if ( programStage.isRepeatable() )
//                {
//                    String nextDate = message.substring( message.indexOf( "$" ) + 1, message.length() );
//                    nameBasedMIDlet.getGenerateRepeatableEventView( programStage, nextDate, patient ).showView();
//                }
//                else
//                {
//                    nameBasedMIDlet.getPersonDashboardView().setPatient( patient );
//                    nameBasedMIDlet.getAlertBoxView( programStage.getName(), Text.UPLOAD_COMPLETED() ).showView();
//                    nameBasedMIDlet.getPersonDashboardView().showView();
//                }

                try
                {
                    ProgramInstance currentProgram = (ProgramInstance) patient.getEnrollmentPrograms().elementAt( 0 );
                    Program program = ProgramRecordStore.getProgram( currentProgram.getProgramId() );

                    if ( hasRepeatableEvents( program ) )
                    {
                        Vector lastDueDates = new Vector();
                        Vector programStages = currentProgram.getProgramStageInstances();

                        for ( int j = 0; j < programStages.size(); j++ )
                        {
                            final ProgramStage programStage = (ProgramStage) programStages.elementAt( j );
                            if ( programStage.isSingleEvent() == false )
                            {
                                String date = "";
                                if ( !programStage.getReportDate().equals( "" ) )
                                {
                                    date = programStage.getReportDate();
                                }
                                else
                                {
                                    date = programStage.getDueDate();
                                }

                                boolean found = false;
                                for ( int i = 0; i < lastDueDates.size(); i++ )
                                {
                                    if ( ((String) lastDueDates.elementAt( i )).startsWith( programStage.getName() ) )
                                    {
                                        lastDueDates.setElementAt( programStage.getName() + "|" + date, i );
                                        found = true;
                                        break;
                                    }
                                }
                                if ( !found )
                                {
                                    lastDueDates.addElement( programStage.getName() + "|" + date );
                                }
                            }
                        }

                        nameBasedMIDlet.getRepeatableProgramStageListView().setPatient( patient );
                        nameBasedMIDlet.getRepeatableProgramStageListView().setProgram( program );
                        nameBasedMIDlet.getRepeatableProgramStageListView().setLastDueDates( lastDueDates );
                        nameBasedMIDlet.getRepeatableProgramStageListView().setProgramInstance( currentProgram );
                        nameBasedMIDlet.getRepeatableProgramStageListView().showView();
                    }
                    else
                    {
                        nameBasedMIDlet.getPersonDashboardView().setPatient( patient );
                        nameBasedMIDlet.getAlertBoxView( programStage.getName(), Text.UPLOAD_COMPLETED() ).showView();
                        nameBasedMIDlet.getPersonDashboardView().showView();
                    }
                }
                catch ( Exception e )
                {
                    e.printStackTrace();
                }
            }
            else if ( message.equalsIgnoreCase( PROGRAM_STAGE_SECTION_UPLOADED ) )
            {
                LogMan.log( LogMan.DEBUG, "Network," + CLASS_TAG, "PROGRAM_STAGE_SECTION_UPLOADED" );
                nameBasedMIDlet.getSectionListView().setPatient( patient );
                nameBasedMIDlet.getSectionListView().setProgramStage( programStage );
                nameBasedMIDlet.getAlertBoxView( section.getName(), Text.UPLOAD_COMPLETED() ).showView();
                nameBasedMIDlet.getSectionListView().showView();
            }
            else if ( message.equalsIgnoreCase( ANONYMOUS_PROGRAM_UPLOADED ) )
            {
                LogMan.log( LogMan.DEBUG, "Network," + CLASS_TAG, "ANONYMOUS_PROGRAM_UPLOADED" );
                nameBasedMIDlet.getAlertBoxView( programStage.getName(), Text.UPLOAD_COMPLETED() ).showView();
                nameBasedMIDlet.getTrackingMainMenuView().showView();
            }
            else if ( message.equalsIgnoreCase( SINGLE_EVENT_UPLOADED ) )
            {
                LogMan.log( LogMan.DEBUG, "Network," + CLASS_TAG, "--SINGLE_EVENT_UPLOADED" );
                programStage.setCompleted( true );
                nameBasedMIDlet.getAlertBoxView( programStage.getName(), Text.UPLOAD_COMPLETED() ).showView();
                nameBasedMIDlet.getTrackingMainMenuView().showView();
            }

            patient = null;
            programStage = null;
            uploadProgramStage = null;
            section = null;
            messageStream = null;
            message = null;
            System.gc();
        }
        catch ( IOException e )
        {
            LogMan.log( "Network," + CLASS_TAG, e );
            e.printStackTrace();

            if ( e.getMessage().equalsIgnoreCase( "INVALID_PROGRAM_STAGE" ) )
            {
                nameBasedMIDlet.getPersonDashboardView().setPatient( patient );
                nameBasedMIDlet.getAlertBoxView( "Invalid Program Stage", Text.ERROR() ).showView();
                nameBasedMIDlet.getPersonDashboardView().showView();
            }
            else if ( e.getMessage().equalsIgnoreCase( Text.HTTP_ERROR() )
                || e.getMessage().equalsIgnoreCase( "TCP open" )
                || e.getMessage().equalsIgnoreCase( "Error in HTTP operation" ) )
            {
                nameBasedMIDlet.getAlertBoxView( "Internet is not available, Please try again later.", "Alert" )
                    .showView();
                nameBasedMIDlet.getTrackingDataEntryView().setPatient( patient );
                nameBasedMIDlet.getTrackingDataEntryView().setProgramStage( programStage );
                nameBasedMIDlet.getTrackingDataEntryView().setTitle( programStage.getName() );
                nameBasedMIDlet.getTrackingDataEntryView().showView();
            }
            patient = null;
            programStage = null;
            uploadProgramStage = null;
            section = null;
            System.gc();
        }
        catch ( Exception e )
        {
            LogMan.log( LogMan.DEV, "Network," + CLASS_TAG, e.getMessage() );
        }
    }

    public boolean hasRepeatableEvents( Program program )
    {
        for ( int i = 0; i < program.getProgramStages().size(); i++ )
        {
            ProgramStage programStage = (ProgramStage) program.getProgramStages().elementAt( i );
            if ( programStage.isRepeatable() )
            {
                return true;
            }
        }
        return false;
    }

    public Patient getPatient()
    {
        return patient;
    }

    public void setPatient( Patient patient )
    {
        this.patient = patient;
    }

    public ProgramStage getProgramStage()
    {
        return programStage;
    }

    public void setProgramStage( ProgramStage programStage )
    {
        this.programStage = programStage;
    }

    public ProgramStage getUploadProgramStage()
    {
        return uploadProgramStage;
    }

    public void setUploadProgramStage( ProgramStage uploadProgramStage )
    {
        this.uploadProgramStage = uploadProgramStage;
    }

    public Section getSection()
    {
        return section;
    }

    public void setSection( Section section )
    {
        this.section = section;
    }

}
