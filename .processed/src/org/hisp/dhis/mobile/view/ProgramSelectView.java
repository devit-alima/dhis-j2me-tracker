package org.hisp.dhis.mobile.view;

import java.util.Vector;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.LinkButton;
import org.hisp.dhis.mobile.model.ModelList;
import org.hisp.dhis.mobile.model.Patient;
import org.hisp.dhis.mobile.model.Program;
import org.hisp.dhis.mobile.model.ProgramStage;
import org.hisp.dhis.mobile.model.Relationship;
import org.hisp.dhis.mobile.recordstore.OrgUnitRecordStore;
import org.hisp.dhis.mobile.recordstore.PatientRecordStore;
import org.hisp.dhis.mobile.ui.Text;

import com.sun.lwuit.CheckBox;
import com.sun.lwuit.ComboBox;
import com.sun.lwuit.Command;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.plaf.Style;

public class ProgramSelectView
    extends AbstractView
    implements ActionListener
{
    private static final String CLASS_TAG = "ProgramSelectView";

    private Form mainForm;

    private Vector programs;

    private Command backCommand;

    private NameBasedMIDlet nameBasedMIDlet;

    private String programType;

    private int targetScreen;

    private ComboBox cbbStatus;

    private CheckBox chbRisk;
    
    private Patient relative;
    
    private Relationship enrollmentRelationship;

    public ProgramSelectView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.nameBasedMIDlet = (NameBasedMIDlet) this.dhisMIDlet;
    }

    public void actionPerformed( ActionEvent ae )
    {
        if ( ae.getCommand().getCommandName().equals( Text.BACK() ) )
        {
            try
            {
                if ( targetScreen != nameBasedMIDlet.HISTORY_PERSON_LIST_VIEW && OrgUnitRecordStore.loadAllOrgUnit().size() > 1 )
                {
                    nameBasedMIDlet.getOrgUnitSelectView().setProgramType( programType );
                    nameBasedMIDlet.getOrgUnitSelectView().setTargetScreen( targetScreen );
                    nameBasedMIDlet.getOrgUnitSelectView().showView();
                }
                else
                {
                    nameBasedMIDlet.getTrackingMainMenuView().showView();
                }
            }
            catch ( Exception e )
            {
                LogMan.log( "UI," + CLASS_TAG, e );
                e.printStackTrace();
            }
        }
    }

    public void prepareView()
    {
        System.gc();
        getMainForm();
        mainForm.removeAll();

        Style labelStyle = new Style();
        labelStyle.setBgColor( Text.HEADER_BG_COLOR() );

        // Status section
        // Label lblStatus = new Label( "Status" );
        // lblStatus.setUnselectedStyle( labelStyle );
        // lblStatus.setSelectedStyle( labelStyle );
        // mainForm.addComponent( lblStatus );
        // mainForm.addComponent( getCbbStatus() );
        // mainForm.addComponent( getChbRisk() );

        // Program section
        Label lblProgram = new Label( "Programs" );
        lblProgram.setUnselectedStyle( labelStyle );
        lblProgram.setSelectedStyle( labelStyle );
        mainForm.addComponent( lblProgram );

        for ( int i = 0; i < programs.size(); i++ )
        {
            final Program program = (Program) programs.elementAt( i );

            // final String programId = programInfo.substring( 0,
            // programInfo.indexOf( "/" ) );
            // String programName = programInfo.substring( programInfo.indexOf(
            // "/" ) + 1, programInfo.length() );

            LinkButton programLink = new LinkButton( program.getName() );
            programLink.addActionListener( new ActionListener()
            {
                public void actionPerformed( ActionEvent ae )
                {
                    nameBasedMIDlet.getWaitingView().showView();
                    try
                    {
                        if ( targetScreen == nameBasedMIDlet.PERSON_REGISTRATION_VIEW ||
                            targetScreen == nameBasedMIDlet.SINGLE_EVENT_WITHOUT_REGISTRATION )
                        {
                            if ( program.getType() != 3 )
                            {
                                ConnectionManager.setUrl( nameBasedMIDlet.getCurrentOrgUnit().getGetVariesInfoUrl() );
                                nameBasedMIDlet.getPersonRegistrationView().setProgram( program );
                                if(relative!=null)
                                {
                                    nameBasedMIDlet.getPersonRegistrationView().setRelative( relative );
                                    nameBasedMIDlet.getPersonRegistrationView().setEnrollmentRelationship( enrollmentRelationship );
                                }
                                nameBasedMIDlet.getPersonRegistrationView().prepareView();
                                nameBasedMIDlet.getPersonRegistrationView().showView();
                            }
                            else
                            {
                                nameBasedMIDlet.getTrackingDataEntryView().setProgramStage(
                                    (ProgramStage) program.getProgramStages().elementAt( 0 ) );
                                nameBasedMIDlet.getTrackingDataEntryView().setTitle( program.getName() );
                                nameBasedMIDlet.getTrackingDataEntryView().showView();
                            }

                        }
                        else if ( targetScreen == nameBasedMIDlet.VISIT_SCHEDULE_VIEW )
                        {
                            nameBasedMIDlet.getVisitScheduleMenuView().setProgram( program );
                            nameBasedMIDlet.getVisitScheduleMenuView().showView();
                        }
                        else if ( targetScreen == nameBasedMIDlet.LOST_TO_FOLLOW_UP_VIEW )
                        {
                            String status = (String) cbbStatus.getSelectedItem();
                            boolean isRiskCase = chbRisk.isSelected();
                            ConnectionManager.setUrl( nameBasedMIDlet.getCurrentOrgUnit().getFindLostToFollowUpUrl() );
                            ConnectionManager.findLostToFollowUp( program.getId() + "-" + status + "-" + isRiskCase );
                        }
                        else if ( targetScreen == nameBasedMIDlet.HISTORY_PERSON_LIST_VIEW )
                        {
                            ModelList modelList = PatientRecordStore.getCurrentPatients();
                            nameBasedMIDlet.getHistoryPersonListView().setProgram( program );
                            nameBasedMIDlet.getHistoryPersonListView().setModelList( modelList );
                            nameBasedMIDlet.getHistoryPersonListView().showView();
                        }

                    }
                    catch ( Exception e )
                    {
                        LogMan.log( "UI," + CLASS_TAG, e );
                        e.printStackTrace();
                    }
                }
            } );
            mainForm.addComponent( programLink );
        }
    }

    public void showView()
    {
        prepareView();
        mainForm.show();
    }

    public Command getBackCommand()
    {
        if ( backCommand == null )
        {
            backCommand = new Command( Text.BACK() );
        }
        return backCommand;
    }

    public Form getMainForm()
    {
        if ( mainForm == null )
        {
            mainForm = new Form( "Select Program" );
            mainForm.setLayout( new BoxLayout( BoxLayout.Y_AXIS ) );
            mainForm.setTransitionOutAnimator( CommonTransitions.createSlide( CommonTransitions.SLIDE_HORIZONTAL, true,
                200 ) );
            mainForm.addCommandListener( this );
            mainForm.addCommand( new Command( "Back" ) );

        }
        return mainForm;
    }

    public void setProgramInfos( Vector programInfos )
    {
        this.programs = programInfos;
    }

    public void setProgramType( String programType )
    {
        this.programType = programType;
    }

    public void setTargetScreen( int targetScreen )
    {
        this.targetScreen = targetScreen;
    }

    public ComboBox getCbbStatus()
    {
        if ( cbbStatus == null )
        {
            cbbStatus = new ComboBox();
            cbbStatus.addItem( "Overdue" );
            cbbStatus.addItem( "Scheduled in future" );
            cbbStatus.setSmoothScrolling( true );
        }
        return cbbStatus;
    }

    public CheckBox getChbRisk()
    {
        if ( chbRisk == null )
        {
            chbRisk = new CheckBox( "Only Risk Case" );
        }
        return chbRisk;
    }

    public Vector getPrograms()
    {
        return programs;
    }

    public void setPrograms( Vector programs )
    {
        this.programs = programs;
    }
    
    public void setRelative( Patient relative )
    {
        this.relative = relative;
    }
    
    public void setEnrollmentRelationship( Relationship enrollmentRelationship )
    {
        this.enrollmentRelationship = enrollmentRelationship;
    }
}
