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
import java.util.Vector;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;

/**
 * @author Nguyen Kim Lai
 * 
 * @version FindLostToFollowUpTask.java 12:43:32 PM Oct 2, 2013 $
 */
public class FindLostToFollowUpTask
    extends AbstractTask
{
    private static final String CLASS_TAG = "FindLostToFollowUpTask";
    
    private String searchEventInfos;

    private NameBasedMIDlet nameBasedMIDlet;

    public FindLostToFollowUpTask( String searchEventInfos )
    {
        this.searchEventInfos = searchEventInfos;
        this.nameBasedMIDlet = (NameBasedMIDlet) ConnectionManager.getDhisMIDlet();
    }

    public void run()
    {
        LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Starting FindLostToFollowUpTask..." );
        
        DataInputStream inputStream = null;
        try
        {
            inputStream = this.download( searchEventInfos, "searchEventInfos" );
        }
        catch ( Exception e )
        {
            LogMan.log( CLASS_TAG, e );
            e.printStackTrace();
            String message = e.getMessage();
            if ( e.getMessage().equalsIgnoreCase( "NO_EVENT_FOUND" ) )
            {
                nameBasedMIDlet.getAlertBoxView( "No event found for this program", "No Result" ).showView();
                nameBasedMIDlet.getProgramSelectView().setTargetScreen( nameBasedMIDlet.LOST_TO_FOLLOW_UP_VIEW );
                nameBasedMIDlet.getProgramSelectView().showView();
            }
            else
            {
                Vector eventsInfo = new Vector();
                while ( message.length() > 0 )
                {
                    eventsInfo.addElement( message.substring( 0, message.indexOf( "$" ) ) );
                    message = message.substring( message.indexOf( "$" ) + 1, message.length() );
                }
                nameBasedMIDlet.getLostToFollowUpView().setEventsInfo( eventsInfo );
                nameBasedMIDlet.getLostToFollowUpView().showView();
                eventsInfo = null;
            }
        }
    }
}
