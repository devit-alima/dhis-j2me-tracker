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
package org.hisp.dhis.mobile.connection.task;

import java.io.DataInputStream;
import java.io.IOException;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.LostEvent;
import org.hisp.dhis.mobile.model.Notification;
import org.hisp.dhis.mobile.ui.Text;
import org.hisp.dhis.mobile.util.SerializationUtil;

import com.jcraft.jzlib.ZInputStream;

/**
 * @author Nguyen Kim Lai
 *
 * @version SendLostToFollowUpTask.java 4:35:02 PM Oct 8, 2013 $
 */
public class SendLostToFollowUpTask extends AbstractTask
{
    private static final String CLASS_TAG = "SendLostToFollowUpTask";
    private NameBasedMIDlet nameBasedMIDlet;
    
    private LostEvent lostEvent;
    
    public SendLostToFollowUpTask( LostEvent lostEvent )
    {
        this.lostEvent = lostEvent;
        this.nameBasedMIDlet = (NameBasedMIDlet) ConnectionManager.getDhisMIDlet();
    }
    
    public void run()
    {
        LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Starting SendLostToFollowUpTask..." );
        
        DataInputStream tempinputStream = null;
        try
        {
            tempinputStream = this.upload( SerializationUtil.serialize( lostEvent ) );
            DataInputStream inputStream = new DataInputStream( new ZInputStream( tempinputStream ) );
            Notification notification = new Notification();
            notification.deSerialize( inputStream );

            if( notification.getMessage().equalsIgnoreCase( Text.SUCCESS() ) )
            {
                nameBasedMIDlet.getAlertBoxView( "Upload Successfully", Text.SUCCESS() ).showView();
            }
            else
            {
                nameBasedMIDlet.getAlertBoxView( "Upload Failed", Text.ERROR() ).showView();
            }
            nameBasedMIDlet.getLostToFollowUpView().getMainForm().show();
        }
        catch( IOException e )
        {
            LogMan.log( "Network," + CLASS_TAG, e );
        }
    }

}
