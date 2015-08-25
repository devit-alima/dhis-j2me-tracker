package org.hisp.dhis.mobile.model;

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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

/**
 * @author Nguyen Kim Lai
 * 
 * @version Patient.java 1:26:50 PM Jan 22, 2013 $
 */
public class Patient
    implements DataStreamSerializable
{
    private int id;

    private String organisationUnitName;

    private String trackedEntityName;

    private Vector patientAttributes = new Vector();

    private Vector enrollmentPrograms = new Vector();

    private Vector completedPrograms = new Vector();

    private Vector relationships = new Vector();

    private int idToAddRelative;
    
    private int relTypeIdToAdd;
    
    private Relationship enrollmentRelationship;

    public Patient()
    {

    }

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public Vector getPatientAttValues()
    {
        return patientAttributes;
    }

    public void setPatientAttValues( Vector patientAttValues )
    {
        this.patientAttributes = patientAttValues;
    }

    public Vector getRelationships()
    {
        return relationships;
    }

    public void setRelationships( Vector relationships )
    {
        this.relationships = relationships;
    }

    public Vector getEnrollmentPrograms()
    {
        return enrollmentPrograms;
    }

    public void setEnrollmentPrograms( Vector enrollmentPrograms )
    {
        this.enrollmentPrograms = enrollmentPrograms;
    }

    public boolean isEnrolledIn( Program program )
    {
        for ( int i = 0; i < enrollmentPrograms.size(); i++ )
        {
            ProgramInstance programInstance = (ProgramInstance) enrollmentPrograms.elementAt( i );
            if ( programInstance.getProgramId() == program.getId() )
            {
                return true;
            }
        }
        return false;
    }

    public String getOrganisationUnitName()
    {
        return organisationUnitName;
    }

    public void setOrganisationUnitName( String organisationUnitName )
    {
        this.organisationUnitName = organisationUnitName;
    }

    public String getTrackedEntityName()
    {
        return trackedEntityName;
    }

    public void setTrackedEntityName( String trackedEntityName )
    {
        this.trackedEntityName = trackedEntityName;
    }

    public Vector getCompletedPrograms()
    {
        return completedPrograms;
    }

    public void setCompletedPrograms( Vector completedPrograms )
    {
        this.completedPrograms = completedPrograms;
    }

    public int getIdToAddRelative()
    {
        return idToAddRelative;
    }

    public void setIdToAddRelative( int idToAddRelative )
    {
        this.idToAddRelative = idToAddRelative;
    }
    
    public int getRelTypeIdToAdd()
    {
        return relTypeIdToAdd;
    }
    
    public void setRelTypeIdToAdd( int relTypeIdToAdd )
    {
        this.relTypeIdToAdd = relTypeIdToAdd;
    }
    
    public Relationship getEnrollmentRelationship()
    {
        return enrollmentRelationship;
    }
    
    public void setEnrollmentRelationship( Relationship enrollmentRelationship )
    {
        this.enrollmentRelationship = enrollmentRelationship;
    }

    public String getDisplayInListAttributeValues()
    {
        String name = "";
        for ( int i = 0; i < getPatientAttValues().size(); i++ )
        {
            PatientAttribute patientAttribute = (PatientAttribute) getPatientAttValues().elementAt( i );
            if ( patientAttribute.isDisplayedInList() )
            {
                name += patientAttribute.getValue() + " ";
            }
        }
        return name.trim();
    }

    public void deSerialize( DataInputStream din )
        throws IOException
    {
        this.setId( din.readInt() );
        this.setOrganisationUnitName( din.readUTF() );
        this.setTrackedEntityName( din.readUTF() );

        // Read attributes
        int attsNumb = din.readInt();
        if ( attsNumb > 0 )
        {
            this.patientAttributes = new Vector();
            for ( int j = 0; j < attsNumb; j++ )
            {
                PatientAttribute patientAttribute = new PatientAttribute();
                patientAttribute.deSerialize( din );
                patientAttributes.addElement( patientAttribute );
            }
        }

        // Read enrollment programs
        int numbEnrollmentPrograms = din.readInt();
        if ( numbEnrollmentPrograms > 0 )
        {
            Vector enrollmentProgramsVector = new Vector();
            for ( int i = 0; i < numbEnrollmentPrograms; i++ )
            {
                ProgramInstance programInstance = new ProgramInstance();
                programInstance.deSerialize( din );
                enrollmentProgramsVector.addElement( programInstance );
            }
            this.setEnrollmentPrograms( enrollmentProgramsVector );
            enrollmentProgramsVector = null;
        }

        // Read completed programs
        int numbCompletedPrograms = din.readInt();
        if ( numbCompletedPrograms > 0 )
        {
            Vector completedProgramsVector = new Vector();
            for ( int i = 0; i < numbCompletedPrograms; i++ )
            {
                ProgramInstance programInstance = new ProgramInstance();
                programInstance.deSerialize( din );
                completedProgramsVector.addElement( programInstance );
            }
            this.setCompletedPrograms( completedProgramsVector );
            completedProgramsVector = null;
        }

        // Read relationship
        int numbRelationships = din.readInt();
        if ( numbRelationships > 0 )
        {
            Vector relationshipsVector = new Vector();
            for ( int i = 0; i < numbRelationships; i++ )
            {
                Relationship relationship = new Relationship();
                relationship.deSerialize( din );
                relationshipsVector.addElement( relationship );
            }
            this.setRelationships( relationshipsVector );
            relationshipsVector = null;
        }
        this.setIdToAddRelative( din.readInt() );
        this.setRelTypeIdToAdd( din.readInt() );
        
        int numEnrollmentRelationships = din.readInt();
        if(numEnrollmentRelationships > 0)
        {
            Relationship enrollmentRelationship = new Relationship();
            enrollmentRelationship.deSerialize( din );
            this.setEnrollmentRelationship( enrollmentRelationship );
        }
    }

    public void serialize( DataOutputStream dout )
        throws IOException
    {
        dout.writeInt( this.getId() );
        dout.writeUTF( this.getOrganisationUnitName() );
        dout.writeUTF( this.getTrackedEntityName() );

        // Write Attribute
        dout.writeInt( patientAttributes.size() );
        for ( int i = 0; i < patientAttributes.size(); i++ )
        {
            PatientAttribute patientAttribute = (PatientAttribute) patientAttributes.elementAt( i );
            patientAttribute.serialize( dout );
        }

        // Write Enrolled Programs
        dout.writeInt( enrollmentPrograms.size() );
        for ( int i = 0; i < enrollmentPrograms.size(); i++ )
        {
            ((ProgramInstance) enrollmentPrograms.elementAt( i )).serialize( dout );
        }

        // Write Completed Programs
        dout.writeInt( completedPrograms.size() );
        for ( int i = 0; i < completedPrograms.size(); i++ )
        {
            ((ProgramInstance) completedPrograms.elementAt( i )).serialize( dout );
        }

        // Write Relationships
        dout.writeInt( relationships.size() );
        for ( int i = 0; i < relationships.size(); i++ )
        {
            ((Relationship) relationships.elementAt( i )).serialize( dout );
        }
        dout.writeInt( this.getIdToAddRelative() );
        dout.writeInt( this.getRelTypeIdToAdd() );
        if(enrollmentRelationship!=null)
        {
            dout.writeInt( 1 );
            enrollmentRelationship.serialize( dout );
        }
        else
        {
            dout.writeInt( 0 );
        }

    }
}
