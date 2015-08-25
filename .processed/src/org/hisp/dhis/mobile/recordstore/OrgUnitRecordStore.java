package org.hisp.dhis.mobile.recordstore;

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
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import org.hisp.dhis.mobile.model.OrgUnit;
import org.hisp.dhis.mobile.util.SerializationUtil;

public class OrgUnitRecordStore
{
    public static final String ORG_UNIT_DB = "ORG_UNIT";

    public static boolean saveOrgUnit( OrgUnit orgUnit )
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        RecordStore recordStore = null;
        try
        {
            recordStore = RecordStore.openRecordStore( ORG_UNIT_DB, true );
            byte[] bite = SerializationUtil.serialize( orgUnit );
            recordStore.addRecord( bite, 0, bite.length );
        }
        finally
        {
            recordStore.closeRecordStore();
            System.gc();
        }
        return true;
    }

    public static Vector loadAllOrgUnit()
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        Vector orgUnitVector = new Vector();
        try
        {
            RecordStore recordStore = RecordStore.openRecordStore( ORG_UNIT_DB, true );
            RecordEnumeration recordEnum = recordStore.enumerateRecords( null, null, false );
            while ( recordEnum.hasNextElement() )
            {
                OrgUnit orgUnit = new OrgUnit();
                SerializationUtil.deSerialize( orgUnit, recordEnum.nextRecord() );
                orgUnitVector.addElement( orgUnit );
            }
            recordEnum.destroy();
            recordStore.closeRecordStore();
        }
        finally
        {
            System.gc();
        }
        return orgUnitVector;
    }

    public static void deleteAllOrgUnit()
        throws RecordStoreNotFoundException, RecordStoreException
    {
        RecordStore recordStore = RecordStore.openRecordStore( ORG_UNIT_DB, true );
        if ( recordStore.getSize() > 0 )
        {
            recordStore.closeRecordStore();
            RecordStore.deleteRecordStore( ORG_UNIT_DB );
        }
    }
}
