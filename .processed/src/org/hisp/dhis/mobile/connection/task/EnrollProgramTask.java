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

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.Patient;
import org.hisp.dhis.mobile.ui.Text;

/**
 * @author Nguyen Kim Lai
 * 
 * @version EnrollProgramTask.java 11:11:10 AM Mar 1, 2013 $
 */

public class EnrollProgramTask
    extends AbstractTask
{
    private static final String CLASS_TAG = "EnrollProgramTask";

    private int patientId;

    private int programId;

    private String programName;

    public EnrollProgramTask( int patientId, int programId, String programName )
    {
        this.patientId = patientId;
        this.programId = programId;
        this.programName = programName;
        this.nameBasedMIDlet = (NameBasedMIDlet) ConnectionManager.getDhisMIDlet();
    }

    NameBasedMIDlet nameBasedMIDlet;

    public void run()
    {
        LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Starting EnrollProgramTask..." );

        DataInputStream inputStream = null;
        Patient patient = new Patient();
        try
        {
            inputStream = this.download( patientId + "-" + programId, "enrollInfo" );
            if ( inputStream != null )
            {
                patient.deSerialize( inputStream );

                if ( patient.getId() > 0 )
                {
                    nameBasedMIDlet.getPersonDashboardView().setPatient( patient );
                    nameBasedMIDlet.getAlertBoxView( Text.ENROLL_SUCCESS(), programName ).showView();
                    nameBasedMIDlet.getPersonDashboardView().showView();
                }
                else
                {
                }
            }
        }
        catch ( Exception e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            LogMan.log( "Network," + CLASS_TAG, e );
        }

    }

    public int getPatientId()
    {
        return patientId;
    }

    public void setPatientId( int patientId )
    {
        this.patientId = patientId;
    }

    public int getProgramId()
    {
        return programId;
    }

    public void setProgramId( int programId )
    {
        this.programId = programId;
    }

}
