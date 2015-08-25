package org.hisp.dhis.mobile.view;

import java.util.Vector;

import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.OrgUnit;
import org.hisp.dhis.mobile.recordstore.OrgUnitRecordStore;

import com.sun.lwuit.Command;
import com.sun.lwuit.Form;
import com.sun.lwuit.List;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;

public class OfflineOrgUnitSelectView
    extends AbstractView
    implements ActionListener
{
    private static final String CLASS_TAG = "OfflineOrgUnitSelectView";
    private Vector orgUnitVector;

    private Form offlineOrgUnitForm;

    private List orgUnitSelectList;

    private NameBasedMIDlet nameBasedMidlet;

    public OfflineOrgUnitSelectView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        nameBasedMidlet = (NameBasedMIDlet) dhisMIDlet;
    }

    public void actionPerformed( ActionEvent ae )
    {
        if ( ae.getSource() == this.getOrgUnitSelectList() )
        {
            dhisMIDlet.setCurrentOrgUnit( ((OrgUnit) getOrgUnitVector().elementAt(
                getOrgUnitSelectList().getSelectedIndex() )) );
            nameBasedMidlet.getOfflineView().showView();

        }
        else if ( ae.getCommand().getCommandName().equals( "Back" ) )
        {
            offlineOrgUnitForm = null;
            orgUnitVector = null;
            orgUnitSelectList = null;

            nameBasedMidlet.getTrackingMainMenuView().showView();
        }

    }

    public void prepareView()
    {
        System.gc();
        getOfflineOrgUnitForm();
    }

    public void showView()
    {
        this.prepareView();
        this.getOfflineOrgUnitForm().show();
    }

    public Vector getOrgUnitVector()
    {
        return orgUnitVector;
    }

    public void setOrgUnitVector( Vector orgUnitVector )
    {
        this.orgUnitVector = orgUnitVector;
    }

    public Form getOfflineOrgUnitForm()
    {
        if ( offlineOrgUnitForm == null )
        {
            offlineOrgUnitForm = new Form( "Select Org Unit" );
            offlineOrgUnitForm.setLayout( new BoxLayout( BoxLayout.Y_AXIS ) );
            offlineOrgUnitForm.setTransitionOutAnimator( CommonTransitions.createSlide(
                CommonTransitions.SLIDE_HORIZONTAL, true, 200 ) );
            offlineOrgUnitForm.addCommandListener( this );
            offlineOrgUnitForm.addComponent( this.getOrgUnitSelectList() );
            offlineOrgUnitForm.addCommand( new Command( "Back" ) );
        }
        return offlineOrgUnitForm;
    }

    public void setOfflineOrgUnitForm( Form offlineOrgUnitForm )
    {
        this.offlineOrgUnitForm = offlineOrgUnitForm;
    }

    public List getOrgUnitSelectList()
    {
        if ( orgUnitSelectList == null )
        {
            orgUnitSelectList = new List();
            // Create a list model
            orgUnitVector = null;
            try
            {
                orgUnitVector = OrgUnitRecordStore.loadAllOrgUnit();
            }
            catch ( Exception e )
            {
                LogMan.log( "UI," + CLASS_TAG, e );
                e.printStackTrace();
            }
            for ( int i = 0; i < orgUnitVector.size(); i++ )
            {
                orgUnitSelectList.addItem( ((OrgUnit) orgUnitVector.elementAt( i )).getName() );
            }
            orgUnitSelectList.setSmoothScrolling( true );
            orgUnitSelectList.addActionListener( this );
        }
        return orgUnitSelectList;
    }

    public void setOrgUnitSelectList( List orgUnitSelectList )
    {
        this.orgUnitSelectList = orgUnitSelectList;
    }

}
