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
package org.hisp.dhis.mobile.view;

/**
 * @author Nguyen Kim Lai
 *
 * @version PersonDashboardView.java 3:03:11 PM Feb 15, 2013 $
 */

import java.io.IOException;
import java.util.Vector;

import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.LinkButton;
import org.hisp.dhis.mobile.model.ModelList;
import org.hisp.dhis.mobile.model.Patient;
import org.hisp.dhis.mobile.model.PatientAttribute;
import org.hisp.dhis.mobile.model.Program;
import org.hisp.dhis.mobile.model.ProgramInstance;
import org.hisp.dhis.mobile.model.ProgramStage;
import org.hisp.dhis.mobile.model.Relationship;
import org.hisp.dhis.mobile.model.RelationshipType;
import org.hisp.dhis.mobile.recordstore.PatientRecordStore;
import org.hisp.dhis.mobile.recordstore.ProgramRecordStore;
import org.hisp.dhis.mobile.recordstore.RelationshipTypeRecordStore;
import org.hisp.dhis.mobile.recordstore.RelativeRelationshipRecordStore;
import org.hisp.dhis.mobile.ui.Text;

import com.sun.lwuit.Button;
import com.sun.lwuit.Command;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Display;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.plaf.Style;

public class PersonDashboardView
    extends AbstractView
    implements ActionListener
{
    private static final String CLASS_TAG = "PersonDashboardView";

    private NameBasedMIDlet namebasedMidlet;

    private Form mainForm;

    private Command backCommand;

    private Patient patient;

    private Program vsProgram;

    private int previousScreen;

    private Dialog confirmDialog;

    private int relativeLinkCount;

    public PersonDashboardView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.namebasedMidlet = (NameBasedMIDlet) dhisMIDlet;
    }

    public void prepareView()
    {
        System.gc();
        this.getMainForm();

        // clear previous data
        mainForm.removeAll();

        Style labelStyle = new Style();
        labelStyle.setBgColor( Text.HEADER_BG_COLOR() );

        // show current programs
        if ( patient.getEnrollmentPrograms() != null )
        {
            Label currentProgramlbl = new Label( "Active Programs" );
            currentProgramlbl.setUnselectedStyle( labelStyle );
            currentProgramlbl.setSelectedStyle( labelStyle );
            mainForm.addComponent( currentProgramlbl );
            this.preparePrograms();

        }

        // show completed programs
        if ( patient.getCompletedPrograms() != null )
        {
            Label completedProgramlbl = new Label( "Completed Programs" );
            completedProgramlbl.setUnselectedStyle( labelStyle );
            completedProgramlbl.setSelectedStyle( labelStyle );
            mainForm.addComponent( completedProgramlbl );
            this.prepareCompletedPrograms();
        }

        Label lblDetail = new Label( "Details" );
        lblDetail.setUnselectedStyle( labelStyle );
        lblDetail.setSelectedStyle( labelStyle );

        TextArea org = new TextArea();
        if ( patient.getOrganisationUnitName() == null )
        {
            org.setText( "Organisation Unit: empty" );
        }
        else
        {
            org.setText( "Organisation Unit: " + patient.getOrganisationUnitName() );
        }
        org.setEditable( false );
        org.setEnabled( false );

        mainForm.addComponent( lblDetail );
        mainForm.addComponent( org );

        for ( int i = 0; i < patient.getPatientAttValues().size(); i++ )
        {
            PatientAttribute patientAttribute = (PatientAttribute) patient.getPatientAttValues().elementAt( i );
            TextArea attributeTextare = new TextArea( patientAttribute.getName() + ": " + patientAttribute.getValue() );
            attributeTextare.setEditable( false );
            attributeTextare.setEnabled( false );
            mainForm.addComponent( attributeTextare );
        }

        // show enrollable programs
        Label lblEnrollmentProgram = new Label( "Enroll In Program" );
        lblEnrollmentProgram.setUnselectedStyle( labelStyle );
        lblEnrollmentProgram.setSelectedStyle( labelStyle );
        mainForm.addComponent( lblEnrollmentProgram );
        this.prepareEnrollmentPrograms();

        // show relationship
        if ( patient.getRelationships() != null )
        {
            Label relationshiplbl = new Label( "Relationships" );
            relationshiplbl.setUnselectedStyle( labelStyle );
            relationshiplbl.setSelectedStyle( labelStyle );
            mainForm.addComponent( relationshiplbl );
            this.prepareRelationships();
        }

        // show enrollment relationship
        Label lblEnrollmentRelationship = new Label( "Add Relative" );
        lblEnrollmentRelationship.setUnselectedStyle( labelStyle );
        lblEnrollmentRelationship.setSelectedStyle( labelStyle );
        mainForm.addComponent( lblEnrollmentRelationship );
        relativeLinkCount = 0;
        this.prepareEnrollmentRelationships();
        try
        {
            this.prepareShortcutLinkToAddRelative();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

        // Add new

    }

    public void showView()
    {
        this.prepareView();
        this.getMainForm().show();
    }

    public void actionPerformed( ActionEvent ae )
    {
        if ( ae.getCommand().getCommandName().equals( Text.BACK() ) )
        {
            mainForm = null;
            backCommand = null;
            try
            {
                PatientRecordStore.savePatient( patient );
            }
            catch ( Exception e )
            {
                LogMan.log( "UI," + CLASS_TAG, e );
                e.printStackTrace();
            }
            patient = null;
            System.gc();

            if ( previousScreen == namebasedMidlet.HISTORY_PERSON_LIST_VIEW )
            {
                ModelList modelList;
                try
                {
                    modelList = PatientRecordStore.getCurrentPatients();
                    namebasedMidlet.getHistoryPersonListView().setModelList( modelList );
                    namebasedMidlet.getHistoryPersonListView().showView();
                }
                catch ( Exception e )
                {
                    LogMan.log( "UI," + CLASS_TAG, e );
                    e.printStackTrace();
                }
            }
            else if ( previousScreen == namebasedMidlet.VISIT_SCHEDULE_VIEW )
            {
                namebasedMidlet.getVisitScheduleMenuView().setProgram( vsProgram );
                namebasedMidlet.getVisitScheduleMenuView().showView();
            }
            else
            {
                namebasedMidlet.getFindBeneficiaryView().getMainForm()
                    .removeComponent( namebasedMidlet.getFindBeneficiaryView().getNotification() );
                namebasedMidlet.getFindBeneficiaryView().showView();
            }
        }
    }

    public int getPreviousScreen()
    {
        return previousScreen;
    }

    public void setPreviousScreen( int previousScreen )
    {
        this.previousScreen = previousScreen;
    }

    public Form getMainForm()
    {
        if ( mainForm == null )
        {
            mainForm = new Form( "Dashboard" );
            mainForm.setLayout( new BoxLayout( BoxLayout.Y_AXIS ) );
            mainForm.setScrollableY( true );
            mainForm.setTransitionOutAnimator( CommonTransitions.createSlide( CommonTransitions.SLIDE_HORIZONTAL, true,
                200 ) );

            mainForm.addCommand( this.getBackCommand() );
            mainForm.addCommandListener( this );
        }
        return mainForm;

    }

    public void setMainForm( Form mainForm )
    {
        this.mainForm = mainForm;
    }

    public Command getBackCommand()
    {
        if ( backCommand == null )
        {
            backCommand = new Command( Text.BACK() );
        }
        return backCommand;
    }

    public void setBackCommand( Command backCommand )
    {
        this.backCommand = backCommand;
    }

    public void preparePrograms()
    {
        Vector programs = patient.getEnrollmentPrograms();

        if ( programs != null )
        {
            for ( int i = 0; i < programs.size(); i++ )
            {
                final ProgramInstance programInstance = (ProgramInstance) programs.elementAt( i );

                if ( programInstance.getProgramStageInstances().size() > 1 )
                {
                    LinkButton programLink = new LinkButton( programInstance.getName() );

                    programLink.addActionListener( new ActionListener()
                    {
                        public void actionPerformed( ActionEvent ae )
                        {
                            namebasedMidlet.getProgramStageListView().setPatient( patient );
                            namebasedMidlet.getProgramStageListView().setProgramInstance( programInstance );
                            namebasedMidlet.getProgramStageListView().showView();
                        }
                    } );

                    mainForm.addComponent( programLink );

                    // show uncompleted program stage
                    for ( int j = 0; j < programInstance.getProgramStageInstances().size(); j++ )
                    {
                        final ProgramStage programStage = (ProgramStage) programInstance.getProgramStageInstances()
                            .elementAt( j );
                        if ( programStage.isCompleted() == false )
                        {
                            String label = programStage.getName();
                            if ( programStage.getReportDate() != null && !programStage.getReportDate().equals( "" ) )
                            {
                                label += " (" + programStage.getReportDate() + ")";
                            }
                            else if ( programStage.getDueDate() != null && !programStage.getDueDate().equals( "" ) )
                            {
                                label += " (" + programStage.getDueDate() + ")";
                            }
                            LinkButton programStageLink = new LinkButton( "--" + label );
                            programStageLink.addActionListener( new ActionListener()
                            {
                                public void actionPerformed( ActionEvent arg )
                                {
                                    if ( programStage.getSections().size() > 0 )
                                    {
                                        namebasedMidlet.getSectionListView().setPatient( patient );
                                        namebasedMidlet.getSectionListView().setProgramStage( programStage );
                                        namebasedMidlet.getSectionListView().showView();
                                    }
                                    else
                                    {
                                        namebasedMidlet.getTrackingDataEntryView().setPatient( patient );
                                        namebasedMidlet.getTrackingDataEntryView().setProgramStage( programStage );
                                        namebasedMidlet.getTrackingDataEntryView().setTitle( programStage.getName() );
                                        namebasedMidlet.getTrackingDataEntryView().showView();
                                    }
                                }
                            } );
                            mainForm.addComponent( programStageLink );
                            break;
                        }
                    }
                }
                // show program has only one program stage which is repeatable
                else if ( programInstance.getProgramStageInstances().size() == 1 )
                {
                    // Single event
                    final ProgramStage programStageSingle = (ProgramStage) programInstance.getProgramStageInstances()
                        .elementAt( 0 );
                    LinkButton programLink = new LinkButton( programInstance.getName() );
                    programLink.addActionListener( new ActionListener()
                    {
                        public void actionPerformed( ActionEvent ae )
                        {
                            namebasedMidlet.getTrackingDataEntryView().setPatient( patient );
                            namebasedMidlet.getTrackingDataEntryView().setProgramStage( programStageSingle );
                            namebasedMidlet.getTrackingDataEntryView().setTitle( programStageSingle.getName() );
                            namebasedMidlet.getTrackingDataEntryView().showView();
                        }
                    } );

                    mainForm.addComponent( programLink );
                    final ProgramStage programStage = (ProgramStage) programInstance.getProgramStageInstances()
                        .elementAt( 0 );
                    if ( programStage.isRepeatable() )
                    {
                        LinkButton programStageLink = new LinkButton( "--" + programStage.getName() );
                        programStageLink.addActionListener( new ActionListener()
                        {
                            public void actionPerformed( ActionEvent arg )
                            {
                                namebasedMidlet.getTrackingDataEntryView().setPatient( patient );
                                namebasedMidlet.getTrackingDataEntryView().setProgramStage( programStage );
                                namebasedMidlet.getTrackingDataEntryView().showView();
                            }
                        } );
                        mainForm.addComponent( programStageLink );
                    }
                }
                System.gc();
            }
        }
        programs = null;
        System.gc();
    }

    public void prepareShortcutLinkToAddRelative()
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        Vector programs = patient.getEnrollmentPrograms();

        if ( programs != null )
        {
            for ( int i = 0; i < programs.size(); i++ )
            {
                final ProgramInstance programInstance = (ProgramInstance) programs.elementAt( i );
                final Program program = ProgramRecordStore.getProgram( programInstance.getProgramId() );
                final Program relativeProgram = ProgramRecordStore.getProgram( program.getRelatedProgramId() );
                if ( relativeProgram != null )
                {
                    LinkButton shortcutLink = new LinkButton( program.getRelationshipText() );
                    shortcutLink.addActionListener( new ActionListener()
                    {

                        public void actionPerformed( ActionEvent ae )
                        {
                            namebasedMidlet.getWaitingView().showView();
                            try
                            {
                                RelativeRelationshipRecordStore.deleteAll();
                                RelativeRelationshipRecordStore.saveId( String.valueOf( patient.getId() ) );

                                if ( program.getType() != 3 )
                                {
                                    ConnectionManager
                                        .setUrl( namebasedMidlet.getCurrentOrgUnit().getGetVariesInfoUrl() );
                                    namebasedMidlet.getAddRelativeView().setProgram( relativeProgram );
                                    namebasedMidlet.getAddRelativeView().setRelative( patient );
                                    namebasedMidlet.getAddRelativeView().setRelationshipType(
                                        program.getRelationshipType() );
                                    namebasedMidlet.getAddRelativeView().prepareView();
                                    namebasedMidlet.getAddRelativeView().showView();
                                }
                                else
                                {
                                    namebasedMidlet.getAddRelativeView().setProgram( program );
                                    namebasedMidlet.getAddRelativeView().prepareView();
                                    namebasedMidlet.getAddRelativeView().showView();
                                }

                            }
                            catch ( Exception e )
                            {
                                e.printStackTrace();
                            }

                        }
                    } );

                    mainForm.addComponent( shortcutLink );
                    relativeLinkCount++;
                }

            }
        }

        if ( relativeLinkCount == 0 )
        {
            LinkButton relationshipLink = new LinkButton( "Not Available" );
            mainForm.addComponent( relationshipLink );
        }

        programs = null;
        System.gc();

    }

    public void prepareEnrollmentPrograms()
    {
        Vector programs = null;

        try
        {
            programs = ProgramRecordStore.getEnrollablePrograms( patient );
        }
        catch ( Exception e )
        {
            LogMan.log( "UI," + CLASS_TAG, e );
            e.printStackTrace();
        }
        for ( int i = 0; i < programs.size(); i++ )
        {
            final Program program = (Program) programs.elementAt( i );

            if ( program.getType() != 3 )
            {

                LinkButton enrollmentProgramLink = new LinkButton( program.getName() );

                enrollmentProgramLink.addActionListener( new ActionListener()
                {
                    public void actionPerformed( ActionEvent arg )
                    {
                        confirmDialog = new Dialog();
                        confirmDialog.setTitle( "Confirmation" );
                        Button yButton = new Button( Text.YES() );
                        Button nButton = new Button( Text.NO() );
                        TextArea textArea = new TextArea( "Do you want to enroll in this program?" );
                        textArea.setFocusable( false );

                        yButton.addActionListener( new ActionListener()
                        {

                            public void actionPerformed( ActionEvent ae )
                            {
                                try
                                {
                                    ConnectionManager
                                        .setUrl( namebasedMidlet.getCurrentOrgUnit().getGetVariesInfoUrl() );
                                    namebasedMidlet.getPersonRegistrationView().setProgram( program );
                                    namebasedMidlet.getPersonRegistrationView().setPatient( patient );
                                    namebasedMidlet.getPersonRegistrationView().prepareView();
                                    namebasedMidlet.getPersonRegistrationView().showView();
                                }
                                catch ( Exception e )
                                {
                                    e.printStackTrace();
                                }
                            }
                        } );

                        nButton.addActionListener( new ActionListener()
                        {

                            public void actionPerformed( ActionEvent ae )
                            {
                                confirmDialog = (Dialog) Display.getInstance().getCurrent();
                                confirmDialog.dispose();
                                confirmDialog = null;
                                System.gc();

                            }
                        } );
                        confirmDialog.addComponent( textArea );
                        confirmDialog.addComponent( yButton );
                        confirmDialog.addComponent( nButton );

                        confirmDialog.show();
                    }
                } );

                mainForm.addComponent( enrollmentProgramLink );
            }
        }

    }

    public void prepareRelationships()
    {
        if ( patient.getRelationships() != null )
        {
            for ( int i = 0; i < patient.getRelationships().size(); i++ )
            {
                final Relationship relationship = (Relationship) patient.getRelationships().elementAt( i );

                LinkButton relationshipLink = new LinkButton( relationship.getbIsToA() + ": "
                    + relationship.getPersonBName() );

                relationshipLink.addActionListener( new ActionListener()
                {
                    public void actionPerformed( ActionEvent arg )
                    {
                        mainForm = null;
                        backCommand = null;
                        patient = null;
                        namebasedMidlet.getWaitingView().showView();
                        ConnectionManager.setUrl( namebasedMidlet.getCurrentOrgUnit().getFindPatientUrl() );
                        ConnectionManager.getPatientById( relationship.getPersonBId() + "", previousScreen, null );
                    }
                } );

                mainForm.addComponent( relationshipLink );
            }
        }
    }

    public void prepareEnrollmentRelationships()
    {
        Vector relTypes = null;
        try
        {
            relTypes = RelationshipTypeRecordStore.loadAllRelationshipType();

        }
        catch ( Exception e )
        {
            LogMan.log( "UI," + CLASS_TAG, e );
            e.printStackTrace();
        }
        if ( relTypes == null || relTypes.size() > 0 )
        {
            for ( int i = 0; i < relTypes.size(); i++ )
            {
                final RelationshipType relType = (RelationshipType) relTypes.elementAt( i );
                final Relationship enrollmentRelationship = new Relationship();
                enrollmentRelationship.setaIsToB( relType.getAIsToB() );
                enrollmentRelationship.setbIsToA( relType.getBIsToA() );
                enrollmentRelationship.setId( relType.getId() );
                enrollmentRelationship.setName( relType.getName() );

                LinkButton enrollmentRelationshipLink = new LinkButton( enrollmentRelationship.getName() );
                enrollmentRelationshipLink.addActionListener( new ActionListener()
                {
                    public void actionPerformed( ActionEvent ae )
                    {
                        enrollmentRelationship.setPersonAId( patient.getId() );
                        enrollmentRelationship.setPersonAName( patient.getDisplayInListAttributeValues() );
                        namebasedMidlet.getAddingRelationshipView().setPatient( patient );
                        namebasedMidlet.getAddingRelationshipView().setEnrollmentRelationship( enrollmentRelationship );
                        namebasedMidlet.getAddingRelationshipView().resetIndexes();
                        namebasedMidlet.getAddingRelationshipView().showView();
                    }
                } );
                relativeLinkCount++;
                mainForm.addComponent( enrollmentRelationshipLink );
            }
        }
    }

    public void prepareCompletedPrograms()
    {
        Vector completedPrograms = patient.getCompletedPrograms();
        if ( completedPrograms != null )
        {
            for ( int i = 0; i < completedPrograms.size(); i++ )
            {
                final ProgramInstance programInstance = (ProgramInstance) completedPrograms.elementAt( i );

                if ( programInstance.getProgramStageInstances().size() > 1 )
                {
                    LinkButton programLink = new LinkButton( programInstance.getName() );

                    programLink.addActionListener( new ActionListener()
                    {
                        public void actionPerformed( ActionEvent ae )
                        {
                            namebasedMidlet.getProgramStageListView().setPatient( patient );
                            namebasedMidlet.getProgramStageListView().setProgramInstance( programInstance );
                            namebasedMidlet.getProgramStageListView().showView();
                        }
                    } );

                    mainForm.addComponent( programLink );
                }
                // show program has only one program stage which is repeatable
                else if ( programInstance.getProgramStageInstances().size() == 1 )
                {
                    // Single event
                    final ProgramStage programStageSingle = (ProgramStage) programInstance.getProgramStageInstances()
                        .elementAt( 0 );
                    LinkButton programLink = new LinkButton( programInstance.getName() );
                    programLink.addActionListener( new ActionListener()
                    {
                        public void actionPerformed( ActionEvent ae )
                        {
                            namebasedMidlet.getTrackingDataEntryView().setPatient( patient );
                            namebasedMidlet.getTrackingDataEntryView().setProgramStage( programStageSingle );
                            namebasedMidlet.getTrackingDataEntryView().setTitle( programStageSingle.getName() );
                            namebasedMidlet.getTrackingDataEntryView().showView();
                        }
                    } );

                    mainForm.addComponent( programLink );
                    final ProgramStage programStage = (ProgramStage) programInstance.getProgramStageInstances()
                        .elementAt( 0 );
                    if ( programStage.isRepeatable() )
                    {
                        LinkButton programStageLink = new LinkButton( "--" + programStage.getName() );
                        programStageLink.addActionListener( new ActionListener()
                        {
                            public void actionPerformed( ActionEvent arg )
                            {
                                namebasedMidlet.getTrackingDataEntryView().setPatient( patient );
                                namebasedMidlet.getTrackingDataEntryView().setProgramStage( programStage );
                                namebasedMidlet.getTrackingDataEntryView().showView();
                            }
                        } );
                        mainForm.addComponent( programStageLink );
                    }
                }
            }
        }
        completedPrograms = null;
        System.gc();
    }

    public Patient getPatient()
    {
        return patient;
    }

    public void setPatient( Patient patient )
    {
        this.patient = patient;
    }

    public Program getVSProgram()
    {
        return vsProgram;
    }

    public void setVSProgram( Program vsProgram )
    {
        this.vsProgram = vsProgram;
    }
}
