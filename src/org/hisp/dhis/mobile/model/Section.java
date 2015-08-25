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

public class Section
    extends Model
{
    private Vector dataElementIds;

    private Vector dataElements;
    
    public Vector getDataElementIds()
    {
        return dataElementIds;
    }

    public void setDataElementIds( Vector dataElementIds )
    {
        this.dataElementIds = dataElementIds;
    }

    public Vector getDataElements()
    {
        return dataElements;
    }

    public void setDataElements( Vector dataElements )
    {
        this.dataElements = dataElements;
    }

    public void deSerialize( DataInputStream din )
        throws IOException
    {
        this.setId( din.readInt() );
        this.setName( din.readUTF() );

        int numbOfDataElement = din.readInt();
        
        if ( numbOfDataElement > 0 )
        {
            dataElementIds = new Vector();
            for ( int i = 0; i < numbOfDataElement; i++ )
            {
                dataElementIds.addElement( Integer.valueOf(din.readInt()+"") );
            }
        }
        else
        {
        }
    }

    public void serialize( DataOutputStream dos )
        throws IOException
    {
        dos.writeInt( this.getId() );
        dos.writeUTF( this.getName() );

        dos.writeInt( dataElementIds.size() );
        for ( int i = 0; i < dataElementIds.size(); i++ )
        {
            Integer deId =  (Integer) dataElementIds.elementAt( i );
            dos.writeInt( deId.intValue() );
        }
        dos.flush();
    }

}
