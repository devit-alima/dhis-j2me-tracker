package org.hisp.dhis.mobile.util;

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

import java.io.IOException;
import java.util.Vector;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.model.OrgUnit;
import org.hisp.dhis.mobile.recordstore.DataSetRecordStore;
import org.hisp.dhis.mobile.recordstore.DataSetValueRecordStore;
import org.hisp.dhis.mobile.recordstore.OrgUnitRecordStore;
import org.hisp.dhis.mobile.recordstore.ProgramRecordStore;
import org.hisp.dhis.mobile.recordstore.SettingRecordStore;

public class RecordStoreUtil
{
    private static final String CLASS_TAG = "RecordStoreUtil";
    
    public static void clearRecordStore( String dbName )
    {
        RecordStore rs = null;
        RecordEnumeration re = null;
        try
        {
            rs = RecordStore.openRecordStore( dbName, true );
            re = rs.enumerateRecords( null, null, false );
            int id;
            while ( re.hasNextElement() )
            {
                id = re.nextRecordId();
                rs.deleteRecord( id );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            LogMan.log( "RMS," + CLASS_TAG, e );
        }
        finally
        {
            if ( re != null )
                re.destroy();
            if ( rs != null )
                try
                {
                    rs.closeRecordStore();
                }
                catch ( RecordStoreNotOpenException e )
                {
                    e.printStackTrace();
                }
                catch ( RecordStoreException e )
                {
                    e.printStackTrace();
                }
        }
    }

    public static void clearAllRecordStore()
    {
        Vector orgUnitVector = null;
        try
        {
            orgUnitVector = OrgUnitRecordStore.loadAllOrgUnit();
        }
        catch ( RecordStoreException e )
        {
            e.printStackTrace();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }

        for ( int i = 0; i < orgUnitVector.size(); i++ )
        {
            OrgUnit orgUnit = (OrgUnit) orgUnitVector.elementAt( i );
            clearRecordStore( DataSetRecordStore.DATASET_DB + orgUnit.getId() );
            clearRecordStore( DataSetValueRecordStore.DATASETVALUE_DB + orgUnit.getId() );
            // clearRecordStore( ActivityRecordStore.ACTIVITY_DB +
            // orgUnit.getId() );
        }
        // clearRecordStore( ActivityValueRecordStore.ACTIVITYVALUE_DB );
        clearRecordStore( ProgramRecordStore.PROGRAM_DB );
        clearRecordStore( SettingRecordStore.SETTINGS_DB );
        clearRecordStore( OrgUnitRecordStore.ORG_UNIT_DB );
    }
}
