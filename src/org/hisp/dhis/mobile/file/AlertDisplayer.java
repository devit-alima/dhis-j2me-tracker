package org.hisp.dhis.mobile.file;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;

/**
 * 
 * @author Paul Mark Castillo
 */
public class AlertDisplayer
{

    /**
     *
     */
    FileManager fileManager;

    /**
     * 
     * @param fileManager
     */
    public AlertDisplayer( FileManager fileManager )
    {
        this.fileManager = fileManager;
    }

    /**
     * 
     * @param e
     */
    public void dispException( Exception e, String moreInfo )
    {
        e.printStackTrace();
        String errorMessage = "Exception: " + e.getClass().getName() + ": " + e.getMessage() + " " + moreInfo;
        dispError( errorMessage );
    }

    /**
     * 
     * @param errorMessage
     */
    public void dispError( String errorMessage )
    {
        Alert alert = new Alert( "Error", errorMessage, null, AlertType.ERROR );
        alert.setTimeout( Alert.FOREVER );
        disp( alert );
    }

    /**
     * 
     * @param processMessage
     */
    public void dispProcessing( String processMessage )
    {
        Alert alert = new Alert( "Processing", processMessage, null, AlertType.INFO );
        disp( alert );
    }

    /**
     * 
     * @param alert
     */
    private void disp( Alert alert )
    {
        Display.getDisplay( fileManager.getMidlet() ).setCurrent( alert );
    }
}
