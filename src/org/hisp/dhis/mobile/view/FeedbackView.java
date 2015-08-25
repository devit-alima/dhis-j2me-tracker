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

import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.recordstore.FeedbackRecordStore;
import org.hisp.dhis.mobile.ui.Text;

import com.sun.lwuit.Command;
import com.sun.lwuit.Form;
import com.sun.lwuit.TextField;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;

public class FeedbackView
    extends AbstractView
    implements ActionListener
{
    private static final String CLASS_TAG = "FeedbackView";

    private Form feedbackForm;

    private Command backCommand;

    private Command nextCommand;

    private TextField subjectTextField;

    private NameBasedMIDlet nameBasedMIDlet;

    public FeedbackView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.nameBasedMIDlet = (NameBasedMIDlet) dhisMIDlet;
    }

    public void prepareView()
    {
        this.feedbackForm = null;
        this.backCommand = null;
        this.nextCommand = null;
        this.subjectTextField = null;
        System.gc();

    }

    public void showView()
    {
        prepareView();
        this.getFeedbackForm().show();

    }

    public void actionPerformed( ActionEvent ae )
    {
        if ( ae.getCommand().getCommandName().equals( Text.NEXT() ) )
        {
            nameBasedMIDlet.getWaitingView().showView();
            try
            {
                FeedbackRecordStore.saveFeedbackSubject( this.getSubjectTextField().getText() );
                nameBasedMIDlet.getFeedbackContentView().showView();

            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }

        }
        else if ( ae.getCommand().getCommandName().equals( Text.BACK() ) )
        {
            nameBasedMIDlet.getMessagingMenuView().showView();
        }

    }

    public Form getFeedbackForm()
    {
        if ( feedbackForm == null )
        {
            feedbackForm = new Form( "Enter subject" );
            feedbackForm.setLayout( new BoxLayout( BoxLayout.Y_AXIS ) );
            feedbackForm.setScrollableY( true );
            feedbackForm.setTransitionOutAnimator( CommonTransitions.createSlide( CommonTransitions.SLIDE_HORIZONTAL,
                true, 200 ) );
            feedbackForm.addCommand( this.getBackCommand() );
            feedbackForm.addCommand( this.getNextCommand() );
            feedbackForm.addComponent( this.getSubjectTextField() );

            feedbackForm.addCommandListener( this );
        }
        return feedbackForm;
    }

    public void setFeedbackForm( Form feedbackForm )
    {
        this.feedbackForm = feedbackForm;
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

    public Command getNextCommand()
    {
        if ( nextCommand == null )
        {
            nextCommand = new Command( Text.NEXT() );
        }

        return nextCommand;
    }

    public void setNextCommand( Command nextCommand )
    {
        this.nextCommand = nextCommand;
    }

    public TextField getSubjectTextField()
    {
        if ( subjectTextField == null )
        {
            subjectTextField = new TextField();

        }
        return subjectTextField;
    }

    public void setSubjectTextField( TextField subjectTextField )
    {
        this.subjectTextField = subjectTextField;
    }

}
