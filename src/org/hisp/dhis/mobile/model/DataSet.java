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
import java.util.Vector;

public class DataSet
    extends Model
    implements DataStreamSerializable
{

    private Vector sections = new Vector();

    private String periodType;

    private int version;

    public int getVersion()
    {
        return version;
    }

    public void setVersion( int version )
    {
        this.version = version;
    }

    public String getPeriodType()
    {
        return periodType;
    }

    public void setPeriodType( String periodType )
    {
        this.periodType = periodType;
    }

    public Vector getSections()
    {
        return sections;
    }

    public void setSections( Vector sections )
    {
        this.sections = sections;
    }

    public void serialize( DataOutputStream dout )
        throws IOException
    {
        dout.writeInt( this.getId() );
        dout.writeUTF( this.getName() );
        dout.writeInt( this.getVersion() );
        dout.writeUTF( this.getPeriodType() );

        if ( sections != null )
        {
            dout.writeInt( sections.size() );

            for ( int i = 0; i < sections.size(); i++ )
            {
                Section section = (Section) sections.elementAt( i );
                section.serialize( dout );
            }
        }
        else
        {
            dout.writeInt( 0 );
        }

    }

    public void deSerialize( DataInputStream din )
        throws IOException
    {

        this.setId( din.readInt() );
        this.setName( din.readUTF() );
        this.setVersion( din.readInt() );
        this.setPeriodType( din.readUTF() );

        int sectionSize = din.readInt();

        for ( int i = 0; i < sectionSize; i++ )
        {
            Section section = new Section();
            section.deSerialize( din );
            sections.addElement( section );
        }
    }
}
