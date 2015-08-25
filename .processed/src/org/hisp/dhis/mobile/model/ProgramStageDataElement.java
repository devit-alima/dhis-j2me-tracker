/*
 * Copyright (c) 2004-2012, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.mobile.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Nguyen Kim Lai
 * 
 * @version ProgramStageDataElement.java 3:16:41 PM Jan 30, 2013 $
 */
public class ProgramStageDataElement
    extends Model
{
    public static final String VALUE_TYPE_STRING = "string";

    public static final String VALUE_TYPE_INT = "int";

    public static final String VALUE_TYPE_NUMBER = "number";

    public static final String VALUE_TYPE_USER_NAME = "username";

    public static final String VALUE_TYPE_POSITIVE_INT = "positiveNumber";

    public static final String VALUE_TYPE_NEGATIVE_INT = "negativeNumber";

    public static final String VALUE_TYPE_TRUE_ONLY = "trueOnly";

    public static final String VALUE_TYPE_TEXT = "text";

    public static final String VALUE_TYPE_LONG_TEXT = "longText";

    public static final String VALUE_TYPE_BOOL = "bool";

    public static final String VALUE_TYPE_DATE = "date";

    private String clientVersion;

    private String type;

    private String numberType;

    private boolean compulsory;

    private ModelList categoryOptionCombos;

    private OptionSet optionSet;

    private String value;

    private String violatedMessage;

    public void serialize( DataOutputStream dout )
        throws IOException
    {
        super.serialize( dout );
        dout.writeUTF( this.getType() );
        if ( this.numberType != null )
        {
            dout.writeBoolean( true );
            dout.writeUTF( this.numberType );
        }
        else
        {
            dout.writeBoolean( false );
        }
        dout.writeBoolean( this.isCompulsory() );
        this.categoryOptionCombos.serialize( dout );
        if( this.optionSet == null )
        {
            dout.writeBoolean( false );
        }
        else
        {
            dout.writeBoolean( true );
            this.optionSet.serialize( dout );
        }
        if ( this.getValue() == null )
        {
            dout.writeBoolean( false );
        }
        else
        {
            dout.writeBoolean( true );
            dout.writeUTF( this.getValue() );
        }
    }

    public void deSerialize( DataInputStream din )
        throws IOException
    {
        super.deSerialize( din );
        this.setType( din.readUTF() );
        if ( din.readBoolean() )
        {
            this.setNumberType( din.readUTF() );
        }
        else
        {
            this.setNumberType( null );
        }
        this.setCompulsory( din.readBoolean() );
        this.categoryOptionCombos = new ModelList();
        this.categoryOptionCombos.deSerialize( din );
        if ( din.readBoolean() == false )
        {
            this.optionSet = null;
        }
        else
        {
            this.optionSet = new OptionSet();
            this.optionSet.deSerialize( din );
        }
        
        if ( din.readBoolean() == false )
        {
            this.setValue( null );
        }
        else
        {
            this.setValue( din.readUTF() );
        }
    }

    public String getClientVersion()
    {
        return clientVersion;
    }

    public void setClientVersion( String clientVersion )
    {
        this.clientVersion = clientVersion;
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

    public boolean isCompulsory()
    {
        return compulsory;
    }

    public void setCompulsory( boolean compulsory )
    {
        this.compulsory = compulsory;
    }

    public ModelList getCategoryOptionCombos()
    {
        return categoryOptionCombos;
    }

    public void setCategoryOptionCombos( ModelList categoryOptionCombos )
    {
        this.categoryOptionCombos = categoryOptionCombos;
    }

    public OptionSet getOptionSet()
    {
        return optionSet;
    }

    public void setOptionSet( OptionSet optionSet )
    {
        this.optionSet = optionSet;
    }

    public String getNumberType()
    {
        return numberType;
    }

    public void setNumberType( String numberType )
    {
        this.numberType = numberType;
    }

    public String getViolatedMessage()
    {
        return violatedMessage;
    }

    public void setViolatedMessage( String violatedMessage )
    {
        this.violatedMessage = violatedMessage;
    }
}
