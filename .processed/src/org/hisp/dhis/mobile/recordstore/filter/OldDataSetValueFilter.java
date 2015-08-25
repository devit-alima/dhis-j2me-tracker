package org.hisp.dhis.mobile.recordstore.filter;

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
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import javax.microedition.rms.RecordFilter;

import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.model.DataSetValue;
import org.hisp.dhis.mobile.util.PeriodUtil;
import org.hisp.dhis.mobile.util.SerializationUtil;

public class OldDataSetValueFilter
    implements RecordFilter
{
    private static final String CLASS_TAG = "OldDataSetValueFilter";
    
    private int dataSetID;

    private int periodType;

    private Vector currentPeriodVector;

    public boolean matches( byte[] candidateBytes )
    {
        DataSetValue temp = new DataSetValue();
        if ( periodType == PeriodUtil.MONTHLY )
        {
            try
            {
                SerializationUtil.deSerialize( temp, candidateBytes );
                if ( temp.getId() == dataSetID )
                {
                    temp.setpName( PeriodUtil.displayMonthlyPeriod( temp.getpName() ) );
                    if ( !currentPeriodVector.contains( temp.getpName() ) )
                    {
                        temp = null;
                        return true;
                    }
                }

            }
            catch ( IOException e )
            {
                LogMan.log( "RMS," + CLASS_TAG, e );
                temp = null;
                e.printStackTrace();
            }
        }
        else if ( periodType == PeriodUtil.DAILY )
        {
            try
            {
                Calendar calendar = Calendar.getInstance();
                SerializationUtil.deSerialize( temp, candidateBytes );
                if ( temp.getId() == dataSetID )
                {
                    long days = (calendar.getTime().getTime() - PeriodUtil.stringToDate( temp.getpName() ).getTime())
                        / (24 * 60 * 60 * 1000);
                    if ( days > 31 )
                    {
                        temp = null;
                        return true;
                    }
                }
            }
            catch ( IOException e )
            {
                LogMan.log( "RMS," + CLASS_TAG, e );
                temp = null;
                e.printStackTrace();
            }
        }
        else if ( periodType == PeriodUtil.WEEKLY )
        {
            // Delete value older than 4 weeks
            // Hard-code for first day of week: Monday

            try
            {
                Calendar calendar = Calendar.getInstance();
                int todayWeekDay = calendar.get( Calendar.DAY_OF_WEEK );

                calendar.set( Calendar.HOUR, 0 );
                calendar.set( Calendar.MINUTE, 0 );
                calendar.set( Calendar.SECOND, 0 );
                calendar.set( Calendar.MILLISECOND, 0 );

                long monday = 0;
                long lowerBound = 0;
                if ( todayWeekDay != 1 )
                {
                    monday = calendar.getTime().getTime() - ((todayWeekDay - 2) * 24 * 60 * 60 * 1000);
                }
                else
                {
                    monday = calendar.getTime().getTime() - 6 * 24 * 60 * 60 * 1000;
                }

                lowerBound = monday - 21 * 24 * 60 * 60 * 1000;

                SerializationUtil.deSerialize( temp, candidateBytes );
                if ( temp.getId() == dataSetID )
                {
                    if ( PeriodUtil.stringToDate( temp.getpName() ).getTime() <= lowerBound )
                    {
                        return true;
                    }
                    else if ( PeriodUtil.weekOfYear( new Date() ).equalsIgnoreCase(
                        PeriodUtil.weekOfYear( PeriodUtil.stringToDate( temp.getpName() ) ) ) )
                    {
                        return true;
                    }
                    temp = null;
                }
            }
            catch ( IOException e )
            {
                LogMan.log( "RMS," + CLASS_TAG, e );
                temp = null;
                e.printStackTrace();
            }
        }
        return false;
    }

    public void setCriteria( int dataSetID, int periodType, Vector currentPeriodVector )
    {
        this.dataSetID = dataSetID;
        this.periodType = periodType;
        this.currentPeriodVector = currentPeriodVector;
    }

    public int getDataSetID()
    {
        return dataSetID;
    }

    public void setDataSetID( int dataSetID )
    {
        this.dataSetID = dataSetID;
    }

    public int getPeriodType()
    {
        return periodType;
    }

    public void setPeriodType( int periodType )
    {
        this.periodType = periodType;
    }

    public Vector getCurrentPeriodVector()
    {
        return currentPeriodVector;
    }

    public void setCurrentPeriodVector( Vector currentPeriodVector )
    {
        this.currentPeriodVector = currentPeriodVector;
    }

}
