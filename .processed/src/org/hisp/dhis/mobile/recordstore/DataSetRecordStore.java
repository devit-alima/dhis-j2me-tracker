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
import org.hisp.dhis.mobile.model.DataSet;
import org.hisp.dhis.mobile.model.OrgUnit;
import org.hisp.dhis.mobile.recordstore.filter.DataSetFilter;
import org.hisp.dhis.mobile.util.SerializationUtil;

public class DataSetRecordStore
{
    public static final String DATASET_DB = "DATASET";

    public DataSetRecordStore()
    {

    }

    public static boolean saveDataSets( Vector dataSetVector, OrgUnit orgUnit )
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        for ( int i = 0; i < dataSetVector.size(); i++ )
        {
            DataSet dataSet = (DataSet) dataSetVector.elementAt( i );
            saveDataSet( dataSet, orgUnit );
        }
        return true;
    }

    public static boolean saveDataSet( DataSet dataSet )
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        try
        {
            RecordStore recordStore = RecordStore.openRecordStore( DATASET_DB, true );
            DataSetFilter dataSetFilter = new DataSetFilter( dataSet );
            RecordEnumeration recordEnum = recordStore.enumerateRecords( dataSetFilter, null, false );
            byte[] bytes = SerializationUtil.serialize( dataSet );
            if ( recordEnum.numRecords() > 0 )
            {
                int id = recordEnum.nextRecordId();
                recordStore.setRecord( id, bytes, 0, bytes.length );
            }
            else
            {
                recordStore.addRecord( bytes, 0, bytes.length );
            }
            recordEnum.destroy();
            recordStore.closeRecordStore();
        }
        finally
        {
            System.gc();
        }
        return true;
    }

    public static boolean saveDataSet( DataSet dataSet, OrgUnit orgUnit )
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        try
        {
            RecordStore recordStore = RecordStore.openRecordStore( DATASET_DB + orgUnit.getId(), true );
            DataSetFilter dataSetFilter = new DataSetFilter( dataSet );
            RecordEnumeration recordEnum = recordStore.enumerateRecords( dataSetFilter, null, false );
            byte[] bytes = SerializationUtil.serialize( dataSet );
            if ( recordEnum.numRecords() > 0 )
            {
                int id = recordEnum.nextRecordId();
                recordStore.setRecord( id, bytes, 0, bytes.length );
            }
            else
            {
                recordStore.addRecord( bytes, 0, bytes.length );
            }
            recordEnum.destroy();
            recordStore.closeRecordStore();
        }
        finally
        {
            System.gc();
        }
        return true;
    }

    public static Vector loadDataSets(OrgUnit orgUnit)
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        Vector dataSetVector = null;
        try
        {
            RecordStore recordStore = RecordStore.openRecordStore( DATASET_DB + orgUnit.getId(), true );
            RecordEnumeration recordEnum = recordStore.enumerateRecords( null, null, false );
            dataSetVector = new Vector();
            while ( recordEnum.hasNextElement() )
            {
                DataSet dataSet = new DataSet();
                SerializationUtil.deSerialize( dataSet, recordEnum.nextRecord() );
                dataSetVector.addElement( dataSet );
            }
            recordEnum.destroy();
            recordStore.closeRecordStore();
        }
        finally
        {
            System.gc();
        }
        return dataSetVector;
    }

    public static boolean deleteDataSet( DataSet dataSet )
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException
    {
        try
        {
            RecordStore recordStore = RecordStore.openRecordStore( DATASET_DB, true );
            DataSetFilter dataSetFilter = new DataSetFilter( dataSet );
            RecordEnumeration recordEnum = recordStore.enumerateRecords( dataSetFilter, null, false );
            if ( recordEnum.numRecords() > 0 )
            {
                int id = recordEnum.nextRecordId();
                recordStore.deleteRecord( id );
                return true;
            }
            recordEnum.destroy();
            recordStore.closeRecordStore();
        }
        finally
        {
            System.gc();
        }
        return false;
    }

    public static void clearDataSetRecordStore()
    {

    }
}
