package org.hisp.dhis.mobile.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;


public class DataSetList
    extends Model
{
    private Vector addedDataSets;

    private Vector deletedDataSets;

    private Vector modifiedDataSets;
    
    private Vector currentDataSets;

    public DataSetList()
    {
    }

    public Vector getAddedDataSets()
    {
        return addedDataSets;
    }

    public void setAddedDataSets( Vector addedDataSets )
    {
        this.addedDataSets = addedDataSets;
    }

    public Vector getDeletedDataSets()
    {
        return deletedDataSets;
    }

    public void setDeletedDataSets( Vector deletedDataSets )
    {
        this.deletedDataSets = deletedDataSets;
    }

    public Vector getModifiedDataSets()
    {
        return modifiedDataSets;
    }

    public void setModifiedDataSets( Vector modifiedDataSets )
    {
        this.modifiedDataSets = modifiedDataSets;
    }

    public Vector getCurrentDataSets()
    {
        return currentDataSets;
    }

    public void setCurrentDataSets( Vector currentDataSets )
    {
        this.currentDataSets = currentDataSets;
    }
    
    public void serialize( DataOutputStream dout )
        throws IOException
    {
        if ( addedDataSets != null )
        {
            dout.writeInt( addedDataSets.size() );
            for ( int i = 0; i <  addedDataSets.size(); i++){
                ((DataSet)addedDataSets.elementAt( i )).serialize( dout );
            }
        }else{
            dout.writeInt( 0 );
        }
        if ( deletedDataSets != null )
        {
            dout.writeInt( deletedDataSets.size() );
            for ( int i = 0; i <  deletedDataSets.size(); i++){
                ((DataSet)deletedDataSets.elementAt( i )).serialize( dout );
            }
        }else{
            dout.writeInt( 0 );
        }
        if ( modifiedDataSets != null )
        {
            dout.writeInt( modifiedDataSets.size() );
            for ( int i = 0; i <  modifiedDataSets.size(); i++){
                ((DataSet)modifiedDataSets.elementAt( i )).serialize( dout );
            }
        }else{
            dout.writeInt( 0 );
        }
        if ( currentDataSets != null )
        {
            dout.writeInt( currentDataSets.size() );
            for ( int i = 0; i <  currentDataSets.size(); i++){
                ((DataSet)currentDataSets.elementAt( i )).serialize( dout );
            }
        }else{
            dout.writeInt( 0 );
        }
    }
    
    public void deSerialize( DataInputStream dataInputStream )
        throws IOException
    {
        int temp = 0;
        temp = dataInputStream.readInt();
        if(temp > 0){
            addedDataSets = new Vector();
            for(int i = 0; i < temp; i++){
                DataSet dataSet = new DataSet();
                dataSet.deSerialize( dataInputStream );
                addedDataSets.addElement( dataSet );
            }
        }
        temp = dataInputStream.readInt();
        if(temp > 0){
            deletedDataSets = new Vector();
            for(int i = 0; i < temp; i++){
                DataSet dataSet = new DataSet();
                dataSet.deSerialize( dataInputStream );
                deletedDataSets.addElement( dataSet );
            }
        }
        temp = dataInputStream.readInt();
        if(temp > 0){
            modifiedDataSets = new Vector();
            for(int i = 0; i < temp; i++){
                DataSet dataSet = new DataSet();
                dataSet.deSerialize( dataInputStream );
                modifiedDataSets.addElement( dataSet );
            }
        }
        temp = dataInputStream.readInt();
        if(temp > 0){
            currentDataSets = new Vector();
            for(int i = 0; i < temp; i++){
                DataSet dataSet = new DataSet();
                dataSet.deSerialize( dataInputStream );
                currentDataSets.addElement( dataSet );
            }
        }
    }
}
