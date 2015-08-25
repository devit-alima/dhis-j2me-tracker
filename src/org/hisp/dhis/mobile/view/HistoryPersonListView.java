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

import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.LinkButton;
import org.hisp.dhis.mobile.model.Model;
import org.hisp.dhis.mobile.model.ModelList;
import org.hisp.dhis.mobile.model.Patient;
import org.hisp.dhis.mobile.model.Program;
import org.hisp.dhis.mobile.recordstore.PatientRecordStore;
import org.hisp.dhis.mobile.ui.Text;

import com.sun.lwuit.Command;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.plaf.Style;

/**
 * @author Nguyen Kim Lai
 * 
 * @version HistoryPersonListView.java 10:16:06 AM Jul 8, 2013 $
 */
public class HistoryPersonListView
    extends AbstractView
    implements ActionListener
{
    private static final String CLASS_TAG = "HistoryPersonListView";

    private NameBasedMIDlet nameBasedMIDlet;

    private Form mainForm;

    private Command backCommand;

    private ModelList modelList;

    private Program program;

    public HistoryPersonListView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        nameBasedMIDlet = (NameBasedMIDlet) dhisMIDlet;
    }

    public void prepareView()
    {
        System.gc();
        this.getMainForm();
        mainForm.removeAll();
        TextArea txtHint = new TextArea( "Select one instance below: " );
        txtHint.setEditable( false );
        mainForm.addComponent( txtHint );

        Vector offlinePatients = modelList.getModels();
        String trackedEntityName = "$$$";
        for ( int i = 0; i < offlinePatients.size(); i++ )
        {
            final Model patientModel = (Model) offlinePatients.elementAt( i );
            if ( !patientModel.getName().startsWith( trackedEntityName ) )
            {
                trackedEntityName = patientModel.getName().substring( 0, patientModel.getName().indexOf( "/" ) );

                Style labelStyle = new Style();
                labelStyle.setBgColor( Text.HEADER_BG_COLOR() );

                Label trackedEntitylbl = new Label( trackedEntityName );
                trackedEntitylbl.setUnselectedStyle( labelStyle );
                trackedEntitylbl.setSelectedStyle( labelStyle );
                trackedEntitylbl.setFocusable( false );
                mainForm.addComponent( trackedEntitylbl );

            }

            Patient patient = null;
            try
            {
                patient = PatientRecordStore.getPatient( patientModel.getId() );
            }
            catch ( Exception e )
            {
                LogMan.log( "UI," + CLASS_TAG, e );
                e.printStackTrace();
            }
            if ( patient != null && patient.isEnrolledIn( program ) )
            {
                String patientInfo = patientModel.getName().substring( patientModel.getName().indexOf( "/" ) + 1 );
                LinkButton personLink = new LinkButton( patientInfo );
                /*
                 * name = null; birthday = null; patientInfo = null;
                 */
                personLink.addActionListener( new ActionListener()
                {
                    public void actionPerformed( ActionEvent ae )
                    {
                        nameBasedMIDlet.getWaitingView().showView();
                        try
                        {
                            Patient patient = PatientRecordStore.getPatient( patientModel.getId() );
                            nameBasedMIDlet.getPersonDashboardView().setPatient( patient );
                            nameBasedMIDlet.getPersonDashboardView().setPreviousScreen(
                                nameBasedMIDlet.HISTORY_PERSON_LIST_VIEW );
                            nameBasedMIDlet.getPersonDashboardView().showView();
                        }
                        catch ( Exception e )
                        {
                            LogMan.log( "UI," + CLASS_TAG, e );
                            e.printStackTrace();
                        }
                        mainForm = null;
                        backCommand = null;
                        modelList = null;
                        System.gc();
                    }
                } );
                mainForm.addComponent( personLink );
            }
        }
    }

    public void showView()
    {
        this.prepareView();
        this.getMainForm().show();
    }

    public void actionPerformed( ActionEvent ae )
    {
        if ( ae.getCommand().getCommandName().equals( Text.BACK() ) )
        {
            nameBasedMIDlet.getTrackingMainMenuView().showView();
            mainForm = null;
            backCommand = null;
            modelList = null;
        }
    }

    public Form getMainForm()
    {
        if ( mainForm == null )
        {
            mainForm = new Form( "History" );
            mainForm.setLayout( new BoxLayout( BoxLayout.Y_AXIS ) );
            mainForm.setTransitionOutAnimator( CommonTransitions.createSlide( CommonTransitions.SLIDE_HORIZONTAL, true,
                200 ) );
            mainForm.addCommand( this.getBackCommand() );
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

    public ModelList getModelList()
    {
        return modelList;
    }

    public void setModelList( ModelList modelList )
    {
        this.modelList = modelList;
    }

    public Program getProgram()
    {
        return program;
    }

    public void setProgram( Program program )
    {
        this.program = program;
    }

}
