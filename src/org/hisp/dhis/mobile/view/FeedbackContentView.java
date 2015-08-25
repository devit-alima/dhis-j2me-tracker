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

import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.Message;
import org.hisp.dhis.mobile.recordstore.FeedbackRecordStore;
import org.hisp.dhis.mobile.ui.Text;

import com.sun.lwuit.Command;
import com.sun.lwuit.Form;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;

public class FeedbackContentView
    extends AbstractView
    implements ActionListener
{
    private TextArea contentTextArea;

    private Command backCommand;

    private Command sendCommand;

    private Message message;

    private Form contentForm;

    private NameBasedMIDlet nameBasedMIDlet;

    public FeedbackContentView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.nameBasedMIDlet = (NameBasedMIDlet) dhisMIDlet;
    }

    public void prepareView()
    {
        this.contentTextArea = null;
        this.backCommand = null;
        this.sendCommand = null;
        this.message = null;
        System.gc();

    }

    public void showView()
    {
        prepareView();
        this.getContentForm().show();

    }

    public void actionPerformed( ActionEvent ae )
    {
        if ( ae.getCommand().getCommandName().equals( Text.BACK() ) )
        {
            nameBasedMIDlet.getWaitingView().showView();
            nameBasedMIDlet.getFeedbackView().showView();
        }
        else if ( ae.getCommand().getCommandName().equals( Text.SEND() ) )
        {

            try
            {
                ConnectionManager.setUrl( dhisMIDlet.getCurrentOrgUnit().getSendFeedbackUrl() );
                ConnectionManager.sendFeedback( this.getMessage() );
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }

        }

    }

    public Form getContentForm()
    {
        if ( contentForm == null )
        {
            contentForm = new Form( "Enter feedback message" );
            contentForm.setLayout( new BoxLayout( BoxLayout.Y_AXIS ) );
            contentForm.setScrollableY( true );
            contentForm.setTransitionOutAnimator( CommonTransitions.createSlide( CommonTransitions.SLIDE_HORIZONTAL,
                true, 200 ) );
            contentForm.addCommand( this.getBackCommand() );
            contentForm.addCommand( this.getSendCommand() );
            contentForm.addComponent( this.getContentTextArea() );

            contentForm.addCommandListener( this );

        }
        return contentForm;
    }

    public void setContentForm( Form contentForm )
    {
        this.contentForm = contentForm;
    }

    public TextArea getContentTextArea()
    {
        if ( contentTextArea == null )
        {
            contentTextArea = new TextArea();

        }
        return contentTextArea;
    }

    public void setContentTextArea( TextArea contentTextArea )
    {
        this.contentTextArea = contentTextArea;
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

    public Command getSendCommand()
    {
        if ( sendCommand == null )
        {
            sendCommand = new Command( Text.SEND() );
        }
        return sendCommand;
    }

    public void setSendCommand( Command sendCommand )
    {
        this.sendCommand = sendCommand;
    }

    public Message getMessage()
        throws RecordStoreNotOpenException, RecordStoreException
    {
        if ( message == null )
        {
            message = new Message();
            message.setSubject( FeedbackRecordStore.load() );
            message.setText( this.getContentTextArea().getText() );
        }
        return message;
    }

    public void setMessage( Message message )
    {
        this.message = message;
    }

}
