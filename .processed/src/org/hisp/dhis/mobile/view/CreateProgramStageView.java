package org.hisp.dhis.mobile.view;

import java.util.Vector;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.Patient;
import org.hisp.dhis.mobile.model.Program;
import org.hisp.dhis.mobile.model.ProgramInstance;
import org.hisp.dhis.mobile.model.ProgramStage;
import org.hisp.dhis.mobile.ui.Text;
import org.hisp.dhis.mobile.util.PeriodUtil;

import com.sun.lwuit.Command;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextField;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;

public class CreateProgramStageView
    extends AbstractView
    implements ActionListener
{
    private static final String CLASS_TAG = "CreateProgramStageView";

    private Form createProgramStageForm;

    private NameBasedMIDlet nameBasedMIDlet;

    private Program program;

    private ProgramInstance programInstance;

    private ProgramStage programStage;

    private TextField programStageTextField;

    private TextField dueDateTextField;

    private Label lblWrongFormat;

    private Patient patient;

    private String nextDueDate;

    private Vector lastDueDates;

    public CreateProgramStageView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.nameBasedMIDlet = (NameBasedMIDlet) this.dhisMIDlet;
    }

    public void prepareView()
    {
        System.gc();
        getCreateProgramStageForm();
        createProgramStageForm.removeAll();

        Label lblProgramStage = new Label( "Program stage" );
        createProgramStageForm.addComponent( lblProgramStage );
        programStageTextField = new TextField();
        programStageTextField.setText( programStage.getName() );
        programStageTextField.setEditable( false );
        createProgramStageForm.addComponent( programStageTextField );

        Label lblDueDate = new Label( "Due date" );
        createProgramStageForm.addComponent( lblDueDate );
        dueDateTextField = new TextField();
        dueDateTextField.setHint( Text.DATE_TYPE() );
        dueDateTextField.setText( nextDueDate );
        createProgramStageForm.addComponent( dueDateTextField );

        createProgramStageForm.addComponent( getLblWrongFormat() );
    }

    public void showView()
    {
        prepareView();
        getCreateProgramStageForm().show();
    }

    private Form getCreateProgramStageForm()
    {
        if ( createProgramStageForm == null )
        {
            createProgramStageForm = new Form( "Create Program Stage" );
            createProgramStageForm.setLayout( new BoxLayout( BoxLayout.Y_AXIS ) );
            createProgramStageForm.setTransitionOutAnimator( CommonTransitions.createSlide(
                CommonTransitions.SLIDE_HORIZONTAL, true, 200 ) );
            createProgramStageForm.addCommandListener( this );
            createProgramStageForm.addCommand( new Command( "Back" ) );
            createProgramStageForm.addCommand( new Command( "Create" ) );
        }
        return createProgramStageForm;
    }

    public Label getLblWrongFormat()
    {
        if ( lblWrongFormat == null )
        {
            lblWrongFormat = new Label();
            lblWrongFormat.getStyle().setFgColor( Text.ERROR_TEXT_COLOR() );
        }
        return lblWrongFormat;
    }

    public void actionPerformed( ActionEvent ae )
    {
        if ( ae.getCommand().getCommandName().equals( "Back" ) )
        {
            nameBasedMIDlet.getRepeatableProgramStageListView().setPatient( patient );
            nameBasedMIDlet.getRepeatableProgramStageListView().setProgram( program );
            nameBasedMIDlet.getRepeatableProgramStageListView().setLastDueDates( lastDueDates );
            nameBasedMIDlet.getRepeatableProgramStageListView().setProgramInstance( programInstance );
            nameBasedMIDlet.getRepeatableProgramStageListView().showView();


            createProgramStageForm = null;
            programStage = null;
            dueDateTextField = null;
            lblWrongFormat = null;
            program = null;
            programInstance = null;
            nextDueDate = null;
            System.gc();
        }
        else if ( ae.getCommand().getCommandName().equals( "Create" ) )
        {
            String dueDate = dueDateTextField.getText().trim();
            if ( validateDueDate( dueDate ) == false )
            {
                getCreateProgramStageForm().show();
            }
            else
            {
                nameBasedMIDlet.getWaitingView().showView();

                ConnectionManager.setUrl( nameBasedMIDlet.getCurrentOrgUnit().getGenerateRepeatableEventUrl() );
                nameBasedMIDlet.getWaitingView().showView();
                ProgramStage programStageInstance = getProgramStageInstance( programStage.getName() );
                if ( programStageInstance == null )
                {
                    ConnectionManager.generateRepeatableEvent( patient.getId() + "_" + program.getId() + "_" + dueDate
                        + "_0_" + programStage.getId() );
                }
                else
                {
                    ConnectionManager.generateRepeatableEvent( patient.getId() + "_" + program.getId() + "_" + dueDate
                        + "_" + programStageInstance.getId() + "_" + programStage.getId() );
                }

                createProgramStageForm = null;
                programStage = null;
                dueDateTextField = null;
                lblWrongFormat = null;
                program = null;
                programInstance = null;
                patient = null;
                nextDueDate = null;
                System.gc();
            }
        }
    }

    public ProgramStage getProgramStageInstance( String programStageName )
    {

        for ( int j = 0; j < programInstance.getProgramStageInstances().size(); j++ )
        {
            ProgramStage programStage = (ProgramStage) programInstance.getProgramStageInstances().elementAt( j );
            if ( programStage.getName().equals( programStageName ) )
            {
                return programStage;
            }
        }
        return null;
    }

    public boolean validateDueDate( String dueDate )
    {
        if ( dueDate.equals( "" ) )
        {
            lblWrongFormat.setText( "(*): Required Field" );
            return false;
        }
        else if ( PeriodUtil.isDateValid( dueDate ) == false )
        {
            lblWrongFormat.setText( Text.DATE_TYPE() );
            return false;
        }
        else
        {
            return true;
        }
    }

    public void setProgram( Program program )
    {
        this.program = program;
    }

    public Program getProgram()
    {
        return this.program;
    }

    public void setProgramStage( ProgramStage programStage )
    {
        this.programStage = programStage;
    }

    public void setProgramInstance( ProgramInstance programInstance )
    {
        this.programInstance = programInstance;
    }

    public ProgramInstance getProgramInstance()
    {
        return this.programInstance;
    }

    public void setPatient( Patient patient )
    {
        this.patient = patient;
    }

    public void setNextDueDate( String nextDueDate )
    {
        this.nextDueDate = nextDueDate;
    }

    public void setLastDueDates( Vector lastDueDates )
    {
        this.lastDueDates = lastDueDates;
    }

}
