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

import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.OrgUnit;
import org.hisp.dhis.mobile.model.Patient;
import org.hisp.dhis.mobile.model.ProgramStage;
import org.hisp.dhis.mobile.model.ProgramStageDataElement;
import org.hisp.dhis.mobile.model.Section;
import org.hisp.dhis.mobile.recordstore.OfflinePatientRecordStore;
import org.hisp.dhis.mobile.recordstore.OrgUnitRecordStore;
import org.hisp.dhis.mobile.ui.Text;
import org.hisp.dhis.mobile.util.TrackingUtils;

import com.sun.lwuit.Button;
import com.sun.lwuit.CheckBox;
import com.sun.lwuit.ComboBox;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Display;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.TextField;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.plaf.Style;

/**
 * @author Nguyen Kim Lai
 * 
 * @version TrackingDataEntryView.java 2:06:37 PM Feb 19, 2013 $
 */
public class TrackingDataEntryView
    extends AbstractView
    implements ActionListener
{
    private static final String CLASS_TAG = "TrackingDataEntryView";

    private String title;

    private NameBasedMIDlet namebasedMidlet;

    private Form mainForm;

    private Hashtable dataElementsMappingTable;

    private Command backCommand;

    private Command sendCommand;

    private Command saveCommand;

    private Patient patient;

    private ProgramStage programStage;

    private ProgramStage uploadProgramStage;

    private Section section;

    private TextField reportDateField = new TextField();

    private CheckBox completeStatusCheckBox;

    private Dialog dialog;

    public TrackingDataEntryView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.namebasedMidlet = (NameBasedMIDlet) dhisMIDlet;
    }

    public void prepareView()
    {
        System.gc();
        getDataElementsMappingTable();
        getMainForm();
        mainForm.removeAll();
        mainForm.setTitle( getTitle() );

        Style labelStyle = new Style();
        labelStyle.setBgColor( Text.HEADER_BG_COLOR() );
        if ( section != null )
        {

            // Add report date field
            mainForm
                .addComponent( new Label( programStage.getReportDateDescription() + " [" + Text.DATE_TYPE() + "]" ) );
            // reportDateField.setText( "Today" );

            Calendar today = Calendar.getInstance();
            int month = today.get( Calendar.MONTH ) + 1;
            reportDateField
                .setText( today.get( Calendar.YEAR )
                    + "-"
                    + (month < 10 ? "0" + month : "" + month)
                    + "-"
                    + (today.get( Calendar.DATE ) < 10 ? "0" + today.get( Calendar.DATE ) : ""
                        + today.get( Calendar.DATE )) );
            mainForm.addComponent( reportDateField );

            for ( int i = 0; i < programStage.getDataElements().size(); i++ )
            {
                for ( int j = 0; j < section.getDataElementIds().size(); j++ )
                {
                    if ( ((Integer) section.getDataElementIds().elementAt( j )).equals( Integer
                        .valueOf( ((ProgramStageDataElement) programStage.getDataElements().elementAt( i )).getId()
                            + "" ) ) )
                    {
                        mainForm.addComponent( generateDataElementLabel( labelStyle,
                            (ProgramStageDataElement) programStage.getDataElements().elementAt( i ) ) );
                        checkIfViolated( (ProgramStageDataElement) programStage.getDataElements().elementAt( i ) );
                        mainForm.addComponent( getComponentByType( (ProgramStageDataElement) programStage
                            .getDataElements().elementAt( i ) ) );
                        break;
                    }
                }
            }

            // Add complete check box
            completeStatusCheckBox = new CheckBox( "Complete" );
            mainForm.addComponent( completeStatusCheckBox );
        }
        else if ( programStage != null )
        {
            mainForm
                .addComponent( new Label( programStage.getReportDateDescription() + " [" + Text.DATE_TYPE() + "]" ) );

            if ( !programStage.getReportDate().equals( "" ) )
            {
                reportDateField.setText( programStage.getReportDate() );
            }
            else
            {
                // reportDateField.setText( "Today" );
                Calendar today = Calendar.getInstance();
                int month = today.get( Calendar.MONTH ) + 1;
                reportDateField.setText( today.get( Calendar.YEAR )
                    + "-"
                    + (month < 10 ? "0" + month : "" + month)
                    + "-"
                    + (today.get( Calendar.DATE ) < 10 ? "0" + today.get( Calendar.DATE ) : ""
                        + today.get( Calendar.DATE )) );
            }
            reportDateField.setEnabled( !programStage.isCompleted() );
            mainForm.addComponent( reportDateField );

            for ( int i = 0; i < programStage.getDataElements().size(); i++ )
            {
                mainForm.addComponent( generateDataElementLabel( labelStyle, ((ProgramStageDataElement) programStage
                    .getDataElements().elementAt( i )) ) );
                checkIfViolated( ((ProgramStageDataElement) programStage.getDataElements().elementAt( i )) );

                Component c = getComponentByType( ((ProgramStageDataElement) programStage.getDataElements().elementAt(
                    i )) );

                c.setEnabled( !programStage.isCompleted() );
                mainForm.addComponent( c );
            }

            // Add complete check box
            completeStatusCheckBox = new CheckBox( "Complete" );
            if ( programStage.isCompleted() )
            {
                completeStatusCheckBox.setSelected( true );
            }
            completeStatusCheckBox.setEnabled( !programStage.isCompleted() );
            mainForm.addComponent( completeStatusCheckBox );
        }

        /*
         * Button btnSend = new Button( "Send" ); btnSend.addActionListener( new
         * ActionListener() {
         * 
         * public void actionPerformed( ActionEvent arg ) { collectInputData();
         * uploadInputData(); } } ); btnSend.getUnselectedStyle().setAlignment(
         * Component.CENTER ); btnSend.getSelectedStyle().setAlignment(
         * Component.CENTER ); btnSend.getPressedStyle().setAlignment(
         * Component.CENTER ); btnSend.getUnselectedStyle().setFont(
         * Font.createSystemFont( Font.FACE_SYSTEM, Font.STYLE_BOLD,
         * Font.SIZE_MEDIUM ) ); btnSend.getSelectedStyle() .setFont(
         * Font.createSystemFont( Font.FACE_SYSTEM, Font.STYLE_BOLD,
         * Font.SIZE_MEDIUM ) ); btnSend.getPressedStyle() .setFont(
         * Font.createSystemFont( Font.FACE_SYSTEM, Font.STYLE_BOLD,
         * Font.SIZE_MEDIUM ) ); mainForm.addComponent( btnSend );
         */
    }

    public void uploadInputData()
    {
        Enumeration dataElemetKeys = dataElementsMappingTable.keys();
        boolean isErrored = false;
        ProgramStageDataElement de;

        while ( dataElemetKeys.hasMoreElements() )
        {
            de = (ProgramStageDataElement) dataElemetKeys.nextElement();
            if ( TrackingUtils.getTypeViolation( de ) != null )
            {
                de.setViolatedMessage( TrackingUtils.getTypeViolation( de ) );
                isErrored = true;
            }
            de = null;
            System.gc();
        }

        if ( isErrored == true )
        {
            namebasedMidlet.getTrackingDataEntryView().setPatient( patient );
            namebasedMidlet.getTrackingDataEntryView().setProgramStage( programStage );
            namebasedMidlet.getTrackingDataEntryView().setSection( section );
            namebasedMidlet.getTrackingDataEntryView().setDataElementsMappingTable( dataElementsMappingTable );
            namebasedMidlet.getTrackingDataEntryView().showView();
        }
        else
        {
            this.namebasedMidlet.getWaitingView().showView();

            if ( namebasedMidlet.getCurrentOrgUnit() == null )
            {
                try
                {
                    namebasedMidlet.setCurrentOrgUnit( (OrgUnit) OrgUnitRecordStore.loadAllOrgUnit().elementAt( 0 ) );
                }

                catch ( Exception e )
                {
                    LogMan.log( "UI," + CLASS_TAG, e );
                    e.printStackTrace();
                }
            }

            if ( patient != null )
            {
                ConnectionManager.setUrl( namebasedMidlet.getCurrentOrgUnit().getUploadProgramStageUrl() + "/"
                    + patient.getId() );
            }
            else
            {
                ConnectionManager.setUrl( namebasedMidlet.getCurrentOrgUnit().getUploadProgramStageUrl() + "/0" );
            }

            if ( programStage.isSingleEvent() && patient == null )
            {
                ConnectionManager
                    .setUrl( namebasedMidlet.getCurrentOrgUnit().getUploadSingleEventWithoutRegistration() );
                ConnectionManager.uploadSingleEventWithoutRegistration( uploadProgramStage );
                // ConnectionManager.uploadProgramStage( programStage, null,
                // null, uploadProgramStage );
            }
            else
            {
                ConnectionManager.uploadProgramStage( programStage, section, patient, uploadProgramStage );
            }
            dataElementsMappingTable = null;
            title = null;
            mainForm.removeAll();
            mainForm = null;
            backCommand = null;
            sendCommand = null;
            patient = null;
            uploadProgramStage = null;
            programStage = null;
            section = null;
            System.gc();
        }
    }

    public void collectInputData()
    {
        uploadProgramStage = new ProgramStage();

        uploadProgramStage.setReportDate( reportDateField.getText() );
        programStage.setReportDate( reportDateField.getText() );

        uploadProgramStage.setId( programStage.getId() );
        uploadProgramStage.setName( programStage.getName() );
        uploadProgramStage.setCompleted( completeStatusCheckBox.isSelected() );
        uploadProgramStage.setRepeatable( programStage.isRepeatable() );
        uploadProgramStage.setStandardInterval( programStage.getStandardInterval() );
        uploadProgramStage.setSingleEvent( programStage.isSingleEvent() );
        uploadProgramStage.setDataElements( programStage.getDataElements() );
        uploadProgramStage.setSections( programStage.getSections() );

        Enumeration dataElemetKeys = dataElementsMappingTable.keys();
        Vector programStageDataElements = programStage.getDataElements();
        Vector finalProgramStageDataElements = new Vector();
        ProgramStageDataElement de;
        // ProgramStageDataElement programStageDataElement;
        Component component;
        String value = "";
        while ( dataElemetKeys.hasMoreElements() )
        {
            de = (ProgramStageDataElement) dataElemetKeys.nextElement();
            component = (Component) dataElementsMappingTable.get( de );
            if ( component.getName().equals( "bool" ) || component.getName().equals( "option" ) )
            {
                ComboBox comboBox = (ComboBox) component;
                if ( comboBox.getSelectedIndex() != 0 )
                {
                    value = (String) comboBox.getSelectedItem();
                }
            }
            else if ( component.getName().equals( "text" ) )
            {
                value = (String) ((TextField) component).getText().trim();
            }

            // Update ProgramStage's Data Elements
            for ( int i = 0; i < programStageDataElements.size(); i++ )
            {
                // programStageDataElement = (ProgramStageDataElement)
                // programStageDataElements.elementAt( i );
                if ( de.equals( (ProgramStageDataElement) programStageDataElements.elementAt( i ) ) )
                {
                    ((ProgramStageDataElement) programStageDataElements.elementAt( i )).setValue( value );
                    finalProgramStageDataElements.addElement( (ProgramStageDataElement) programStageDataElements
                        .elementAt( i ) );
                    break;
                }
            }
            de = null;
            value = "";
            System.gc();
        }
        uploadProgramStage.setDataElements( finalProgramStageDataElements );
        programStage.setDataElements( programStageDataElements );

        finalProgramStageDataElements = null;
        programStageDataElements = null;
        // dataElemetKeys = null;
        // de = null;
        // programStageDataElement = null;
        component = null;
        value = null;
        System.gc();
    }

    public Component generateDataElementLabel( Style labelStyle, ProgramStageDataElement de )
    {
        Label lblDataElement = new Label();
        if ( de.isCompulsory() && de.getType().equals( "date" ) )
        {
            lblDataElement.setText( de.getName() + "* DD-MM-YYYY" );
        }
        else if ( de.isCompulsory() && de.getType() != "date" )
        {
            lblDataElement.setText( de.getName() + "*" );
        }
        else if ( !de.isCompulsory() && de.getType().equals( "date" ) )
        {
            lblDataElement.setText( de.getName() + " DD-MM-YYYY" );
        }

        else
        {
            lblDataElement.setText( de.getName() );
        }
        lblDataElement.setName( de.getId() + "" );
        lblDataElement.setUnselectedStyle( labelStyle );
        lblDataElement.setSelectedStyle( labelStyle );

        return lblDataElement;
    }

    public void checkIfViolated( ProgramStageDataElement de )
    {
        if ( de.getViolatedMessage() != null )
        {
            Label lblError = new Label();
            lblError.getSelectedStyle().setFgColor( Text.ERROR_TEXT_COLOR() );
            lblError.getUnselectedStyle().setFgColor( Text.ERROR_TEXT_COLOR() );

            if ( de.getViolatedMessage().equalsIgnoreCase( "is_required" ) )
            {
                lblError.setText( "Required Field" );
            }
            else if ( de.getViolatedMessage().equalsIgnoreCase( "is_invalid_date" ) )
            {
                // lblError.setText( "YYYY-mm-dd" );
                // change to support standard date format
                lblError.setText( "dd-mm-yyyy" );
            }
            else if ( de.getViolatedMessage().equalsIgnoreCase( "is_invalid_number" ) )
            {
                lblError.setText( "Pls Enter A Number" );
            }

            mainForm.addComponent( lblError );
            de.setViolatedMessage( null );
        }
    }

    public void showView()
    {
        this.prepareView();
        this.getMainForm().show();
    }

    public void actionPerformed( ActionEvent arg )
    {
        if ( arg.getCommand().getCommandName().equals( Text.BACK() ) )
        {
            dataElementsMappingTable = null;
            title = null;
            mainForm.removeAll();
            mainForm = null;
            backCommand = null;
            uploadProgramStage = null;
            System.gc();

            if ( section != null )
            {
                section = null;
                namebasedMidlet.getSectionListView().setPatient( patient );
                namebasedMidlet.getSectionListView().showView();
            }
            else
            {
                if ( programStage.isSingleEvent() )
                {
                    programStage = null;
                    namebasedMidlet.getTrackingMainMenuView().showView();
                }
                else
                {
                    programStage = null;
                    namebasedMidlet.getPersonDashboardView().setPatient( patient );
                    namebasedMidlet.getPersonDashboardView().showView();
                }
            }
        }
        else if ( arg.getCommand().getCommandName().equals( Text.SAVE() ) )
        {
            namebasedMidlet.getWaitingView().showView();
            collectInputData();
            if ( patient.getId() == 0 )
            {
                try
                {
                    OfflinePatientRecordStore.saveOfflinePatient( patient );
                }
                catch ( Exception e )
                {
                    e.printStackTrace();
                }
            }
        }
        else if ( arg.getCommand().getCommandName().equals( Text.SEND() ) )
        {
            namebasedMidlet.getWaitingView().showView();
            if ( patient == null || patient.getId() == 0 )
            {
                collectInputData();
                uploadInputData();
            }
            else
            {
                collectInputData();
                if ( completeStatusCheckBox.isSelected() )
                {
                    uploadInputData();
                }
                else
                {
                    dialog = new Dialog();
                    dialog.setTitle( "Confirmation" );
                    Button yButton = new Button( Text.YES() );
                    Button nButton = new Button( Text.NO() );
                    // Label label = new Label(
                    // "This stage is not completed yet. Proceed anyway?" );
                    TextArea textArea = new TextArea( "This stage is not completed yet. Proceed anyway?" );

                    yButton.addActionListener( new ActionListener()
                    {

                        public void actionPerformed( ActionEvent ae )
                        {
                            uploadInputData();
                        }
                    } );

                    nButton.addActionListener( new ActionListener()
                    {

                        public void actionPerformed( ActionEvent ae )
                        {
                            dialog = (Dialog) Display.getInstance().getCurrent();
                            dialog.dispose();
                            dialog = null;
                            System.gc();

                        }
                    } );
                    textArea.setFocusable( false );
                    dialog.addComponent( textArea );
                    dialog.addComponent( yButton );
                    dialog.addComponent( nButton );

                    dialog.show();
                }
            }

        }
    }

    public Component getComponentByType( ProgramStageDataElement de )
    {
        if ( de.getOptionSet() == null )
        {
            if ( de.getType().equals( "bool" ) )
            {
                String[] content = { "Please Select", "true", "false" };
                ComboBox comboBox = new ComboBox( content );
                comboBox.setName( "bool" );
                if ( de.getValue() != null )
                {
                    if ( de.getValue().equalsIgnoreCase( "true" ) )
                    {
                        comboBox.setSelectedIndex( 1 );
                    }
                    else if ( de.getValue().equalsIgnoreCase( "false" ) )
                    {
                        comboBox.setSelectedIndex( 2 );
                    }
                }
                dataElementsMappingTable.put( de, comboBox );

                return comboBox;
            }
            else
            {
                TextField textField = new TextField();
                textField.setName( "text" );
                textField.setText( de.getValue() != null ? de.getValue() : "" );

                if ( de.getType().equals( ProgramStageDataElement.VALUE_TYPE_INT ) ||  de.getType().equals( ProgramStageDataElement.VALUE_TYPE_NUMBER ))
                {
                    textField.setConstraint( TextField.NUMERIC );
                    textField.setInputModeOrder( new String[] { "123" } );
                }
                else if ( namebasedMidlet.getCurrentOrgUnit().getUploadProgramStageUrl().indexOf( "wemr.ug" ) != -1
                    && de.getType().equals( ProgramStageDataElement.VALUE_TYPE_DATE ) )
                {
                    Calendar today = Calendar.getInstance();
                    if ( textField.getText().equals( "" ) )
                    {
                        int month = today.get( Calendar.MONTH ) + 1;
                        textField.setText( (today.get( Calendar.DATE ) < 10 ? "0" + today.get( Calendar.DATE ) : ""
                            + today.get( Calendar.DATE ))
                            + "-" + (month < 10 ? "0" + month : "" + month) + "-" + today.get( Calendar.YEAR ) );
                    }
                }
                dataElementsMappingTable.put( de, textField );

                return textField;
            }
        }
        else
        {
            ComboBox comboBox = new ComboBox();
            comboBox.setName( "option" );
            comboBox.addItem( "Option" );
            for ( int i = 0; i < de.getOptionSet().getOptions().size(); i++ )
            {
                String option = (String) de.getOptionSet().getOptions().elementAt( i );
                comboBox.addItem( option );
                if ( de.getValue() != null )
                {
                    if ( de.getValue().equalsIgnoreCase( option ) )
                    {
                        comboBox.setSelectedItem( option );
                    }
                }
            }
            dataElementsMappingTable.put( de, comboBox );

            return comboBox;
        }
    }

    public Form getMainForm()
    {
        if ( mainForm == null )
        {
            mainForm = new Form();
            mainForm.setLayout( new BoxLayout( BoxLayout.Y_AXIS ) );
            mainForm.setScrollableY( true );
            mainForm.setTransitionOutAnimator( CommonTransitions.createSlide( CommonTransitions.SLIDE_HORIZONTAL, true,
                200 ) );
            mainForm.addCommand( this.getBackCommand() );
            if ( !programStage.isCompleted() )
            {
                mainForm.addCommand( this.getSendCommand() );
                // mainForm.addCommand( this.getSaveCommand() );
            }
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

    public Command getSendCommand()
    {
        if ( sendCommand == null )
        {
            sendCommand = new Command( Text.SEND() );
        }
        return sendCommand;
    }

    public void setSendCommand( Command sendCommand )
    {
        this.sendCommand = sendCommand;
    }

    public Command getSaveCommand()
    {
        if ( saveCommand == null )
        {
            saveCommand = new Command( Text.SAVE() );
        }
        return saveCommand;
    }

    public void setSaveCommand( Command saveCommand )
    {
        this.saveCommand = saveCommand;
    }

    public Patient getPatient()
    {
        return patient;
    }

    public void setPatient( Patient patient )
    {
        this.patient = patient;
    }

    public ProgramStage getProgramStage()
    {
        return programStage;
    }

    public void setProgramStage( ProgramStage programStage )
    {
        this.programStage = programStage;
    }

    public Section getSection()
    {
        return section;
    }

    public void setSection( Section section )
    {
        this.section = section;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle( String title )
    {
        this.title = title;
    }

    public Hashtable getDataElementsMappingTable()
    {
        if ( dataElementsMappingTable == null )
        {
            dataElementsMappingTable = new Hashtable();
        }
        return dataElementsMappingTable;
    }

    public void setDataElementsMappingTable( Hashtable dataElementsMappingTable )
    {
        this.dataElementsMappingTable = dataElementsMappingTable;
    }
}
