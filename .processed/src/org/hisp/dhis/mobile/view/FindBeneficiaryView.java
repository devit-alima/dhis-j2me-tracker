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
import org.hisp.dhis.mobile.model.Model;
import org.hisp.dhis.mobile.model.OrgUnit;
import org.hisp.dhis.mobile.model.PatientAttribute;
import org.hisp.dhis.mobile.model.Program;
import org.hisp.dhis.mobile.recordstore.OrgUnitRecordStore;
import org.hisp.dhis.mobile.recordstore.ProgramRecordStore;
import org.hisp.dhis.mobile.ui.Text;
import org.hisp.dhis.mobile.util.TrackingUtils;

import com.sun.lwuit.Button;
import com.sun.lwuit.ComboBox;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Font;
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
 * @version DataEntryView.java 2:06:37 PM Feb 19, 2013 $
 */
public class FindBeneficiaryView
    extends AbstractView
    implements ActionListener
{
    private static final String CLASS_TAG = "FindBeneficiaryView";

    private static final int MINIMUM_KEYWORD_LENGTH = 3;

    private Form mainForm;

    private TextField keywordTextField;

    private Button btnFind;

    private Command backCommand;

    private NameBasedMIDlet nameBasedMIDlet;

    private TextArea notification;

    private ComboBox attributeOptions;

    private ComboBox orgUnitOptions;

    private ComboBox programOptions;

    private int programIndex = 0;

    private int orgUnitIndex = 0;

    private int attributeIndex = 0;

    private String keyword;

    private String query;

    Vector attributeVector;

    Vector orgUnitVector;

    Vector programVector;

    public FindBeneficiaryView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.nameBasedMIDlet = (NameBasedMIDlet) this.dhisMIDlet;
    }

    public void prepareView()
    {
        System.gc();
        getMainForm();
        mainForm.removeAll();

        TextArea txtSearchHint = new TextArea( Text.SEARCH_HINT() );
        txtSearchHint.setEditable( false );
        txtSearchHint.setFocusable( false );
        mainForm.addComponent( txtSearchHint );

        mainForm.addComponent( getAttributeOptions() );
        attributeOptions.requestFocus();

        mainForm.addComponent( getKeywordTextField() );

        TextArea txtSelectOrgUnit = new TextArea( Text.ORG_UNIT() );
        txtSelectOrgUnit.setEditable( false );
        txtSelectOrgUnit.setFocusable( false );
        mainForm.addComponent( txtSelectOrgUnit );
        mainForm.addComponent( getOrgUnitOptions() );

        // TextArea txtSelectProgram = new TextArea( Text.PROGRAM() );
        // txtSelectProgram.setEditable( false );
        // mainForm.addComponent( txtSelectProgram );
        // mainForm.addComponent( getProgramOptions() );

        mainForm.addComponent( getBtnFind() );
        mainForm.addComponent( getNotification() );
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
            mainForm = new Form( Text.PERSON_SEARCH() );
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

    public TextField getKeywordTextField()
    {
        if ( keywordTextField == null )
        {
            keywordTextField = new TextField();
        }
        if ( keyword != null )
        {
            keywordTextField.setText( keyword );
        }
        return keywordTextField;
    }

    public void setKeywordTextField( TextField keywordTextField )
    {
        this.keywordTextField = keywordTextField;
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

    public Button getBtnFind()
    {
        if ( btnFind == null )
        {
            btnFind = new Button( Text.ENTER() );
            btnFind.addActionListener( new ActionListener()
            {
                public void actionPerformed( ActionEvent ae )
                {
                    keyword = keywordTextField.getText().trim();
                    if ( keyword.equals( "" ) )
                    {
                        prepareView();
                        getNotification().setText( "(*): Required Field" );
                        getMainForm().show();
                    }

                    else
                    {
                        nameBasedMIDlet.getWaitingView().showView();

                        // Org-Unit Selection
                        if ( ((String) orgUnitOptions.getSelectedItem()).equals( "All" ) )
                        {
                            //nameBasedMIDlet.setCurrentOrgUnit( (OrgUnit) orgUnitVector.elementAt( 0 ) );

                            String tempURL = nameBasedMIDlet.getCurrentOrgUnit().getFindPatientInAdvancedUrl();

                            ConnectionManager.setUrl( TrackingUtils.getUrlForSelectionAll( tempURL ) );

                            tempURL = null;

                            orgUnitIndex = 0;
                        }
                        else
                        {
//                            nameBasedMIDlet.setCurrentOrgUnit( (OrgUnit) orgUnitVector.elementAt( orgUnitOptions
//                                .getSelectedIndex() - 1 ) );
                            ConnectionManager
                                .setUrl( nameBasedMIDlet.getCurrentOrgUnit().getFindPatientInAdvancedUrl() );
                            orgUnitIndex = orgUnitOptions.getSelectedIndex();
                        }

                        // Program Selection
                        String tempURL = ConnectionManager.getUrl() + "/0";
                        ConnectionManager.setUrl( tempURL );

                        if ( attributeOptions.getSelectedItem().toString().equals( "All" ) )
                        {
                            query = "like:" + keyword;
                            attributeIndex = 0;
                        }
                        else
                        {
                            query = attributeOptions.getSelectedItem().toString() + ":like:" + keyword;
                            attributeIndex = attributeOptions.getSelectedIndex();
                        }
                        ConnectionManager.findPatientByName( query );
                        mainForm = null;
                        attributeOptions = null;
                        keywordTextField = null;
                        btnFind = null;
                        backCommand = null;
                        notification = null;
                        orgUnitOptions = null;
                        orgUnitVector = null;
                        programOptions = null;
                        programVector = null;
                        query = null;

                        System.gc();
                    }
                }
            } );
            btnFind.getUnselectedStyle().setAlignment( Component.CENTER );
            btnFind.getSelectedStyle().setAlignment( Component.CENTER );
            btnFind.getPressedStyle().setAlignment( Component.CENTER );
            btnFind.getUnselectedStyle().setFont(
                Font.createSystemFont( Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM ) );
            btnFind.getSelectedStyle().setFont(
                Font.createSystemFont( Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM ) );
            btnFind.getPressedStyle().setFont(
                Font.createSystemFont( Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM ) );
        }
        return btnFind;
    }

    public void setBtnFind( Button btnFind )
    {
        this.btnFind = btnFind;
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

    public ComboBox getAttributeOptions()
    {
        if ( attributeOptions == null )
        {
            try
            {
                attributeOptions = new ComboBox();

                attributeVector = new Vector();

                programVector = ProgramRecordStore.getCurrentPrograms().getModels();

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
                LogMan.log( "UI," + CLASS_TAG, e );
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
                LogMan.log( "UI," + CLASS_TAG, e );
                e.printStackTrace();
            }

            orgUnitOptions.addItem( "All" );

            for ( int i = 0; i < orgUnitVector.size(); i++ )
            {
                orgUnitOptions.addItem( ((OrgUnit) orgUnitVector.elementAt( i )).getName() );
            }
        }
        orgUnitOptions.setSelectedIndex( orgUnitIndex );
        return orgUnitOptions;
    }

    public void setOrgUnitOptions( ComboBox orgUnitOptions )
    {
        this.orgUnitOptions = orgUnitOptions;
    }

    public ComboBox getProgramOptions()
    {
        if ( programOptions == null )
        {
            programOptions = new ComboBox();

            programVector = null;

            try
            {
                programVector = ProgramRecordStore.getCurrentPrograms().getModels();
            }
            catch ( Exception e )
            {
                LogMan.log( "UI," + CLASS_TAG, e );
                e.printStackTrace();
            }

            programOptions.addItem( "All" );

            for ( int i = 0; i < programVector.size(); i++ )
            {
                programOptions.addItem( ((Model) programVector.elementAt( i )).getName() );
            }
        }
        programOptions.setSelectedIndex( programIndex );
        return programOptions;
    }

    public void setProgramOptions( ComboBox programOptions )
    {
        this.programOptions = programOptions;
    }

    public void actionPerformed( ActionEvent ae )
    {
        if ( ae.getCommand().getCommandName().equals( Text.BACK() ) )
        {
            nameBasedMIDlet.getTrackingMainMenuView().showView();
            mainForm = null;
            attributeOptions = null;
            keywordTextField = null;
            btnFind = null;
            backCommand = null;
            notification = null;
            orgUnitOptions = null;
            orgUnitVector = null;
            programOptions = null;
            programVector = null;
            query = null;
            System.gc();
        }
    }
}
