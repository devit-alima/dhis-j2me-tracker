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

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.ui.Text;

public class MessageOptionView
    extends AbstractView
    implements CommandListener

{
    private static final String CLASS_TAG = "MessageOptionView";

    private Command backCommand;

    private Command nextCommand;

    private NameBasedMIDlet nameBasedMIDlet;

    private Form messageOptionForm;

    public MessageOptionView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.nameBasedMIDlet = (NameBasedMIDlet) dhisMIDlet;
    }

    public void prepareView()
    {
        this.backCommand = null;
        this.nextCommand = null;
        this.messageOptionForm = null;
        System.gc();

    }

    public void showView()
    {
        prepareView();
        this.switchDisplayable( null, this.getMessageOptionForm() );

    }
    
    public void commandAction( Command command, Displayable displayable )
    {
        if ( command == this.getBackCommand() )
        {
            nameBasedMIDlet.getWaitingView().showView();
            nameBasedMIDlet.getFindUserView().showView();
        }
        else if ( command == this.getNextCommand() )
        {
            nameBasedMIDlet.getWaitingView().showView();
            nameBasedMIDlet.getMessageSubjectView().showView();
        }

    }

    public Command getBackCommand()
    {
        if ( backCommand == null )
        {
            backCommand = new Command( Text.BACK(), Command.BACK, 0 );
        }
        return backCommand;
    }

    public void setBackCommand( Command backCommand )
    {
        this.backCommand = backCommand;
    }

    public Command getNextCommand()
    {
        if ( nextCommand == null )
        {
            nextCommand = new Command( Text.NEXT(), Command.SCREEN, 0 );
        }
        return nextCommand;
    }

    public void setNextCommand( Command nextCommand )
    {
        this.nextCommand = nextCommand;
    }

    public Form getMessageOptionForm()
    {
        if ( messageOptionForm == null )
        {

            messageOptionForm = new Form( Text.MESSAGE() );
            messageOptionForm.append( "Press Back to add more recipient or Next to send message" );
            messageOptionForm.addCommand( this.getBackCommand() );
            messageOptionForm.addCommand( this.getNextCommand() );

            messageOptionForm.setCommandListener( this );

        }
        return messageOptionForm;
    }

    public void setMessageOptionForm( Form messageOptionForm )
    {
        this.messageOptionForm = messageOptionForm;
    }

  

}
