/*
 * Copyright (c) 2004-2012, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.mobile.view;

import java.util.Vector;

import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.LinkButton;
import org.hisp.dhis.mobile.model.LostEvent;
import org.hisp.dhis.mobile.ui.Text;

import com.sun.lwuit.Command;
import com.sun.lwuit.Form;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;

/**
 * @author Nguyen Kim Lai
 * 
 * @version LostToFollowUpView.java 2:15:31 PM Oct 3, 2013 $
 */
public class LostToFollowUpView
    extends AbstractView
    implements ActionListener
{
    private NameBasedMIDlet nameBasedMIDlet;

    private Form mainForm;

    private Vector eventsInfo;

    private Command backCommand;

    public LostToFollowUpView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        nameBasedMIDlet = (NameBasedMIDlet) dhisMIDlet;
    }

    public void prepareView()
    {
        System.gc();
        getMainForm();
        mainForm.removeAll();

        for ( int i = 0; i < eventsInfo.size(); i++ )
        {
            String eventInfo = (String) eventsInfo.elementAt( i );

            final String programStageInstanceId = eventInfo.substring( 0, eventInfo.indexOf( "/" ) );
            eventInfo = eventInfo.substring( eventInfo.indexOf( "/" ) + 1, eventInfo.length() );

            LinkButton personLink = new LinkButton( eventInfo );
            final String programStageName = eventInfo.substring( eventInfo.indexOf( "," ) + 2, eventInfo.indexOf( "(" ) );
            final String dueDate = eventInfo.substring( eventInfo.indexOf( "(" ) + 1, eventInfo.indexOf( ")" ) );
            personLink.addActionListener( new ActionListener()
            {
                public void actionPerformed( ActionEvent ae )
                {
                    LostEvent lostEvent = new LostEvent( Integer.parseInt( programStageInstanceId ), programStageName, dueDate );
                    nameBasedMIDlet.getLostToFollowUpDetailView().setLostEvent( lostEvent );
                    nameBasedMIDlet.getLostToFollowUpDetailView().showView();
                }
            } );
            mainForm.addComponent( personLink );
            eventInfo = null;
        }
    }

    public void showView()
    {
        prepareView();
        mainForm.show();
    }

    public Form getMainForm()
    {
        if ( mainForm == null )
        {
            mainForm = new Form( "Lost to Follow Up" );
            mainForm.setLayout( new BoxLayout( BoxLayout.Y_AXIS ) );
            mainForm.setTransitionOutAnimator( CommonTransitions.createSlide( CommonTransitions.SLIDE_HORIZONTAL, true,
                200 ) );
            mainForm.addCommand( this.getBackCommand() );
            mainForm.addCommandListener( this );
        }
        return mainForm;
    }

    public Command getBackCommand()
    {
        if ( backCommand == null )
        {
            backCommand = new Command( Text.BACK() );
        }
        return backCommand;
    }

    public void setEventsInfo( Vector eventsInfo )
    {
        this.eventsInfo = eventsInfo;
    }

    public void actionPerformed( ActionEvent ae )
    {
        mainForm = null;
        eventsInfo = null;
        backCommand = null;
        nameBasedMIDlet.getProgramSelectView().setTargetScreen( nameBasedMIDlet.LOST_TO_FOLLOW_UP_VIEW );
        nameBasedMIDlet.getProgramSelectView().getMainForm().show();
        System.gc();
    }

}
