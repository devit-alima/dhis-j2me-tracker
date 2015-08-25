package org.hisp.dhis.mobile.view;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.OrgUnit;
import org.hisp.dhis.mobile.model.Program;
import org.hisp.dhis.mobile.recordstore.OrgUnitRecordStore;
import org.hisp.dhis.mobile.ui.Text;
import org.hisp.dhis.mobile.util.TrackingUtils;

import com.sun.lwuit.Button;
import com.sun.lwuit.ComboBox;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Font;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;

public class SearchVisitScheduleView
    extends AbstractView
    implements ActionListener
{
    private static final String CLASS_TAG = "SearchVisitScheduleView";

    private Form searchVisitScheduleForm;

    private NameBasedMIDlet nameBasedMIDlet;

    private Program program;

    private ComboBox statusOptions;

    private ComboBox eventFromOptions;

    private ComboBox eventToOptions;

    private String[] statusItems = { "All", "Schedule in future", "Overdue", "Incomplete", "Completed", "Skipped" };

    private String[] eventItems = { "1 day", "3 days", "1 week", "1 month" };

    private Button btnFind;

    public SearchVisitScheduleView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        nameBasedMIDlet = (NameBasedMIDlet) dhisMIDlet;
    }

    public void prepareView()
    {
        System.gc();
        getSearchVisitScheduleForm();
        searchVisitScheduleForm.removeAll();

        Label lblSelectStatus = new Label( "Status" );
        searchVisitScheduleForm.addComponent( lblSelectStatus );
        searchVisitScheduleForm.addComponent( getStatusOptions() );

        Label lblSelectEventStart = new Label( "Show event since" );
        searchVisitScheduleForm.addComponent( lblSelectEventStart );
        searchVisitScheduleForm.addComponent( getEventStartOptions() );

        Label lblSelectEventEnd = new Label( "Show event up to" );
        searchVisitScheduleForm.addComponent( lblSelectEventEnd );
        searchVisitScheduleForm.addComponent( getEventEndOptions() );

        searchVisitScheduleForm.addComponent( getBtnFind() );
    }

    public void showView()
    {
        this.prepareView();
        this.getSearchVisitScheduleForm().show();
    }

    private Form getSearchVisitScheduleForm()
    {
        if ( searchVisitScheduleForm == null )
        {
            searchVisitScheduleForm = new Form( "Visit Schedule" );
            searchVisitScheduleForm.setLayout( new BoxLayout( BoxLayout.Y_AXIS ) );
            searchVisitScheduleForm.setTransitionOutAnimator( CommonTransitions.createSlide(
                CommonTransitions.SLIDE_HORIZONTAL, true, 200 ) );
            searchVisitScheduleForm.addCommandListener( this );
            searchVisitScheduleForm.addCommand( new Command( "Back" ) );
        }
        return searchVisitScheduleForm;
    }

    public void actionPerformed( ActionEvent ae )
    {
        if ( ae.getCommand().getCommandName().equals( "Back" ) )
        {
            nameBasedMIDlet.getVisitScheduleMenuView().setProgram( program );
            nameBasedMIDlet.getVisitScheduleMenuView().showView();

            searchVisitScheduleForm = null;
            program = null;
            statusOptions = null;
            eventFromOptions = null;
            eventToOptions = null;
            btnFind = null;
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

    public ComboBox getStatusOptions()
    {
        if ( statusOptions == null )
        {
            statusOptions = new ComboBox();
            populateComboBox( statusOptions, statusItems );
        }
        statusOptions.setSelectedIndex( 1 );

        return statusOptions;
    }

    public ComboBox getEventStartOptions()
    {
        if ( eventFromOptions == null )
        {
            eventFromOptions = new ComboBox();
            populateComboBox( eventFromOptions, eventItems );
        }
        eventFromOptions.setSelectedIndex( 2 );

        return eventFromOptions;
    }

    public ComboBox getEventEndOptions()
    {
        if ( eventToOptions == null )
        {
            eventToOptions = new ComboBox();
            populateComboBox( eventToOptions, eventItems );
        }
        eventToOptions.setSelectedIndex( 2 );

        return eventToOptions;
    }

    private void populateComboBox( ComboBox cb, String[] items )
    {
        for ( int i = 0; i < items.length; i++ )
        {
            cb.addItem( items[i] );
        }
    }

    public Button getBtnFind()
    {
        if ( btnFind == null )
        {
            btnFind = new Button( Text.ENTER() );
            btnFind.addActionListener( new ActionListener()
            {
                public void actionPerformed( ActionEvent ae )
                {

                    nameBasedMIDlet.getWaitingView().showView();

                    String info = statusOptions.getSelectedItem().toString() + "$"
                        + eventFromOptions.getSelectedItem().toString() + "/"
                        + eventToOptions.getSelectedItem().toString();

                    ConnectionManager.setUrl( nameBasedMIDlet.getCurrentOrgUnit().getFindVisitScheduleUrl() + "/" + program.getId() );
                    ConnectionManager.findVisitSchedule( program, info );

                    searchVisitScheduleForm = null;
                    program = null;
                    statusOptions = null;
                    eventFromOptions = null;
                    eventToOptions = null;
                    btnFind = null;

                    System.gc();
                }
            } );
            btnFind.getUnselectedStyle().setAlignment( Component.CENTER );
            btnFind.getSelectedStyle().setAlignment( Component.CENTER );
            btnFind.getPressedStyle().setAlignment( Component.CENTER );
            btnFind.getUnselectedStyle().setFont(
                Font.createSystemFont( Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM ) );
            btnFind.getSelectedStyle().setFont(
                Font.createSystemFont( Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM ) );
            btnFind.getPressedStyle().setFont(
                Font.createSystemFont( Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM ) );
        }
        return btnFind;
    }
}
