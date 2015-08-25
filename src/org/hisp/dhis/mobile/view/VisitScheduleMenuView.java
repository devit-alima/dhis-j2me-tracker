package org.hisp.dhis.mobile.view;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.LinkButton;
import org.hisp.dhis.mobile.model.Program;
import org.hisp.dhis.mobile.model.ProgramStage;

import com.sun.lwuit.Command;
import com.sun.lwuit.Form;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;

public class VisitScheduleMenuView
    extends AbstractView
    implements ActionListener
{
    private static final String CLASS_TAG = "VisitScheduleMenuView";

    private Form visitScheduleMenuForm;

    private NameBasedMIDlet nameBasedMIDlet;

    private Program program;

    public VisitScheduleMenuView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        nameBasedMIDlet = (NameBasedMIDlet) dhisMIDlet;
    }

    public void prepareView()
    {
        getVisitScheduleMenuForm();
    }

    public void showView()
    {
        this.prepareView();
        this.getVisitScheduleMenuForm().show();
    }

    private Form getVisitScheduleMenuForm()
    {
        if ( visitScheduleMenuForm == null )
        {
            visitScheduleMenuForm = new Form( "Visit Schedule" );
            visitScheduleMenuForm.setLayout( new BoxLayout( BoxLayout.Y_AXIS ) );
            visitScheduleMenuForm.setTransitionOutAnimator( CommonTransitions.createSlide(
                CommonTransitions.SLIDE_HORIZONTAL, true, 200 ) );
            visitScheduleMenuForm.addCommandListener( this );
            visitScheduleMenuForm.addCommand( new Command( "Back" ) );

            LinkButton currentVisitSchedule = new LinkButton( "Current Visit Schedule" );
            currentVisitSchedule.addActionListener( new ActionListener()
            {
                public void actionPerformed( ActionEvent ae )
                {
                    nameBasedMIDlet.getWaitingView().showView();

                    String info = "Schedule in future$1 week/1 week";

                    ConnectionManager.setUrl( nameBasedMIDlet.getCurrentOrgUnit().getFindVisitScheduleUrl() + "/"
                        + program.getId() );
                    ConnectionManager.findVisitSchedule( program, info );
                }
            } );
            visitScheduleMenuForm.addComponent( currentVisitSchedule );

            LinkButton searchVisitSchedule = new LinkButton( "Search Visit Schedule" );
            searchVisitSchedule.addActionListener( new ActionListener()
            {
                public void actionPerformed( ActionEvent ae )
                {
                    nameBasedMIDlet.getWaitingView().showView();
                    nameBasedMIDlet.getSearchVisitScheduleView().setProgram( program );
                    nameBasedMIDlet.getSearchVisitScheduleView().showView();
                }
            } );
            visitScheduleMenuForm.addComponent( searchVisitSchedule );
        }
        return visitScheduleMenuForm;
    }

    public void actionPerformed( ActionEvent ae )
    {
        if ( ae.getCommand().getCommandName().equals( "Back" ) )
        {
            nameBasedMIDlet.getProgramSelectView().setTargetScreen( nameBasedMIDlet.VISIT_SCHEDULE_VIEW );
            nameBasedMIDlet.getProgramSelectView().getMainForm().show();

            visitScheduleMenuForm = null;
            program = null;
            System.gc();
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
}
