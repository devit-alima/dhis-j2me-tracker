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

public class Program
    extends Model
{
    private int version;

    // multiple event with registration: 1
    // single event with registration: 2
    // single event without registration: 3
    private int type;

    private String dateOfEnrollmentDescription = "Date of Enrollment";

    private String dateOfIncidentDescription = "Date of Incident";

    private String trackedEntityName = "Tracked Entity";

    private Vector programStages = new Vector();

    private Vector programAttributes = new Vector();

    private String relationshipText;

    private int relatedProgramId;
    
    private int relationshipType;

    public Vector getProgramStages()
    {
        return programStages;
    }

    public void setProgramStages( Vector programStages )
    {
        this.programStages = programStages;
    }

    public int getVersion()
    {
        return version;
    }

    public void setVersion( int version )
    {
        this.version = version;
    }

    public int getType()
    {
        return type;
    }

    public void setType( int type )
    {
        this.type = type;
    }

    public String getDateOfEnrollmentDescription()
    {
        return dateOfEnrollmentDescription;
    }

    public void setDateOfEnrollmentDescription( String dateOfEnrollmentDescription )
    {
        this.dateOfEnrollmentDescription = dateOfEnrollmentDescription;
    }

    public String getDateOfIncidentDescription()
    {
        return dateOfIncidentDescription;
    }

    public void setDateOfIncidentDescription( String dateOfIncidentDescription )
    {
        this.dateOfIncidentDescription = dateOfIncidentDescription;
    }

    public String getTrackedEntityName()
    {
        return trackedEntityName;
    }

    public void setTrackedEntityName( String trackedEntityName )
    {
        this.trackedEntityName = trackedEntityName;
    }

    public Vector getProgramAttributes()
    {
        return programAttributes;
    }

    public void setProgramAttributes( Vector programAttributes )
    {
        this.programAttributes = programAttributes;
    }

    public String getRelationshipText()
    {
        return relationshipText;
    }

    public void setRelationshipText( String relationshipText )
    {
        this.relationshipText = relationshipText;
    }

    public int getRelatedProgramId()
    {
        return relatedProgramId;
    }

    public void setRelatedProgramId( int relatedProgramId )
    {
        this.relatedProgramId = relatedProgramId;
    }
    
    public int getRelationshipType()
    {
        return relationshipType;
    }
    
    public void setRelationshipType( int relationshipType )
    {
        this.relationshipType = relationshipType;
    }

    public void serialize( DataOutputStream dout )
        throws IOException
    {
        super.serialize( dout );
        dout.writeInt( getVersion() );
        dout.writeInt( getType() );
        dout.writeUTF( getDateOfEnrollmentDescription() );
        dout.writeUTF( getDateOfIncidentDescription() );
        dout.writeUTF( getTrackedEntityName() );

        dout.writeInt( programStages.size() );
        for ( int i = 0; i < programStages.size(); i++ )
        {
            ProgramStage prorgamStage = (ProgramStage) programStages.elementAt( i );
            prorgamStage.serialize( dout );
        }

        dout.writeInt( programAttributes.size() );
        for ( int i = 0; i < programAttributes.size(); i++ )
        {
            PatientAttribute pa = (PatientAttribute) programAttributes.elementAt( i );
            pa.serialize( dout );
        }

        dout.writeUTF( getRelationshipText() );
        dout.writeInt( getRelatedProgramId() );
        dout.writeInt( getRelationshipType() );
    }

    public void deSerialize( DataInputStream din )
        throws IOException
    {
        super.deSerialize( din );
        this.setVersion( din.readInt() );
        this.setType( din.readInt() );
        this.setDateOfEnrollmentDescription( din.readUTF() );
        this.setDateOfIncidentDescription( din.readUTF() );
        this.setTrackedEntityName( din.readUTF() );

        int size = din.readInt();
        for ( int i = 0; i < size; i++ )
        {
            ProgramStage prStg = new ProgramStage();
            prStg.deSerialize( din );
            this.programStages.addElement( prStg );
        }

        int programAttSize = din.readInt();
        for ( int i = 0; i < programAttSize; i++ )
        {
            PatientAttribute pa = new PatientAttribute();
            pa.deSerialize( din );
            this.programAttributes.addElement( pa );
        }

        this.setRelationshipText( din.readUTF() );
        this.setRelatedProgramId( din.readInt() );
        this.setRelationshipType( din.readInt() );
    }
}
