package org.hisp.dhis.mobile.model;

/*
 * Copyright (c) 2004-2010, University of Oslo All rights reserved.
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

public class DataSetValue
    extends Model
    implements DataStreamSerializable
{

    private String pName;

    private boolean completed;

    private Hashtable dataValues = new Hashtable();

    public DataSetValue()
    {
    }

    public String getpName()
    {
        return pName;
    }

    public void setpName( String pName )
    {
        this.pName = pName;
    }

    public Hashtable getDataValues()
    {
        return dataValues;
    }

    public void setDataValues( Hashtable dataValues )
    {
        this.dataValues = dataValues;
    }

    public boolean isCompleted()
    {
        return completed;
    }

    public void setCompleted( boolean completed )
    {
        this.completed = completed;
    }

    public void serialize( DataOutputStream dout )
        throws IOException
    {

        dout.writeInt( this.getId() );
        dout.writeUTF( this.getName() );
        dout.writeUTF( this.getpName() );
        dout.writeBoolean( this.isCompleted() );

        dout.writeInt( dataValues.size() );

        Enumeration enumeration = dataValues.elements();
        while ( enumeration.hasMoreElements() )
        {
            DataValue dv = (DataValue) enumeration.nextElement();
            dout.writeInt( dv.getId() );
            dout.writeInt( dv.getCategoryOptComboID() );
            dout.writeUTF( dv.getVal() );
        }
    }

    public void deSerialize( DataInputStream din )
        throws IOException
    {

        this.setId( din.readInt() );
        this.setName( din.readUTF() );
        this.setpName( din.readUTF() );
        this.setCompleted( din.readBoolean() );

        int size = din.readInt();

        for ( int i = 0; i < size; i++ )
        {
            DataValue dv = new DataValue();
            dv.setId( din.readInt() );
            dv.setCategoryOptComboID( din.readInt() );
            dv.setVal( din.readUTF() );
            this.dataValues.put( dv.getId() + "-" + dv.getCategoryOptComboID(), dv );
        }
    }

}
