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
import java.util.Date;
import java.util.Vector;

public class Beneficiary
    implements DataStreamSerializable
{

    private int id;

    private String firstName, middleName, lastName;

    private int age;

    private PatientAttribute groupByAttribute;

    private Vector attsValues;

    private Vector identifiers;

    private String gender;

    private Date birthDate;

    private String bloodGroup;

    private Date registrationDate;

    private Character dobType;

    public Beneficiary()
    {
    }

    public String getGender()
    {
        return gender;
    }

    public void setGender( String gender )
    {
        this.gender = gender;
    }

    public Date getBirthDate()
    {
        return birthDate;
    }

    public void setBirthDate( Date birthDate )
    {
        this.birthDate = birthDate;
    }

    public String getBloodGroup()
    {
        return bloodGroup;
    }

    public void setBloodGroup( String bloodGroup )
    {
        this.bloodGroup = bloodGroup;
    }

    public Date getRegistrationDate()
    {
        return registrationDate;
    }

    public void setRegistrationDate( Date registrationDate )
    {
        this.registrationDate = registrationDate;
    }

    public Character getDobType()
    {
        return dobType;
    }

    public void setDobType( Character dobType )
    {
        this.dobType = dobType;
    }

    public Vector getPatientIdentifier()
    {
        return identifiers;
    }

    public void setPatientIdentifier( Vector patientIdentifier )
    {
        this.identifiers = patientIdentifier;
    }

    public String getFullName()
    {
        boolean space = false;
        String name = "";

        if ( firstName != null && firstName.length() != 0 )
        {
            name = firstName;
            space = true;
        }
        if ( middleName != null && middleName.length() != 0 )
        {
            if ( space )
                name += " ";
            name += middleName;
            space = true;
        }
        if ( lastName != null && lastName.length() != 0 )
        {
            if ( space )
                name += " ";
            name += lastName;
        }
        return name;
    }

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public int getAge()
    {
        return age;
    }

    public void setAge( int age )
    {
        this.age = age;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName( String firstName )
    {
        this.firstName = firstName;
    }

    public String getMiddleName()
    {
        return middleName;
    }

    public void setMiddleName( String middleName )
    {
        this.middleName = middleName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName( String lastName )
    {
        this.lastName = lastName;
    }

    public Vector getAttsValues()
    {
        if ( attsValues == null )
        {
            this.attsValues = new Vector();
            return attsValues;
        }
        else
        {
            return attsValues;
        }
    }

    public void setAttsValues( Vector attsValues )
    {
        this.attsValues = attsValues;
    }

    public PatientAttribute getGroupByAttribute()
    {
        if ( this.groupByAttribute != null )
        {
            return groupByAttribute;
        }
        else
        {
            return null;
        }

    }

    public void setGroupByAttribute( PatientAttribute groupByAttribute )
    {
        this.groupByAttribute = groupByAttribute;
    }

    public void deSerialize( DataInputStream din )
        throws IOException
    {
        this.setId( din.readInt() );
        this.setFirstName( din.readUTF() );
        this.setMiddleName( din.readUTF() );
        this.setLastName( din.readUTF() );
        this.setAge( din.readInt() );
        if ( din.readBoolean() )
        {
            this.setGender( din.readUTF() );
        }
        else
        {
            this.setGender( null );
        }

        if ( din.readBoolean() )
        {
            char dobTypeDeserialized = din.readChar();
            this.setDobType( new Character( dobTypeDeserialized ) );
        }
        else
        {
            this.setDobType( null );
        }

        if ( din.readBoolean() )
        {
            this.setBirthDate( new Date( din.readLong() ) );
        }
        else
        {
            this.setBirthDate( null );
        }

        if ( din.readBoolean() )
        {
            this.setBloodGroup( din.readUTF() );
        }
        else
        {
            this.setBloodGroup( null );
        }

        if ( din.readBoolean() )
        {
            this.setRegistrationDate( new Date( din.readLong() ) );
        }
        else
        {
            this.setRegistrationDate( null );
        }

        if ( din.readBoolean() )
        {
            this.groupByAttribute = new PatientAttribute();
            this.groupByAttribute.deSerialize( din );
        }

        this.attsValues = new Vector();
        int attsNumb = din.readInt();
        for ( int j = 0; j < attsNumb; j++ )
        {
            attsValues.addElement( din.readUTF() );
        }

        int numbIdentifiers = din.readInt();

        if ( numbIdentifiers > 0 )
        {
            Vector identifiersVector = new Vector();
            for ( int i = 0; i < numbIdentifiers; i++ )
            {
                PatientIdentifier id = new PatientIdentifier();
                id.deSerialize( din );
                identifiersVector.addElement( id );
            }
            this.setPatientIdentifier( identifiersVector );
        }
        else
        {
            this.setPatientIdentifier( new Vector() );
        }

    }

    public void serialize( DataOutputStream dout )
        throws IOException
    {
        dout.writeInt( getId() );
        dout.writeUTF( getFirstName() );
        dout.writeUTF( getMiddleName() );
        dout.writeUTF( getLastName() );
        dout.writeInt( getAge() );

        // Write static attributes if it is required (gender, dobtype,
        // birthdate, bloodgroup, registrationdate)
        if ( gender != null )
        {
            dout.writeBoolean( true );
            dout.writeUTF( gender );
        }
        else
        {
            dout.writeBoolean( false );
        }

        if ( dobType != null )
        {
            dout.writeBoolean( true );
            dout.writeChar( dobType.hashCode() );
        }
        else
        {
            dout.writeBoolean( false );
        }

        if ( birthDate != null )
        {
            dout.writeBoolean( true );
            dout.writeLong( birthDate.getTime() );
        }
        else
        {
            dout.writeBoolean( false );
        }

        if ( bloodGroup != null )
        {
            dout.writeBoolean( true );
            dout.writeUTF( bloodGroup );
        }
        else
        {
            dout.writeBoolean( false );
        }

        if ( registrationDate != null )
        {
            dout.writeBoolean( true );
            dout.writeLong( registrationDate.getTime() );
        }
        else
        {
            dout.writeBoolean( false );
        }
        // End

        if ( getGroupByAttribute() == null )
        {
            dout.writeBoolean( false );
        }
        else
        {
            dout.writeBoolean( true );
            groupByAttribute.serialize( dout );
        }

        int numAtt = attsValues.size();
        dout.writeInt( numAtt );
        for ( int i = 0; i < numAtt; i++ )
        {
            dout.writeUTF( (String) attsValues.elementAt( i ) );
        }

        if ( identifiers != null )
        {
            int numbId = identifiers.size();
            dout.writeInt( numbId );
            for ( int i = 0; i < numbId; i++ )
            {
                ((PatientIdentifier) identifiers.elementAt( i )).serialize( dout );
            }
        }
        else
        {
            dout.writeInt( 0 );
        }
    }
}
