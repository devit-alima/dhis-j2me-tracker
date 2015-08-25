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
import java.util.Date;
import java.util.Vector;

/**
 * @author Nguyen Kim Lai
 * 
 * @version MobileModel.java 12:59:01 PM Jul 1, 2013 $
 */
public class MobileModel
    extends Model
{
    private Vector programs;

    private Date serverCurrentDate;
    
    private Vector relationshipTypes;
    
    public void serialize( DataOutputStream din )
        throws IOException
    {
    
    }

    public void deSerialize( DataInputStream din )
        throws IOException
    {
        this.programs = new Vector();
        int numbProgram = din.readInt();
        for ( int i = 0; i < numbProgram; i++ )
        {
            Program program = new Program();
            program.deSerialize( din );
            programs.addElement( program );
        }
        
        this.serverCurrentDate = new Date( din.readLong() );
        
        this.relationshipTypes = new Vector();
        int numRelTypes =  din.readInt();
        for(int i=0; i<numRelTypes; i++)
        {
            RelationshipType relType = new RelationshipType();
            relType.deSerialize( din );
            relationshipTypes.addElement( relType );
        }
    }

    public Vector getPrograms()
    {
        return programs;
    }

    public void setPrograms( Vector programs )
    {
        this.programs = programs;
    }

    public Date getServerCurrentDate()
    {
        return serverCurrentDate;
    }

    public void setServerCurrentDate( Date serverCurrentDate )
    {
        this.serverCurrentDate = serverCurrentDate;
    }
    
    public Vector getRelationshipTypes()
    {
        return relationshipTypes;
    }
    
    public void setRelationshipTypes(Vector relationshipTypes)
    {
        this.relationshipTypes = relationshipTypes;
    }
}
