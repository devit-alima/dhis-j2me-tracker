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
package org.hisp.dhis.mobile.recordstore;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.rms.RecordComparator;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;

import org.hisp.dhis.mobile.model.Model;
import org.hisp.dhis.mobile.model.ModelList;
import org.hisp.dhis.mobile.model.Patient;
import org.hisp.dhis.mobile.model.PatientAttribute;
import org.hisp.dhis.mobile.recordstore.filter.PatientFilter;
import org.hisp.dhis.mobile.util.SerializationUtil;

/**
 * @author Nguyen Kim Lai
 * 
 * @version PatientRecordStore.java 1:10:27 PM Jul 4, 2013 $
 */
public class PatientRecordStore
{
    public static final String PATIENT_DB = "PATIENT";

    public static final int MAX_NO_OF_RECORDS = 50;

    public static final int RECORDS_TO_DELETE = 5;

    public static boolean savePatients( Vector patientVector )
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        for ( int i = 0; i < patientVector.size(); i++ )
        {
            Patient patient = (Patient) patientVector.elementAt( i );
            if ( !savePatient( patient ) )
            {
                return false;
            }
        }
        return true;
    }

    public static boolean savePatient( Patient patient )
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        RecordStore recordStore = null;
        RecordEnumeration recordEnumeration = null;
        RecordEnumeration allRecords = null;
        PatientFilter patientFilter = new PatientFilter();
        patientFilter.setPatientID( patient.getId() );

        try
        {
            recordStore = RecordStore.openRecordStore( PATIENT_DB, true );
            recordEnumeration = recordStore.enumerateRecords( patientFilter, null, false );
            byte[] bite = SerializationUtil.serialize( patient );
            if ( recordEnumeration.numRecords() > 0 )
            {
                int id = recordEnumeration.nextRecordId();
                // recordStore.setRecord( id, bite, 0, bite.length );
                recordStore.deleteRecord( id );
                recordStore.addRecord( bite, 0, bite.length );
            }
            else
            {
                // check if PATIENT_DB has reached the max limit
                if ( recordStore.getNumRecords() >= MAX_NO_OF_RECORDS )
                {
                    allRecords = recordStore.enumerateRecords( null, null, true );
                    Vector ids = new Vector();
                    int id = 0;
                    while ( allRecords.hasNextElement() )
                    {
                        id = allRecords.nextRecordId();
                        ids.insertElementAt( "" + id, 0 );
                    }

                    for ( int i = 0; i < RECORDS_TO_DELETE; i++ )
                    {
                        recordStore.deleteRecord( Integer.parseInt( (String) ids.elementAt( i ) ) );
                    }
                }

                recordStore.addRecord( bite, 0, bite.length );
            }
        }
        finally
        {
            recordEnumeration.destroy();
            if ( allRecords != null )
            {
                allRecords.destroy();
            }
            recordStore.closeRecordStore();
            System.gc();
        }
        return true;
    }

    public static Patient getPatient( int patientID )
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        RecordStore recordStore = null;
        RecordEnumeration recordEnumeration = null;
        PatientFilter patientFilter = new PatientFilter();
        patientFilter.setPatientID( patientID );
        try
        {
            recordStore = RecordStore.openRecordStore( PATIENT_DB, true );
            recordEnumeration = recordStore.enumerateRecords( patientFilter, null, false );

            if ( recordEnumeration.numRecords() > 0 )
            {
                Patient patient = new Patient();
                SerializationUtil.deSerialize( patient, recordEnumeration.nextRecord() );
                return patient;
            }
        }
        finally
        {
            recordEnumeration.destroy();
            recordStore.closeRecordStore();
            System.gc();
        }
        return null;
    }

    public static ModelList getCurrentPatients()
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        ModelList modelList = new ModelList();
        RecordStore recordStore = null;
        RecordEnumeration recordEnumeration = null;
        Patient patient;

        try
        {
            recordStore = RecordStore.openRecordStore( PATIENT_DB, true );
            recordEnumeration = recordStore.enumerateRecords( null, null, false );

            while ( recordEnumeration.hasNextElement() )
            {
                patient = new Patient();
                Model model = new Model();
                String patientInfo = "";
                SerializationUtil.deSerialize( patient, recordEnumeration.nextRecord() );

                for ( int i = 0; i < patient.getPatientAttValues().size(); i++ )
                {
                    PatientAttribute attribute = (PatientAttribute) patient.getPatientAttValues().elementAt( i );
                    if ( attribute.isDisplayedInList() )
                    {
                        patientInfo += attribute.getValue() + " ";
                    }
                }
                patientInfo = patientInfo.trim();

                model.setName( patient.getTrackedEntityName() + "/" + patientInfo );
                model.setId( patient.getId() );

                int index;
                for ( index = modelList.getModels().size(); index > 0; index-- )
                {
                    if ( ((Model) modelList.getModels().elementAt( index - 1 )).getName().startsWith(
                        patient.getTrackedEntityName() ) )
                    {
                        break;
                    }
                }

                if ( index > 0 )
                {
                    modelList.getModels().insertElementAt( model, index - 1 );
                }
                else
                {
                    modelList.getModels().addElement( model );
                }
                patient = null;
                model = null;
            }
        }
        finally
        {
            recordEnumeration.destroy();
            recordStore.closeRecordStore();
            System.gc();
        }

        return modelList;
    }
}
