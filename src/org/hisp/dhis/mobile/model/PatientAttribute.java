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
package org.hisp.dhis.mobile.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

public class PatientAttribute
    implements DataStreamSerializable // Comparable
{

    private String name;

    private String value;

    private String type;

    private boolean isMandatory;

    private boolean isDisplayedInList = false;

    private OptionSet optionSet;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public PatientAttribute()
    {
    }

    public PatientAttribute( String name, String value )
    {
        super();
        this.name = name;
        this.value = value;
    }

    // -------------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------------

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public boolean isMandatory()
    {
        return isMandatory;
    }

    public void setMandatory( boolean isMandatory )
    {
        this.isMandatory = isMandatory;
    }

    public OptionSet getOptionSet()
    {
        return optionSet;
    }

    public void setOptionSet( OptionSet optionSet )
    {
        this.optionSet = optionSet;
    }

    public boolean isDisplayedInList()
    {
        return isDisplayedInList;
    }

    public void setDisplayedInList( boolean isDisplayedInList )
    {
        this.isDisplayedInList = isDisplayedInList;
    }

    public boolean equals( PatientAttribute patientAttribute )
    {
        if ( this.name.equals( patientAttribute.getName() ) && this.value.equals( patientAttribute.getValue() ) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public int compareTo( Object att )
    {
        return this.value.compareTo( ((PatientAttribute) att).getValue() );
    }

    public void serialize( DataOutputStream dout )
        throws IOException
    {
        dout.writeUTF( name );
        dout.writeUTF( value );
        dout.writeUTF( type );
        dout.writeBoolean( isMandatory );
        dout.writeBoolean( isDisplayedInList );

        int optionSize = (optionSet == null || optionSet.getOptions() == null) ? 0 : optionSet.getOptions().size();
        
        dout.writeInt( optionSize );
        if ( optionSize > 0 )
        {
            optionSet.serialize( dout );
        }
    }

    public void deSerialize( DataInputStream din )
        throws IOException
    {
        this.setName( din.readUTF() );
        this.setValue( din.readUTF() );
        this.setType( din.readUTF() );
        this.setMandatory( din.readBoolean() );
        this.setDisplayedInList( din.readBoolean() );
        int optionSize = din.readInt();
        
        if( optionSize > 0 )
        {
            this.optionSet = new OptionSet();
            this.optionSet.deSerialize( din );
        }
    }

}
