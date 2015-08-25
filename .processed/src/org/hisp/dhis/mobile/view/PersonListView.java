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
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.LinkButton;
import org.hisp.dhis.mobile.model.Patient;
import org.hisp.dhis.mobile.model.Relationship;
import org.hisp.dhis.mobile.recordstore.PatientRecordStore;
import org.hisp.dhis.mobile.ui.Text;

import com.sun.lwuit.Command;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.plaf.Style;

/**
 * @author Nguyen Kim Lai
 * 
 * @version PersonListView.java 2:46:19 PM Mar 19, 2013 $
 */
public class PersonListView
    extends AbstractView
    implements ActionListener
{
    private static final String CLASS_TAG = "PersonListView";

    private NameBasedMIDlet nameBasedMIDlet;

    private Form mainForm;

    private Vector patientInfos;

    private Command backCommand;

    private Relationship enrollmentRelationship;
    
    private Patient patient;

    private boolean isOffline;

    public PersonListView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        nameBasedMIDlet = (NameBasedMIDlet) dhisMIDlet;
    }

    public void prepareView()
    {
        System.gc();
        getMainForm();
        mainForm.removeAll();

        for ( int i = 0; i < patientInfos.size(); i++ )
        {
            String patientInfo = (String) patientInfos.elementAt( i );

            if ( patientInfo.indexOf( "/" ) == -1 )
            {
                Style labelStyle = new Style();
                labelStyle.setBgColor( Text.HEADER_BG_COLOR() );

                Label trackedEntitylbl = new Label( patientInfo );
                trackedEntitylbl.setUnselectedStyle( labelStyle );
                trackedEntitylbl.setSelectedStyle( labelStyle );
                trackedEntitylbl.setFocusable( false );
                mainForm.addComponent( trackedEntitylbl );
            }
            else
            {
                final String id = patientInfo.substring( 0, patientInfo.indexOf( "/" ) );

                patientInfo = patientInfo.substring( patientInfo.indexOf( "/" ) + 1, patientInfo.length() );
                /*
                 * String name = patientInfo.substring( 0, patientInfo.indexOf(
                 * "/" ) ); String birthday = patientInfo.substring(
                 * patientInfo.indexOf( "/" ) + 1, patientInfo.length() );
                 * LinkButton personLink = new LinkButton( name + ", DOB: " +
                 * birthday );
                 */
                LinkButton personLink = new LinkButton( patientInfo );
                personLink.addActionListener( new ActionListener()
                {
                    public void actionPerformed( ActionEvent ae )
                    {
                        nameBasedMIDlet.getWaitingView().showView();
                        if ( isOffline )
                        {
                            try
                            {
                                Patient patient = PatientRecordStore.getPatient( Integer.parseInt( id ) );
                                nameBasedMIDlet.getPersonDashboardView().setPatient( patient );
                                nameBasedMIDlet.getPersonDashboardView().showView();
                            }
                            catch ( Exception e )
                            {
                                LogMan.log( "UI," + CLASS_TAG, e );
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            if ( enrollmentRelationship == null )
                            {
                                ConnectionManager.setUrl( nameBasedMIDlet.getCurrentOrgUnit().getFindPatientUrl() );
                                ConnectionManager.getPatientById( id, nameBasedMIDlet.FIND_INSTANCE_VIEW, null );
                            }
                            else
                            {
                                enrollmentRelationship.setPersonBId( Integer.parseInt( id ) );
                                ConnectionManager.addRelationship( enrollmentRelationship, patient );
                            }
                        }
                        mainForm = null;
                        patientInfos = null;
                        backCommand = null;
                        enrollmentRelationship = null;
                        patient = null;
                        System.gc();
                    }
                } );
                mainForm.addComponent( personLink );
            }
        }
        if ( patientInfos.size() >= 10 )
        {
            nameBasedMIDlet.getAlertBoxView( "Too many results, be more specific", "Warning" ).showView();
        }
    }

    public void showView()
    {
        prepareView();
        mainForm.show();
    }

    public void actionPerformed( ActionEvent ae )
    {
        if ( ae.getCommand().getCommandName().equals( Text.BACK() ) )
        {
            patientInfos = null;
            mainForm = null;
            backCommand = null;
            if ( enrollmentRelationship == null )
            {
                nameBasedMIDlet.getFindBeneficiaryView().showView();
            }
            else
            {
                nameBasedMIDlet.getAddingRelationshipView().setPatient( patient );
                nameBasedMIDlet.getAddingRelationshipView().setEnrollmentRelationship( enrollmentRelationship );
                nameBasedMIDlet.getAddingRelationshipView().showView();
                enrollmentRelationship = null;
                patient = null;
            }
            System.gc();
        }
    }

    public Form getMainForm()
    {
        if ( mainForm == null )
        {
            mainForm = new Form( "Select Instance" );
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

    public Vector getPatientInfos()
    {
        return patientInfos;
    }

    public void setPatientInfos( Vector patientInfos )
    {
        this.patientInfos = patientInfos;
    }

    public Relationship getEnrollmentRelationship()
    {
        return enrollmentRelationship;
    }

    public void setEnrollmentRelationship( Relationship enrollmentRelationship )
    {
        this.enrollmentRelationship = enrollmentRelationship;
    }
    
    public void setPatient( Patient patient )
    {
        this.patient = patient;
    }

    public void setIsOffline( boolean isOffline )
    {
        this.isOffline = isOffline;
    }

    public boolean getIsOffline()
    {
        return isOffline;
    }

}
