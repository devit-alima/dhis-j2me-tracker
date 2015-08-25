package org.hisp.dhis.mobile.midlet;

/*
 * Copyright (c) 2004-2011, University of Oslo All rights reserved.
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

import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.model.Patient;
import org.hisp.dhis.mobile.model.ProgramStage;
import org.hisp.dhis.mobile.view.AddRelativeView;
import org.hisp.dhis.mobile.view.AddingRelationshipView;
import org.hisp.dhis.mobile.view.AnonymousProgramListView;
import org.hisp.dhis.mobile.view.CreateProgramStageView;
import org.hisp.dhis.mobile.view.DashboardLinkView;
import org.hisp.dhis.mobile.view.FeedbackContentView;
import org.hisp.dhis.mobile.view.FeedbackView;
import org.hisp.dhis.mobile.view.FindBeneficiaryView;
import org.hisp.dhis.mobile.view.FindUserView;
import org.hisp.dhis.mobile.view.GenerateRepeatableEventView;
import org.hisp.dhis.mobile.view.HistoryPersonListView;
import org.hisp.dhis.mobile.view.LostToFollowUpDetailView;
import org.hisp.dhis.mobile.view.LostToFollowUpView;
import org.hisp.dhis.mobile.view.MessageConversationView;
import org.hisp.dhis.mobile.view.MessageDetailView;
import org.hisp.dhis.mobile.view.MessageOptionView;
import org.hisp.dhis.mobile.view.MessageReplyView;
import org.hisp.dhis.mobile.view.MessageSubjectView;
import org.hisp.dhis.mobile.view.MessageView;
import org.hisp.dhis.mobile.view.MessagingMenuView;
import org.hisp.dhis.mobile.view.NoMatchingView;
import org.hisp.dhis.mobile.view.OfflineOrgUnitSelectView;
import org.hisp.dhis.mobile.view.OfflineView;
import org.hisp.dhis.mobile.view.OrgUnitSelectView;
import org.hisp.dhis.mobile.view.PersonDashboardView;
import org.hisp.dhis.mobile.view.PersonListView;
import org.hisp.dhis.mobile.view.PersonRegistrationView;
import org.hisp.dhis.mobile.view.ProgramSelectView;
import org.hisp.dhis.mobile.view.ProgramStageListView;
import org.hisp.dhis.mobile.view.RepeatableProgramStageListView;
import org.hisp.dhis.mobile.view.SearchVisitScheduleView;
import org.hisp.dhis.mobile.view.SectionListView;
import org.hisp.dhis.mobile.view.TrackingDataEntryView;
import org.hisp.dhis.mobile.view.TrackingMainMenuView;
import org.hisp.dhis.mobile.view.UpdateContactView;
import org.hisp.dhis.mobile.view.UserListView;
import org.hisp.dhis.mobile.view.VisitScheduleListView;
import org.hisp.dhis.mobile.view.VisitScheduleMenuView;
import org.hisp.dhis.mobile.view.WarningView;

public class NameBasedMIDlet
    extends DHISMIDlet
{
    private static final String CLASS_TAG = "NameBasedMIDlet";

    private TrackingMainMenuView trackingMainMenuView;

    private FindBeneficiaryView findBeneficiaryView;

    private PersonDashboardView personDashboardView;

    private CreateProgramStageView createProgramStageView;

    private RepeatableProgramStageListView repeatableProgramStageListView;

    private TrackingDataEntryView trackingDataEntryView;

    private PersonRegistrationView personRegistrationView;

    private SectionListView sectionListView;

    private AddingRelationshipView addingRelationshipView;

    private PersonListView personListView;

    private UpdateContactView updateContactView;

    private AnonymousProgramListView anonymousProgramListView;

    private OrgUnitSelectView orgUnitSelectView;

    private ProgramStageListView programStageListView;

    private DashboardLinkView dashboardLinkView;

    private HistoryPersonListView historyPersonListView;

    private OfflineView offlineView;

    private OfflineOrgUnitSelectView offlineOrgUnitSelectView;

    private NoMatchingView noMatchingView;

    private WarningView warningView;

    private ProgramSelectView programSelectView;

    private LostToFollowUpView lostToFollowUpView;

    private LostToFollowUpDetailView lostToFollowUpDetailView;

    private GenerateRepeatableEventView generateRepeatableEventView;

    private VisitScheduleMenuView visitScheduleMenuView;

    private SearchVisitScheduleView searchVisitScheduleView;

    private VisitScheduleListView visitScheduleListView;

    private MessagingMenuView messagingMenuView;

    private FeedbackView feedbackView;

    private FeedbackContentView feedbackContentView;

    private FindUserView findUserView;

    private UserListView userListView;

    private MessageSubjectView messageSubjectView;

    private MessageOptionView messageOptionView;

    private MessageView messageView;

    private MessageConversationView messageConversationView;

    private MessageDetailView messageDetailView;

    private MessageReplyView messageReplyView;

    private AddRelativeView addRelativeView;

    private String previousScreen = "";

    public final int FIND_INSTANCE_VIEW = 1;

    public final int PERSON_REGISTRATION_VIEW = 2;

    public final int LOST_TO_FOLLOW_UP_VIEW = 3;

    public final int VISIT_SCHEDULE_VIEW = 4;

    public final int HISTORY_PERSON_LIST_VIEW = 5;

    public final int SINGLE_EVENT_WITHOUT_REGISTRATION = 6;

    public NameBasedMIDlet()
    {
        LogMan.log( LogMan.DEV, "", "Initializing Name-based Reporting Application" );
    }

    public String getPreviousScreen()
    {
        return previousScreen;
    }

    public void setPreviousScreen( String previousScreen )
    {
        this.previousScreen = previousScreen;
    }

    public FindBeneficiaryView getFindBeneficiaryView()
    {
        if ( findBeneficiaryView == null )
        {
            findBeneficiaryView = new FindBeneficiaryView( this );
        }
        return findBeneficiaryView;
    }

    public TrackingMainMenuView getTrackingMainMenuView()
    {
        if ( trackingMainMenuView == null )
        {
            trackingMainMenuView = new TrackingMainMenuView( this );
        }
        return trackingMainMenuView;
    }

    public PersonDashboardView getPersonDashboardView()
    {
        if ( personDashboardView == null )
        {
            personDashboardView = new PersonDashboardView( this );
        }
        return personDashboardView;
    }

    public CreateProgramStageView getCreateProgramStageView()
    {
        if ( createProgramStageView == null )
        {
            createProgramStageView = new CreateProgramStageView( this );
        }
        return createProgramStageView;
    }

    public RepeatableProgramStageListView getRepeatableProgramStageListView()
    {
        if ( repeatableProgramStageListView == null )
        {
            repeatableProgramStageListView = new RepeatableProgramStageListView( this );
        }
        return repeatableProgramStageListView;
    }

    public TrackingDataEntryView getTrackingDataEntryView()
    {
        if ( trackingDataEntryView == null )
        {
            trackingDataEntryView = new TrackingDataEntryView( this );
        }
        return trackingDataEntryView;
    }

    public PersonRegistrationView getPersonRegistrationView()
    {
        if ( personRegistrationView == null )
        {
            personRegistrationView = new PersonRegistrationView( this );
        }

        return personRegistrationView;
    }

    public VisitScheduleMenuView getVisitScheduleMenuView()
    {
        if ( visitScheduleMenuView == null )
        {
            visitScheduleMenuView = new VisitScheduleMenuView( this );
        }

        return visitScheduleMenuView;
    }

    public SearchVisitScheduleView getSearchVisitScheduleView()
    {
        if ( searchVisitScheduleView == null )
        {
            searchVisitScheduleView = new SearchVisitScheduleView( this );
        }

        return searchVisitScheduleView;
    }

    public VisitScheduleListView getVisitScheduleListView()
    {
        if ( visitScheduleListView == null )
        {
            visitScheduleListView = new VisitScheduleListView( this );
        }

        return visitScheduleListView;
    }

    public SectionListView getSectionListView()
    {
        if ( sectionListView == null )
        {
            sectionListView = new SectionListView( this );
        }
        return sectionListView;
    }

    public AddingRelationshipView getAddingRelationshipView()
    {
        if ( addingRelationshipView == null )
        {
            addingRelationshipView = new AddingRelationshipView( this );
        }
        return addingRelationshipView;
    }

    public PersonListView getPersonListView()
    {
        if ( personListView == null )
        {
            personListView = new PersonListView( this );
        }
        return personListView;
    }

    public UpdateContactView getUpdateContactView()
    {
        if ( updateContactView == null )
        {
            updateContactView = new UpdateContactView( this );
        }
        return updateContactView;
    }

    public AnonymousProgramListView getAnonymousProgramListView()
    {
        if ( anonymousProgramListView == null )
        {
            anonymousProgramListView = new AnonymousProgramListView( this );
        }
        return anonymousProgramListView;
    }

    public OrgUnitSelectView getOrgUnitSelectView()
    {
        if ( orgUnitSelectView == null )
        {
            orgUnitSelectView = new OrgUnitSelectView( this );
        }
        return orgUnitSelectView;
    }

    public ProgramStageListView getProgramStageListView()
    {
        if ( programStageListView == null )
        {
            programStageListView = new ProgramStageListView( this );
        }
        return programStageListView;
    }

    public DashboardLinkView getDashboardLinkView()
    {
        if ( dashboardLinkView == null )
        {
            dashboardLinkView = new DashboardLinkView( this );
        }
        return dashboardLinkView;
    }

    public HistoryPersonListView getHistoryPersonListView()
    {
        if ( historyPersonListView == null )
        {
            historyPersonListView = new HistoryPersonListView( this );
        }
        return historyPersonListView;
    }

    public OfflineView getOfflineView()
    {
        if ( offlineView == null )
        {
            offlineView = new OfflineView( this );
        }
        return offlineView;
    }

    public OfflineOrgUnitSelectView getOfflineOrgUnitSelectView()
    {
        if ( offlineOrgUnitSelectView == null )
        {
            offlineOrgUnitSelectView = new OfflineOrgUnitSelectView( this );
        }
        return offlineOrgUnitSelectView;
    }

    public NoMatchingView getNoMatchingView()
    {
        if ( noMatchingView == null )
        {
            noMatchingView = new NoMatchingView( this );
        }
        return noMatchingView;
    }

    public WarningView getWarningView()
    {
        if ( warningView == null )
        {
            warningView = new WarningView( this );
        }
        return warningView;
    }

    public ProgramSelectView getProgramSelectView()
    {
        if ( programSelectView == null )
        {
            programSelectView = new ProgramSelectView( this );
        }
        return programSelectView;
    }

    public LostToFollowUpView getLostToFollowUpView()
    {
        if ( lostToFollowUpView == null )
        {
            lostToFollowUpView = new LostToFollowUpView( this );
        }
        return lostToFollowUpView;
    }

    public LostToFollowUpDetailView getLostToFollowUpDetailView()
    {
        if ( lostToFollowUpDetailView == null )
        {
            lostToFollowUpDetailView = new LostToFollowUpDetailView( this );
        }
        return lostToFollowUpDetailView;
    }

    public GenerateRepeatableEventView getGenerateRepeatableEventView( ProgramStage programStage,
        String defaultDueDate, Patient patient )
    {
        if ( generateRepeatableEventView == null )
        {
            generateRepeatableEventView = new GenerateRepeatableEventView( this, programStage, defaultDueDate, patient );
        }
        else
        {
            generateRepeatableEventView.setProgramStage( programStage );
            generateRepeatableEventView.setDefaultDueDate( defaultDueDate );
            generateRepeatableEventView.setPatient( patient );
        }
        return generateRepeatableEventView;
    }

    public MessagingMenuView getMessagingMenuView()
    {
        if ( messagingMenuView == null )
        {
            messagingMenuView = new MessagingMenuView( this );
        }
        return messagingMenuView;
    }

    public FeedbackView getFeedbackView()
    {
        if ( feedbackView == null )
        {
            feedbackView = new FeedbackView( this );
        }
        return feedbackView;
    }

    public FeedbackContentView getFeedbackContentView()
    {
        if ( feedbackContentView == null )
        {
            feedbackContentView = new FeedbackContentView( this );
        }
        return feedbackContentView;
    }

    public FindUserView getFindUserView()
    {
        if ( findUserView == null )
        {
            findUserView = new FindUserView( this );
        }
        return findUserView;
    }

    public UserListView getUserListView()
    {
        if ( userListView == null )
        {
            userListView = new UserListView( this );
        }
        return userListView;
    }

    public MessageSubjectView getMessageSubjectView()
    {
        if ( messageSubjectView == null )
        {
            messageSubjectView = new MessageSubjectView( this );
        }
        return messageSubjectView;
    }

    public MessageOptionView getMessageOptionView()
    {
        if ( messageOptionView == null )
        {
            messageOptionView = new MessageOptionView( this );
        }
        return messageOptionView;
    }

    public MessageView getMessageView()
    {
        if ( messageView == null )
        {
            messageView = new MessageView( this );
        }

        return messageView;
    }

    public MessageConversationView getMessageConversationView()
    {
        if ( messageConversationView == null )
        {
            messageConversationView = new MessageConversationView( this );
        }
        return messageConversationView;
    }

    public MessageDetailView getMessageDetailView()
    {
        if ( messageDetailView == null )
        {
            messageDetailView = new MessageDetailView( this );
        }
        return messageDetailView;
    }

    public MessageReplyView getMessageReplyView()
    {
        if ( messageReplyView == null )
        {
            messageReplyView = new MessageReplyView( this );
        }
        return messageReplyView;
    }

    public AddRelativeView getAddRelativeView()
    {
        if ( addRelativeView == null )
        {
            addRelativeView = new AddRelativeView( this );
        }
        return addRelativeView;
    }

}
