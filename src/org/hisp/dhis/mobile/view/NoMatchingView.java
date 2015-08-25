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

import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.recordstore.OrgUnitRecordStore;
import org.hisp.dhis.mobile.recordstore.ProgramRecordStore;
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
 * @version NoMatchingView.java 12:09:45 AM Sep 7, 2013 $
 */
public class NoMatchingView
    extends AbstractView
    implements ActionListener
{
    private NameBasedMIDlet namebasedMidlet;

    private Form mainForm;
    
    private TextArea txtNotification;
    
    private Command backCommand;
    
    private Command addPersonCommand;

    public NoMatchingView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.namebasedMidlet = (NameBasedMIDlet) dhisMIDlet;
    }

    public void actionPerformed( ActionEvent ae )
    {
        if ( ae.getCommand().getCommandName().equals( Text.BACK() ) )
        {
            mainForm.removeAll();
            mainForm = null;
            txtNotification = null;
            backCommand = null;
            addPersonCommand = null;
            System.gc();
            namebasedMidlet.getFindBeneficiaryView().showView();
        }
        else
        {
            mainForm.removeAll();
            mainForm = null;
            txtNotification = null;
            backCommand = null;
            addPersonCommand = null;
            System.gc();

            try
            {
                if ( OrgUnitRecordStore.loadAllOrgUnit().size() > 1 )
                {
                    namebasedMidlet.getOrgUnitSelectView().setTargetScreen( namebasedMidlet.PERSON_REGISTRATION_VIEW );
                    namebasedMidlet.getOrgUnitSelectView().setProgramType( "1" );
                    namebasedMidlet.getOrgUnitSelectView().showView();
                }
                else
                {
                    namebasedMidlet.getProgramSelectView().setPrograms( ProgramRecordStore.getNonAnonymousPrograms() );
                    namebasedMidlet.getProgramSelectView().setTargetScreen( namebasedMidlet.PERSON_REGISTRATION_VIEW );
                    namebasedMidlet.getProgramSelectView().showView();

                }
            }
            catch ( Exception e )
            {
                LogMan.log( "UI,NoMatchingView", e );
                e.printStackTrace();
            }
        }
    }

    public void prepareView()
    {
        System.gc();
        getMainForm();
        mainForm.removeAll();
        mainForm.addComponent( getTxtNotification() );
    }

    public void showView()
    {
        getMainForm().show();
    }

    public Form getMainForm()
    {
        if ( mainForm == null )
        {
            mainForm = new Form( "Warning" );
            mainForm.setLayout( new BoxLayout( BoxLayout.Y_AXIS ) );
            mainForm.setSmoothScrolling( true );
            mainForm.addCommand( getBackCommand() );
            mainForm.addCommand( getAddPersonCommand() );
            mainForm.setTransitionOutAnimator( CommonTransitions.createSlide( CommonTransitions.SLIDE_HORIZONTAL, true,
                200 ) );
            mainForm.addCommandListener( this );
        }
        return mainForm;
    }

    public void setMainForm( Form mainForm )
    {
        this.mainForm = mainForm;
    }

    public TextArea getTxtNotification()
    {
        if ( txtNotification == null )
        {
            txtNotification = new TextArea();
            txtNotification.setEditable( false );
        }
        return txtNotification;
    }

    public void setTxtNotification( TextArea txtNotification )
    {
        this.txtNotification = txtNotification;
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

    public Command getAddPersonCommand()
    {
        if ( addPersonCommand == null )
        {
            addPersonCommand = new Command( Text.ADD_PERSON() );
        }
        return addPersonCommand;
    }

    public void setAddPersonCommand( Command addPersonCommand )
    {
        this.addPersonCommand = addPersonCommand;
    }
    
}
