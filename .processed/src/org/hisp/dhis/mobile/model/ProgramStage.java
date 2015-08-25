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

public class ProgramStage
    extends Model
{
    private String reportDate;
    
    private String dueDate;

    private String reportDateDescription;

    private boolean isRepeatable;

    private boolean isCompleted;

    private boolean isSingleEvent;
    
    private int standardInterval;

    private Vector dataElements = new Vector();

    private Vector sections = new Vector();

    public Vector getSections()
    {
        return sections;
    }

    public void setSections( Vector sections )
    {
        this.sections = sections;
    }

    public boolean isRepeatable()
    {
        return isRepeatable;
    }

    public void setRepeatable( boolean isRepeatable )
    {
        this.isRepeatable = isRepeatable;
    }

    public boolean isCompleted()
    {
        return isCompleted;
    }

    public void setCompleted( boolean isCompleted )
    {
        this.isCompleted = isCompleted;
    }

    public Vector getDataElements()
    {
        return dataElements;
    }

    public void setDataElements( Vector dataElements )
    {
        this.dataElements = dataElements;
    }

    public boolean isSingleEvent()
    {
        return isSingleEvent;
    }

    public void setSingleEvent( boolean isSingleEvent )
    {
        this.isSingleEvent = isSingleEvent;
    }

    public String getReportDate()
    {
        return reportDate;
    }

    public void setReportDate( String reportDate )
    {
        this.reportDate = reportDate;
    }

    public String getReportDateDescription()
    {
        return reportDateDescription;
    }

    public void setReportDateDescription( String reportDateDescription )
    {
        this.reportDateDescription = reportDateDescription;
    }

    public String getDueDate()
    {
        return dueDate;
    }

    public void setDueDate( String dueDate )
    {
        this.dueDate = dueDate;
    }

    public int getStandardInterval()
    {
        return standardInterval;
    }

    public void setStandardInterval( int standardInterval )
    {
        this.standardInterval = standardInterval;
    }

    public void serialize( DataOutputStream dout )
        throws IOException
    {
        super.serialize( dout );

        if ( dueDate == null )
        {
            dueDate = "";
        }
        dout.writeUTF( this.reportDate );
        dout.writeUTF( "" );
        dout.writeUTF( this.dueDate );
        dout.writeBoolean( this.isRepeatable() );
        dout.writeInt( standardInterval );
        dout.writeBoolean( this.isCompleted() );
        dout.writeBoolean( this.isSingleEvent() );

        dout.writeInt( this.dataElements.size() );
        for ( int i = 0; i < this.dataElements.size(); i++ )
        {
            ProgramStageDataElement de = (ProgramStageDataElement) this.dataElements.elementAt( i );
            de.serialize( dout );
        }

        dout.writeInt( this.sections.size() );
        for ( int i = 0; i < this.sections.size(); i++ )
        {
            Section se = (Section) this.sections.elementAt( i );
            se.serialize( dout );
        }

    }

    public void deSerialize( DataInputStream din )
        throws IOException
    {
        super.deSerialize( din );
        this.setReportDate( din.readUTF() );
        this.setReportDateDescription( din.readUTF() );
        this.setDueDate( din.readUTF() );
        this.setRepeatable( din.readBoolean() );
        setStandardInterval( din.readInt() );
        this.setCompleted( din.readBoolean() );
        this.setSingleEvent( din.readBoolean() );
        int dataElementSize = din.readInt();
        if ( dataElementSize > 0 )
        {
            for ( int i = 0; i < dataElementSize; i++ )
            {
                ProgramStageDataElement de = new ProgramStageDataElement();
                de.deSerialize( din );
                this.dataElements.addElement( de );
            }
        }
        else
        {
        }

        int sectionSize = din.readInt();
        if ( sectionSize > 0 )
        {
            for ( int i = 0; i < sectionSize; i++ )
            {
                Section se = new Section();
                se.deSerialize( din );
                this.sections.addElement( se );
            }
        }
        else
        {
        }
    }
}
