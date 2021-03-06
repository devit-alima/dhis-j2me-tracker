package org.hisp.dhis.mobile.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RelationshipType
    extends Model
{
    private int id;

    private String aIsToB;

    private String bIsToA;

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public String getAIsToB()
    {
        return aIsToB;
    }

    public void setAIsToB( String aIsToB )
    {
        this.aIsToB = aIsToB;
    }

    public String getBIsToA()
    {
        return bIsToA;
    }

    public void setBIsToA( String bIsToA )
    {
        this.bIsToA = bIsToA;
    }

    public void serialize( DataOutputStream dataOutputStream )
        throws IOException
    {
        dataOutputStream.writeInt( this.id );
        dataOutputStream.writeUTF( getName() );
        dataOutputStream.writeUTF( this.aIsToB );
        dataOutputStream.writeUTF( this.bIsToA );
    }

    public void deSerialize( DataInputStream dataInputStream )
        throws IOException
    {
        this.id = dataInputStream.readInt();
        setName( dataInputStream.readUTF() );
        this.aIsToB = dataInputStream.readUTF();
        this.bIsToA = dataInputStream.readUTF();
    }

    public void serializeVersion2_8( DataOutputStream dataOutputStream )
        throws IOException
    {
        // TODO Auto-generated method stub

    }

    public void serializeVersion2_9( DataOutputStream dataOutputStream )
        throws IOException
    {
        // TODO Auto-generated method stub

    }

    public void serializeVersion2_10( DataOutputStream dataOutputStream )
        throws IOException
    {
        // TODO Auto-generated method stub
    }
}
