package org.hisp.dhis.mobile.view;

/*
 * Copyright (c) 2004-2014, University of Oslo All rights reserved.
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

public class MessagingMenuView
    extends AbstractView
    implements ActionListener
{
    private static final String CLASS_TAG = "MessagingMenuView";

    private static final String SEND_MESSAGE = "Send Message";

    private static final String CONVERSATIONS = "Conversations";

    private static final String FEEDBACK = "Feedback";

    private List menuList;

    private Form menuForm;

    private Command backCommand;

    private NameBasedMIDlet nameBasedMidlet;

    private Dialog warningDialog;

    public MessagingMenuView( DHISMIDlet dhisMIDlet )
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
        else if (ae.getCommand().getCommandName().equals( Text.BACK() ))
        {
            nameBasedMidlet.getWaitingView().showView();
            nameBasedMidlet.getTrackingMainMenuView().showView();
            menuList = null;
            menuForm = null;
            backCommand = null;
            warningDialog = null;
        }
        else
        {
            this.showView();
        }
    }

    private void navigate( String nextPageName )
    {
        if ( nextPageName.equals( SEND_MESSAGE ) )
        {
            nameBasedMidlet.getWaitingView().showView();
            nameBasedMidlet.getFindUserView().showView();
        }
        else if ( nextPageName.equals( CONVERSATIONS ) )
        {
            ConnectionManager.setUrl( dhisMIDlet.getCurrentOrgUnit().getDownloadMessageConversationUrl() );
            ConnectionManager.downloadMessageConversation();
        }
        else if ( nextPageName.equals( FEEDBACK ) )
        {
            nameBasedMidlet.getWaitingView().showView();
            nameBasedMidlet.getFeedbackView().showView();
        }
    }

    public List getMenuList()
    {
        String[] menuItems = { SEND_MESSAGE, CONVERSATIONS, FEEDBACK };

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
            menuForm = new Form( "Messaging Menu" );
            menuForm.setLayout( new BoxLayout( BoxLayout.Y_AXIS ) );
            menuForm.setSmoothScrolling( true );
            menuForm.addComponent( this.getMenuList() );
            menuForm.addCommand( getBackCommand() );
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
