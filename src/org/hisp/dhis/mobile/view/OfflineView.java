package org.hisp.dhis.mobile.view;

import java.util.Vector;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.LinkButton;
import org.hisp.dhis.mobile.model.Patient;
import org.hisp.dhis.mobile.model.PatientAttribute;
import org.hisp.dhis.mobile.recordstore.OfflinePatientRecordStore;
import org.hisp.dhis.mobile.util.RecordStoreUtil;
import org.hisp.dhis.mobile.ui.Text;

import com.sun.lwuit.Command;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.plaf.Style;

public class OfflineView
    extends AbstractView
    implements ActionListener
{
    private static final String CLASS_TAG = "OfflineView";

    private Vector offlinePatientVector;

    private Form offlineForm;

    private Label patientLabel;

    private NameBasedMIDlet nameBasedMIDlet;

    public OfflineView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        nameBasedMIDlet = (NameBasedMIDlet) dhisMIDlet;
    }

    public void prepareView()
    {
        System.gc();
        getOfflineForm();
        offlineForm.removeAll();
        try
        {
            String trackedEntityName = "";
            this.offlinePatientVector = OfflinePatientRecordStore.loadAllOfflinePatientsByTrackedEntity();
            for ( int i = 0; i < offlinePatientVector.size(); i++ )
            {
                String displayText = "";
                final Patient patient = (Patient) offlinePatientVector.elementAt( i );

                if ( !patient.getTrackedEntityName().equals( trackedEntityName ) )
                {
                    trackedEntityName = patient.getTrackedEntityName();

                    Style labelStyle = new Style();
                    labelStyle.setBgColor( Text.HEADER_BG_COLOR() );

                    Label trackedEntitylbl = new Label( trackedEntityName );
                    trackedEntitylbl.setUnselectedStyle( labelStyle );
                    trackedEntitylbl.setSelectedStyle( labelStyle );
                    trackedEntitylbl.setFocusable( false );
                    offlineForm.addComponent( trackedEntitylbl );

                }

                for ( int j = 0; j < patient.getPatientAttValues().size(); j++ )
                {
                    PatientAttribute patientAttribute = (PatientAttribute) patient.getPatientAttValues().elementAt( j );
                    if ( patientAttribute.isDisplayedInList() )
                    {
                        displayText += patientAttribute.getValue() + " ";
                    }
                }

                displayText = displayText.trim();

                LinkButton personLink = new LinkButton( displayText );
                personLink.addActionListener( new ActionListener()
                {

                    public void actionPerformed( ActionEvent arg0 )
                    {
                        nameBasedMIDlet.getPersonDashboardView().setPatient( patient );
                        nameBasedMIDlet.getPersonDashboardView().showView();
                    }
                } );
                offlineForm.addComponent( personLink );

            }
        }
        catch ( Exception e )
        {
            LogMan.log( "UI," + CLASS_TAG, e );
            e.printStackTrace();
        }

    }

    public Form getOfflineForm()
    {
        if ( offlineForm == null )
        {
            offlineForm = new Form( "Unregistered Patient List" );
            offlineForm.setLayout( new BoxLayout( BoxLayout.Y_AXIS ) );
            offlineForm.setTransitionOutAnimator( CommonTransitions.createSlide( CommonTransitions.SLIDE_HORIZONTAL,
                true, 200 ) );
            offlineForm.addCommandListener( this );
            offlineForm.addCommand( new Command( "Back" ) );
            offlineForm.addCommand( new Command( "Send All" ) );
        }
        return offlineForm;
    }

    public void actionPerformed( ActionEvent ae )
    {
        if ( ae.getCommand().getCommandName().equals( Text.SEND_ALL() ) )
        {
            try
            {
                this.offlinePatientVector = OfflinePatientRecordStore.loadAllOfflinePatients();
                if ( offlinePatientVector != null && offlinePatientVector.size() > 0 )
                {
                    for ( int i = 0; i < this.offlinePatientVector.size(); i++ )
                    {
                        Patient patient = (Patient) offlinePatientVector.elementAt( i );
                        ConnectionManager.setUrl( dhisMIDlet.getCurrentOrgUnit().getRegisterPersonUrl() );
                        ConnectionManager.registerAllOfflinePatient( patient );
                    }
                    // nameBasedMIDlet.getWaitingView().showView();
                    offlineForm.removeAll();
                    RecordStoreUtil.clearRecordStore( OfflinePatientRecordStore.OFFLINEPATIENT_DB );
                    nameBasedMIDlet.getAlertBoxView( "All patient uploaded", "Success" ).showView();
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
            nameBasedMIDlet.getTrackingMainMenuView().showView();
            System.gc();
        }

    }

    public void showView()
    {
        this.prepareView();
        this.getOfflineForm().show();

    }

    public void setOfflineForm( Form offlineForm )
    {
        this.offlineForm = offlineForm;
    }

    public Vector getOfflinePatientVector()
    {
        return offlinePatientVector;
    }

    public void setOfflinePatientVector( Vector offlinePatientVector )
    {
        this.offlinePatientVector = offlinePatientVector;
    }

    public Label getPatientLabel()
    {
        return patientLabel;
    }

    public void setPatientLabel( Label patientLabel )
    {
        this.patientLabel = patientLabel;
    }

}
