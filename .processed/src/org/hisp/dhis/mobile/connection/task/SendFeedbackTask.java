package org.hisp.dhis.mobile.connection.task;

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

import java.io.DataInputStream;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.Message;
import org.hisp.dhis.mobile.recordstore.FeedbackRecordStore;
import org.hisp.dhis.mobile.ui.Text;
import org.hisp.dhis.mobile.util.SerializationUtil;

public class SendFeedbackTask
    extends AbstractTask
{
    private static final String FEEDBACK_SENT = "feedback_sent";

    private Message message;

    private NameBasedMIDlet nameBasedMIDlet;

    public SendFeedbackTask( Message message )
    {
        this.message = message;
    }

    public SendFeedbackTask()
    {
    }

    public void run()
    {
        this.nameBasedMIDlet = (NameBasedMIDlet) ConnectionManager.getDhisMIDlet();

        try
        {
            DataInputStream messageStream = this.upload( SerializationUtil.serialize( message ) );

            String message = this.readMessage( messageStream );

            if ( message.equalsIgnoreCase( FEEDBACK_SENT ) )
            {
                FeedbackRecordStore.deleteSubject();
                nameBasedMIDlet.getAlertBoxView( Text.MESSAGE_SENT_SUCESSFULLY(), "" ).showView();
                nameBasedMIDlet.getMessagingMenuView().showView();
            }
            else
            {
                FeedbackRecordStore.deleteSubject();
                nameBasedMIDlet.getAlertBoxView( Text.SEND_FAIL(), "" ).showView();
                nameBasedMIDlet.getMessagingMenuView().showView();

            }

        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

    }

    public Message getMessage()
    {
        return message;
    }

    public void setMessage( Message message )
    {
        this.message = message;
    }

}
