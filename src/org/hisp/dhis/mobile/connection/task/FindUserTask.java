package org.hisp.dhis.mobile.connection.task;

import java.io.DataInputStream;
import java.util.Vector;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.Recipient;
import org.hisp.dhis.mobile.recordstore.UserRecordStore;

public class FindUserTask
    extends AbstractTask
{
    private static final String CLASS_TAG = "FindUserTask";

    private String keyword;

    private NameBasedMIDlet nameBasedMIDlet;

    public FindUserTask( String keyword )
    {
        super();
        this.keyword = keyword;
    }

    public void run()
    {
        this.nameBasedMIDlet = (NameBasedMIDlet) ConnectionManager.getDhisMIDlet();
        
        DataInputStream inputStream = null;
        Recipient recipient = new Recipient();
        
        try
        {
            inputStream = this.download( keyword, "name" );

            recipient.deSerialize( inputStream );

            Vector userVector = recipient.getUsers();

            if ( userVector != null )
            {
                UserRecordStore.deleteRecordStore();
                UserRecordStore.saveUsers( userVector );
            }

            userVector = null;

        }
        catch ( Exception e )
        {
            e.printStackTrace();
            LogMan.log( "Network," + CLASS_TAG, e );
        }
        finally
        {
            nameBasedMIDlet.getUserListView().showView();
        }

    }

}
