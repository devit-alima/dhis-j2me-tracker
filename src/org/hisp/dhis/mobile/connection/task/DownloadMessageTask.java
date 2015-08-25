package org.hisp.dhis.mobile.connection.task;

import java.io.DataInputStream;
import java.util.Vector;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.Conversation;
import org.hisp.dhis.mobile.recordstore.MessageRecordStore;

public class DownloadMessageTask extends AbstractTask
{

    private int id;

    public DownloadMessageTask( int id )
    {
        super();
        this.id = id;
    }
    public void run()
    {
        NameBasedMIDlet nameBasedMIDlet = (NameBasedMIDlet) ConnectionManager.getDhisMIDlet();

        DataInputStream inputStream = null;

        Conversation conversation = new Conversation();

        try
        {

            inputStream = this.download( Integer.toString( id ), "id" );

            conversation.deSerialize( inputStream );

            Vector messageVector = conversation.getMessages();

            if ( messageVector != null )
            {
                MessageRecordStore.deleteRecordStore();
                MessageRecordStore.saveMessages( messageVector );

            }

            messageVector = null;

        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        finally
        {
            nameBasedMIDlet.getMessageDetailView().showView();
        }
        
    }

}
