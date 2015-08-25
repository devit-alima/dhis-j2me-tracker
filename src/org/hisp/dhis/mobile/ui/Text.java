package org.hisp.dhis.mobile.ui;

/*
 * Copyright (c) 2004-2010, University of Oslo All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met: * Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer. * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or other materials provided with the
 * distribution. * Neither the name of the HISP project nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.hisp.dhis.mobile.model.TextMapping;

/**
 * Class containing all texts used in the user interface (Pattern borrowed from
 * openXdata).
 * <p>
 * Allows for other languages to be supported by resolving the short assigned to
 * every string against a supplied {@link TextMapping}
 */
public class Text
{

    private static TextMapping textMapping;

    public static void setTextMapping( TextMapping textMapping )
    {
        Text.textMapping = textMapping;
    }

    /**
     * Method for resolving alternative representations of the string
     * 
     * @param key - the id assigned to the string
     * @param english - default string defined in this class
     * @return the string resolved from the <code>key</code> in the currently
     *         loaded text mapping, if none, return <code>english</code>
     */
    private static String get( short key, String english )
    {
        if ( textMapping == null )
            return english;

        String string = (String) textMapping.get( new Short( key ) );

        if ( string == null )
            return english;

        return string;
    }

    public static String FACILITY_REPORTING()
    {
        return get( (short) 1, "Facility reporting" );
    }

    public static String ACTIVITY_REPORTING()
    {
        return get( (short) 2, "Activity reporting" );
    }

    public static String SERVICES()
    {
        return get( (short) 3, "Services" );
    }

    public static String INCOMPLETE_FORM()
    {
        return get( (short) 4, "Incomplete form" );
    }

    public static String U_P_MISSING()
    {
        return get( (short) 5, "Username or password missing" );
    }

    public static String STATUS()
    {
        return get( (short) 6, "Status" );
    }

    public static String REINIT()
    {
        return get( (short) 7, "Re-initialize" );
    }

    public static String REINIT_MESSAGE()
    {
        return get( (short) 8, "Forms stored on the phone will be lost. Are you sure you want to re-initialize?" );
    }

    public static String DOWNLOADING()
    {
        return get( (short) 9, "Dowloading..." );
    }

    public static String ERROR()
    {
        return get( (short) 10, "Error" );
    }

    public static String EMPTY_PIN()
    {
        return get( (short) 11, "PIN must has 4 digits" );
    }

    public static String INVALID_PIN()
    {
        return get( (short) 12, "Invalid PIN" );
    }

    public static String SETTINGS()
    {
        return get( (short) 13, "Settings" );
    }

    public static String CURRENT_ACTIVITY_PLAN()
    {
        return get( (short) 14, "Current activity plan" );
    }

    public static String COMPLETED_ACTIVITIES()
    {
        return get( (short) 15, "Sent activities" );
    }

    public static String UPDATE_ACT_PLAN()
    {
        return get( (short) 16, "Update avtivity plan" );
    }

    public static String PLEASE_WAIT()
    {
        return get( (short) 17, "Please wait..." );
    }

    public static String UPDATING()
    {
        return get( (short) 18, "Updating" );
    }

    public static String LOADING_ACTIVITIES()
    {
        return get( (short) 19, "Loading activities" );
    }

    public static String EXIT()
    {
        return get( (short) 20, "Exit" );
    }

    public static String DOWNLOAD()
    {
        return get( (short) 21, "Download" );
    }

    public static String BACK()
    {
        return get( (short) 22, "Back" );
    }

    public static String CONFIGURABLE_PARAMS()
    {
        return get( (short) 23, "Configurable parameters" );
    }

    public static String SAVE()
    {
        return get( (short) 24, "Save" );
    }

    public static String URL()
    {
        return get( (short) 25, "Server location" );
    }

    public static String MAIN_MENU()
    {
        return get( (short) 26, "Main menu" );
    }

    public static String FORM()
    {
        return get( (short) 27, "Form" );
    }

    public static String DETAILS()
    {
        return get( (short) 28, "Details" );
    }

    public static String OK()
    {
        return get( (short) 29, "OK" );
    }

    public static String SEND()
    {
        return get( (short) 30, "Send" );
    }

    public static String SENDSMS()
    {
        return get( (short) 30, "Send SMS" );
    }

    public static String PLEASE_LOGIN()
    {
        return get( (short) 32, "Please login" );
    }

    public static String ENTER_PIN()
    {
        return get( (short) 33, "Enter a 4 digit PIN" );
    }

    public static String PIN()
    {
        return get( (short) 34, "PIN" );
    }

    public static String NEXT()
    {
        return get( (short) 35, "Next" );
    }

    public static String USERNAME()
    {
        return get( (short) 36, "Username" );
    }

    public static String PASSWORD()
    {
        return get( (short) 37, "Password" );
    }

    public static String LOGIN()
    {
        return get( (short) 38, "Login" );
    }

    public static String SELECT_ACTIVITY()
    {
        return get( (short) 39, "Select activity" );
    }

    public static String SELECT_FORM()
    {
        return get( (short) 40, "Select report form" );
    }

    public static String NO()
    {
        return get( (short) 41, "No" );
    }

    public static String YES()
    {
        return get( (short) 42, "Yes" );
    }

    public static String SELECT_OPTION()
    {
        return get( (short) 43, "Select option" );
    }

    public static String FORM_UNAVAILABLE()
    {
        return get( (short) 44, "The requested form is not available" );
    }

    public static String SELECT_PERIOD()
    {
        return get( (short) 45, "Select period" );
    }

    public static String PERIOD()
    {
        return get( (short) 46, "Period" );
    }

    public static String ALERT()
    {
        return get( (short) 47, "Alert" );
    }

    public static String PLEASE_SELECT()
    {
        return get( (short) 48, "Please select" );
    }

    public static String DELETE()
    {
        return get( (short) 49, "Delete" );
    }

    public static String UPLOADING()
    {
        return get( (short) 50, "Uploading" );
    }

    public static String FIELD_NOT_FILLED()
    {
        return get( (short) 51, "fields are not filled. Do you want to send anyway?" );
    }

    public static String SELECT_DATE()
    {
        return get( (short) 52, "Select Date" );
    }

    public static String PREVIOUS()
    {
        return get( (short) 53, "Previous" );
    }

    public static String UPLOAD_FAIL()
    {
        return get( (short) 54, "Upload Failed" );
    }

    public static String SUCCESS()
    {
        return get( (short) 55, "Success" );
    }

    public static String WARNING()
    {
        return get( (short) 56, "Warning" );
    }

    public static String CREATE_PIN_SUGGESTION()
    {
        return get( (short) 57,
            "Please choose a secret PIN code with 4 digits. The next time you start the application, use this PIN to login." );
    }

    public static String CHECK_PIN_SUGGESTION()
    {
        return get( (short) 58,
            "If you have forgotten your PIN, please select Reinitialize to login with username and password again." );
    }

    public static String SERVER_NOT_FOUND()
    {
        return get( (short) 59, "Server not found" );
    }

    public static String SETTING_SAVED_MESSAGE()
    {
        return get( (short) 61, "Settings saved. If you change locale, please restart the application." );
    }

    public static String RESOURCE_FILE()
    {
        return get( (short) 62, "Resource file" );
    }

    public static String DOES_NOT_EXIST()
    {
        return get( (short) 63, "does not exist." );
    }

    public static String DATASET_LOCKED()
    {
        return get( (short) 64, "DataSet is locked. It is not possible to upload" );
    }

    public static String CLOSE()
    {
        return get( (short) 65, "Close" );
    }

    public static String REPORT_SENT()
    {
        return get( (short) 66, "Report sent sucessfully" );
    }

    public static String LOADING()
    {
        return get( (short) 67, "Loading" );
    }

    public static String COMPLETED()
    {
        return get( (short) 68, "Sent" );
    }

    public static String SAVED()
    {
        return get( (short) 69, "Saved" );
    }

    public static String DATAVALUE_SAVED()
    {
        return get( (short) 70, "Data saved to mobile phone" );
    }

    public static String SAVE_DATAVALUE_FAIL()
    {
        return get( (short) 71, "Failed to save data value" );
    }

    public static String DATAVALUE_LOAD_ERROR()
    {
        return get( (short) 72, "Error while loading values. Please try again." );
    }

    public static String UPDATE_DATASET()
    {
        return get( (short) 73, "Update data set" );
    }

    public static String EMPTY_DATASET()
    {
        return get( (short) 74, "Empty data set" );
    }

    public static String UPDATE_ACTIVITY_PLAN()
    {
        return get( (short) 75, "Update activity plan" );
    }

    public static String CHECKING_FOR_UPDATE()
    {
        return get( (short) 76, "Checking for updates" );
    }

    public static String UPDATE_FORMS()
    {
        return get( (short) 77, "Forms are out of date\nWould you like to refresh the form list?" );
    }

    public static String UPDATE_FAIL()
    {
        return get( (short) 78, "Update failed. Please try again later." );
    }

    public static String ORG_UNIT()
    {
        return get( (short) 79, "Org Unit: " );
    }

    public static String NO_ORGUNIT_ERROR()
    {
        return get( (short) 80,
            "You have not been assigned to any org unit. Please contact the administrator for more information." );
    }

    public static String NAME_LIST()
    {
        return get( (short) 81, "Beneficiary List" );
    }

    public static String PERSON_SEARCH()
    {
        return get( (short) 82, "Find Instance" );
    }

    public static String ENTER_KEY_WORD()
    {
        return get( (short) 83, "Enter Key Word: " );
    }

    public static String NO_ACTIVITY_FOUND()
    {
        return get( (short) 84, "No Activity Found!" );
    }

    public static String MESSAGE()
    {
        return get( (short) 85, "Message" );
    }

    public static String END_OF_FORM()
    {
        return get( (short) 86, "End of Form" );
    }

    public static String UPLOADED_SUCCESSFULLY()
    {
        return get( (short) 87, "Report uploaded sucessfully" );
    }

    public static String SEARCH_HINT()
    {
        return get( (short) 88, "Search for: *" );
    }

    public static String FIND()
    {
        return get( (short) 89, "Find" );
    }

    public static String ADD()
    {
        return get( (short) 90, "Add" );
    }

    public static String ADD_ACTIVITY_CONFIRM()
    {
        return get( (short) 91, "Do you want to add this activity into your activity plan?" );
    }

    public static String USE_SMS_CONFIRM()
    {
        return get( (short) 92, "Upload fail with GPRS. Do you want to try with SMS?" );
    }

    public static String SMS_REPORT_SENT()
    {
        return get( (short) 92, "SMS Report sent." );
    }

    public static String SERVER_PHONE_NUMBER()
    {
        return get( (short) 93, "Server Phone Number: " );
    }

    public static String NO_PERSON_FOUND()
    {
        return get( (short) 94, "No match found" );
    }

    public static String TOO_MANY_RESULT()
    {
        return get( (short) 95, "Too many results, please be more specific" );
    }

    public static String NOT_ALLOWED_SENT_REPORT()
    {
        return get( (short) 96, "Currently not allowed to send activity reports: " );
    }

    public static String RESPONSE_STATUS_NOT_OK()
    {
        return get( (short) 97, "Response status not OK: " );
    }

    public static String CONN_NOT_FOUND()
    {
        return get( (short) 98, "Connection not found" );
    }

    public static String INVALID_USER_PASS()
    {
        return get( (short) 99, "Invalid username or password" );
    }

    public static String TOO_MUCH_REDIRECT()
    {
        return get( (short) 100, "Too many redirects" );
    }

    public static String SERVER_ERROR()
    {
        return get( (short) 101, "Server encountered an unexpected problem" );
    }

    public static String NO_PHONE_NUMBER()
    {
        return get( (short) 102, "No server phone number found" );
    }

    public static String ADDED_FORM()
    {
        return get( (short) 103, "Added Form(s): " );
    }

    public static String REMOVED_FORM()
    {
        return get( (short) 104, "Removed Form(s): " );
    }

    public static String MODIFIED_FORM()
    {
        return get( (short) 105, "Modifided Form(s): " );
    }

    public static String PROBLEM_READING_RESPONSE()
    {
        return get( (short) 106, "Problem reading response, wrong server address?" );
    }

    public static String LANGUAGE_LOCALE()
    {
        return get( (short) 107, "Language Locale: " );
    }

    public static String SENT_DATA_PROBLEM()
    {
        return get( (short) 108, "Problem with sending data" );
    }

    public static String SERVER_COMMUNICATE_FAILED()
    {
        return get( (short) 109, "Communication with the server failed" );
    }

    public static String MESSAGE_TOO_LONG()
    {
        return get( (short) 110, "Unable to send SMS. The message is too long. Please send report in GPRS area" );
    }

    public static String ACTIVITY_ADDED_SUCCESS()
    {
        return get( (short) 111, "Activity successfully added" );
    }

    public static String ACTIVITY_ADDED_FAIL()
    {
        return get( (short) 112, "Cannot add activity to activity plan" );
    }

    public static String INVALID_PHONE_NUMBER()
    {
        return get( (short) 113, "Invalid Phone Number. Please check your setting!" );
    }

    public static String UPDATE_NEW_VERSION()
    {
        return get( (short) 114, "New Version Available" );
    }

    public static String UPDATE_NEW_NOTIFICATION()
    {
        return get( (short) 115, "Your version is out of date! Would you like to update new version?" );
    }

    public static String SELECT_ORGUNIT()
    {
        return get( (short) 116, "Select OrgUnit" );
    }

    public static String EXIT_CONFIRM()
    {
        return get( (short) 117, "Are you sure you want to exit?" );
    }

    public static String CLOSE_CONFIRM()
    {
        return get( (short) 118, "Are you sure you want to close the form?" );
    }

    public static String REGISTRATION()
    {
        return get( (short) 119, " REGISTRATION" );
    }

    public static String FULL_NAME()
    {
        return get( (short) 120, "Full Name" );
    }

    public static String GENDER()
    {
        return get( (short) 121, "Gender" );
    }

    public static String DATE_OF_BIRTH()
    {
        return get( (short) 122, "Date of Birth" );
    }

    public static String PHONE_NUMBER()
    {
        return get( (short) 123, "Phone Number" );
    }

    public static String NATIONAL_HEALTH_ID_NUMBER()
    {
        return get( (short) 124, "National Health ID Number" );
    }

    public static String CHILD_CONTACT_NAME()
    {
        return get( (short) 125, "Child contact name" );
    }

    public static String CHILD_CONTACT_TYPE()
    {
        return get( (short) 126, "Child contact type" );
    }

    public static String REGISTERED_SUCCESSFULLY()
    {
        return get( (short) 127, "Registered successfully" );
    }

    public static String REGISTERED_FAILED()
    {
        return get( (short) 128, "Registered failed" );
    }

    public static String RELATED_PERSON_SEARCH()
    {
        return get( (short) 129, "Related Person Search" );
    }

    public static String UPLOAD_COMPLETED()
    {
        return get( (short) 128, "Completed" );
    }

    public static String RELATIONSHIP_ADDED_SUCCESS()
    {
        return get( (short) 129, "Relationship has been added successfully" );
    }

    public static String AGE_IN_YEAR_HINT()
    {
        return get( (short) 132, "Enter age in years..." );
    }

    public static String DATE_TYPE()
    {
        return get( (short) 133, "YYYY-MM-dd" );
    }

    public static String DATE_OF_BIRTH_TYPE_DEFINITION()
    {
        return get( (short) 134, "Enter age in years for Approximated, yyyy-mm-dd for others." );
    }

    public static String DOBTYPE_DEFINITION()
    {
        return get( (short) 135, "DOB Type Definition" );
    }

    public static String REQUIRED_FIELD()
    {
        return get( (short) 136, "(*)Required field" );
    }

    public static String ENTER()
    {
        return get( (short) 137, "Enter" );
    }

    public static String LINKDASHBOARD()
    {
        return get( (short) 138, "Registered successfully, press Next to First Data Entry View or Back to Menu view." );
    }

    public static String DOB_WRONG_FORMAT()
    {
        return get( (short) 139, "See DOB Type Definition" );
    }

    public static String SEND_ALL()
    {
        return get( (short) 140, "Send All" );
    }

    public static String PROGRAM()
    {
        return get( (short) 141, "Program: " );
    }

    public static String ADD_PERSON()
    {
        return get( (short) 142, "Add Instance" );
    }

    public static String HTTP_ERROR()
    {
        return get( (short) 143, "Error in HTTP operation" );
    }

    public static String ENROLL_SUCCESS()
    {
        return get( (short) 144, "Enroll Program Successfully" );
    }

    public static String LOG()
    {
        return "Log";
    }

    public static String MESSAGE_SENT_SUCESSFULLY()
    {
        return get( (short) 145, "Message sent sucessfully" );
    }

    public static String SEND_FAIL()
    {
        return get( (short) 146, "Send fail" );
    }

    public static String ADD_ALL()
    {
        return get( (short) 147, "Add all" );
    }

    public static String SELECT_USER()
    {

        return get( (short) 148, "Select user" );
    }

    public static String SELECT_CONVERSATION()
    {
        return get( (short) 149, "Select conversation" );
    }

    public static String REPLY()
    {
        return get( (short) 150, "Reply" );
    }

    public static String ADD_NEW_INSTANCE()
    {
        return get( (short) 151, "Add Instance" );
    }

    public static String CREATE_NEW_EVENT()
    {
        return get( (short) 152, "Add Event" );
    }
    
    
    /***************************************
     * DEFAULT COLORS
     ***************************************/
    
    public static int HIGHLIGHT_COLOR()
    {
        return 0x66ff99;
    }
    
    public static int ERROR_TEXT_COLOR()
    {
        return 0xcc0000;
    }
    
    public static int HEADER_BG_COLOR()
    {
        return 0xcccccc;
    }

}
