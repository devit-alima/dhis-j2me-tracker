package org.hisp.dhis.mobile.view;

import java.util.Vector;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.LinkButton;
import org.hisp.dhis.mobile.model.Program;
import org.hisp.dhis.mobile.recordstore.PatientRecordStore;
import org.hisp.dhis.mobile.ui.Text;

import com.sun.lwuit.Command;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.plaf.Style;

public class VisitScheduleListView
    extends AbstractView
    implements ActionListener
{
    private static final String CLASS_TAG = "VisitScheduleListView";

    private Form visitScheduleListForm;

    private NameBasedMIDlet nameBasedMIDlet;

    private Program program;

    private Vector instanceInfos;

    public VisitScheduleListView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        nameBasedMIDlet = (NameBasedMIDlet) dhisMIDlet;
    }

    public void prepareView()
    {
        System.gc();
        getVisitScheduleListForm();
        visitScheduleListForm.removeAll();

        for ( int i = 0; i < instanceInfos.size(); i++ )
        {
            String instanceInfo = (String) instanceInfos.elementAt( i );

            if ( instanceInfo.indexOf( "/" ) == -1 )
            {
                Style labelStyle = new Style();
                labelStyle.setBgColor( Text.HEADER_BG_COLOR() );

                Label trackedEntitylbl = new Label( instanceInfo );
                trackedEntitylbl.setUnselectedStyle( labelStyle );
                trackedEntitylbl.setSelectedStyle( labelStyle );
                trackedEntitylbl.setFocusable( false );
                visitScheduleListForm.addComponent( trackedEntitylbl );
            }
            else
            {
                final String id = instanceInfo.substring( 0, instanceInfo.indexOf( "/" ) );

                instanceInfo = instanceInfo.substring( instanceInfo.indexOf( "/" ) + 1, instanceInfo.length() );
                /*
                 * String name = patientInfo.substring( 0, patientInfo.indexOf(
                 * "/" ) ); String birthday = patientInfo.substring(
                 * patientInfo.indexOf( "/" ) + 1, patientInfo.length() );
                 * LinkButton personLink = new LinkButton( name + ", DOB: " +
                 * birthday );
                 */
                LinkButton personLink = new LinkButton( instanceInfo );
                personLink.addActionListener( new ActionListener()
                {
                    public void actionPerformed( ActionEvent ae )
                    {
                        nameBasedMIDlet.getWaitingView().showView();

                        ConnectionManager.setUrl( nameBasedMIDlet.getCurrentOrgUnit().getFindPatientUrl() );
                        ConnectionManager.getPatientById( id, nameBasedMIDlet.VISIT_SCHEDULE_VIEW, program );

                        visitScheduleListForm = null;
                        program = null;
                        instanceInfos = null;
                        System.gc();
                    }
                } );
                visitScheduleListForm.addComponent( personLink );
            }
        }
    }

    public void showView()
    {
        this.prepareView();
        this.getVisitScheduleListForm().show();
    }

    private Form getVisitScheduleListForm()
    {
        if ( visitScheduleListForm == null )
        {
            visitScheduleListForm = new Form( "Select Instance" );
            visitScheduleListForm.setLayout( new BoxLayout( BoxLayout.Y_AXIS ) );
            visitScheduleListForm.setTransitionOutAnimator( CommonTransitions.createSlide(
                CommonTransitions.SLIDE_HORIZONTAL, true, 200 ) );
            visitScheduleListForm.addCommandListener( this );
            visitScheduleListForm.addCommand( new Command( "Back" ) );
            visitScheduleListForm.addCommand( new Command( "Save All" ) );
        }
        return visitScheduleListForm;
    }

    public void actionPerformed( ActionEvent ae )
    {
        if ( ae.getCommand().getCommandName().equals( "Back" ) )
        {
            nameBasedMIDlet.getVisitScheduleMenuView().setProgram( program );
            nameBasedMIDlet.getVisitScheduleMenuView().showView();

            visitScheduleListForm = null;
            program = null;
            instanceInfos = null;
            System.gc();
        }
        else if ( ae.getCommand().getCommandName().equals( "Save All" ) )
        {
            if ( instanceInfos.size() >= PatientRecordStore.MAX_NO_OF_RECORDS )
            {
                nameBasedMIDlet.getAlertBoxView( "Please refine your search before saving data.", "Warning" )
                    .showView();
            }
            else
            {
                try
                {
                    String ids = "";
                    for ( int i = 0; i < instanceInfos.size(); i++ )
                    {
                        String instanceInfo = (String) instanceInfos.elementAt( i );
                        if ( instanceInfo.indexOf( "/" ) > -1 )
                        {
                            ids += instanceInfo.substring( 0, instanceInfo.indexOf( "/" ) ) + "$";
                        }
                    }

                    ConnectionManager.setUrl( dhisMIDlet.getCurrentOrgUnit().getFindPatientsUrl() );
                    ConnectionManager.getPatientsById( ids );
                }
                catch ( Exception e )
                {
                    LogMan.log( "UI," + CLASS_TAG, e );
                    e.printStackTrace();
                }
            }
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

    public void setInstanceInfos( Vector instanceinfos )
    {
        this.instanceInfos = instanceinfos;
    }

    public Vector getInstanceInfos()
    {
        return this.instanceInfos;
    }
}
