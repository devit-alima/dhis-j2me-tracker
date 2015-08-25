package org.hisp.dhis.mobile.view;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
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

import com.sun.lwuit.ComboBox;
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

public class RepeatableProgramStageListView
    extends AbstractView
    implements ActionListener
{
    private static final String CLASS_TAG = "RepeatableProgramStageListView";

    private NameBasedMIDlet nameBasedMIDlet;

    private Form repeatableProgramStageListForm;

    private Command backCommand;

    private Patient patient;

    private Program program;

    private ProgramInstance programInstance;

    private Vector lastDueDates;

    public RepeatableProgramStageListView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.nameBasedMIDlet = (NameBasedMIDlet) dhisMIDlet;
    }

    public void prepareView()
    {
        System.gc();
        getRepeatableProgramStageListForm();
        repeatableProgramStageListForm.removeAll();

        Style labelStyle = new Style();
        labelStyle.setBgColor( Text.HEADER_BG_COLOR() );

        this.prepareProgramStageList();
    }

    public void prepareProgramStageList()
    {
        for ( int j = 0; j < program.getProgramStages().size(); j++ )
        {
            final ProgramStage programStage = (ProgramStage) program.getProgramStages().elementAt( j );

            if ( programStage.isRepeatable() )
            {

                final String programStageText = programStage.getName();

                LinkButton programStageLink = new LinkButton( programStageText );
                repeatableProgramStageListForm.addComponent( programStageLink );
                programStageLink.addActionListener( new ActionListener()
                {
                    public void actionPerformed( ActionEvent arg0 )
                    {
                        String nextDueDate = "";
                        try
                        {
                            String lastDueDate = null;
                            if ( lastDueDates != null )
                            {
                                for ( int i = 0; i < lastDueDates.size(); i++ )
                                {
                                    if ( ((String) lastDueDates.elementAt( i )).startsWith( programStageText ) )
                                    {
                                        lastDueDate = ((String) lastDueDates.elementAt( i ))
                                            .substring( ((String) lastDueDates.elementAt( i )).indexOf( "|" ) + 1 );
                                    }
                                }
                                if ( lastDueDate == null )
                                {
                                    lastDueDate = (String) lastDueDates.elementAt( lastDueDates.size() - 1 );
                                    lastDueDate = lastDueDate.substring( lastDueDate.indexOf( "|" ) + 1 );
                                }
                            }
                            if ( lastDueDate != null && !lastDueDate.equals( "" ) )
                            {
                                int year = Integer.parseInt( lastDueDate.substring( 0, 4 ) );
                                int month = Integer.parseInt( lastDueDate.substring( 5, 7 ) );
                                int date = Integer.parseInt( lastDueDate.substring( 8, 10 ) );
                                Calendar nextDate = Calendar.getInstance();
                                nextDate.set( Calendar.YEAR, year );
                                nextDate.set( Calendar.MONTH, month );
                                nextDate.set( Calendar.DATE, date + programStage.getStandardInterval() );

                                int daysInTheMonth = getDaysOfTheMonth( month, year );
                                while ( nextDate.get( Calendar.DATE ) > daysInTheMonth )
                                {
                                    nextDate.set( Calendar.DATE, nextDate.get( Calendar.DATE ) - daysInTheMonth );
                                    nextDate.set( Calendar.MONTH, nextDate.get( Calendar.MONTH ) + 1 );
                                    if ( nextDate.get( Calendar.MONTH ) > 12 )
                                    {
                                        nextDate.set( Calendar.MONTH, 1 );
                                        nextDate.set( Calendar.YEAR, nextDate.get( Calendar.YEAR ) + 1 );
                                    }
                                    daysInTheMonth = getDaysOfTheMonth( nextDate.get( Calendar.MONTH ),
                                        nextDate.get( Calendar.YEAR ) );
                                }

                                nextDueDate = nextDate.get( Calendar.YEAR )
                                    + "-"
                                    + (nextDate.get( Calendar.MONTH ) < 10 ? "0" + nextDate.get( Calendar.MONTH ) : ""
                                        + nextDate.get( Calendar.MONTH ))
                                    + "-"
                                    + (nextDate.get( Calendar.DATE ) < 10 ? "0" + nextDate.get( Calendar.DATE ) : ""
                                        + nextDate.get( Calendar.DATE ));
                            }
                        }
                        catch ( Exception e )
                        {
                            e.printStackTrace();
                        }
                        if ( nextDueDate.equals( "" ) )
                        {
                            Calendar today = Calendar.getInstance();
                            int month = today.get( Calendar.MONTH ) + 1;
                            nextDueDate = today.get( Calendar.YEAR )
                                + "-"
                                + (month < 10 ? "0" + month : "" + month)
                                + "-"
                                + (today.get( Calendar.DATE ) < 10 ? "0" + today.get( Calendar.DATE ) : ""
                                    + today.get( Calendar.DATE ));
                        }

                        nameBasedMIDlet.getCreateProgramStageView().setPatient( patient );
                        nameBasedMIDlet.getCreateProgramStageView().setProgram( program );
                        nameBasedMIDlet.getCreateProgramStageView().setNextDueDate( nextDueDate );
                        nameBasedMIDlet.getCreateProgramStageView().setLastDueDates( lastDueDates );
                        nameBasedMIDlet.getCreateProgramStageView().setProgramInstance( programInstance );
                        nameBasedMIDlet.getCreateProgramStageView().setProgramStage( programStage );
                        nameBasedMIDlet.getCreateProgramStageView().showView();

                        repeatableProgramStageListForm = null;
                        backCommand = null;
                        patient = null;
                        program = null;
                        programInstance = null;
                        lastDueDates = null;
                        System.gc();
                    }
                } );
            }
        }
        System.gc();
    }

    public int getDaysOfTheMonth( int month, int year )
    {
        switch ( month )
        {
        case 2:
            if ( year % 4 == 0 )
            {
                return 29;
            }
            else
            {
                return 28;
            }
        case 4:
        case 6:
        case 9:
        case 11:
            return 30;
        default:
            return 31;
        }
    }

    public void showView()
    {
        this.prepareView();
        this.getRepeatableProgramStageListForm().show();

    }

    public Form getRepeatableProgramStageListForm()
    {
        if ( repeatableProgramStageListForm == null )
        {
            repeatableProgramStageListForm = new Form( "Select Program Stage" );
            repeatableProgramStageListForm.setLayout( new BoxLayout( BoxLayout.Y_AXIS ) );
            repeatableProgramStageListForm.setScrollableY( true );
            repeatableProgramStageListForm.setTransitionOutAnimator( CommonTransitions.createSlide(
                CommonTransitions.SLIDE_HORIZONTAL, true, 200 ) );
            repeatableProgramStageListForm.addCommand( this.getBackCommand() );
            repeatableProgramStageListForm.addCommandListener( this );

        }
        return repeatableProgramStageListForm;
    }

    public void actionPerformed( ActionEvent ae )
    {
        if ( ae.getCommand().getCommandName().equals( Text.BACK() ) )
        {
            nameBasedMIDlet.getPersonDashboardView().setPatient( patient );
            nameBasedMIDlet.getPersonDashboardView().showView();

            repeatableProgramStageListForm = null;
            backCommand = null;
            patient = null;
            program = null;
            programInstance = null;
            lastDueDates = null;
            System.gc();
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

    public void setPatient( Patient patient )
    {
        this.patient = patient;
    }

    public void setProgram( Program program )
    {
        this.program = program;
    }

    public void setProgramInstance( ProgramInstance programInstance )
    {
        this.programInstance = programInstance;
    }

    public void setLastDueDates( Vector lastDueDates )
    {
        this.lastDueDates = lastDueDates;
    }
}
