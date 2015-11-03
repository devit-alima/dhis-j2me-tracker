package org.hisp.dhis.mobile.view;

import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.Patient;
import org.hisp.dhis.mobile.model.PatientAttribute;
import org.hisp.dhis.mobile.model.Program;
import org.hisp.dhis.mobile.model.ProgramInstance;
import org.hisp.dhis.mobile.model.Relationship;
import org.hisp.dhis.mobile.recordstore.PatientAttributeRecordStore;
import org.hisp.dhis.mobile.recordstore.PatientIdentifierRecordStore;
import org.hisp.dhis.mobile.recordstore.ProgramRecordStore;
import org.hisp.dhis.mobile.ui.Text;
import org.hisp.dhis.mobile.util.PeriodUtil;

import com.sun.lwuit.ComboBox;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextField;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;

public class PersonRegistrationView
    extends AbstractView
    implements ActionListener
{
    private static final String CLASS_TAG = "PersonRegistrationView";

    public static final String BLANK = "";

    private Patient patient;

    private Patient relative;

    private Relationship enrollmentRelationship;

    private Form mainForm;

    private Label patientAttributeLabel;

    private Label requiredLabel;

    private TextField patientAttributeTextField;

    private TextField txtIncidentDate;

    private Command personRegistrationBackCommand;

    private Command personRegistrationSendCommand;

    private Command personRegistrationSaveCommand;

    private NameBasedMIDlet nameBasedMIDlet;

    private Vector patientAttributeValueVector = new Vector();

    private Hashtable warningLabels = new Hashtable();

    private Vector requireAttributeVector = new Vector();

    private boolean isValid = true;

    private Vector patientAttributeVector;

    private Program program;

    private boolean isEditMode = false;

    public PersonRegistrationView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        this.nameBasedMIDlet = (NameBasedMIDlet) this.dhisMIDlet;
    }

    public void prepareView()
    {
        System.gc();

        getMainForm();
        mainForm.removeAll();
        requiredLabel = null;
        txtIncidentDate = null;
        patientAttributeValueVector = new Vector();
        warningLabels = new Hashtable();
        requireAttributeVector = new Vector();

        isValid = true;
        System.gc();

        if ( program != null )
        {
            Label lblIncidentDate = new Label( program.getDateOfIncidentDescription() + " (*)" );
            mainForm.addComponent( lblIncidentDate );
            mainForm.addComponent( getTxtIncidentDate() );
            mainForm.addComponent( getRequiredLabel() );
        }

        // add attribute
        try
        {
            patientAttributeVector = program.getProgramAttributes();
            isEditMode = (patient != null);
            for ( int i = 0; i < patientAttributeVector.size(); i++ )
            {
                PatientAttribute patientAttribute = (PatientAttribute) patientAttributeVector.elementAt( i );
                requireAttributeVector.addElement( patientAttribute.getName().substring(
                    patientAttribute.getName().length() - 1 ) );

                // Add warning field
                Label warningLabel = this.getWarningLabel();
                mainForm.addComponent( warningLabel );
                warningLabels.put( patientAttribute.getName(), warningLabel );

                if ( patientAttribute.isMandatory() )
                {
                    patientAttributeLabel = new Label( patientAttribute.getName() + " (*)" );

                }
                else
                {
                    patientAttributeLabel = new Label( patientAttribute.getName() );
                }
                mainForm.addComponent( patientAttributeLabel );

                String patientAttributeValue = getPatientAttributeValue( patientAttribute.getName() );

				if ( patientAttribute.getOptionSet() != null ) {
					ComboBox comboBox = new ComboBox();
                    comboBox.setLabelForComponent( patientAttributeLabel );
                    comboBox.addItem( "Option" );

                    for ( int j = 0; j < patientAttribute.getOptionSet().getOptions().size(); j++ )
                    {
                        String option = (String) patientAttribute.getOptionSet().getOptions().elementAt( j );
                        comboBox.addItem( option );
                        if ( patientAttributeValue != null && patientAttributeValue.equalsIgnoreCase( option ) )
                        {
                            comboBox.setSelectedIndex( j + 1 );
                        }
                    }

                    mainForm.addComponent( comboBox );
                    patientAttributeValueVector.addElement( comboBox );

				} else {
					if ( patientAttribute.getType().equals( "bool" ) )
                {
                    String[] option = { "Option", "Yes", "No" };

                    ComboBox comboBox = new ComboBox( option );
                    comboBox.setLabelForComponent( patientAttributeLabel );

                    if ( patientAttributeValue != null && !patientAttributeValue.equals( "" ) )
                    {
                        if ( patientAttributeValue.equalsIgnoreCase( "Yes" ) )
                        {
                            comboBox.setSelectedIndex( 1 );
                        }
                        else if ( patientAttributeValue.equalsIgnoreCase( "No" ) )
                        {
                            comboBox.setSelectedIndex( 2 );
                        }
                    }

                    mainForm.addComponent( comboBox );
                    patientAttributeValueVector.addElement( comboBox );
                }

                else if ( patientAttribute.getType().equals( "optionSet" ) )
                {
                    ComboBox comboBox = new ComboBox();
                    comboBox.setLabelForComponent( patientAttributeLabel );
                    comboBox.addItem( "Option" );

                    for ( int j = 0; j < patientAttribute.getOptionSet().getOptions().size(); j++ )
                    {
                        String option = (String) patientAttribute.getOptionSet().getOptions().elementAt( j );
                        comboBox.addItem( option );
                        if ( patientAttributeValue != null && patientAttributeValue.equalsIgnoreCase( option ) )
                        {
                            comboBox.setSelectedIndex( j + 1 );
                        }
                    }

                    mainForm.addComponent( comboBox );
                    patientAttributeValueVector.addElement( comboBox );

                }

                else if ( patientAttribute.getType().equals( "combo" ) )
                {
                    LogMan.log( LogMan.DEBUG, "UI," + CLASS_TAG, "adding combo box" );

                    Vector optionSet = patientAttribute.getOptionSet().getOptions();
                    String[] options = new String[optionSet.size()];
                    LogMan.log( LogMan.DEBUG, "UI," + CLASS_TAG, "options size=" + optionSet.size() );
                    optionSet.copyInto( options );

                    ComboBox comboBox = new ComboBox( options );
                    if ( patientAttributeValue != null && !patientAttributeValue.equals( "" ) )
                    {
                        for ( int j = 0; j < options.length; j++ )
                        {
                            if ( options[j].equalsIgnoreCase( patientAttributeValue ) )
                            {
                                comboBox.setSelectedIndex( j );
                                break;
                            }
                        }
                    }
                    comboBox.setLabelForComponent( patientAttributeLabel );

                    mainForm.addComponent( comboBox );
                    patientAttributeValueVector.addElement( comboBox );
                }
                else if ( patientAttribute.getType().equals( "date" ) )
                {
                    patientAttributeTextField = new TextField();
                    patientAttributeTextField.setHint( Text.DATE_TYPE() );
                    patientAttributeTextField.setLabelForComponent( patientAttributeLabel );
                    if ( patientAttributeValue != null && !patientAttributeValue.equals( "" ) )
                    {
                        patientAttributeTextField.setText( patientAttributeValue );
                    }
                    mainForm.addComponent( patientAttributeTextField );
                    patientAttributeValueVector.addElement( patientAttributeTextField );
                }
                else
                {
                    patientAttributeTextField = new TextField();
                    patientAttributeTextField.setLabelForComponent( patientAttributeLabel );

                    if ( patientAttribute.getType().equals( "number" )
                        || patientAttribute.getType().equals( "phoneNumber" ) )
                    {
                        patientAttributeTextField.setConstraint( TextField.NUMERIC );
                        patientAttributeTextField.setInputModeOrder( new String[] { "123" } );
                        patientAttributeTextField.setHint( "Please enter number" );
                    }
                    if ( patientAttributeValue != null && !patientAttributeValue.equals( "" ) )
                    {
                        patientAttributeTextField.setText( patientAttributeValue );
                    }

                    mainForm.addComponent( patientAttributeTextField );
                    patientAttributeValueVector.addElement( patientAttributeTextField );
                }
				}
                
            }
        }
        catch ( Exception e )
        {
            LogMan.log( "UI," + CLASS_TAG, e );
            e.printStackTrace();
        }
    }

    public String getPatientAttributeValue( String patientAttributeName )
    {
        if ( patient == null )
        {
            return null;
        }
        else
        {
            Vector patientAttributes = patient.getPatientAttValues();
            for ( int i = 0; i < patientAttributes.size(); i++ )
            {
                PatientAttribute patientAttribute = (PatientAttribute) patientAttributes.elementAt( i );
                if ( patientAttribute.getName().equals( patientAttributeName ) )
                {
                    return patientAttribute.getValue();
                }
            }
            return null;
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
            mainForm = new Form( program.getTrackedEntityName().toUpperCase() + Text.REGISTRATION() );
            mainForm.setLayout( new BoxLayout( BoxLayout.Y_AXIS ) );
            mainForm.setTransitionOutAnimator( CommonTransitions.createSlide( CommonTransitions.SLIDE_HORIZONTAL, true,
                200 ) );
            mainForm.addCommand( getPersonRegistrationBackCommand() );
            mainForm.addCommand( getPeronRegistrationSendCommand() );
            // mainForm.addCommand( getPersonRegistrationSaveCommand() );

            mainForm.addCommandListener( this );
        }
        else
        {
            mainForm.setTitle( program.getTrackedEntityName().toUpperCase() + Text.REGISTRATION() );
        }

        return mainForm;
    }

    public void setMainForm( Form personRegistrationForm )
    {
        this.mainForm = personRegistrationForm;
    }

    public void actionPerformed( ActionEvent ae )
        throws ClassCastException
    {
        if ( ae.getCommand().getCommandName().equals( Text.SEND() ) )
        {
            nameBasedMIDlet.getWaitingView().showView();
            patient = collectData();
            if ( enrollmentRelationship != null )
            {
                patient.setEnrollmentRelationship( enrollmentRelationship );
            }

            if ( isValid == false )
            {
                getMainForm().show();
                return;
            }

            ConnectionManager.setUrl( dhisMIDlet.getCurrentOrgUnit().getRegisterPersonUrl() );
            ConnectionManager.registerPerson( patient, String.valueOf( program.getId() ) );
            try
            {
                PatientIdentifierRecordStore.deleteRecordStore();
                PatientAttributeRecordStore.deleteRecordStore();
            }
            catch ( Exception e )
            {
                LogMan.log( "UI," + CLASS_TAG, e );
                e.printStackTrace();
            }

            patientAttributeValueVector.removeAllElements();
            requiredLabel = null;
            patientAttributeVector = null;
            txtIncidentDate = null;
            relative = null;
            enrollmentRelationship = null;
            patient = null;
            program = null;
            System.gc();

        }
        else if ( ae.getCommand().getCommandName().equals( Text.SAVE() ) )
        {
            nameBasedMIDlet.getWaitingView().showView();
            patient = collectData();
            this.createProgramStageInstance( patient );
            if ( enrollmentRelationship != null )
            {
                patient.setEnrollmentRelationship( enrollmentRelationship );
            }

            if ( isValid == false )
            {
                getMainForm().show();
                return;
            }

            ConnectionManager.saveUnregisterdPatient( patient );
            try
            {
                PatientIdentifierRecordStore.deleteRecordStore();
                PatientAttributeRecordStore.deleteRecordStore();
            }
            catch ( Exception e )
            {
                LogMan.log( "UI," + CLASS_TAG, e );
                e.printStackTrace();
            }

            patientAttributeValueVector.removeAllElements();
            requiredLabel = null;
            patientAttributeVector = null;
            txtIncidentDate = null;
            relative = null;
            enrollmentRelationship = null;
            patient = null;
            program = null;
            System.gc();

        }
        else
        {
            try
            {
                PatientAttributeRecordStore.deleteRecordStore();
                PatientIdentifierRecordStore.deleteRecordStore();
            }
            catch ( Exception e )
            {
                LogMan.log( "UI," + CLASS_TAG, e );
                e.printStackTrace();
            }

            nameBasedMIDlet.getProgramSelectView().setTargetScreen( nameBasedMIDlet.PERSON_REGISTRATION_VIEW );
            nameBasedMIDlet.getProgramSelectView().getMainForm().show();
            patientAttributeValueVector.removeAllElements();
            patientAttributeVector = null;
            patientAttributeLabel = null;
            requiredLabel = null;
            patientAttributeTextField = null;
            txtIncidentDate = null;
            personRegistrationBackCommand = null;
            personRegistrationSendCommand = null;
            personRegistrationSaveCommand = null;
            patientAttributeValueVector = null;
            requireAttributeVector = null;
            relative = null;
            enrollmentRelationship = null;
            patient = null;
            program = null;
            System.gc();
        }
    }

    private void createProgramStageInstance( Patient patient )
    {
        ProgramInstance programInstance = (ProgramInstance) patient.getEnrollmentPrograms().elementAt( 0 );
        try
        {
            Program program = ProgramRecordStore.getProgram( programInstance.getProgramId() );
            Vector programStages = program.getProgramStages();

            // get program stage as instance for offline patient;
            programInstance.setProgramStageInstances( programStages );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

    }

    private Patient collectData()
    {
        isValid = true;
        String incidentDate = null;
        Vector attributeVector;
        boolean isEdit;

        if ( patient == null )
        {
            patient = new Patient();
            attributeVector = new Vector();
            isEdit = false;
        }
        else
        {
            attributeVector = patient.getPatientAttValues();
            isEdit = true;
        }

        if ( program != null )
        {
            incidentDate = getTxtIncidentDate().getText();
            validateIncidentDate( incidentDate );
        }

        try
        {
            for ( int i = 0; i < patientAttributeValueVector.size(); i++ )
            {
                PatientAttribute programAttribute = (PatientAttribute) patientAttributeVector.elementAt( i );
                String value = null;
                Component field = (Component) patientAttributeValueVector.elementAt( i );

                if ( patientAttributeValueVector.elementAt( i ) instanceof TextField )
                {
                    TextField textField = (TextField) patientAttributeValueVector.elementAt( i );
                    value = textField.getText().trim();
                    String errorMessage = this.validateAttributeValue( value, programAttribute );
                    Label warningLabel = (Label) warningLabels.get( programAttribute.getName() );
                    if ( errorMessage != null )
                    {
                        warningLabel.setText( errorMessage );
                        this.isValid = false;
                    }
                    else
                    {
                        warningLabel.setText( null );
                    }
                }
                else
                {
                    ComboBox comboBox = (ComboBox) patientAttributeValueVector.elementAt( i );
                    value = (String) comboBox.getSelectedItem();
                    if ( programAttribute.getType().equals( "bool" ) || programAttribute.getType().equals( "trueOnly" ) )
                    {
                        if ( value.equalsIgnoreCase( "Yes" ) )
                        {
                            value = "true";
                        }
                        else if ( value.equalsIgnoreCase( "No" ) )
                        {
                            value = "false";
                        }
                        else
                        {
                            value = "";
                        }
                    }
                    else if ( programAttribute.getType().equals( "optionSet" ) && comboBox.getSelectedIndex() == 0 )
                    {
                        value = "";
                        Label warningLabel = (Label) warningLabels.get( programAttribute.getName() );
                        if ( programAttribute.isMandatory() )
                        {
                            warningLabel.setText( "This field is required" );
                            this.isValid = false;
                        }
                    }
                }
                String name = "";
                name = field.getLabelForComponent().getText().toString().trim();
                if ( name.endsWith( " (*)" ) )
                {
                    name = name.substring( 0, name.length() - 4 );
                }
                PatientAttribute patientAttribute = new PatientAttribute();
                patientAttribute.setName( name );
                patientAttribute.setValue( value );
                patientAttribute.setType( "" );
                patientAttribute.setOptionSet( programAttribute.getOptionSet() );
                patientAttribute.setDisplayedInList( programAttribute.isDisplayedInList() );
                if ( isEdit )
                {
                    removeAttribute( attributeVector, patientAttribute );
                }
                if ( !value.equals( "" ) )
                {
                    attributeVector.addElement( patientAttribute );
                }
            }
        }
        catch ( Exception e )
        {
            LogMan.log( "UI," + CLASS_TAG, e );
            e.printStackTrace();
        }

        patient.setPatientAttValues( attributeVector );
        patient.setOrganisationUnitName( dhisMIDlet.getCurrentOrgUnit().getName() );
        patient.setTrackedEntityName( program.getTrackedEntityName() );

        if ( isValid )
        {
            ProgramInstance programInstance = new ProgramInstance();
            programInstance.setId( 0 );
            programInstance.setName( program.getName() );
            programInstance.setDateOfIncident( incidentDate );
            programInstance.setProgramId( program.getId() );
            programInstance.setPatientId( patient.getId() );
            programInstance.setStatus( 0 );
            programInstance.setDateOfEnrollment( BLANK );

            patient.getEnrollmentPrograms().addElement( programInstance );
        }

        return patient;
    }

    public void removeAttribute( Vector attributeVector, PatientAttribute patientAttribute )
    {
        for ( int i = 0; i < attributeVector.size(); i++ )
        {
            if ( ((PatientAttribute) attributeVector.elementAt( i )).getName().equals( patientAttribute.getName() ) )
            {
                attributeVector.removeElementAt( i );
                break;
            }
        }
    }

    private String validateAttributeValue( String value, PatientAttribute programAttribute )
    {
        if ( programAttribute.isMandatory() && value.trim().equals( "" ) )
        {
            return "This field is required";
        }

        if ( !value.trim().equals( "" ) )
        {
            if ( programAttribute.getType().equals( "date" ) )
            {
                if ( !PeriodUtil.isDateValid( value ) )
                {
                    return "Incorrect date format";
                }
            }
        }

        return null;
    }

    // -------------------------------------------------------------------------
    // Support Methods
    // -------------------------------------------------------------------------

    public void validateIncidentDate( String incidentDate )
    {
        if ( program != null && incidentDate.trim().equals( "" ) )
        {
            getRequiredLabel().setText( "(*): Required Field" );
            isValid = false;
        }
        else if ( program != null && !PeriodUtil.isDateValid( incidentDate ) )
        {
            getRequiredLabel().setText( Text.DATE_TYPE() );
            isValid = false;
        }
        else
        {
            getRequiredLabel().setText( "" );
        }
    }

    // -------------------------------------------------------------------------
    // Getters/Setters
    // -------------------------------------------------------------------------

    public Label getWarningLabel()
    {
        Label label = new Label();
        label.getStyle().setFgColor( Text.ERROR_TEXT_COLOR() );
        return label;
    }

    public Label getRequiredLabel()
    {
        if ( requiredLabel == null )
        {
            requiredLabel = new Label();
            requiredLabel.getStyle().setFgColor( Text.ERROR_TEXT_COLOR() );
        }
        return requiredLabel;
    }

    public TextField getTxtIncidentDate()
    {
        if ( txtIncidentDate == null )
        {
            txtIncidentDate = new TextField();
            txtIncidentDate.setText( PeriodUtil.dateToString( new Date() ) );
        }
        return txtIncidentDate;
    }

    public Command getPersonRegistrationBackCommand()
    {
        if ( personRegistrationBackCommand == null )
        {
            personRegistrationBackCommand = new Command( Text.BACK() );
        }
        return personRegistrationBackCommand;
    }

    public Command getPeronRegistrationSendCommand()
    {
        if ( personRegistrationSendCommand == null )
        {
            personRegistrationSendCommand = new Command( Text.SEND() );
        }
        return personRegistrationSendCommand;
    }

    public Command getPersonRegistrationSaveCommand()
    {
        if ( personRegistrationSaveCommand == null )
        {
            personRegistrationSaveCommand = new Command( Text.SAVE() );
        }
        return personRegistrationSaveCommand;
    }

    public Label getPatientAttributeLabel()
    {
        return patientAttributeLabel;
    }

    public TextField getPatientAttributeTextField()
    {
        if ( patientAttributeTextField == null )
        {
            patientAttributeTextField = new TextField();
        }
        return patientAttributeTextField;
    }

    public Program getProgram()
    {
        return program;
    }

    public void setProgram( Program program )
    {
        this.program = program;
    }

    public void setRelative( Patient relative )
    {
        this.relative = relative;
    }

    public void setEnrollmentRelationship( Relationship enrollmentRelationship )
    {
        this.enrollmentRelationship = enrollmentRelationship;
    }

    public void setPatient( Patient patient )
    {
        this.patient = patient;
    }

}