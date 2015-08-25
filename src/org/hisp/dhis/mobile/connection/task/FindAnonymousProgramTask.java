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
import org.hisp.dhis.mobile.model.Program;
import org.hisp.dhis.mobile.model.ProgramStage;

/**
 * @author Nguyen Kim Lai
 *
 * @version FindProgramTask.java 2:00:07 PM Mar 29, 2013 $
 */
public class FindAnonymousProgramTask extends AbstractTask
{
    private static final String CLASS_TAG = "FindAnonymousProgramTask";

    private String keyWord;

    private NameBasedMIDlet nameBasedMIDlet;
    
    public FindAnonymousProgramTask( String keyWord )
    {
        super();
        this.keyWord = keyWord;
    }
    
    public void run()
    {
        LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Starting FindAnonymousProgramTask..." );
        nameBasedMIDlet = (NameBasedMIDlet) ConnectionManager.getDhisMIDlet();
        DataInputStream inputStream = null;
        Program program = new Program();
        
        try
        {
            inputStream = this.download( keyWord, "info" );
            if ( inputStream != null )
            {
                program.deSerialize( inputStream );

                if ( program.getId() > 0 )
                {
                    ProgramStage programStage = (ProgramStage) program.getProgramStages().elementAt( 0 );
                    if ( programStage.isSingleEvent() )
                    {
                        nameBasedMIDlet.getTrackingDataEntryView().setProgramStage( programStage );
                        nameBasedMIDlet.getTrackingDataEntryView().setTitle( program.getName() );
                        programStage = null;
                        nameBasedMIDlet.getTrackingDataEntryView().showView();
                    }
                }
                else
                {
                }
            }
            inputStream = null;
            program = null;
            System.gc();
        }
        catch ( IOException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            LogMan.log( "Network," + CLASS_TAG, e );
        }
    }
    
}
