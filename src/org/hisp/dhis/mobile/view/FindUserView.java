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
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.ui.Text;

import com.sun.lwuit.Command;
import com.sun.lwuit.Form;
import com.sun.lwuit.TextField;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;

public class FindUserView
    extends AbstractView
    implements ActionListener
{
    private static final String CLASS_TAG = "FindUserView";

    private Form findUserForm;

    private Command backCommand;

    private Command findCommand;

    private TextField keywordTextField;

    private NameBasedMIDlet nameBasedMIDlet;

    public FindUserView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        nameBasedMIDlet = (NameBasedMIDlet) this.dhisMIDlet;
    }

    public void prepareView()
    {
        this.findUserForm = null;
        this.backCommand = null;
        this.findCommand = null;
        this.keywordTextField = null;
        System.gc();

    }

    public void showView()
    {
        prepareView();
        this.getFindUserForm().show();

    }

    public void actionPerformed( ActionEvent ae )
    {
        if ( ae.getCommand().getCommandName().equals( Text.BACK() ) )
        {
            nameBasedMIDlet.getWaitingView().showView();
            nameBasedMIDlet.getMessagingMenuView().showView();
        }
        else if ( ae.getCommand().getCommandName().equals( Text.FIND() ) )
        {
            ConnectionManager.setUrl( dhisMIDlet.getCurrentOrgUnit().getFindUserUrl() );
            ConnectionManager.findUser( this.getKeywordTextField().getText() );
        }

    }

    public Form getFindUserForm()
    {
        if ( findUserForm == null )
        {
            findUserForm = new Form( "Enter name" );
            findUserForm.setLayout( new BoxLayout( BoxLayout.Y_AXIS ) );
            findUserForm.setScrollableY( true );
            findUserForm.setTransitionOutAnimator( CommonTransitions.createSlide( CommonTransitions.SLIDE_HORIZONTAL,
                true, 200 ) );
            findUserForm.addCommand( this.getBackCommand() );
            findUserForm.addCommand( this.getFindCommand() );
            findUserForm.addComponent( this.getKeywordTextField() );

            findUserForm.addCommandListener( this );

        }
        return findUserForm;
    }

    public void setFindUserForm( Form findUserForm )
    {
        this.findUserForm = findUserForm;
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

    public Command getFindCommand()
    {
        if ( findCommand == null )
        {
            findCommand = new Command( Text.FIND() );
        }
        return findCommand;
    }

    public void setFindCommand( Command findCommand )
    {
        this.findCommand = findCommand;
    }

    public TextField getKeywordTextField()
    {
        if ( keywordTextField == null )
        {
            keywordTextField = new TextField();
        }
        return keywordTextField;
    }

    public void setKeywordTextField( TextField keywordTextField )
    {
        this.keywordTextField = keywordTextField;
    }

}
