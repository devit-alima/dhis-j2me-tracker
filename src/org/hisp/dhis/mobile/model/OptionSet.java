package org.hisp.dhis.mobile.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

public class OptionSet
    extends Model
    implements DataStreamSerializable
{
    private Vector options = new Vector();

    public Vector getOptions()
    {
        return options;
    }

    public void setOptions( Vector options )
    {
        this.options = options;
    }

    public void serialize( DataOutputStream dout )
        throws IOException
    {
        dout.writeInt( this.getId() );
        dout.writeUTF( this.getName() );
        dout.writeInt( options.size() );

        for ( int i = 0; i < options.size(); i++ )
        {
            String option = (String) options.elementAt( i );
            dout.writeUTF( option );
        }
    }

    public void deSerialize( DataInputStream din )
        throws IOException
    {
        this.setId( din.readInt() );
        this.setName( din.readUTF() );
        int optionSize = din.readInt();
        if ( optionSize > 0 )
        {
            for ( int i = 0; i < optionSize; i++ )
            {
                String option = din.readUTF();
                options.addElement( option );
            }
        }
    }
}
