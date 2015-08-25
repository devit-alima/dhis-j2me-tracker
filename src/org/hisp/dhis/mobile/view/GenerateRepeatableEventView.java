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
import org.hisp.dhis.mobile.model.Patient;
import org.hisp.dhis.mobile.model.ProgramInstance;
import org.hisp.dhis.mobile.model.ProgramStage;
import org.hisp.dhis.mobile.ui.Text;
import org.hisp.dhis.mobile.util.PeriodUtil;

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
 * @version GenerateRepeatableEventView.java 3:24:10 PM Oct 28, 2013 $
 */
public class GenerateRepeatableEventView
    extends AbstractView
    implements ActionListener
{
    private Form mainForm;

    private Command createCommand, noCommand;

    private Label lblProgramStageName;

    private TextField txtDueDate;

    private Label lbldueDate;

    private Label lblWrongFormat;

    private String defaultDueDate;

    private ProgramStage programStage;

    private Patient patient;

    private NameBasedMIDlet nameBasedMidlet;

    public GenerateRepeatableEventView( DHISMIDlet dhisMIDlet, ProgramStage programStage, String defaultDueDate,
        Patient patient )
    {
        super( dhisMIDlet );
        this.programStage = programStage;
        this.defaultDueDate = defaultDueDate;
        this.patient = patient;
        nameBasedMidlet = (NameBasedMIDlet) this.dhisMIDlet;
    }

    public void actionPerformed( ActionEvent ae )
    {
        if ( ae.getCommand().getCommandName().equalsIgnoreCase( Text.NO() ) )
        {
            ProgramInstance programInstance = findProgramInstance();
            if ( programStage.isRepeatable() && isAllProgramStageFinished( programInstance ) )
            {
                ConnectionManager.setUrl( nameBasedMidlet.getCurrentOrgUnit().getCompleteProgramInstanceUrl() );
                ConnectionManager.completeProgramInstance( programInstance, patient );
            }
            else
            {
                nameBasedMidlet.getPersonDashboardView().setPatient( patient );
                nameBasedMidlet.getPersonDashboardView().showView();
            }
        }
        else
        {
            String dueDate = txtDueDate.getText().trim();
            if ( validateDueDate( dueDate ) == false )
            {
                getMainForm().show();
            }
            else
            {
                ConnectionManager.setUrl( nameBasedMidlet.getCurrentOrgUnit().getGenerateRepeatableEventUrl() );
                nameBasedMidlet.getWaitingView().showView();
                ConnectionManager.generateRepeatableEvent( programStage.getId() + "$" + dueDate );
            }
        }
    }

    private ProgramInstance findProgramInstance()
    {
        for ( int i = 0; i < patient.getEnrollmentPrograms().size(); i++ )
        {
            ProgramInstance programInstance = (ProgramInstance) patient.getEnrollmentPrograms().elementAt( i );
            for ( int j = 0; j < programInstance.getProgramStageInstances().size(); j++ )
            {
                ProgramStage programStage = (ProgramStage) programInstance.getProgramStageInstances().elementAt( j );
                System.out.println( programStage.getId() + "==" + programStage.getId() );
                if ( programStage.getId() == programStage.getId() )
                {
                    return programInstance;
                }
            }
        }
        return null;
    }

    private boolean isAllProgramStageFinished( ProgramInstance programInstance )
    {
        if ( programInstance == null )
        {
            return false;
        }
        for ( int j = 0; j < programInstance.getProgramStageInstances().size(); j++ )
        {
            final ProgramStage programStage = (ProgramStage) programInstance.getProgramStageInstances().elementAt( j );
            if ( !programStage.isCompleted() )
            {
                return false;
            }
        }
        return true;
    }

    public boolean validateDueDate( String dueDate )
    {
        if ( dueDate.equals( "" ) )
        {
            lblWrongFormat.setText( "(*): Required Field" );
            return false;
        }
        else if ( PeriodUtil.isDateValid( dueDate ) == false )
        {
            lblWrongFormat.setText( Text.DATE_TYPE() );
            return false;
        }
        else
        {
            return true;
        }
    }

    public void prepareView()
    {
        System.gc();
        getMainForm();
        mainForm.removeAll();
    }

    public void showView()
    {
        prepareView();
        mainForm.show();
        mainForm.addComponent( getLblProgramStageName() );
        mainForm.addComponent( getLbldueDate() );
        mainForm.addComponent( getTxtDueDate() );
        mainForm.addComponent( getLblWrongFormat() );
    }

    public Form getMainForm()
    {
        if ( mainForm == null )
        {
            mainForm = new Form( "Create new event" );
            mainForm.setLayout( new BoxLayout( BoxLayout.Y_AXIS ) );
            mainForm.setTransitionOutAnimator( CommonTransitions.createSlide( CommonTransitions.SLIDE_HORIZONTAL, true,
                200 ) );
            mainForm.setTransitionOutAnimator( CommonTransitions.createSlide( CommonTransitions.SLIDE_HORIZONTAL, true,
                200 ) );
            mainForm.addCommand( getNoCommand() );
            mainForm.addCommand( getCreateCommand() );
            mainForm.addCommandListener( this );
        }
        return mainForm;
    }

    public void setMainForm( Form mainForm )
    {
        this.mainForm = mainForm;
    }

    public Command getCreateCommand()
    {
        if ( createCommand == null )
        {
            createCommand = new Command( "Create" );
        }
        return createCommand;
    }

    public Command getNoCommand()
    {
        if ( noCommand == null )
        {
            noCommand = new Command( Text.NO() );
        }
        return noCommand;
    }

    public TextField getTxtDueDate()
    {
        if ( txtDueDate == null )
        {
            txtDueDate = new TextField( defaultDueDate );
        }
        return txtDueDate;
    }

    public Label getLbldueDate()
    {
        if ( lbldueDate == null )
        {
            lbldueDate = new Label( "Due date: " );
        }
        return lbldueDate;
    }

    public String getDefaultDueDate()
    {
        return defaultDueDate;
    }

    public void setDefaultDueDate( String defaultDueDate )
    {
        this.defaultDueDate = defaultDueDate;
    }

    public void setProgramStage( ProgramStage programStage )
    {
        this.programStage = programStage;
    }

    public void setPatient( Patient patient )
    {
        this.patient = patient;
    }

    public Label getLblProgramStageName()
    {
        if ( lblProgramStageName == null )
        {
            lblProgramStageName = new Label( programStage.getName() );
        }
        return lblProgramStageName;
    }

    public Label getLblWrongFormat()
    {
        if ( lblWrongFormat == null )
        {
            lblWrongFormat = new Label();
            lblWrongFormat.getStyle().setFgColor( Text.ERROR_TEXT_COLOR() );
        }
        return lblWrongFormat;
    }

}
