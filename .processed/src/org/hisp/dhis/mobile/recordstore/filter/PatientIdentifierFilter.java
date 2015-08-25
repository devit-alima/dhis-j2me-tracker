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
package org.hisp.dhis.mobile.recordstore.filter;

import javax.microedition.rms.RecordFilter;

import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.model.PatientIdentifier;
import org.hisp.dhis.mobile.util.SerializationUtil;

public class PatientIdentifierFilter
    implements RecordFilter
{
    private static final String CLASS_TAG = "PatientIdentifierFilter";
    
    private PatientIdentifier patientIdentifier;

    public PatientIdentifierFilter( PatientIdentifier patientIdentifier )
    {
        this.patientIdentifier = patientIdentifier;
    }

    public boolean matches( byte[] suspect )
    {
        if ( patientIdentifier == null )
        {
            return false;
        }
        else
        {
            try
            {
                PatientIdentifier suspectIdentifier = new PatientIdentifier();
                SerializationUtil.deSerialize( suspectIdentifier, suspect );
                return patientIdentifier.getIdentifierType() == suspectIdentifier.getIdentifierType();
            }
            catch ( Exception e )
            {
                LogMan.log( "RMS," + CLASS_TAG, e );
                e.printStackTrace();
            }
        }
        return false;
    }

    public PatientIdentifier getPatientIdentifier()
    {
        return patientIdentifier;
    }

    public void setPatientIdentifier( PatientIdentifier patientIdentifier )
    {
        this.patientIdentifier = patientIdentifier;
    }

}
