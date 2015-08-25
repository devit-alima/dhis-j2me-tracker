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
import java.util.Vector;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.Patient;
import org.hisp.dhis.mobile.model.Relationship;
import org.hisp.dhis.mobile.ui.Text;
import org.hisp.dhis.mobile.util.SerializationUtil;

import com.jcraft.jzlib.ZInputStream;

/**
 * @author Nguyen Kim Lai
 * 
 * @version AddRelationshipTask.java 11:13:02 AM Mar 18, 2013 $
 */
public class AddRelationshipTask
    extends AbstractTask
{
    private static final String CLASS_TAG = "AddRelationshipTask";
    
    private Relationship enrollmentRelationship;

    private NameBasedMIDlet nameBasedMIDlet;
    
    private Patient patient;

    public void run()
    {
        LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Starting AddRelationshipTask..." );
        nameBasedMIDlet = (NameBasedMIDlet) ConnectionManager.getDhisMIDlet();
        DataInputStream tempInputStream = null;
        Patient newPatient = new Patient();
        try
        {
            tempInputStream = this.upload( SerializationUtil.serialize( enrollmentRelationship ) );
            DataInputStream inputStream = new DataInputStream( new ZInputStream( tempInputStream ) );
            if ( inputStream != null )
            {
                newPatient.deSerialize( inputStream );

                if ( newPatient.getId() > 0 )
                {
                    nameBasedMIDlet.getPersonDashboardView().setPatient( newPatient );
                    nameBasedMIDlet.getAlertBoxView( Text.RELATIONSHIP_ADDED_SUCCESS(), Text.UPLOAD_COMPLETED() )
                        .showView();
                    nameBasedMIDlet.getPersonDashboardView().showView();
                }
                else
                {
                }
            }
            tempInputStream = null;
            inputStream = null;
            newPatient = null;
            patient = null;
            nameBasedMIDlet = null;
            enrollmentRelationship = null;
            System.gc();
        }
        catch ( IOException e )
        {
            LogMan.log( CLASS_TAG, e );
            e.printStackTrace();

            String message = null;
            
            String personBName = enrollmentRelationship.getPersonBName();
            enrollmentRelationship.setPersonBName( personBName.substring( personBName.lastIndexOf( ':' ) + 1 ) ); 

            if ( e.getMessage().equalsIgnoreCase( "NO_BENEFICIARY_FOUND" ) )
            {
                message = Text.NO_PERSON_FOUND() + ": " + enrollmentRelationship.getPersonBName();
                nameBasedMIDlet.getAddingRelationshipView().setPatient( patient );
                nameBasedMIDlet.getAddingRelationshipView().setEnrollmentRelationship( enrollmentRelationship );
                nameBasedMIDlet.getAddingRelationshipView().prepareView();
                nameBasedMIDlet.getAddingRelationshipView().getNotification().setText( message );
                nameBasedMIDlet.getAddingRelationshipView().getMainForm().show();

            }
            else
            {
                message = e.getMessage();
                Vector patientInfos = new Vector();
                while ( message.length() > 0 )
                {
                    patientInfos.addElement( message.substring( 0, message.indexOf( "$" ) ) );
                    message = message.substring( message.indexOf( "$" ) + 1, message.length() );
                }
                nameBasedMIDlet.getPersonListView().setPatientInfos( patientInfos );
                nameBasedMIDlet.getPersonListView().setEnrollmentRelationship( enrollmentRelationship );
                nameBasedMIDlet.getPersonListView().setPatient( patient );
                patientInfos = null;
                message = null;
                nameBasedMIDlet.getPersonListView().showView();
            }
            nameBasedMIDlet = null;
            enrollmentRelationship = null;
            patient = null;
            System.gc();
        }

    }

    public Relationship getEnrollmentRelationship()
    {
        return enrollmentRelationship;
    }

    public void setEnrollmentRelationship( Relationship enrollmentRelationship )
    {
        this.enrollmentRelationship = enrollmentRelationship;
    }
    
    public void setPatient( Patient patient )
    {
        this.patient = patient;
    }

}
