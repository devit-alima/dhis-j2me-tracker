package org.hisp.dhis.mobile.view;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.connection.task.LoginTask;
import org.hisp.dhis.mobile.connection.task.UpdateNewVersionTask;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.recordstore.SettingRecordStore;
import org.hisp.dhis.mobile.ui.Text;

public class UpdateNewVersionView
    extends AbstractView
    implements CommandListener
{
    private static final String CLASS_TAG = "UpdateNewVersionView";

    private Form updateNewVersionForm;

    private Command updateVersionFormYesCommand;

    private Command updateVersionFormNoCommand;
    
    private double serverVersion;

    public UpdateNewVersionView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
        // TODO Auto-generated constructor stub
    }

    public void commandAction( Command command, Displayable displayable )
    {
        if ( command == updateVersionFormYesCommand )
        {
            try
            {
                this.dhisMIDlet.getWaitingView().showView();

                int size = LoginTask.inputStream.readInt();

                LoginTask.orgUnit.deSerialize( LoginTask.inputStream );

                ConnectionManager.setUrl( LoginTask.orgUnit.getUpdateNewVersionUrl() );

                ConnectionManager.updateNewVersion();

                if ( UpdateNewVersionTask.isDownloadSuccessfully == true )
                {
                    // Save the new version value
                    SettingRecordStore settingRecordStore = null;
                    settingRecordStore = new SettingRecordStore();
                    settingRecordStore.put( SettingRecordStore.CLIENT_VERSION, this.serverVersion + "" );
                    settingRecordStore.save();
                }
            }
            catch ( Exception e )
            {
                LogMan.log( "UI," + CLASS_TAG, e );
                e.printStackTrace();
            }
            this.dhisMIDlet.getSplashScreenView().showView();
        }
        else
        {
            try
            {
                LoginTask.handleLogIn( LoginTask.inputStream );
            }
            catch ( Exception e )
            {
                LogMan.log( "UI," + CLASS_TAG, e );
                e.printStackTrace();
            }
        }

    }

    public void prepareView()
    {
        // TODO Auto-generated method stub

    }

    public void showView()
    {
        this.switchDisplayable( null, this.getUpdateNewVersionForm() );
    }

    public Form getUpdateNewVersionForm()
    {

        if ( updateNewVersionForm == null )
        {
            updateNewVersionForm = new Form( Text.UPDATE_NEW_VERSION(), new Item[] {} );

            updateNewVersionForm.append( Text.UPDATE_NEW_NOTIFICATION() );

            updateNewVersionForm.addCommand( this.getUpdateVersionFormNoCommand() );

            updateNewVersionForm.addCommand( this.getUpdateVersionFormYesCommand() );

            updateNewVersionForm.setCommandListener( this );
        }

        return updateNewVersionForm;
    }

    public void setUpdateNewVersionForm( Form updateNewVersionForm )
    {
        this.updateNewVersionForm = updateNewVersionForm;
    }

    public Command getUpdateVersionFormYesCommand()
    {
        if ( updateVersionFormYesCommand == null )
        {
            updateVersionFormYesCommand = new Command( Text.YES(), Command.OK, 0 );
        }
        return updateVersionFormYesCommand;
    }

    public void setUpdateVersionFormYesCommand( Command updateVersionFormYesCommand )
    {
        this.updateVersionFormYesCommand = updateVersionFormYesCommand;
    }

    public Command getUpdateVersionFormNoCommand()
    {
        if ( updateVersionFormNoCommand == null )
        {
            updateVersionFormNoCommand = new Command( Text.NO(), Command.CANCEL, 0 );
        }
        return updateVersionFormNoCommand;
    }

    public void setUpdateVersionFormNoCommand( Command updateVersionFormNoCommand )
    {
        this.updateVersionFormNoCommand = updateVersionFormNoCommand;
    }

    public void setServerVersion( double serverVersion )
    {
        this.serverVersion = serverVersion;
    }
}
