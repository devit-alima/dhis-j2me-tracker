package org.hisp.dhis.mobile.view;

import java.io.IOException;
import java.util.Vector;

import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.LinkButton;
import org.hisp.dhis.mobile.model.Patient;
import org.hisp.dhis.mobile.model.Program;
import org.hisp.dhis.mobile.model.ProgramInstance;
import org.hisp.dhis.mobile.model.ProgramStage;
import org.hisp.dhis.mobile.recordstore.ProgramRecordStore;
import org.hisp.dhis.mobile.ui.Text;

import com.sun.lwuit.Command;
import com.sun.lwuit.Container;
import com.sun.lwuit.Form;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.plaf.Style;

public class ProgramStageListView
    extends AbstractView
    implements ActionListener
{
    private static final String CLASS_TAG = "ProgramStageListView";

    private NameBasedMIDlet nameBasedMIDlet;

    private Form personProgramStageListForm;

    private Command backCommand;

    private Command addCommand;

    private Patient patient;

    private Program program;

    private ProgramInstance programInstance;

    private Vector lastDueDates;

    public ProgramStageListView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.nameBasedMIDlet = (NameBasedMIDlet) dhisMIDlet;
    }

    public void prepareView()
    {
        System.gc();
        getPersonProgramStageListForm();
        personProgramStageListForm.removeAll();

        Style labelStyle = new Style();
        labelStyle.setBgColor( Text.HEADER_BG_COLOR() );

        this.prepareProgramStageList();
    }

    public void prepareProgramStageList()
    {
        lastDueDates = new Vector();
        for ( int j = 0; j < programInstance.getProgramStageInstances().size(); j++ )
        {
            final ProgramStage programStage = (ProgramStage) programInstance.getProgramStageInstances().elementAt( j );
            if ( programStage.isSingleEvent() == false )
            {
                String programStageText = programStage.getName();

                if ( !programStage.getReportDate().equals( "" ) )
                {
                    programStageText += " (" + programStage.getReportDate() + ")";
                    addLastDueDate( programStage.getName(), programStage.getReportDate() );
                }
                else if ( !programStage.getDueDate().equals( "" ) )
                {
                    programStageText += " (" + programStage.getDueDate() + ")";
                    addLastDueDate( programStage.getName(), programStage.getDueDate() );
                }
                LinkButton programStageLink = new LinkButton( programStageText );
                if ( programStage.isCompleted() )
                {
                    try
                    {
                        Image image = Image.createImage( "/sent.gif" );
                        Label sentLabel = new Label( image );

                        Container c = new Container( new BorderLayout() );
                        c.addComponent( BorderLayout.CENTER, programStageLink );
                        c.addComponent( BorderLayout.EAST, sentLabel );

                        personProgramStageListForm.addComponent( c );
                    }
                    catch ( IOException e )
                    {
                        LogMan.log( "UI," + CLASS_TAG, e );
                        e.printStackTrace();
                    }
                }
                else
                {
                    personProgramStageListForm.addComponent( programStageLink );
                }
                programStageLink.addActionListener( new ActionListener()
                {

                    public void actionPerformed( ActionEvent arg0 )
                    {
                        if ( programStage.getSections().size() > 0 )
                        {
                            nameBasedMIDlet.getSectionListView().setPatient( patient );
                            nameBasedMIDlet.getSectionListView().setProgramStage( programStage );
                            nameBasedMIDlet.getSectionListView().showView();
                        }
                        else
                        {
                            nameBasedMIDlet.getTrackingDataEntryView().setPatient( patient );
                            nameBasedMIDlet.getTrackingDataEntryView().setProgramStage( programStage );
                            nameBasedMIDlet.getTrackingDataEntryView().setTitle( programStage.getName() );
                            nameBasedMIDlet.getTrackingDataEntryView().showView();
                        }

                    }
                } );
            }
        }
        System.gc();
    }

    private void addLastDueDate( String programStageName, String reportDate )
    {
        for ( int i = 0; i < lastDueDates.size(); i++ )
        {
            if ( ((String) lastDueDates.elementAt( i )).startsWith( programStageName ) )
            {
                lastDueDates.setElementAt( programStageName + "|" + reportDate, i );
                return;
            }
        }
        lastDueDates.addElement( programStageName + "|" + reportDate );
    }

    public void showView()
    {
        this.prepareView();
        this.getPersonProgramStageListForm().show();

    }

    public Form getPersonProgramStageListForm()
    {
        if ( personProgramStageListForm == null )
        {
            personProgramStageListForm = new Form( "Program Stages" );
            personProgramStageListForm.setLayout( new BoxLayout( BoxLayout.Y_AXIS ) );
            personProgramStageListForm.setScrollableY( true );
            personProgramStageListForm.setTransitionOutAnimator( CommonTransitions.createSlide(
                CommonTransitions.SLIDE_HORIZONTAL, true, 200 ) );
            personProgramStageListForm.addCommand( this.getBackCommand() );
            if ( hasRepeatableEvents( programInstance.getProgramId() ) )
            {
                personProgramStageListForm.addCommand( this.getCreateNewEventCommand() );
            }
            personProgramStageListForm.addCommandListener( this );

        }
        return personProgramStageListForm;
    }

    public boolean hasRepeatableEvents( int programId )
    {
        try
        {
            Program program = ProgramRecordStore.getProgram( programId );
            for ( int i = 0; i < program.getProgramStages().size(); i++ )
            {
                ProgramStage programStage = (ProgramStage) program.getProgramStages().elementAt( i );
                if ( programStage.isRepeatable() )
                {
                    return true;
                }
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        return false;
    }

    public void setPersonProgramStageListForm( Form personProgramStageListForm )
    {
        this.personProgramStageListForm = personProgramStageListForm;
    }

    public void actionPerformed( ActionEvent ae )
    {
        if ( ae.getCommand().getCommandName().equals( Text.BACK() ) )
        {
            nameBasedMIDlet.getPersonDashboardView().setPatient( patient );
            nameBasedMIDlet.getPersonDashboardView().showView();

            personProgramStageListForm = null;
            backCommand = null;
            addCommand = null;
            patient = null;
            lastDueDates = null;
            System.gc();
        }
        else if ( ae.getCommand().getCommandName().equals( Text.CREATE_NEW_EVENT() ) )
        {
            try
            {
                nameBasedMIDlet.getRepeatableProgramStageListView().setPatient( patient );
                nameBasedMIDlet.getRepeatableProgramStageListView().setProgram(
                    ProgramRecordStore.getProgram( programInstance.getProgramId() ) );
                nameBasedMIDlet.getRepeatableProgramStageListView().setLastDueDates( lastDueDates );
                nameBasedMIDlet.getRepeatableProgramStageListView().setProgramInstance( programInstance );
                nameBasedMIDlet.getRepeatableProgramStageListView().showView();

                personProgramStageListForm = null;
                backCommand = null;
                addCommand = null;
                patient = null;
                lastDueDates = null;
                System.gc();
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }
        }
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

    public Command getCreateNewEventCommand()
    {
        if ( addCommand == null )
        {
            addCommand = new Command( Text.CREATE_NEW_EVENT() );
        }
        return addCommand;
    }

    public Patient getPatient()
    {
        return patient;
    }

    public void setPatient( Patient patient )
    {
        this.patient = patient;
    }

    public Program getProgram()
    {
        return program;
    }

    public void setProgram( Program program )
    {
        this.program = program;
    }

    public ProgramInstance getProgramInstance()
    {
        return programInstance;
    }

    public void setProgramInstance( ProgramInstance programInstance )
    {
        this.programInstance = programInstance;
    }

}
