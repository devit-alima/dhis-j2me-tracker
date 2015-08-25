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

import java.io.IOException;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;

import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.Message;
import org.hisp.dhis.mobile.recordstore.MessageRecordStore;
import org.hisp.dhis.mobile.ui.Text;

public class MessageDetailView
    extends AbstractView
    implements CommandListener
{
    private static final String CLASS_TAG = "MessageDetailView";

    NameBasedMIDlet nameBasedMIDlet;

    private Vector messageVector;

    private Command backCommand;

    private Command replyCommand;

    private Form messageDetailForm;

    public MessageDetailView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.nameBasedMIDlet = (NameBasedMIDlet) dhisMIDlet;
    }

    public void prepareView()
    {
        this.messageDetailForm = null;
        this.messageVector = null;
        this.backCommand = null;
        this.replyCommand = null;
        System.gc();
    }

    public void showView()
    {

        try
        {
            prepareView();
            this.switchDisplayable( null, this.getMessageDetailForm() );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

    }

    public void commandAction( Command command, Displayable displayable )
    {
        if ( command == this.getBackCommand() )
        {
            nameBasedMIDlet.getMessageConversationView().showView();
        }
        if ( command == this.getReplyCommand() )
        {
            nameBasedMIDlet.getMessageReplyView().showView();
        }

    }

    public Form getMessageDetailForm()
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        if ( messageDetailForm == null )
        {

            messageVector = MessageRecordStore.loadMessages();

            String text = "";

            for ( int i = 0; i < messageVector.size(); i++ )
            {
                Message message = (Message) messageVector.elementAt( i );

                text += message.getLastSenderName() + ": " + message.getText() + " / -";

            }

            messageDetailForm = new Form( "Message details" );

            messageDetailForm.append( text.substring( 0, text.length() - 3 ) );
            messageDetailForm.addCommand( this.getBackCommand() );
            messageDetailForm.addCommand( this.getReplyCommand() );

            messageDetailForm.setCommandListener( this );
        }

        return messageDetailForm;
    }

    public void setMessageDetailForm( Form messageDetailForm )
    {
        this.messageDetailForm = messageDetailForm;
    }

    public Vector getMessageVector()
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        if ( messageVector == null )
        {
            messageVector = MessageRecordStore.loadMessages();
        }

        return messageVector;
    }

    public void setMessageVector( Vector messageVector )
    {
        this.messageVector = messageVector;
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

    public Command getReplyCommand()
    {
        if ( replyCommand == null )
        {
            replyCommand = new Command( Text.REPLY(), Command.SCREEN, 0 );
        }

        return replyCommand;
    }

    public void setReplyCommand( Command replyCommand )
    {
        this.replyCommand = replyCommand;
    }

}
