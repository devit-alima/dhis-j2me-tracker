package org.hisp.dhis.mobile.view;

import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.Patient;
import org.hisp.dhis.mobile.model.ProgramInstance;
import org.hisp.dhis.mobile.model.ProgramStage;
import org.hisp.dhis.mobile.ui.Text;

import com.sun.lwuit.Command;
import com.sun.lwuit.Form;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;

public class DashboardLinkView
    extends AbstractView
    implements ActionListener
{
    private static final String CLASS_TAG = "DashboardLinkView";
    
    private Form dashboardLinkForm;

    private TextArea linkTextArea;

    private Command backCommand;

    private Command nextCommand;

    private Patient patient;

    private int enrollProgramId;

    private NameBasedMIDlet namebasedMidlet;

    public DashboardLinkView( DHISMIDlet dhisMIDlet, Patient patient )
    {
        super( dhisMIDlet );
        this.patient = patient;
    }

    public Form getDashboardLinkForm()
    {
        if ( dashboardLinkForm == null )
        {
            dashboardLinkForm = new Form( Text.REGISTERED_SUCCESSFULLY() );
            dashboardLinkForm.setLayout( new BoxLayout( BoxLayout.Y_AXIS ) );
            dashboardLinkForm.setTransitionOutAnimator( CommonTransitions.createSlide(
                CommonTransitions.SLIDE_HORIZONTAL, true, 200 ) );
            dashboardLinkForm.addCommand( this.getBackCommand() );
            dashboardLinkForm.addCommand( this.getNextCommand() );

            linkTextArea = new TextArea( Text.LINKDASHBOARD() );
            linkTextArea.setEditable( false );

            dashboardLinkForm.addCommandListener( this );
            dashboardLinkForm.addComponent( linkTextArea );
        }
        return dashboardLinkForm;
    }

    public void setDashboardLinkForm( Form dashboardLinkForm )
    {
        this.dashboardLinkForm = dashboardLinkForm;
    }

    public TextArea getLinkTextArea()
    {
        return linkTextArea;
    }

    public void setLinkTextArea( TextArea linkTextArea )
    {
        this.linkTextArea = linkTextArea;
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

    public Command getNextCommand()
    {
        if ( nextCommand == null )
        {
            nextCommand = new Command( Text.NEXT() );
        }
        return nextCommand;
    }

    public void setNextCommand( Command nextCommand )
    {
        this.nextCommand = nextCommand;
    }

    public DashboardLinkView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.namebasedMidlet = (NameBasedMIDlet) this.dhisMIDlet;
    }

    public void actionPerformed( ActionEvent ae )
    {
        if ( ae.getCommand().getCommandName().equals( Text.NEXT() ) )
        {
            try
            {
                ProgramInstance currentProgram = null;
                ProgramStage programStage = null;

                for ( int i = 0; i < patient.getEnrollmentPrograms().size(); i++ )
                {
                    ProgramInstance programInstance = (ProgramInstance) patient.getEnrollmentPrograms().elementAt( i );
                    if ( programInstance.getProgramId() == enrollProgramId )
                    {
                        currentProgram = programInstance;
                        programStage = (ProgramStage) currentProgram.getProgramStageInstances().elementAt( 0 );
                        break;
                    }
                }

                if ( currentProgram != null && currentProgram.getProgramStageInstances().size() > 1 )
                {
                    namebasedMidlet.getProgramStageListView().setPatient( patient );
                    namebasedMidlet.getProgramStageListView().setProgramInstance( currentProgram );
                    namebasedMidlet.getProgramStageListView().showView();
                }
                else if ( currentProgram.getProgramStageInstances().size() == 1 )
                {
                    namebasedMidlet.getTrackingDataEntryView().setPatient( patient );
                    namebasedMidlet.getTrackingDataEntryView().setProgramStage( programStage );
                    namebasedMidlet.getTrackingDataEntryView().setTitle( programStage.getName() );
                    namebasedMidlet.getTrackingDataEntryView().showView();
                }
            }
            catch ( Exception e )
            {
                LogMan.log( "UI," + CLASS_TAG, e );
                e.printStackTrace();
            }
        }
        else
        {
            namebasedMidlet.getTrackingMainMenuView().showView();
        }
        dashboardLinkForm = null;
        linkTextArea = null;
        backCommand = null;
        nextCommand = null;
        patient = null;
        System.gc();
    }

    public void prepareView()
    {

    }

    public void showView()
    {
        this.getDashboardLinkForm().show();
    }

    public Patient getPatient()
    {
        return patient;
    }

    public void setPatient( Patient patient )
    {
        this.patient = patient;
    }

    public void setEnrollProgramId( int enrollProgramId )
    {
        this.enrollProgramId = enrollProgramId;
    }

}
