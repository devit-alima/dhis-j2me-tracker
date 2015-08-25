package org.hisp.dhis.mobile.log;

import org.hisp.dhis.mobile.file.SaveFileCallback;
import java.io.*;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Gauge;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

/**
 * 
 * @author Paul Mark Castillo
 */
public class RMSLogger
    extends Logger
    implements SaveFileCallback
{

    /**
     *
     */
    private static final String LOGS_DB = "LOGS";

    // TODO: Show Log notification
    /**
     *
     */
    RecordStore rs;

    /**
     *
     */
    public RMSLogger()
    {
        System.out.println( "[LOG] Opening RMS Logger..." );
        openRecordDB();
        setInitialized( true );
    }

    /**
     *
     */
    public void openRecordDB()
    {
        try
        {
            rs = RecordStore.openRecordStore( LOGS_DB, true, RecordStore.AUTHMODE_ANY, true );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public void closeRecordDB()
    {
        try
        {
            if ( rs != null )
            {
                rs.closeRecordStore();
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    public void deleteRecordDB()
    {
        try
        {
            closeRecordDB();
            RecordStore.deleteRecordStore( LOGS_DB );
        }
        catch ( Exception e )
        {
        }
    }

    // TODO: Polish exception handling
    /**
     * 
     * @return
     */
    public void getLogs()
    {
        getLogsTo();
    }

    /**
     * 
     * @param persist
     */
    public void getLogsTo()
    {
        System.out.println( "[LOG] RMS Logger getting logs..." );
        if ( rs != null )
        {
            try
            {
                int size = rs.getNumRecords();
                if ( size > 0 )
                {
                    LogUtils.clearConsole();

                    int count = 0;
                    int index = 1;

                    while ( count != size )
                    {
                        try
                        {
                            byte record[] = rs.getRecord( index );
                            count++;
                            if ( record != null )
                            {
                                Log log = convertToLog( record );
                                if ( LogMan.isValidLog( log ) )
                                {
                                    System.out.println( "#" + index + " " + LogUtils.composeLog( log, true ) );
                                    LogMan.displayLogs( log, false );
                                }
                            }
                        }
                        catch ( Exception e )
                        {
                            e.printStackTrace();
                        }
                        index++;
                    }
                }
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 
     * @param recordID
     * @return
     */
    public Log getLog( int recordID )
        throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException, IOException
    {
        byte record[] = rs.getRecord( recordID );

        Log log = null;
        if ( record != null )
        {
            log = convertToLog( record );
        }

        return log;
    }

    /**
     * 
     * @param record
     * @return
     * @throws Exception
     */
    private Log convertToLog( byte record[] )
        throws IOException
    {
        ByteArrayInputStream bais = new ByteArrayInputStream( record );
        DataInputStream dais = new DataInputStream( bais );

        Log log = new Log();
        log.setTimeDay( dais.readUTF() );
        log.setLevel( dais.readInt() );
        log.setTags( LogUtils.toVector( dais.readUTF() ) );
        log.setMessage( dais.readUTF() );

        try
        {
            dais.close();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        finally
        {
            dais = null;
        }

        try
        {
            bais.close();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        finally
        {
            bais = null;
        }
        return log;
    }

    // TODO: Polish exception handling
    /**
     * 
     * @param log
     */
    public void addLog( Log log )
    {
        if ( log != null )
        {
            try
            {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream( baos );

                dos.writeUTF( log.getTimeDay() );
                dos.writeInt( log.getLevel() );
                dos.writeUTF( LogUtils.toStringTags( log.getTags(), "," ) );
                dos.writeUTF( log.getMessage() );
                dos.flush();

                byte data[] = baos.toByteArray();
                baos.reset();
                rs.addRecord( data, 0, data.length );

                try
                {
                    dos.close();
                }
                catch ( Exception e )
                {
                    e.printStackTrace();
                }
                finally
                {
                    dos = null;
                }

                try
                {
                    baos.close();
                }
                catch ( Exception e )
                {
                    e.printStackTrace();
                }
                finally
                {
                    baos = null;
                }

            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     */
    public void clearLogs()
    {
        System.out.println( "[LOG] RMS Logger clearing logs..." );
        deleteRecordDB();
        openRecordDB();
    }

    /**
     *
     */
    public void close()
    {
        System.out.println( "[LOG] Closing RMS Logger..." );
        closeRecordDB();
    }

    /**
     * 
     * @param os
     */
    public void saveFile( OutputStream os )
    {
        System.out.println( "[LOG] Saving logs to file..." );
        if ( rs != null )
        {
            try
            {
                int size = rs.getNumRecords();

                if ( size > 0 )
                {
                    int count = 0;
                    int index = 1;

                    final Alert alert = new Alert( "Saving to File" );
                    alert.setTimeout( Alert.FOREVER );
                    Gauge gauge = new Gauge( null, false, size, count );
                    alert.setIndicator( gauge );

                    LogMan.getDisplay().setCurrent( alert );

                    while ( count != size )
                    {
                        try
                        {
                            byte record[] = rs.getRecord( index );
                            count++;

                            alert.setString( "Saving Logs: " + count + "/" + size );
                            gauge.setValue( count );

                            if ( record != null )
                            {
                                Log log = convertToLog( record );
                                if ( LogMan.isValidLog( log ) )
                                {
                                    String logStr = index + "# " + LogUtils.composeLog( log, true ) + "\r\n";
                                    os.write( logStr.getBytes( "UTF-8" ) );
                                }
                            }
                        }
                        catch ( Exception e )
                        {
                            e.printStackTrace();
                        }
                        index++;
                    }

                    alert.setString( "Saving Logs: FINISHED" );
                }
            }
            catch ( Exception e )
            {
                e.printStackTrace();
                Alert alert = new Alert( "Saving to File", e.getClass().getName() + ": " + e.getMessage(), null,
                    AlertType.ERROR );
                alert.setTimeout( Alert.FOREVER );
                LogMan.getDisplay().setCurrent( alert );
            }
        }
        else
        {
            Alert alert = new Alert( "Saving to File", "RMS not open.", null, AlertType.ERROR );
            alert.setTimeout( Alert.FOREVER );
            LogMan.getDisplay().setCurrent( alert );
        }

        try
        {
            os.close();
        }
        catch ( Exception e )
        {
        }
        finally
        {
            os = null;
        }
    }
}
