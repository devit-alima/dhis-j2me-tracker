package org.hisp.dhis.mobile.view;

import java.util.Vector;

import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.Model;
import org.hisp.dhis.mobile.model.ModelList;
import org.hisp.dhis.mobile.model.Patient;
import org.hisp.dhis.mobile.model.PatientAttribute;
import org.hisp.dhis.mobile.recordstore.PatientRecordStore;
import org.hisp.dhis.mobile.ui.Text;

import com.sun.lwuit.Command;
import com.sun.lwuit.Form;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;

public class WarningView
    extends AbstractView
    implements ActionListener
{
    private static final String CLASS_TAG = "WarningView";

    private NameBasedMIDlet namebasedMidlet;

    private Form mainForm;

    private TextArea txtNotification;

    private String keyword;

    public WarningView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.namebasedMidlet = (NameBasedMIDlet) dhisMIDlet;
    }

    public void actionPerformed( ActionEvent ae )
    {
        mainForm.removeAll();
        mainForm = null;
        txtNotification = null;
        System.gc();

        if ( ae.getCommand().getCommandName().equals( "Back" ) )
        {
            namebasedMidlet.getFindBeneficiaryView().showView();
        }
        // "Next"
        else
        {
            ModelList modelList;
            try
            {
                modelList = PatientRecordStore.getCurrentPatients();
                Vector models = modelList.getModels();
                Vector modelInfos = new Vector();
                String trackedEntityName = "$$$";
                for ( int i = 0; i < models.size(); i++ )
                {
                    Model instanceModel = (Model) models.elementAt( i );
                    if ( !instanceModel.getName().startsWith( trackedEntityName ) )
                    {
                        trackedEntityName = instanceModel.getName().substring( 0, instanceModel.getName().indexOf( "/" ) );
                        modelInfos.addElement( trackedEntityName );
                    }
                    
                    String instanceName = instanceModel.getName();
                    Patient patient = PatientRecordStore.getPatient( instanceModel.getId() );
                    for ( int j = 0; j < patient.getPatientAttValues().size(); j++ )
                    {
                        PatientAttribute attribute = (PatientAttribute) patient.getPatientAttValues().elementAt( j );
                        if ( attribute.getValue().equals( keyword ) )
                        {
                            modelInfos.addElement( instanceModel.getId() + "/"
                                + instanceName.substring( instanceName.indexOf( "/" ) + 1, instanceName.length() ) );
                            break;
                        }
                    }

                }
                namebasedMidlet.getPersonListView().setPatientInfos( modelInfos );
                namebasedMidlet.getPersonListView().setIsOffline( true );
                modelInfos = null;
                namebasedMidlet.getPersonListView().showView();
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
        mainForm.addComponent( getTxtNotification() );
    }

    public void showView()
    {
        getMainForm().show();
    }

    public Form getMainForm()
    {
        if ( mainForm == null )
        {
            mainForm = new Form( "Warning" );
            mainForm.setLayout( new BoxLayout( BoxLayout.Y_AXIS ) );
            mainForm.setSmoothScrolling( true );
            mainForm.addCommand( new Command( Text.BACK() ) );
            mainForm.addCommand( new Command( Text.NEXT() ) );
            mainForm.setTransitionOutAnimator( CommonTransitions.createSlide( CommonTransitions.SLIDE_HORIZONTAL, true,
                200 ) );
            mainForm.addCommandListener( this );
        }
        return mainForm;
    }

    public void setMainForm( Form mainForm )
    {
        this.mainForm = mainForm;
    }

    public TextArea getTxtNotification()
    {
        if ( txtNotification == null )
        {
            txtNotification = new TextArea();
            txtNotification.setEditable( false );
        }
        return txtNotification;
    }

    public void setTxtNotification( TextArea txtNotification )
    {
        this.txtNotification = txtNotification;
    }

    public String getKeyword()
    {
        return keyword;
    }

    public void setKeyword( String keyword )
    {
        this.keyword = keyword;
    }

}
