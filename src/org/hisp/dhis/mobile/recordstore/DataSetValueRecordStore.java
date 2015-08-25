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

import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.model.DataSet;
import org.hisp.dhis.mobile.model.DataSetValue;
import org.hisp.dhis.mobile.model.OrgUnit;
import org.hisp.dhis.mobile.recordstore.filter.DataSetValueFilter;
import org.hisp.dhis.mobile.recordstore.filter.OldDataSetValueFilter;
import org.hisp.dhis.mobile.util.PeriodUtil;
import org.hisp.dhis.mobile.util.SerializationUtil;

public class DataSetValueRecordStore
{
    public static final String CLASS_TAG = "DataSetValueRecordStore";
    
    public static final String DATASETVALUE_DB = "DATASETVALUE";

    public static final short STATE_NEW = 0;

    public static final short STATE_SAVED = 1;

    public static final short STATE_COMPLETED = 2;

    public static final short STATE_ERROR = 3;

    public static DataSetValue getDataSetValue( int dataSetID, String periodName, OrgUnit orgUnit )
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        RecordStore rs = null;
        RecordEnumeration re = null;
        DataSetValueFilter dataSetValueFilter = null;
        DataSetValue dataSetValue = new DataSetValue();
        try
        {
            dataSetValueFilter = new DataSetValueFilter();
            dataSetValueFilter.setCriteria( new Integer( dataSetID ), periodName, null );
            rs = RecordStore.openRecordStore( DATASETVALUE_DB + orgUnit.getId(), true );
            re = rs.enumerateRecords( dataSetValueFilter, null, true );

            if ( re.numRecords() == 1 )
            {
                SerializationUtil.deSerialize( dataSetValue, re.nextRecord() );
                return dataSetValue;
            }
            else if ( re.numRecords() > 1 )
            {
                throw new IOException( "Has more than one DataSet Value for this DataSet" );
            }
            else
            {
                return null;
            }
        }
        finally
        {
            dataSetValueFilter = null;
            re.destroy();
            rs.closeRecordStore();
            System.gc();
        }
    }

    public static boolean saveDataSetValue( DataSetValue dataSetValue, OrgUnit orgUnit )
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        RecordStore rs = null;
        RecordEnumeration re = null;
        DataSetValueFilter dataSetValueFilter = null;
        try
        {
            dataSetValueFilter = new DataSetValueFilter();
            dataSetValueFilter.setCriteria( new Integer( dataSetValue.getId() ), dataSetValue.getpName(), null );
            rs = RecordStore.openRecordStore( DATASETVALUE_DB + orgUnit.getId(), true );
            re = rs.enumerateRecords( dataSetValueFilter, null, true );
            byte[] bytes = SerializationUtil.serialize( dataSetValue );
            if ( re.numRecords() > 0 )
            {
                int recordID = re.nextRecordId();
                rs.setRecord( recordID, bytes, 0, bytes.length );
            }
            else
            {
                rs.addRecord( bytes, 0, bytes.length );
            }
        }
        finally
        {
            re.destroy();
            rs.closeRecordStore();
            System.gc();
        }
        return true;
    }

    public static boolean isCompleted( int dataSetID, String periodName )
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        RecordStore rs = null;
        RecordEnumeration re = null;
        DataSetValueFilter dataSetValueFilter = null;
        DataSetValue dataSetValue = new DataSetValue();
        try
        {
            dataSetValueFilter = new DataSetValueFilter();
            dataSetValueFilter.setCriteria( new Integer( dataSetID ), periodName, null );
            rs = RecordStore.openRecordStore( DATASETVALUE_DB, true );
            re = rs.enumerateRecords( dataSetValueFilter, null, true );

            if ( re.numRecords() == 1 )
            {
                SerializationUtil.deSerialize( dataSetValue, re.nextRecord() );
                return dataSetValue.isCompleted();
            }
            else if ( re.numRecords() > 1 )
            {
                throw new IOException( "Has more than one DataSet Value for this DataSet" );
            }
            else
            {
                return false;
            }
        }
        finally
        {
            dataSetValueFilter = null;
            re.destroy();
            rs.closeRecordStore();
            System.gc();
        }
    }

    public static void cleanOldDataSetValues( int dataSetID, int periodType, Vector currentPeriodVector )
        throws RecordStoreException
    {
        RecordStore rs = null;
        RecordEnumeration re = null;
        OldDataSetValueFilter filter = new OldDataSetValueFilter();
        filter.setCriteria( dataSetID, periodType, currentPeriodVector );
        try
        {
            rs = RecordStore.openRecordStore( DATASETVALUE_DB, true );
            re = rs.enumerateRecords( filter, null, true );
            LogMan.log( LogMan.INFO, "RMS," + CLASS_TAG, "Number of value will be deleted:" + re.numRecords() );
            
            while ( re.hasNextElement() )
            {
                rs.deleteRecord( re.nextRecordId() );
            }
            if ( currentPeriodVector != null )
            {
                currentPeriodVector.removeAllElements();
                currentPeriodVector = null;
            }
        }
        finally
        {
            filter = null;
            re.destroy();
            rs.closeRecordStore();
            System.gc();
        }
    }

    public static short getDataSetState( DataSet selectedDataSet, String periodName, OrgUnit orgUnit )
    {
        RecordStore rs = null;
        RecordEnumeration re = null;

        if ( selectedDataSet.getPeriodType().equals( "Monthly" ) )
        {
            periodName = PeriodUtil.formatMonthlyPeriod( periodName );
        }
        else if ( selectedDataSet.getPeriodType().equals( "Weekly" ) )
        {
            periodName = PeriodUtil.formatWeeklyPeriod( periodName );
        }
        DataSetValueFilter dsValueFilter = new DataSetValueFilter();
        dsValueFilter.setCriteria( new Integer( selectedDataSet.getId() ), periodName, null );

        try
        {
            rs = RecordStore.openRecordStore( DATASETVALUE_DB + orgUnit.getId(), true );
            re = rs.enumerateRecords( dsValueFilter, null, true );
            if ( re.numRecords() > 0 )
            {
                DataSetValue dsValue = new DataSetValue();
                SerializationUtil.deSerialize( dsValue, re.nextRecord() );
                if ( dsValue.isCompleted() )
                    return STATE_COMPLETED;
                else
                    return STATE_SAVED;
            }
        }
        catch ( Exception e )
        {
            LogMan.log( "RMS," + CLASS_TAG, e );
            e.printStackTrace();
            return STATE_ERROR;
        }
        finally
        {
            if ( rs != null )
            {
                try
                {
                    rs.closeRecordStore();
                }
                catch ( RecordStoreException e )
                {
                    LogMan.log( "RMS," + CLASS_TAG, e );
                }
            }
        }

        return STATE_NEW;
    }
}
