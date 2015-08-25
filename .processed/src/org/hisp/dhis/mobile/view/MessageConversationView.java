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
import javax.microedition.lcdui.List;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.MessageConversation;
import org.hisp.dhis.mobile.recordstore.FeedbackRecordStore;
import org.hisp.dhis.mobile.recordstore.MessageConversationRecordStore;
import org.hisp.dhis.mobile.ui.Text;

public class MessageConversationView
    extends AbstractView
    implements CommandListener
{
    private static final String CLASS_TAG = "MessageConversationView";

    private NameBasedMIDlet nameBasedMIDlet;

    private Vector conversationVector;

    private Command backCommand;

    private List messageList;

    public MessageConversationView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.nameBasedMIDlet = (NameBasedMIDlet) dhisMIDlet;
        this.conversationVector = new Vector();
    }

    public void prepareView()
    {
        this.conversationVector = null;
        this.backCommand = null;
        this.messageList = null;

        try
        {
            this.conversationVector = MessageConversationRecordStore.loadConversations();
            this.getMessageList().deleteAll();

            for ( int i = 0; i < conversationVector.size(); i++ )
            {
                MessageConversation messConversation = (MessageConversation) conversationVector.elementAt( i );
                this.getMessageList().append( messConversation.getSubject(), null );

            }

        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

        System.gc();

    }

    public void showView()
    {
        LogMan.log( LogMan.DEV, "UI," + CLASS_TAG, "Showing " + "Message conversation" + " Screen..." );
        prepareView();
        switchDisplayable( null, this.getMessageList() );

    }

    public void commandAction( Command command, Displayable displayable )
    {
        if ( command == this.getBackCommand() )
        {

            this.nameBasedMIDlet.getMessagingMenuView().showView();
        }
        else if ( command == List.SELECT_COMMAND )
        {
            try
            {
                MessageConversation messageConversation = (MessageConversation) this.getConversationVector().elementAt(
                    this.getMessageList().getSelectedIndex() );

                FeedbackRecordStore.deleteSubject();
                FeedbackRecordStore.saveFeedbackSubject( Integer.toString( messageConversation.getId() ) );

                ConnectionManager.setUrl( dhisMIDlet.getCurrentOrgUnit().getGetMessageUrl() );
                ConnectionManager.downloadMessage( messageConversation.getId() );

            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }
        }

    }

    public Vector getConversationVector()
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        if ( conversationVector == null )
        {
            conversationVector = MessageConversationRecordStore.loadConversations();
        }
        return conversationVector;
    }

    public void setConversationVector( Vector conversationVector )
    {
        this.conversationVector = conversationVector;
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

    public List getMessageList()
    {
        if ( messageList == null )
        {
            messageList = new List( Text.SELECT_CONVERSATION(), List.IMPLICIT );

            this.messageList.setFitPolicy( List.TEXT_WRAP_ON );
            this.messageList.addCommand( this.getBackCommand() );
            this.messageList.setCommandListener( this );
        }
        return messageList;
    }

    public void setMessageList( List messageList )
    {
        this.messageList = messageList;
    }

}
