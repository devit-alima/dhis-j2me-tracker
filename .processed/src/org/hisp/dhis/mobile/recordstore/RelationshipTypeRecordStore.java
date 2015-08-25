package org.hisp.dhis.mobile.recordstore;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;

import org.hisp.dhis.mobile.model.OrgUnit;
import org.hisp.dhis.mobile.model.Program;
import org.hisp.dhis.mobile.model.RelationshipType;
import org.hisp.dhis.mobile.util.SerializationUtil;

public class RelationshipTypeRecordStore
{
    public static final String RELATIONSHIP_TYPE_DB = "RELATIONSHIP_TYPE";

    public static boolean saveRelationshipTypes( Vector relTypeVector )
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        for ( int i = 0; i < relTypeVector.size(); i++ )
        {
            RelationshipType relType = (RelationshipType) relTypeVector.elementAt( i );
            if ( !saveRelationshipType( relType ) )
            {
                return false;
            }
        }
        return true;
    }
    
    public static boolean saveRelationshipType( RelationshipType relType )
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        RecordStore recordStore = null;
        try
        {
            recordStore = RecordStore.openRecordStore( RELATIONSHIP_TYPE_DB, true );
            byte[] bite = SerializationUtil.serialize( relType );
            recordStore.addRecord( bite, 0, bite.length );
        }
        finally
        {
            recordStore.closeRecordStore();
            System.gc();
        }
        return true;
    }

    public static Vector loadAllRelationshipType()
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        Vector relTypeVector = new Vector();
        try
        {
            RecordStore recordStore = RecordStore.openRecordStore( RELATIONSHIP_TYPE_DB, true );
            RecordEnumeration recordEnum = recordStore.enumerateRecords( null, null, false );
            while ( recordEnum.hasNextElement() )
            {
                RelationshipType relType = new RelationshipType();
                SerializationUtil.deSerialize( relType, recordEnum.nextRecord() );
                relTypeVector.addElement( relType );
            }
            recordEnum.destroy();
            recordStore.closeRecordStore();
        }
        finally
        {
            System.gc();
        }
        return relTypeVector;
    }

    public static void deleteAllRelationshipType()
        throws RecordStoreNotFoundException, RecordStoreException
    {
        RecordStore recordStore = RecordStore.openRecordStore( RELATIONSHIP_TYPE_DB, true );
        if ( recordStore.getSize() > 0 )
        {
            recordStore.closeRecordStore();
            RecordStore.deleteRecordStore( RELATIONSHIP_TYPE_DB );
        }
    }
}
