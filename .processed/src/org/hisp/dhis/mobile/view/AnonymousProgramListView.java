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

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.LinkButton;
import org.hisp.dhis.mobile.ui.Text;

import com.sun.lwuit.Command;
import com.sun.lwuit.Form;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;

/**
 * @author Nguyen Kim Lai
 * 
 * @version AnonymousProgramListView.java 10:52:10 AM Mar 29, 2013 $
 */
public class AnonymousProgramListView
    extends AbstractView
    implements ActionListener
{

    private Vector anonymousProgramInfos;

    private NameBasedMIDlet nameBasedMIDlet;

    private Form mainForm;

    private Command backCommand;

    public AnonymousProgramListView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        nameBasedMIDlet = (NameBasedMIDlet) dhisMIDlet;
    }

    public void actionPerformed( ActionEvent ae )
    {
        if ( ae.getCommand().getCommandName().equals( Text.BACK() ) )
        {
            anonymousProgramInfos = null;
            mainForm = null;
            backCommand = null;
            System.gc();
            
            nameBasedMIDlet.getOrgUnitSelectView().setProgramType( "3" );
            nameBasedMIDlet.getOrgUnitSelectView().showView();
        }
    }

    public void prepareView()
    {
        System.gc();
        getMainForm();
        mainForm.removeAll();
        TextArea txtHint = new TextArea( "Please select one program below: " );
        txtHint.setEditable( false );
        mainForm.addComponent( txtHint );

        for ( int i = 0; i < anonymousProgramInfos.size(); i++ )
        {
            //String programInfo = ((String) anonymousProgramInfos.elementAt( i ));
            final String id = ((String) anonymousProgramInfos.elementAt( i )).substring( 0, ((String) anonymousProgramInfos.elementAt( i )).indexOf( "/" ) );
            String name = ((String) anonymousProgramInfos.elementAt( i )).substring( ((String) anonymousProgramInfos.elementAt( i )).indexOf( "/" ) + 1, ((String) anonymousProgramInfos.elementAt( i )).length() );

            LinkButton programLink = new LinkButton( name );
            programLink.addActionListener( new ActionListener()
            {
                public void actionPerformed( ActionEvent ae )
                {
                    nameBasedMIDlet.getWaitingView().showView();
                    ConnectionManager.setUrl( nameBasedMIDlet.getCurrentOrgUnit().getFindProgramUrl() );
                    ConnectionManager.findAnonymousProgram( id );
                }
            } );
            mainForm.addComponent( programLink );
        }
    }

    public void showView()
    {
        prepareView();
        getMainForm().show();
    }

    public Form getMainForm()
    {
        if ( mainForm == null )
        {
            mainForm = new Form( "Anonymous Program List" );
            mainForm.setLayout( new BoxLayout( BoxLayout.Y_AXIS ) );
            mainForm.setTransitionOutAnimator( CommonTransitions.createSlide( CommonTransitions.SLIDE_HORIZONTAL, true,
                200 ) );
            mainForm.addCommand( getBackCommand() );
            mainForm.addCommandListener( this );
        }
        return mainForm;
    }

    public void setMainForm( Form mainForm )
    {
        this.mainForm = mainForm;
    }

    public Vector getAnonymousProgramInfos()
    {
        return anonymousProgramInfos;
    }

    public void setAnonymousProgramInfos( Vector anonymousProgramInfos )
    {
        this.anonymousProgramInfos = anonymousProgramInfos;
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

}
