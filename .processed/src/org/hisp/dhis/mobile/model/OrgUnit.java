package org.hisp.dhis.mobile.model;

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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.recordstore.SettingRecordStore;

public class OrgUnit
    implements DataStreamSerializable
{
    private static final String CLASS_TAG = "OrgUnit";

    private int id;

    private String name;

    private String downloadAllUrl;

    private String updateActivityPlanUrl;

    private String uploadFacilityReportUrl;

    private String downloadFacilityReportUrl;

    private String uploadActivityReportUrl;

    private String updateDataSetsUrl;

    private String changeDataSetLangUrl;

    private String searchUrl;

    private String updateNewVersionUrl;

    private String sendFeedbackUrl;

    private String findUserUrl;

    private String sendMessageUrl;

    private String downloadMessageConversationUrl;

    private String getMessageUrl;

    private String replyMessageUrl;

    private String downloadInterpretationUrl;

    private String postInterpretationUrl;

    private String postCommentUrl;

    private String updateContactUrl;

    private String findPatientUrl;

    private String uploadProgramStageUrl;

    private String registerPersonUrl;

    private String enrollProgramUrl;

    private String getVariesInfoUrl;

    private String addRelationshipUrl;

    private String downloadAnonymousProgramUrl;

    private String findProgramUrl;

    private String findPatientInAdvancedUrl;

    private String findPatientsUrl;

    private String findVisitScheduleUrl;

    private String findLostToFollowUpUrl;

    private String handleLostToFollowUpUrl;

    private String generateRepeatableEventUrl;

    private String uploadSingleEventWithoutRegistration;

    private String completeProgramInstanceUrl;

    private String registerRelativeUrl;

    public static double serverVersion;

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getDownloadAllUrl()
    {
        return downloadAllUrl;
    }

    public String getUploadFacilityReportUrl()
    {
        return uploadFacilityReportUrl;
    }

    public String getDownloadFacilityReportUrl()
    {
        return downloadFacilityReportUrl;
    }

    public String getUploadActivityReportUrl()
    {
        return uploadActivityReportUrl;
    }

    public String getUpdateDataSetsUrl()
    {
        return updateDataSetsUrl;
    }

    public String getUpdateActivityPlanUrl()
    {
        return updateActivityPlanUrl;
    }

    public String getChangeDataSetLangUrl()
    {
        return changeDataSetLangUrl;
    }

    public String getSearchUrl()
    {
        return searchUrl;
    }

    public String getUpdateNewVersionUrl()
    {
        return updateNewVersionUrl;
    }

    public String getSendFeedbackUrl()
    {
        return sendFeedbackUrl;
    }

    public String getFindUserUrl()
    {
        return findUserUrl;
    }

    public String getSendMessageUrl()
    {
        return sendMessageUrl;
    }

    public String getDownloadMessageConversationUrl()
    {
        return downloadMessageConversationUrl;
    }

    public String getGetMessageUrl()
    {
        return getMessageUrl;
    }

    public String getReplyMessageUrl()
    {
        return replyMessageUrl;
    }

    public String getDownloadInterpretationUrl()
    {
        return downloadInterpretationUrl;
    }

    public String getPostInterpretationUrl()
    {
        return postInterpretationUrl;
    }

    public String getPostCommentUrl()
    {
        return postCommentUrl;
    }

    public String getUpdateContactUrl()
    {
        return updateContactUrl;
    }

    public String getFindPatientUrl()
    {
        return findPatientUrl;
    }

    public String getUploadProgramStageUrl()
    {
        return uploadProgramStageUrl;
    }

    public String getRegisterPersonUrl()
    {
        return registerPersonUrl;
    }

    public String getEnrollProgramUrl()
    {
        return enrollProgramUrl;
    }

    public String getGetVariesInfoUrl()
    {
        return getVariesInfoUrl;
    }

    public String getAddRelationshipUrl()
    {
        return addRelationshipUrl;
    }

    public String getDownloadAnonymousProgramUrl()
    {
        return downloadAnonymousProgramUrl;
    }

    public String getFindProgramUrl()
    {
        return findProgramUrl;
    }

    public String getFindPatientInAdvancedUrl()
    {
        return findPatientInAdvancedUrl;
    }

    public String getFindPatientsUrl()
    {
        return findPatientsUrl;
    }

    public String getFindVisitScheduleUrl()
    {
        return findVisitScheduleUrl;
    }

    public String getFindLostToFollowUpUrl()
    {
        return findLostToFollowUpUrl;
    }

    public String getHandleLostToFollowUpUrl()
    {
        return handleLostToFollowUpUrl;
    }

    public String getGenerateRepeatableEventUrl()
    {
        return generateRepeatableEventUrl;
    }

    public String getCompleteProgramInstanceUrl()
    {
        return completeProgramInstanceUrl;
    }

    public String getUploadSingleEventWithoutRegistration()
    {
        return uploadSingleEventWithoutRegistration;
    }

    public void setUploadSingleEventWithoutRegistration( String uploadSingleEventWithoutRegistration )
    {
        this.uploadSingleEventWithoutRegistration = uploadSingleEventWithoutRegistration;
    }

    public String getRegisterRelativeUrl()
    {
        return registerRelativeUrl;
    }

    public void serialize( DataOutputStream dataOutputStream )
        throws IOException
    {
        dataOutputStream.writeInt( id );
        dataOutputStream.writeUTF( name );
        dataOutputStream.writeUTF( downloadAllUrl );
        dataOutputStream.writeUTF( updateActivityPlanUrl );
        dataOutputStream.writeUTF( uploadFacilityReportUrl );
        dataOutputStream.writeUTF( downloadFacilityReportUrl );
        dataOutputStream.writeUTF( uploadActivityReportUrl );
        dataOutputStream.writeUTF( updateDataSetsUrl );
        dataOutputStream.writeUTF( changeDataSetLangUrl );
        dataOutputStream.writeUTF( searchUrl );
        dataOutputStream.writeUTF( updateNewVersionUrl );
        dataOutputStream.writeUTF( sendFeedbackUrl );
        dataOutputStream.writeUTF( findUserUrl );
        dataOutputStream.writeUTF( sendMessageUrl );
        dataOutputStream.writeUTF( downloadMessageConversationUrl );
        dataOutputStream.writeUTF( getMessageUrl );
        dataOutputStream.writeUTF( replyMessageUrl );
        dataOutputStream.writeUTF( downloadInterpretationUrl );
        dataOutputStream.writeUTF( postInterpretationUrl );
        dataOutputStream.writeUTF( postCommentUrl );
        dataOutputStream.writeUTF( updateContactUrl );
        dataOutputStream.writeUTF( findPatientUrl );
        dataOutputStream.writeUTF( registerPersonUrl );
        dataOutputStream.writeUTF( uploadProgramStageUrl );
        dataOutputStream.writeUTF( enrollProgramUrl );
        dataOutputStream.writeUTF( getVariesInfoUrl );
        dataOutputStream.writeUTF( addRelationshipUrl );
        dataOutputStream.writeUTF( downloadAnonymousProgramUrl );
        dataOutputStream.writeUTF( findProgramUrl );
        dataOutputStream.writeUTF( findPatientInAdvancedUrl );
        dataOutputStream.writeUTF( findPatientsUrl );
        dataOutputStream.writeUTF( findVisitScheduleUrl );
        dataOutputStream.writeUTF( findLostToFollowUpUrl );
        dataOutputStream.writeUTF( handleLostToFollowUpUrl );
        dataOutputStream.writeUTF( generateRepeatableEventUrl );
        dataOutputStream.writeUTF( uploadSingleEventWithoutRegistration );
        dataOutputStream.writeUTF( completeProgramInstanceUrl );
        dataOutputStream.writeUTF( getRegisterRelativeUrl() );
    }

    public void deSerialize( DataInputStream dataInputStream )
        throws IOException
    {
        LogMan.log( LogMan.DEBUG, "OrgUnit," + CLASS_TAG, "Deserializing Organization Unit..." );

        id = dataInputStream.readInt();
        LogMan.log( LogMan.DEBUG, "OrgUnit," + CLASS_TAG, "id=" + id );

        name = dataInputStream.readUTF();
        LogMan.log( LogMan.DEBUG, "OrgUnit," + CLASS_TAG, "name=" + name );

        downloadAllUrl = dataInputStream.readUTF();
        LogMan.log( LogMan.DEBUG, "OrgUnit," + CLASS_TAG, "downloadAllUrl=" + downloadAllUrl );

        updateActivityPlanUrl = dataInputStream.readUTF();
        LogMan.log( LogMan.DEBUG, "OrgUnit," + CLASS_TAG, "updateActivityPlanUrl=" + updateActivityPlanUrl );

        uploadFacilityReportUrl = dataInputStream.readUTF();
        LogMan.log( LogMan.DEBUG, "OrgUnit," + CLASS_TAG, "uploadFacilityReportUrl=" + uploadFacilityReportUrl );

        downloadFacilityReportUrl = dataInputStream.readUTF();
        LogMan.log( LogMan.DEBUG, "OrgUnit," + CLASS_TAG, "downloadFacilityReportUrl=" + downloadFacilityReportUrl );

        uploadActivityReportUrl = dataInputStream.readUTF();
        LogMan.log( LogMan.DEBUG, "OrgUnit," + CLASS_TAG, "uploadActivityReportUrl=" + uploadActivityReportUrl );

        updateDataSetsUrl = dataInputStream.readUTF();
        LogMan.log( LogMan.DEBUG, "OrgUnit," + CLASS_TAG, "updateDataSetsUrl=" + updateDataSetsUrl );

        changeDataSetLangUrl = dataInputStream.readUTF();
        LogMan.log( LogMan.DEBUG, "OrgUnit," + CLASS_TAG, "changeDataSetLangUrl=" + changeDataSetLangUrl );

        searchUrl = dataInputStream.readUTF();
        LogMan.log( LogMan.DEBUG, "OrgUnit," + CLASS_TAG, "searchUrl=" + searchUrl );

        updateNewVersionUrl = dataInputStream.readUTF();
        LogMan.log( LogMan.DEBUG, "OrgUnit," + CLASS_TAG, "updateNewVersionUrl=" + updateNewVersionUrl );

        sendFeedbackUrl = dataInputStream.readUTF();
        LogMan.log( LogMan.DEBUG, "OrgUnit," + CLASS_TAG, "sendFeedbackUrl=" + sendFeedbackUrl );

        findUserUrl = dataInputStream.readUTF();
        LogMan.log( LogMan.DEBUG, "OrgUnit," + CLASS_TAG, "findUserUrl=" + findUserUrl );

        sendMessageUrl = dataInputStream.readUTF();
        LogMan.log( LogMan.DEBUG, "OrgUnit," + CLASS_TAG, "sendMessageUrl=" + sendMessageUrl );

        downloadMessageConversationUrl = dataInputStream.readUTF();
        LogMan.log( LogMan.DEBUG, "OrgUnit," + CLASS_TAG, "downloadMessageConversationUrl="
            + downloadMessageConversationUrl );

        getMessageUrl = dataInputStream.readUTF();
        LogMan.log( LogMan.DEBUG, "OrgUnit," + CLASS_TAG, "getMessageUrl=" + getMessageUrl );

        replyMessageUrl = dataInputStream.readUTF();
        LogMan.log( LogMan.DEBUG, "OrgUnit," + CLASS_TAG, "replyMessageUrl=" + replyMessageUrl );

        downloadInterpretationUrl = dataInputStream.readUTF();
        LogMan.log( LogMan.DEBUG, "OrgUnit," + CLASS_TAG, "downloadInterpretationUrl=" + downloadInterpretationUrl );

        postInterpretationUrl = dataInputStream.readUTF();
        LogMan.log( LogMan.DEBUG, "OrgUnit," + CLASS_TAG, "postInterpretationUrl=" + postInterpretationUrl );

        postCommentUrl = dataInputStream.readUTF();
        LogMan.log( LogMan.DEBUG, "OrgUnit," + CLASS_TAG, "postCommentUrl=" + postCommentUrl );

        updateContactUrl = dataInputStream.readUTF();
        LogMan.log( LogMan.DEBUG, "OrgUnit," + CLASS_TAG, "updateContactUrl=" + updateContactUrl );

        findPatientUrl = dataInputStream.readUTF();
        LogMan.log( LogMan.DEBUG, "OrgUnit," + CLASS_TAG, "findPatientUrl=" + findPatientUrl );

        registerPersonUrl = dataInputStream.readUTF();
        LogMan.log( LogMan.DEBUG, "OrgUnit," + CLASS_TAG, "registerPersonUrl=" + registerPersonUrl );

        uploadProgramStageUrl = dataInputStream.readUTF();
        LogMan.log( LogMan.DEBUG, "OrgUnit," + CLASS_TAG, "uploadProgramStageUrl=" + uploadProgramStageUrl );

        enrollProgramUrl = dataInputStream.readUTF();
        LogMan.log( LogMan.DEBUG, "OrgUnit," + CLASS_TAG, "enrollProgramUrl=" + enrollProgramUrl );

        getVariesInfoUrl = dataInputStream.readUTF();
        LogMan.log( LogMan.DEBUG, "OrgUnit," + CLASS_TAG, "getVariesInfoUrl=" + getVariesInfoUrl );

        addRelationshipUrl = dataInputStream.readUTF();
        LogMan.log( LogMan.DEBUG, "OrgUnit," + CLASS_TAG, "addRelationshipUrl=" + addRelationshipUrl );

        downloadAnonymousProgramUrl = dataInputStream.readUTF();
        LogMan.log( LogMan.DEBUG, "OrgUnit," + CLASS_TAG, "downloadAnonymousProgramUrl=" + downloadAnonymousProgramUrl );

        findProgramUrl = dataInputStream.readUTF();
        LogMan.log( LogMan.DEBUG, "OrgUnit," + CLASS_TAG, "findProgramUrl=" + findProgramUrl );

        findPatientInAdvancedUrl = dataInputStream.readUTF();
        LogMan.log( LogMan.DEBUG, "OrgUnit," + CLASS_TAG, "findPatientInAdvancedUrl=" + findPatientInAdvancedUrl );

        findPatientsUrl = dataInputStream.readUTF();
        LogMan.log( LogMan.DEBUG, "OrgUnit" + CLASS_TAG, "findPatientsUrl=" + findPatientsUrl );
        findVisitScheduleUrl = dataInputStream.readUTF();
        LogMan.log( LogMan.DEBUG, "OrgUnit," + CLASS_TAG, "findVisitScheduleUrl=" + findVisitScheduleUrl );

        findLostToFollowUpUrl = dataInputStream.readUTF();
        LogMan.log( LogMan.DEBUG, "OrgUnit," + CLASS_TAG, "findLostToFollowUpUrl=" + findLostToFollowUpUrl );

        handleLostToFollowUpUrl = dataInputStream.readUTF();
        LogMan.log( LogMan.DEBUG, "OrgUnit," + CLASS_TAG, "handleLostToFollowUpUrl=" + handleLostToFollowUpUrl );

        generateRepeatableEventUrl = dataInputStream.readUTF();
        LogMan.log( LogMan.DEBUG, "OrgUnit," + CLASS_TAG, "generateRepeatableEventUrl=" + generateRepeatableEventUrl );

        uploadSingleEventWithoutRegistration = dataInputStream.readUTF();
        LogMan.log( LogMan.DEBUG, "OrgUnit," + CLASS_TAG, "uploadSingleEventWithoutRegistration="
            + uploadSingleEventWithoutRegistration );

        completeProgramInstanceUrl = dataInputStream.readUTF();
        LogMan.log( LogMan.DEBUG, "OrgUnit," + CLASS_TAG, "completeProgramInstanceUrl=" + completeProgramInstanceUrl );
        
        registerRelativeUrl = dataInputStream.readUTF();
        LogMan.log( LogMan.DEBUG, "OrgUnit," + CLASS_TAG, "registerRelativeUrl=" + registerRelativeUrl );
    }

    public boolean checkNewVersion( double serverVersion )
    {
        double clientVersion;
        boolean result = false;
        try
        {
            SettingRecordStore settingRecordStore = new SettingRecordStore();
            clientVersion = Double.parseDouble( settingRecordStore.get( SettingRecordStore.CLIENT_VERSION ) );
            if ( serverVersion > clientVersion )
            {
                result = true;
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        return result;
    }
}
