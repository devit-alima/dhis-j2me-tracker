package org.hisp.dhis.mobile.view;

import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.Patient;
import org.hisp.dhis.mobile.model.PatientAttribute;
import org.hisp.dhis.mobile.model.Program;
import org.hisp.dhis.mobile.model.ProgramInstance;
import org.hisp.dhis.mobile.recordstore.PatientAttributeRecordStore;
import org.hisp.dhis.mobile.recordstore.PatientIdentifierRecordStore;
import org.hisp.dhis.mobile.recordstore.ProgramRecordStore;
import org.hisp.dhis.mobile.recordstore.RelativeRelationshipRecordStore;
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

public class AddRelativeView
    extends AbstractView
    implements ActionListener
{
    private static final String CLASS_TAG = "AddRelativeView";

    public static final String BLANK = "";

    private Patient patient;

    private Patient relative;

    private int relationshipType;

    private Form mainForm;

    private Label patientAttributeLabel;

    private Label requiredLabel;

    private TextField patientAttributeTextField;

    private TextField txtIncidentDate;

    private Command personRegistrationBackCommand;

    private Command personRegistrationSendCommand;

    private NameBasedMIDlet nameBasedMIDlet;

    private Vector patientAttributeValueVector = new Vector();

    private Hashtable warningLabels = new Hashtable();

    private Vector requireAttributeVector = new Vector();

    private boolean isValid = true;

    private Vector patientAttributeVector;

    private Program program;

    public AddRelativeView( DHISMIDlet dhisMIDlet )
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

                if ( patientAttribute.getType().equals( "bool" ) )
                {
                    String[] option = { "Option", "Yes", "No" };

                    ComboBox comboBox = new ComboBox( option );
                    comboBox.setLabelForComponent( patientAttributeLabel );

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

                        comboBox.addItem( (String) patientAttribute.getOptionSet().getOptions().elementAt( j ) );
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
                    comboBox.setLabelForComponent( patientAttributeLabel );

                    mainForm.addComponent( comboBox );
                    patientAttributeValueVector.addElement( comboBox );
                }
                else if ( patientAttribute.getType().equals( "date" ) )
                {
                    patientAttributeTextField = new TextField();
                    patientAttributeTextField.setHint( Text.DATE_TYPE() );
                    patientAttributeTextField.setLabelForComponent( patientAttributeLabel );
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

                    mainForm.addComponent( patientAttributeTextField );
                    patientAttributeValueVector.addElement( patientAttributeTextField );
                }
            }
        }
        catch ( Exception e )
        {
            LogMan.log( "UI," + CLASS_TAG, e );
            e.printStackTrace();
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
    {
        if ( ae.getCommand().getCommandName().equals( Text.SEND() ) )
        {
            nameBasedMIDlet.getWaitingView().showView();
            patient = collectData();
            if ( isValid == false )
            {
                getMainForm().show();
                return;
            }
            ConnectionManager.setUrl( dhisMIDlet.getCurrentOrgUnit().getRegisterRelativeUrl() );
            ConnectionManager.registerRelative( patient, String.valueOf( program.getId() ), relative );
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

            nameBasedMIDlet.getPersonDashboardView().setPatient( relative );
            nameBasedMIDlet.getPersonDashboardView().showView();
            patientAttributeValueVector.removeAllElements();
            patientAttributeVector = null;
            patientAttributeLabel = null;
            requiredLabel = null;
            patientAttributeTextField = null;
            txtIncidentDate = null;
            personRegistrationBackCommand = null;
            personRegistrationSendCommand = null;
            patientAttributeValueVector = null;
            requireAttributeVector = null;
            System.gc();
        }

    }

    private Patient collectData()
    {
        isValid = true;
        String incidentDate = null;
        Patient patient = new Patient();
        Vector attributeVector = new Vector();

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

                if ( !value.equals( "" ) )
                {
                    PatientAttribute patientAttribute = new PatientAttribute();
                    patientAttribute.setName( name );
                    patientAttribute.setValue( value );
                    patientAttribute.setType( "" );
                    patientAttribute.setOptionSet( programAttribute.getOptionSet() );
                    patientAttribute.setDisplayedInList( programAttribute.isDisplayedInList() );

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
        patient.setIdToAddRelative( relative.getId() );
        patient.setRelTypeIdToAdd( relationshipType );

        ProgramInstance programInstance = new ProgramInstance();
        programInstance.setId( 0 );
        programInstance.setName( program.getName() );
        programInstance.setDateOfIncident( incidentDate );
        programInstance.setProgramId( program.getId() );
        programInstance.setPatientId( 0 );
        programInstance.setStatus( 0 );
        programInstance.setDateOfEnrollment( BLANK );

        patient.getEnrollmentPrograms().addElement( programInstance );
        return patient;
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

    public void setRelationshipType( int relationshipType )
    {
        this.relationshipType = relationshipType;
    }

}
