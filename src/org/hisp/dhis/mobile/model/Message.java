package org.hisp.dhis.mobile.model;

/*
 * Copyright (c) 2004-2014, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Message
    implements DataStreamSerializable
{
    private String subject;

    private String text;

    private String lastSenderName;

    private Recipient recipient;

    public void serialize( DataOutputStream dout )
        throws IOException
    {

        dout.writeUTF( subject );

        dout.writeUTF( text );

        if ( lastSenderName != null )
        {
            dout.writeUTF( lastSenderName );
        }

        int recipientSize = (recipient == null || recipient.getUsers() == null) ? 0 : recipient.getUsers().size();

        dout.writeInt( recipientSize );
        if ( recipientSize > 0 )
        {
            recipient.serialize( dout );
        }

    }

    public void deSerialize( DataInputStream din )
        throws IOException
    {

        this.setSubject( din.readUTF() );
        this.setText( din.readUTF() );
        this.setLastSenderName( din.readUTF() );

        int recipientSize = din.readInt();
        if ( recipientSize > 0 )
        {
            this.recipient = new Recipient();
            recipient.deSerialize( din );
        }

    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject( String subject )
    {
        this.subject = subject;
    }

    public String getText()
    {
        return text;
    }

    public void setText( String text )
    {
        this.text = text;
    }

    public String getLastSenderName()
    {
        return lastSenderName;
    }

    public void setLastSenderName( String lastSenderName )
    {
        this.lastSenderName = lastSenderName;
    }

    public Recipient getRecipient()
    {
        return recipient;
    }

    public void setRecipient( Recipient recipient )
    {
        this.recipient = recipient;
    }

}
