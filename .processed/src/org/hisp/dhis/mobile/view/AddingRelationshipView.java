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
import org.hisp.dhis.mobile.model.Model;
import org.hisp.dhis.mobile.model.OrgUnit;
import org.hisp.dhis.mobile.model.Patient;
import org.hisp.dhis.mobile.model.PatientAttribute;
import org.hisp.dhis.mobile.model.Program;
import org.hisp.dhis.mobile.model.Relationship;
import org.hisp.dhis.mobile.recordstore.OrgUnitRecordStore;
import org.hisp.dhis.mobile.recordstore.ProgramRecordStore;
import org.hisp.dhis.mobile.ui.Text;
import org.hisp.dhis.mobile.util.TrackingUtils;

import com.sun.lwuit.ComboBox;
import com.sun.lwuit.Command;
import com.sun.lwuit.Form;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.TextField;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;

/**
 * @author Nguyen Kim Lai
 * 
 * @version AddingRelationshipForm.java 10:11:49 AM Mar 18, 2013 $
 */
public class AddingRelationshipView
    extends AbstractView
    implements ActionListener
{
    private NameBasedMIDlet nameBasedMIDlet;

    private Command findCommand;
    
    private Command addNewInstanceCommand;

    private Command backCommand;

    private Form mainForm;

    private ComboBox relationshipOptions;

    private ComboBox attributeOptions;

    private Vector attributeVector;

    private TextField keywordTextField;

    private Relationship enrollmentRelationship;

    private TextArea notification;

    private ComboBox orgUnitOptions;

    private TextArea txtPatientAName;

    private TextArea txtSearchHint;

    private TextArea txtSelectOrgUnit;

    private Patient patient;

    Vector orgUnitVector;

    private int relationshipIndex = 0;

    private int orgUnitIndex = 0;

    private int attributeIndex = 0;

    public AddingRelationshipView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.nameBasedMIDlet = (NameBasedMIDlet) dhisMIDlet;
    }

    public void actionPerformed( ActionEvent ae )
    {
        if ( ae.getCommand().getCommandName().equals( Text.FIND() ) )
        {
            nameBasedMIDlet.getWaitingView().showView();
            String keyWord = this.getKeywordTextField().getText().trim();
            enrollmentRelationship.setPersonBName( keyWord );
            enrollmentRelationship.setChosenRelationship( (String) relationshipOptions.getSelectedItem() );
            relationshipIndex = relationshipOptions.getSelectedIndex();

            if ( keyWord.equals( "" ) )
            {
                this.prepareView();
                this.getNotification().setText( "(*): Required Field" );
                this.getMainForm().show();
            }
            else
            {
                String query = "";
                if ( attributeOptions.getSelectedItem().toString().equals( "All" ) )
                {
                    query = "like:" + keyWord;
                    attributeIndex = 0;
                }
                else
                {
                    query = attributeOptions.getSelectedItem().toString() + ":like:" + keyWord;
                    attributeIndex = attributeOptions.getSelectedIndex();
                }
                enrollmentRelationship.setPersonBName( query );

                if ( ((String) orgUnitOptions.getSelectedItem()).equals( "All" ) )
                {
                    String tempURL = nameBasedMIDlet.getCurrentOrgUnit().getAddRelationshipUrl();

                    ConnectionManager.setUrl( TrackingUtils.getUrlForSelectionAll( tempURL ) );

                    tempURL = null;
                    orgUnitIndex = orgUnitOptions.getSelectedIndex();
                }
                else
                {
                    nameBasedMIDlet.setCurrentOrgUnit( (OrgUnit) orgUnitVector.elementAt( orgUnitOptions
                        .getSelectedIndex() ) );
                    ConnectionManager.setUrl( nameBasedMIDlet.getCurrentOrgUnit().getAddRelationshipUrl() );
                    orgUnitIndex = orgUnitOptions.getSelectedIndex();
                }

                ConnectionManager.addRelationship( enrollmentRelationship, patient );
                findCommand = null;
                addNewInstanceCommand = null;
                backCommand = null;
                mainForm = null;
                relationshipOptions = null;
                attributeOptions = null;
                attributeVector = null;
                keywordTextField = null;
                enrollmentRelationship = null;
                notification = null;
                orgUnitOptions = null;
                orgUnitVector = null;
                txtPatientAName = null;
                txtSearchHint = null;
                txtSelectOrgUnit = null;

                System.gc();
            }
        }
        else if ( ae.getCommand().getCommandName().equals( Text.ADD_NEW_INSTANCE() ) )
        {
            try
            {
                nameBasedMIDlet.getProgramSelectView().setPrograms(
                    ProgramRecordStore.getNonAnonymousPrograms() );
                nameBasedMIDlet.getProgramSelectView().setProgramType( "1" );
                nameBasedMIDlet.getProgramSelectView().setTargetScreen(
                    nameBasedMIDlet.PERSON_REGISTRATION_VIEW );
                nameBasedMIDlet.getProgramSelectView().setRelative( patient );
                enrollmentRelationship.setChosenRelationship( (String) relationshipOptions.getSelectedItem() );
                nameBasedMIDlet.getProgramSelectView().setEnrollmentRelationship( enrollmentRelationship );
                nameBasedMIDlet.getProgramSelectView().showView();

                findCommand = null;
                backCommand = null;
                mainForm = null;
                relationshipOptions = null;
                attributeOptions = null;
                attributeVector = null;
                keywordTextField = null;
                enrollmentRelationship = null;
                notification = null;
                orgUnitOptions = null;
                orgUnitVector = null;
                txtPatientAName = null;
                txtSearchHint = null;
                txtSelectOrgUnit = null;
                
                System.gc();
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }
        }
        else
        {
            findCommand = null;
            addNewInstanceCommand = null;
            backCommand = null;
            mainForm = null;
            relationshipOptions = null;
            attributeOptions = null;
            attributeVector = null;
            keywordTextField = null;
            enrollmentRelationship = null;
            notification = null;
            orgUnitOptions = null;
            orgUnitVector = null;
            txtPatientAName = null;
            txtSearchHint = null;
            txtSelectOrgUnit = null;

            nameBasedMIDlet.getPersonDashboardView().setPatient( patient );
            nameBasedMIDlet.getPersonDashboardView().showView();
        }
    }

    public void prepareView()
    {
        System.gc();
        getMainForm();
        mainForm.removeAll();
        txtPatientAName = new TextArea( enrollmentRelationship.getPersonAName() + " is" );
        txtPatientAName.setEditable( false );
        txtPatientAName.setFocusable( false );
        mainForm.addComponent( txtPatientAName );

        mainForm.addComponent( getRelationshipOptions() );
        relationshipOptions.requestFocus();

        txtSearchHint = new TextArea( "of: *" );
        txtSearchHint.setEditable( false );
        txtSearchHint.setFocusable( false );
        mainForm.addComponent( txtSearchHint );
        mainForm.addComponent( getAttributeOptions() );
        mainForm.addComponent( this.getKeywordTextField() );

        txtSelectOrgUnit = new TextArea( "Select Organisation Unit" );
        txtSearchHint.setEditable( false );
        txtSearchHint.setFocusable( false );
        mainForm.addComponent( txtSelectOrgUnit );
        mainForm.addComponent( getOrgUnitOptions() );

        mainForm.addComponent( this.getNotification() );

    }

    public void showView()
    {
        prepareView();
        this.getMainForm().show();
    }

    public Form getMainForm()
    {
        if ( mainForm == null )
        {
            mainForm = new Form( "Add Relationship" );
            mainForm.setLayout( new BoxLayout( BoxLayout.Y_AXIS ) );
            mainForm.setTransitionOutAnimator( CommonTransitions.createSlide( CommonTransitions.SLIDE_HORIZONTAL, true,
                200 ) );
            mainForm.addCommand( this.getBackCommand() );
            mainForm.addCommand( this.getFindCommand() );
            mainForm.addCommand( this.getAddNewInstanceCommand() );
            mainForm.addCommandListener( this );
        }
        return mainForm;
    }

    public void setMainForm( Form mainForm )
    {
        this.mainForm = mainForm;
    }

    public NameBasedMIDlet getNameBasedMIDlet()
    {
        return nameBasedMIDlet;
    }

    public void setNameBasedMIDlet( NameBasedMIDlet nameBasedMIDlet )
    {
        this.nameBasedMIDlet = nameBasedMIDlet;
    }
    
    public Command getAddNewInstanceCommand()
    {
        if ( addNewInstanceCommand == null )
        {
            addNewInstanceCommand = new Command( Text.ADD_NEW_INSTANCE() );
        }
        return addNewInstanceCommand;
    }

    public Command getFindCommand()
    {
        if ( findCommand == null )
        {
            findCommand = new Command( Text.FIND() );
        }
        return findCommand;
    }

    public void setFindCommand( Command findCommand )
    {
        this.findCommand = findCommand;
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

    public TextField getKeywordTextField()
    {
        if ( keywordTextField == null )
        {
            keywordTextField = new TextField();
        }
        return keywordTextField;
    }

    public void setKeywordTextField( TextField keywordTextField )
    {
        this.keywordTextField = keywordTextField;
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

    public ComboBox getRelationshipOptions()
    {
        relationshipOptions = new ComboBox();
        relationshipOptions.addItem( this.enrollmentRelationship.getaIsToB() );
        relationshipOptions.addItem( this.enrollmentRelationship.getbIsToA() );
        relationshipOptions.setSelectedIndex( relationshipIndex );
        return relationshipOptions;
    }

    public void setRelationshipOptions( ComboBox relationshipOptions )
    {
        this.relationshipOptions = relationshipOptions;
    }

    public ComboBox getAttributeOptions()
    {
        if ( attributeOptions == null )
        {
            try
            {
                attributeOptions = new ComboBox();

                attributeVector = new Vector();

                Vector programVector = ProgramRecordStore.getCurrentPrograms().getModels();

                for ( int j = 0; j < programVector.size(); j++ )
                {
                    Program program = ProgramRecordStore.getProgram( ((Model) programVector.elementAt( j )).getId() );
                    Vector patientAttributeVector = program.getProgramAttributes();
                    if ( patientAttributeVector != null )
                    {
                        for ( int i = 0; i < patientAttributeVector.size(); i++ )
                        {
                            PatientAttribute patientAttribute = (PatientAttribute) patientAttributeVector.elementAt( i );
                            if ( !attributeVectorContains( patientAttribute ) )
                            {
                                attributeVector.addElement( patientAttribute );
                            }
                        }
                    }
                }

                attributeOptions.addItem( "All" );
                for ( int i = 0; i < attributeVector.size(); i++ )
                {
                    attributeOptions.addItem( ((PatientAttribute) attributeVector.elementAt( i )).getName() );
                }

                attributeOptions.setHint( "Select attribute" );
                ActionListener ae = new ActionListener()
                {
                    public void actionPerformed( ActionEvent arg0 )
                    {
                        int index = attributeOptions.getSelectedIndex();
                        if ( index != 0
                            && ((PatientAttribute) attributeVector.elementAt( index - 1 )).getType().equals( "date" ) )
                        {
                            keywordTextField.setHint( Text.DATE_TYPE() );
                        }
                        else
                        {
                            keywordTextField.setHint( "" );
                        }
                    }
                };
                attributeOptions.addActionListener( ae );
                attributeOptions.setSelectedIndex( attributeIndex );
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }
        }

        return attributeOptions;
    }

    private boolean attributeVectorContains( PatientAttribute patientAttribute )
    {
        if ( attributeVector != null )
        {
            for ( int i = 0; i < attributeVector.size(); i++ )
            {
                if ( ((PatientAttribute) attributeVector.elementAt( i )).getName().equals( patientAttribute.getName() ) )
                {
                    return true;
                }
            }
        }
        return false;
    }

    public TextArea getNotification()
    {
        if ( notification == null )
        {
            notification = new TextArea();
            notification.setEditable( false );
            notification.getStyle().setFgColor( Text.ERROR_TEXT_COLOR() );
        }
        return notification;
    }

    public void setNotification( TextArea notification )
    {
        this.notification = notification;
    }

    public ComboBox getOrgUnitOptions()
    {
        if ( orgUnitOptions == null )
        {
            orgUnitOptions = new ComboBox();

            orgUnitVector = null;

            try
            {
                orgUnitVector = OrgUnitRecordStore.loadAllOrgUnit();
            }
            catch ( Exception e )
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            for ( int i = 0; i < orgUnitVector.size(); i++ )
            {
                orgUnitOptions.addItem( ((OrgUnit) orgUnitVector.elementAt( i )).getName() );
            }
            orgUnitOptions.addItem( "All" );
            orgUnitOptions.setSelectedIndex( orgUnitIndex );
        }
        return orgUnitOptions;
    }

    public void setOrgUnitOptions( ComboBox orgUnitOptions )
    {
        this.orgUnitOptions = orgUnitOptions;
    }

    public void resetIndexes()
    {
        relationshipIndex = 0;
        attributeIndex = 0;
        orgUnitIndex = 0;
    }
}
