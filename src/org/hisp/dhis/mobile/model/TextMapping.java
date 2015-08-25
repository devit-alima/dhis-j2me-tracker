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

public class TextMapping
    implements DataStreamSerializable
{
    private Hashtable textTable;

    public String get( Short key )
    {
        if ( this.textTable != null )
        {
            return (String) textTable.get( String.valueOf( key ) );
        }
        else
        {
            return null;
        }
    }

    public void setTextTable( Hashtable textTable )
    {
        this.textTable = textTable;
    }

    public void serialize( DataOutputStream dataOutputStream )
        throws IOException
    {
        if ( textTable == null )
        {
            dataOutputStream.writeInt( 0 );
            return;
        }

        dataOutputStream.writeShort( textTable.size() );

        Enumeration keys = textTable.keys();
        while ( keys.hasMoreElements() )
        {
            String key = (String) keys.nextElement();
            String value = (String) textTable.get( key );
            dataOutputStream.writeShort( Short.parseShort( key ) );
            dataOutputStream.writeUTF( value );
        }
    }

    public void deSerialize( DataInputStream dataInputStream )
        throws IOException
    {
        short size = dataInputStream.readShort();

        Hashtable table = new Hashtable( size );

        if ( size == 0 )
        {
            return;
        }

        for ( int i = 0; i < size; i++ )
        {
            table.put( "" + dataInputStream.readShort(), dataInputStream.readUTF() );
        }

        this.textTable = table;
    }

}
