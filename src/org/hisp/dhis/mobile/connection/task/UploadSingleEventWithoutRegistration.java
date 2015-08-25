package org.hisp.dhis.mobile.connection.task;

import java.io.DataInputStream;
import java.io.IOException;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.ProgramStage;
import org.hisp.dhis.mobile.ui.Text;
import org.hisp.dhis.mobile.util.SerializationUtil;

public class UploadSingleEventWithoutRegistration extends AbstractTask
{
    
    private static final String SINGLE_EVENT_WITHOUT_REGISTRATION_UPLOADED = "single_event_without_registration_uploaded";
    
    private ProgramStage programStage;

    public void run()
    {
        NameBasedMIDlet nameBasedMIDlet = (NameBasedMIDlet) ConnectionManager.getDhisMIDlet();
        DataInputStream messageStream;
        try
        {
            messageStream = this.upload( SerializationUtil.serialize( programStage ) );
            String message = this.readMessage( messageStream );
            if ( message.startsWith( SINGLE_EVENT_WITHOUT_REGISTRATION_UPLOADED ) )
            {
                nameBasedMIDlet.getAlertBoxView( programStage.getName(), Text.UPLOAD_COMPLETED() ).showView();
                nameBasedMIDlet.getTrackingMainMenuView().showView();
            }
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
       
    }

    public ProgramStage getProgramStage()
    {
        return programStage;
    }

    public void setProgramStage( ProgramStage programStage )
    {
        this.programStage = programStage;
    }
    
    

}
