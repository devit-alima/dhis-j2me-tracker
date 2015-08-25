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
import java.util.Vector;

public class PatientIdentifierAndAttribute
    implements DataStreamSerializable
{
    private Vector patientIdentifiers;

    private Vector patientAttributes;

    public Vector getPatientIdentifiers()
    {
        return patientIdentifiers;
    }

    public void setPatientIdentifiers( Vector patientIdentifiers )
    {
        this.patientIdentifiers = patientIdentifiers;
    }

    public Vector getPatientAttributes()
    {
        return patientAttributes;
    }

    public void setPatientAttributes( Vector patientAttributes )
    {
        this.patientAttributes = patientAttributes;
    }

    public void serialize( DataOutputStream dout )
        throws IOException
    {
        if ( patientIdentifiers != null )
        {
            int numId = patientIdentifiers.size();
            dout.writeInt( numId );
            for ( int i = 0; i < numId; i++ )
            {
                ((PatientIdentifier) patientIdentifiers.elementAt( i )).serialize( dout );
            }
        }
        else
        {
            dout.writeInt( 0 );
        }

        if ( patientAttributes != null )
        {
            int numAt = patientAttributes.size();
            dout.writeInt( numAt );
            for ( int i = 0; i < numAt; i++ )
            {
                ((PatientAttribute) patientAttributes.elementAt( i )).serialize( dout );
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
            this.setPatientIdentifiers( identifiersVector );
        }
        else
        {
            this.setPatientIdentifiers( new Vector() );
        }

        int numbAttributes = din.readInt();
        if ( numbAttributes > 0 )
        {
            Vector attributesVector = new Vector();

            for ( int i = 0; i < numbAttributes; i++ )
            {
                PatientAttribute pa = new PatientAttribute();
                pa.deSerialize( din );
                attributesVector.addElement( pa );
            }
            this.setPatientAttributes( attributesVector );
        }
        else
        {
            this.setPatientAttributes( new Vector() );
        }

    }

}
