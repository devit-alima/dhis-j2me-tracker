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

import javax.microedition.rms.RecordFilter;

import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.model.DataSetValue;
import org.hisp.dhis.mobile.util.SerializationUtil;

public class DataSetValueFilter
    implements RecordFilter
{
    private static final String CLASS_TAG = "DataSetValueFilter";
    
    private Integer dataSetID;

    private String periodName;

    private Integer periodType;

    public boolean matches( byte[] suspect )
    {
        if ( dataSetID != null && periodName != null )
        {
            DataSetValue temp = new DataSetValue();
            try
            {
                SerializationUtil.deSerialize( temp, suspect );

                if ( temp.getId() == dataSetID.intValue() && temp.getpName().equals( periodName ) )
                {
                    temp = null;
                    return true;
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

    public void setCriteria( Integer dataSetID, String periodName, Integer periodType )
    {
        this.dataSetID = dataSetID;
        this.periodName = periodName;
        this.periodType = periodType;
    }

    public Integer getDataSetID()
    {
        return dataSetID;
    }

    public void setDataSetID( Integer dataSetID )
    {
        this.dataSetID = dataSetID;
    }

    public String getPeriodName()
    {
        return periodName;
    }

    public void setPeriodName( String periodName )
    {
        this.periodName = periodName;
    }

    public Integer getPeriodType()
    {
        return periodType;
    }

    public void setPeriodType( Integer periodType )
    {
        this.periodType = periodType;
    }
}
