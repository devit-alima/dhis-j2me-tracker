package org.hisp.dhis.mobile.view;

/*
 * Copyright (c) 2004-2011, University of Oslo All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met: * Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer. * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or other materials provided with the
 * distribution. * Neither the name of the HISP project nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.ModelList;
import org.hisp.dhis.mobile.model.OrgUnit;
import org.hisp.dhis.mobile.recordstore.OrgUnitRecordStore;
import org.hisp.dhis.mobile.recordstore.PatientRecordStore;
import org.hisp.dhis.mobile.recordstore.ProgramRecordStore;
import org.hisp.dhis.mobile.ui.Text;

import com.sun.lwuit.Command;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Form;
import com.sun.lwuit.List;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.list.DefaultListCellRenderer;

public class TrackingMainMenuView
    extends AbstractView
    implements ActionListener
{
    private static final String CLASS_TAG = "TrackingMainMenuView";

    private static final String MENU_FIND_INSTANCE = "Find Instance";

    private static final String MENU_ADD_INSTANCE = "Add Instance";

    private static final String MENU_VISIT_SCHEDULE = "Visit Schedule";

    private static final String MENU_HISTORY = "History";

    private static final String MENU_PENDING_REGISTRATION = "Pending Registration";

    private static final String MENU_SINGLE_EVENT_WITHOUT_REGISTRATION = "Single Event Without Registration";

    private static final String MENU_LOST_TO_FOLLOW_UP = "Lost to Follow Up";

    private static final String MESSAGING = "Messaging";

    private List menuList;

    private Form menuForm;

    private Command exitCommand;

    private NameBasedMIDlet nameBasedMidlet;

    private Dialog warningDialog;

    public TrackingMainMenuView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        nameBasedMidlet = (NameBasedMIDlet) this.dhisMIDlet;
    }

    public void prepareView()
    {

        System.gc();
        getMenuForm();
    }

    public void showView()
    {
        prepareView();
        getMenuForm().show();
    }

    public void actionPerformed( ActionEvent ae )
    {
        if ( ae.getSource() == this.getMenuList() )
        {
            this.navigate( (String) menuList.getSelectedItem() );
        }
        else if ( ae.getCommand().getCommandName().equals( Text.LOG() ) )
        {
            LogMan.showLogMonitorScreen();
        }
        else if ( ae.getCommand().getCommandName().equals( "Exit" ) )
        {
            getWarningDialog().show();
        }
        else if ( ae.getCommand().getCommandName().equals( "Yes" ) )
        {
            dhisMIDlet.exitMIDlet();
        }
        else
        {
            this.showView();
        }
    }

    private void navigate( String nextPageName )
    {
        try
        {
            int sizeOfOrgUnits = OrgUnitRecordStore.loadAllOrgUnit().size();
            nameBasedMidlet.setCurrentOrgUnit( (OrgUnit) OrgUnitRecordStore.loadAllOrgUnit().elementAt( 0 ) );

            if ( nextPageName.equals( MENU_FIND_INSTANCE ) )
            {
                if ( sizeOfOrgUnits > 1 )
                {
                    nameBasedMidlet.getWaitingView().showView();
                    nameBasedMidlet.getOrgUnitSelectView().setProgramType( "1" );
                    nameBasedMidlet.getOrgUnitSelectView().setTargetScreen( nameBasedMidlet.FIND_INSTANCE_VIEW );
                    nameBasedMidlet.getOrgUnitSelectView().showView();
                }
                else
                {
                    nameBasedMidlet.getFindBeneficiaryView().showView();
                }
            }
            else if ( nextPageName.equals( MENU_ADD_INSTANCE ) )
            {
                if ( sizeOfOrgUnits > 1 )
                {
                    nameBasedMidlet.getWaitingView().showView();
                    nameBasedMidlet.getOrgUnitSelectView().setProgramType( "1" );
                    nameBasedMidlet.getOrgUnitSelectView().setTargetScreen( nameBasedMidlet.PERSON_REGISTRATION_VIEW );
                    nameBasedMidlet.getOrgUnitSelectView().showView();
                }
                else
                {
                    nameBasedMidlet.getProgramSelectView().setPrograms( ProgramRecordStore.getNonAnonymousPrograms() );
                    nameBasedMidlet.getProgramSelectView().setProgramType( "1" );
                    nameBasedMidlet.getProgramSelectView().setTargetScreen( nameBasedMidlet.PERSON_REGISTRATION_VIEW );
                    nameBasedMidlet.getProgramSelectView().showView();
                }
            }
            else if ( nextPageName.equals( MENU_VISIT_SCHEDULE ) )
            {
                if ( sizeOfOrgUnits > 1 )
                {
                    nameBasedMidlet.getWaitingView().showView();
                    nameBasedMidlet.getOrgUnitSelectView().setProgramType( "1" );
                    nameBasedMidlet.getOrgUnitSelectView().setTargetScreen( nameBasedMidlet.VISIT_SCHEDULE_VIEW );
                    nameBasedMidlet.getOrgUnitSelectView().showView();
                }
                else
                {
                    nameBasedMidlet.getProgramSelectView().setPrograms( ProgramRecordStore.getNonAnonymousPrograms() );
                    nameBasedMidlet.getProgramSelectView().setProgramType( "1" );
                    nameBasedMidlet.getProgramSelectView().setTargetScreen( nameBasedMidlet.VISIT_SCHEDULE_VIEW );
                    nameBasedMidlet.getProgramSelectView().showView();
                }
            }
            else if ( nextPageName.equals( MENU_LOST_TO_FOLLOW_UP ) )
            {
                if ( sizeOfOrgUnits > 1 )
                {
                    nameBasedMidlet.getWaitingView().showView();
                    nameBasedMidlet.getOrgUnitSelectView().setProgramType( "1" );
                    nameBasedMidlet.getOrgUnitSelectView().setTargetScreen( nameBasedMidlet.LOST_TO_FOLLOW_UP_VIEW );
                    nameBasedMidlet.getOrgUnitSelectView().showView();
                }
                else
                {
                    ConnectionManager.setUrl( nameBasedMidlet.getCurrentOrgUnit().getDownloadAnonymousProgramUrl() );
                    ConnectionManager.getAllAnonymousProgram( "1", nameBasedMidlet.LOST_TO_FOLLOW_UP_VIEW );
                }
            }
            else if ( nextPageName.equals( MENU_HISTORY ) )
            {
                nameBasedMidlet.getProgramSelectView().setPrograms( ProgramRecordStore.getNonAnonymousPrograms() );
                nameBasedMidlet.getProgramSelectView().setTargetScreen( nameBasedMidlet.HISTORY_PERSON_LIST_VIEW );
                nameBasedMidlet.getProgramSelectView().showView();

            }
            else if ( nextPageName.equals( MENU_PENDING_REGISTRATION ) )
            {
                nameBasedMidlet.getWaitingView().showView();
                nameBasedMidlet.getOfflineOrgUnitSelectView().showView();
            }
            else if ( nextPageName.equals( MENU_SINGLE_EVENT_WITHOUT_REGISTRATION ) )
            {
                if ( sizeOfOrgUnits > 1 )
                {
                    nameBasedMidlet.getWaitingView().showView();
                    nameBasedMidlet.getOrgUnitSelectView().setTargetScreen( nameBasedMidlet.SINGLE_EVENT_WITHOUT_REGISTRATION );
                    nameBasedMidlet.getOrgUnitSelectView().setProgramType( "3" );
                    nameBasedMidlet.getProgramSelectView().setTargetScreen( nameBasedMidlet.SINGLE_EVENT_WITHOUT_REGISTRATION );
                    nameBasedMidlet.getOrgUnitSelectView().showView();
                }
                else
                {
                    nameBasedMidlet.getProgramSelectView().setPrograms( ProgramRecordStore.getProgramByType( 3 ) );
                    nameBasedMidlet.getProgramSelectView().setTargetScreen( nameBasedMidlet.SINGLE_EVENT_WITHOUT_REGISTRATION );
                    nameBasedMidlet.getProgramSelectView().showView();
                }
            }
            else if ( nextPageName.equals( MESSAGING ) )
            {
                nameBasedMidlet.getWaitingView().showView();
                nameBasedMidlet.getMessagingMenuView().showView();
            }
        }

        catch ( Exception e )
        {
            LogMan.log( "UI," + CLASS_TAG, e );
            e.printStackTrace();
        }
    }

    public List getMenuList()
    {
        String[] menuItems = { MENU_FIND_INSTANCE, MENU_ADD_INSTANCE, MENU_VISIT_SCHEDULE, MENU_HISTORY,
            MENU_PENDING_REGISTRATION, MENU_SINGLE_EVENT_WITHOUT_REGISTRATION, MESSAGING };

        if ( menuList == null )
        {
            menuList = new List( menuItems );
            menuList.setListCellRenderer( new DefaultListCellRenderer( false ) );
            menuList.setSmoothScrolling( true );
            menuList.setFixedSelection( List.FIXED_NONE );
            menuList.addActionListener( this );
        }
        return menuList;
    }

    public void setMenuList( List menuList )
    {
        this.menuList = menuList;
    }

    public Form getMenuForm()
    {
        if ( menuForm == null )
        {
            menuForm = new Form( "Tracking Main Menu" );
            menuForm.setLayout( new BoxLayout( BoxLayout.Y_AXIS ) );
            menuForm.setSmoothScrolling( true );
            menuForm.addComponent( this.getMenuList() );
            menuForm.addCommand( getExitCommand() );
            if ( LogMan.isEnabled() )
            {
                menuForm.addCommand( new Command( Text.LOG() ) );
            }
            menuForm.setTransitionOutAnimator( CommonTransitions.createSlide( CommonTransitions.SLIDE_HORIZONTAL, true,
                200 ) );
            menuForm.addCommandListener( this );
        }

        return menuForm;
    }

    public void setMenuForm( Form menuForm )
    {
        this.menuForm = menuForm;
    }

    public Command getExitCommand()
    {
        if ( exitCommand == null )
        {
            exitCommand = new Command( Text.EXIT() );
        }
        return exitCommand;
    }

    public void setExitCommand( Command exitCommand )
    {
        this.exitCommand = exitCommand;
    }

    public Dialog getWarningDialog()
    {
        if ( warningDialog == null )
        {
            warningDialog = new Dialog( Text.WARNING() );
            warningDialog.setDialogType( Dialog.TYPE_CONFIRMATION );
            warningDialog.addComponent( new TextArea( Text.EXIT_CONFIRM() ) );
            warningDialog.addCommandListener( this );
            warningDialog.addCommand( new Command( Text.NO() ) );
            warningDialog.addCommand( new Command( Text.YES() ) );
        }
        return warningDialog;
    }

    public void setWarningDialog( Dialog warningDialog )
    {
        this.warningDialog = warningDialog;
    }

}
