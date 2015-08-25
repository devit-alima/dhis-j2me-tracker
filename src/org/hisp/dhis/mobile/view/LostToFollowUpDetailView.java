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

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.LostEvent;
import org.hisp.dhis.mobile.ui.Text;
import org.hisp.dhis.mobile.util.PeriodUtil;

import com.sun.lwuit.ComboBox;
import com.sun.lwuit.Command;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextField;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;

/**
 * @author Nguyen Kim Lai
 *
 * @version LostToFollowUpDetailView.java 2:41:02 PM Oct 7, 2013 $
 */
public class LostToFollowUpDetailView extends AbstractView implements ActionListener
{
    private NameBasedMIDlet nameBasedMIDlet;

    private Form mainForm;
    
    private Command backCommand;
    
    private Command sendCommand;
    
    private LostEvent lostEvent;
    
    private TextField txtDueDate, txtComment, txtSMS;
    
    private ComboBox cbxStatus;
    
    private Label lblWrongDOB;
    
    private boolean isValid;
    
    public LostToFollowUpDetailView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        nameBasedMIDlet = (NameBasedMIDlet) dhisMIDlet;
    }

    public void actionPerformed( ActionEvent ae )
    {
        if( ae.getCommand().getCommandName().equals( Text.SEND() ) )
        {
            String dueDate = txtDueDate.getText();

            validateDueDate( dueDate );
            
            if ( isValid == false )
            {
                mainForm.show();
            }
            else
            {
                lostEvent.setDueDate( dueDate );
                
                // why +4? Check class ProgramStageInstance for clearer explanation
                lostEvent.setStatus( cbxStatus.getSelectedIndex()+4 );
                lostEvent.setRiskCase( false );
                lostEvent.setComment( txtComment.getText() );
                lostEvent.setSMS( txtSMS.getText() );
                
                nameBasedMIDlet.getWaitingView().showView();
                ConnectionManager.setUrl( nameBasedMIDlet.getCurrentOrgUnit().getHandleLostToFollowUpUrl() );
                ConnectionManager.sendLostToFollowUp( lostEvent );
                
                backCommand = null;
                sendCommand = null;
                lostEvent = null;
                txtDueDate = null; 
                txtComment = null;
                txtSMS = null;
                cbxStatus = null;
                lblWrongDOB = null;
                mainForm = null;
            }
        }
        else
        {
            nameBasedMIDlet.getLostToFollowUpView().getMainForm().show();
            backCommand = null;
            sendCommand = null;
            lostEvent = null;
            txtDueDate = null; 
            txtComment = null;
            txtSMS = null;
            cbxStatus = null;
            lblWrongDOB = null;
            mainForm = null;
        }
    }

    public void prepareView()
    {
        System.gc();
        getMainForm();
        mainForm.removeAll();
        
        //due date
        Label lblDueDate = new Label( "Due Date" );
        mainForm.addComponent( lblDueDate );
        mainForm.addComponent( getTxtDueDate() );
        
        //wrong DOB label
        mainForm.addComponent( getLblWrongDOB() );
        
        //status
        Label lblStatus = new Label( "Status" );
        mainForm.addComponent( lblStatus );
        mainForm.addComponent( getCbxStatus() );
        
        //comment
        Label lblComment = new Label( "Comment" );
        mainForm.addComponent( lblComment );
        mainForm.addComponent( getTxtComment() );
        
        //SMS
        Label lblSMS = new Label( "SMS" );
        mainForm.addComponent( lblSMS );
        mainForm.addComponent( getTxtSMS() );
    }

    public void showView()
    {
        prepareView();
        mainForm.show();
    }
    
    public void validateDueDate( String dueDate )
    {
        if ( dueDate.trim().equals( "" ) )
        {
            getLblWrongDOB().setText( "(*): Required Field" );
            isValid = false;
        }
        else if ( !PeriodUtil.isDateValid( dueDate ) )
        {
            getLblWrongDOB().setText( Text.DATE_TYPE() );
            isValid = false;
        }
        else
        {
            isValid = true;
            getLblWrongDOB().setText( "" );
        }
    }

    public Form getMainForm()
    {
        if ( mainForm == null )
        {
            mainForm = new Form( lostEvent.getName() );
            mainForm.setLayout( new BoxLayout( BoxLayout.Y_AXIS ) );
            mainForm.setTransitionOutAnimator( CommonTransitions.createSlide( CommonTransitions.SLIDE_HORIZONTAL, true,
                200 ) );
            mainForm.addCommand( this.getBackCommand() );
            mainForm.addCommand( this.getSendCommand() );
            mainForm.addCommandListener( this );
        }
        return mainForm;
    }

    public void setMainForm( Form mainForm )
    {
        this.mainForm = mainForm;
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

    public LostEvent getLostEvent()
    {
        return lostEvent;
    }

    public void setLostEvent( LostEvent lostEvent )
    {
        this.lostEvent = lostEvent;
    }

    public Command getSendCommand()
    {
        if ( sendCommand == null )
        {
            sendCommand = new Command( Text.SEND() );
        }
        return sendCommand;
    }

    public TextField getTxtDueDate()
    {
        if ( txtDueDate == null )
        {
            txtDueDate = new TextField();
            txtDueDate.setText( lostEvent.getDueDate() );
        }
        return txtDueDate;
    }

    public TextField getTxtComment()
    {
        if ( txtComment == null )
        {
            txtComment = new TextField();
        }
        return txtComment;
    }

    public TextField getTxtSMS()
    {
        if ( txtSMS == null )
        {
            txtSMS = new TextField();
        }
        return txtSMS;
    }

    public ComboBox getCbxStatus()
    {
        if ( cbxStatus == null )
        {
            String[] status = { "Overdue", "Skipped"};
            cbxStatus = new ComboBox( status );
            cbxStatus.setSelectedIndex( 0 );
        }
        return cbxStatus;
    }
    
    public Label getLblWrongDOB()
    {
        if ( lblWrongDOB == null )
        {
            lblWrongDOB = new Label();
            lblWrongDOB.getStyle().setFgColor( 0xcc0000 );
        }
        return lblWrongDOB;
    }
}
