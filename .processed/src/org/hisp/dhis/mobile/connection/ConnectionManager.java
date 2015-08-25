package org.hisp.dhis.mobile.connection;

/*
 * Copyright (c) 2004-2014, University of Oslo All rights reserved.
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

import java.io.IOException;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.HttpsConnection;

import org.hisp.dhis.mobile.connection.task.AbstractTask;
import org.hisp.dhis.mobile.connection.task.AddRelationshipTask;
import org.hisp.dhis.mobile.connection.task.CompleteProgramInstanceTask;
import org.hisp.dhis.mobile.connection.task.DownloadAllResourceTask;
import org.hisp.dhis.mobile.connection.task.DownloadMessageConversationTask;
import org.hisp.dhis.mobile.connection.task.DownloadMessageTask;
import org.hisp.dhis.mobile.connection.task.EnrollProgramTask;
import org.hisp.dhis.mobile.connection.task.FindAnonymousProgramTask;
import org.hisp.dhis.mobile.connection.task.FindLostToFollowUpTask;
import org.hisp.dhis.mobile.connection.task.FindPatientTask;
import org.hisp.dhis.mobile.connection.task.FindUserTask;
import org.hisp.dhis.mobile.connection.task.FindVisitScheduleTask;
import org.hisp.dhis.mobile.connection.task.GenerateRepeatableEventTask;
import org.hisp.dhis.mobile.connection.task.GetPatientTask;
import org.hisp.dhis.mobile.connection.task.GetPatientsTask;
import org.hisp.dhis.mobile.connection.task.GetProgramsTask;
import org.hisp.dhis.mobile.connection.task.GetVariesInfoTask;
import org.hisp.dhis.mobile.connection.task.LoginTask;
import org.hisp.dhis.mobile.connection.task.PersonRegistrationTask;
import org.hisp.dhis.mobile.connection.task.RegisterAllOfflinePatientTask;
import org.hisp.dhis.mobile.connection.task.RelativeRegistrationTask;
import org.hisp.dhis.mobile.connection.task.ReplyMessageTask;
import org.hisp.dhis.mobile.connection.task.SaveUnregisteredPersonTask;
import org.hisp.dhis.mobile.connection.task.SendFeedbackTask;
import org.hisp.dhis.mobile.connection.task.SendLostToFollowUpTask;
import org.hisp.dhis.mobile.connection.task.SendMessageTask;
import org.hisp.dhis.mobile.connection.task.UpdateContactTask;
import org.hisp.dhis.mobile.connection.task.UpdateNewVersionTask;
import org.hisp.dhis.mobile.connection.task.UploadSingleEventWithoutRegistration;
import org.hisp.dhis.mobile.connection.task.UploadTrackingFormTask;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.model.LostEvent;
import org.hisp.dhis.mobile.model.Message;
import org.hisp.dhis.mobile.model.OrgUnit;
import org.hisp.dhis.mobile.model.Patient;
import org.hisp.dhis.mobile.model.Program;
import org.hisp.dhis.mobile.model.ProgramInstance;
import org.hisp.dhis.mobile.model.ProgramStage;
import org.hisp.dhis.mobile.model.Relationship;
import org.hisp.dhis.mobile.model.Section;

public class ConnectionManager
{
    private static final String CLASS_TAG = "ConnectionManager";

    public static final String MEDIATYPE_MOBILE_SERIALIZED = "application/vnd.org.dhis2.mobile+serialized";

    // Class variables

    private static DHISMIDlet dhisMIDlet;

    private static String url;

    private static String userName;

    private static String password;

    private static String ua;

    private static String locale;

    private static OrgUnit orgUnit;

    /**
     * 
     */
    public ConnectionManager()
    {
    }

    /**
     * 
     * @param dhisMIDlet
     * @param url
     * @param userName
     * @param password
     * @param locale
     * @param orgUnit
     */
    public static void init( org.hisp.dhis.mobile.midlet.DHISMIDlet dhisMIDlet, String url, String userName,
        String password, String locale, OrgUnit orgUnit )
    {
        LogMan.log( LogMan.DEV, "Network," + CLASS_TAG, "Initializing Connection Manager..." );

        ConnectionManager.dhisMIDlet = dhisMIDlet;
        ConnectionManager.url = url;
        ConnectionManager.userName = userName;
        ConnectionManager.password = password;
        ConnectionManager.locale = locale;
        ConnectionManager.orgUnit = orgUnit;

        ua = "Profile/" + System.getProperty( "microedition.profiles" ) + " Configuration/"
            + System.getProperty( "microedition.configuration" );

        LogMan.log( LogMan.DEV, "Network," + CLASS_TAG, "Connection Settings: URL=" + url );
        LogMan.log( LogMan.DEV, "Network,Authentication," + CLASS_TAG, "Connection Settings: Username=" + userName );
        LogMan.log( LogMan.DEV, "Network,Authentication," + CLASS_TAG, "Connection Settings: Password=" + password );
        LogMan.log( LogMan.DEV, "Network," + CLASS_TAG, "Connection Settings: Locale=" + locale );
        LogMan.log( LogMan.DEV, "Network," + CLASS_TAG, "Connection Settings: User-Agent=" + ua );
    }

    /**
     * 
     * @return
     */
    public static HttpConnection createConnection()
    {
        LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Creating connection..." );

        HttpConnection connection = null;
        try
        {
            connection = getConnection( url );
            connection.setRequestProperty( "User-Agent", ua );
            connection.setRequestProperty( "Accept-Language", locale );
            connection.setRequestProperty( "Content-Type", MEDIATYPE_MOBILE_SERIALIZED );
            connection.setRequestProperty( "Accept", MEDIATYPE_MOBILE_SERIALIZED );

            LogMan.log( LogMan.INFO, "Network,HTTPHeader" + CLASS_TAG, "HTTP Header: User-Agent: " + ua );
            LogMan.log( LogMan.INFO, "Network,HTTPHeader" + CLASS_TAG, "HTTP Header: Accept-Language: " + locale );
            LogMan.log( LogMan.INFO, "Network,HTTPHeader" + CLASS_TAG, "HTTP Header: Content-Type: "
                + MEDIATYPE_MOBILE_SERIALIZED );
            LogMan.log( LogMan.INFO, "Network,HTTPHeader" + CLASS_TAG, "HTTP Header: Accept: "
                + MEDIATYPE_MOBILE_SERIALIZED );

            // set HTTP basic authentication
            if ( ConnectionManager.userName != null && password != null )
            {
                LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Encoding... " + userName + ":" + password );
                byte[] auth = (userName + ":" + password).getBytes();
                String encoded = Base64.encode( auth, 0, auth.length );

                LogMan.log( LogMan.INFO, "Network,HTTPHeader,Authentication," + CLASS_TAG,
                    "HTTP Header: Authorization: " + "Basic " + encoded );
                connection.setRequestProperty( "Authorization", "Basic " + encoded );
            }
        }
        catch ( IOException e )
        {
            LogMan.log( "Network,HTTPHeader,Authentication", e );
            e.printStackTrace();
        }
        return connection;
    }

    public static HttpConnection getConnection( String url )
        throws IOException
    {
        if ( url.startsWith( "https://" ) )
        {
            LogMan.log( LogMan.DEV, "Network," + CLASS_TAG, "Opening HTTPS Connection: " + url );
            return (HttpsConnection) Connector.open( url, Connector.READ_WRITE, true );
        }
        else
        {
            LogMan.log( LogMan.DEV, "Network," + CLASS_TAG, "Opening HTTPS Connection: " + url );
            return (HttpConnection) Connector.open( url, Connector.READ_WRITE, true );
        }
    }

    public static void login()
    {
        runTask( new LoginTask() );
    }

    public static void downloadAllResource( Vector orgUnitVector )
    {
        runTask( new DownloadAllResourceTask( orgUnitVector ) );
    }

    public static void updateNewVersion()
    {
        runTask( new UpdateNewVersionTask() );
    }

    public static void findPatientByName( String keyWord )
    {
        FindPatientTask findPatientTask = new FindPatientTask( keyWord );
        runTask( findPatientTask );
    }

    public static void findVisitSchedule( Program program, String info )
    {
        FindVisitScheduleTask findVisitScheduleTask = new FindVisitScheduleTask( program, info );
        runTask( findVisitScheduleTask );
    }

    public static void getPatientById( String patientId, int previousScreen, Program vsProgram )
    {
        GetPatientTask getPatientTask = new GetPatientTask();
        getPatientTask.setPatientId( patientId );
        getPatientTask.setPreviousScreen( previousScreen );
        getPatientTask.setVSProgram( vsProgram );
        runTask( getPatientTask );
    }

    public static void getPatientsById( String patientIds )
    {
        GetPatientsTask getPatientsTask = new GetPatientsTask();
        getPatientsTask.setPatientIds( patientIds );
        runTask( getPatientsTask );
    }

    public static void uploadProgramStage( ProgramStage programStage, Section section, Patient patient,
        ProgramStage uploadProgramStage )
    {
        UploadTrackingFormTask uploadTrackingFormTask = new UploadTrackingFormTask();
        uploadTrackingFormTask.setProgramStage( programStage );
        uploadTrackingFormTask.setSection( section );
        uploadTrackingFormTask.setPatient( patient );
        uploadTrackingFormTask.setUploadProgramStage( uploadProgramStage );

        runTask( uploadTrackingFormTask );
    }

    public static void enrollProgram( int patientId, int programId, String programName )
    {
        EnrollProgramTask enrollProgramTask = new EnrollProgramTask( patientId, programId, programName );
        enrollProgramTask.setPatientId( patientId );
        enrollProgramTask.setProgramId( programId );
        runTask( enrollProgramTask );
    }

    public static void addRelationship( Relationship enrollmentRelationship, Patient patient )
    {
        AddRelationshipTask addRelationshipTask = new AddRelationshipTask();
        addRelationshipTask.setEnrollmentRelationship( enrollmentRelationship );
        addRelationshipTask.setPatient( patient );
        runTask( addRelationshipTask );
    }

    public static void updateContactFromServer()
    {
        UpdateContactTask updateContactTask = new UpdateContactTask();
        runTask( updateContactTask );
    }

    public static void getAllAnonymousProgram( String programType, int targetScreen )
    {
        GetProgramsTask getAllAnonymousProgramTask = new GetProgramsTask( programType, targetScreen );
        runTask( getAllAnonymousProgramTask );
    }

    public static void findAnonymousProgram( String keyword )
    {
        FindAnonymousProgramTask findProgramTask = new FindAnonymousProgramTask( keyword );
        runTask( findProgramTask );
    }

    public static void registerPerson( Patient patient, String enrollProgramId )
    {
        PersonRegistrationTask registerTask = new PersonRegistrationTask( patient, enrollProgramId );
        runTask( registerTask );
    }

    public static void getVariesInfo( String programId )
    {
        GetVariesInfoTask getVariesInfoTask = new GetVariesInfoTask();
        getVariesInfoTask.setProgramId( programId );
        runTask( getVariesInfoTask );
    }

    public static void registerAllOfflinePatient( Patient patient )
    {
        RegisterAllOfflinePatientTask registerAllOfflinePatientTask = new RegisterAllOfflinePatientTask( patient );
        runTask( registerAllOfflinePatientTask );
    }

    public static void findLostToFollowUp( String searchEventInfos )
    {
        FindLostToFollowUpTask findLostToFollowUpTask = new FindLostToFollowUpTask( searchEventInfos );
        runTask( findLostToFollowUpTask );
    }

    public static void sendLostToFollowUp( LostEvent lostEvent )
    {
        SendLostToFollowUpTask sendLostToFollowUpTask = new SendLostToFollowUpTask( lostEvent );
        runTask( sendLostToFollowUpTask );
    }

    public static void saveUnregisterdPatient( Patient patient )
    {
        SaveUnregisteredPersonTask saveUnregisteredPersonTask = new SaveUnregisteredPersonTask( patient );
        runTask( saveUnregisteredPersonTask );
    }

    public static void generateRepeatableEvent( String eventInfo )
    {
        GenerateRepeatableEventTask generateRepeatableEventTask = new GenerateRepeatableEventTask( eventInfo );
        runTask( generateRepeatableEventTask );
    }

    public static void completeProgramInstance( ProgramInstance programInstance, Patient patient )
    {
        CompleteProgramInstanceTask completeProgramInstanceTask = new CompleteProgramInstanceTask( programInstance,
            patient );
        runTask( completeProgramInstanceTask );
    }

    public static void uploadSingleEventWithoutRegistration( ProgramStage programStage )
    {
        UploadSingleEventWithoutRegistration uploadSingleEventWithoutRegistration = new UploadSingleEventWithoutRegistration();
        uploadSingleEventWithoutRegistration.setProgramStage( programStage );
        runTask( uploadSingleEventWithoutRegistration );
    }

    public static void sendFeedback( Message message )
    {
        SendFeedbackTask sendFeedbackTask = new SendFeedbackTask( message );
        runTask( sendFeedbackTask );

    }

    public static void findUser( String keyword )
    {
        FindUserTask findUserTask = new FindUserTask( keyword );
        runTask( findUserTask );
    }

    public static void sendMessage( Message message )
    {
        SendMessageTask sendMessageTask = new SendMessageTask( message );
        runTask( sendMessageTask );
    }

    public static void downloadMessageConversation()
    {
        DownloadMessageConversationTask downloadMessageConversationTask = new DownloadMessageConversationTask();
        runTask( downloadMessageConversationTask );
    }

    public static void downloadMessage( int conversationId )
    {
        DownloadMessageTask downloadMessageTask = new DownloadMessageTask( conversationId );
        runTask( downloadMessageTask );
    }

    public static void replyMessage( Message message )
    {
        ReplyMessageTask replyMessageTask = new ReplyMessageTask();
        replyMessageTask.setMessage( message );
        runTask( replyMessageTask );

    }

    public static void registerRelative( Patient patient, String enrollProgramId, Patient relative )
    {
        RelativeRegistrationTask rrTask = new RelativeRegistrationTask( patient, enrollProgramId, relative );
        runTask( rrTask );
    }

    public static void runTask( AbstractTask task )
    {
        new Thread( task ).start();
    }

    public static DHISMIDlet getDhisMIDlet()
    {
        return dhisMIDlet;
    }

    public static void setDhisMIDlet( DHISMIDlet dhisMIDlet )
    {
        ConnectionManager.dhisMIDlet = dhisMIDlet;
    }

    public static String getUrl()
    {
        return url;
    }

    public static void setUrl( String url )
    {
        ConnectionManager.url = url;
    }

    public static String getUserName()
    {
        return userName;
    }

    public static void setUserName( String userName )
    {
        ConnectionManager.userName = userName;
    }

    public static String getPassword()
    {
        return password;
    }

    public static void setPassword( String password )
    {
        ConnectionManager.password = password;
    }

    public static String getUa()
    {
        return ua;
    }

    public static void setUa( String ua )
    {
        ConnectionManager.ua = ua;
    }

    public static String getLocale()
    {
        return locale;
    }

    public static void setLocale( String locale )
    {
        ConnectionManager.locale = locale;
    }

    public static OrgUnit getOrgUnit()
    {
        return orgUnit;
    }

    public static void setOrgUnit( OrgUnit orgUnit )
    {
        ConnectionManager.orgUnit = orgUnit;
    }

    public static String getMediatypeMobileSerialized()
    {
        return MEDIATYPE_MOBILE_SERIALIZED;
    }

}
