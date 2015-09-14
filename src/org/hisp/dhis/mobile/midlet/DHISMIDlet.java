package org.hisp.dhis.mobile.midlet;

/*
 * Copyright (c) 2004-2011, University of Oslo All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met: * Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer. * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or other materials provided with the
 * distribution. * Neither the name of the HISP project nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.log.LogUtils;
import org.hisp.dhis.mobile.model.OrgUnit;
import org.hisp.dhis.mobile.view.AlertBoxView;
import org.hisp.dhis.mobile.view.LoginView;
import org.hisp.dhis.mobile.view.OrgUnitSelectView;
import org.hisp.dhis.mobile.view.PinView;
import org.hisp.dhis.mobile.view.SettingView;
import org.hisp.dhis.mobile.view.SplashScreenView;
import org.hisp.dhis.mobile.view.UpdateNewVersionView;
import org.hisp.dhis.mobile.view.WaitingView;

import com.sun.lwuit.Display;
import com.sun.lwuit.plaf.UIManager;
import com.sun.lwuit.util.Resources;

public class DHISMIDlet
    extends MIDlet
{
    // Debug Mode
    public static final boolean DEBUG = true;

    // Flag

    public static boolean isDownloading;

    public static boolean isFirstTimeLogIn;

    // Common MIDlet Variables

    private SplashScreenView splashScreenView;

    private WaitingView waitingView;

    private LoginView loginView;

    private OrgUnitSelectView orgUnitSelectView;

    private PinView pinView;

    private SettingView settingView;

    private UpdateNewVersionView updateNewVersionView;

    private OrgUnit currentOrgUnit;
    
    private AlertBoxView alertBoxView;

    // Others

    public static String DEFAULT_LOCALE = "en-GB";

    public static final String BLANK = "";

    // For text value

    public static final int TEXT_NUMERIC = 8;

    public static final int TEXT_STRING = 64;

    /**
     * 
     */
    public DHISMIDlet()
    {
        System.out.println("Setting Up Logger...");
        setupLogger();

        if ( DEBUG )
        {
            System.out.println("Overriding Logger Settings...");
            LogMan.setEnabled( true );
            LogMan.setEnableSaveToFile( true );
            LogMan.setLevel( LogMan.INFO );
            LogMan.setBuffSize( 20 );
            LogMan.setIncludeTags( "" );
            LogMan.setExcludeTags( "" );
            LogMan.setIncludeMessages( "" );
            LogMan.setExcludeMessages( "" );
        }
    }    
    
    protected void destroyApp( boolean b )
        throws MIDletStateChangeException
    {
        // TODO Auto-generated method stub
    }

    protected void pauseApp()
    {
        // TODO Auto-generated method stub
    }

    protected void startApp()
        throws MIDletStateChangeException
    {
        Display.init( this );
        try
        {
            Resources r = Resources.open( "/LWUITtheme.res" );
            UIManager.getInstance().setThemeProps( r.getTheme( r.getThemeResourceNames()[0] ) );
        }
        catch ( java.io.IOException e )
        {
            e.printStackTrace();
        }
        //this.getLoginView().showView();
        this.getSplashScreenView().showView();
    }

    public void exitMIDlet()
    {
        try
        {
            this.destroyApp( true );
        }
        catch ( MIDletStateChangeException e )
        {
            e.printStackTrace();
        }
        notifyDestroyed();
    }

    /**
    *
    */
    public void setupLogger()
    {
        LogMan.initialize( this );

        String jadEnabled = getAppProperty( "LOG_ENABLED" );
        if ( jadEnabled != null )
        {
            if ( jadEnabled.toLowerCase().equals( "enabled" ) || jadEnabled.toLowerCase().equals( "enable" ) )
            {
                LogMan.setEnabled( true );
            }
            else
            {
                LogMan.setEnabled( false );
            }
        }
        else
        {
            LogMan.setEnabled( false );
        }

        String jadEnableSaveToFile = getAppProperty( "LOG_ENABLE_SAVE_TO_FILE" );
        if ( jadEnableSaveToFile != null )
        {
            if ( jadEnableSaveToFile.toLowerCase().equals( "enabled" )
                || jadEnableSaveToFile.toLowerCase().equals( "enable" ) )
            {
                LogMan.setEnableSaveToFile( true );
            }
            else
            {
                LogMan.setEnableSaveToFile( false );
            }
        }
        else
        {
            LogMan.setEnableSaveToFile( false );
        }

        String jadBuffer = getAppProperty( "LOG_BUFFER" );
        if ( jadBuffer != null )
        {
            if ( !jadBuffer.equals( "" ) )
            {
                LogMan.setBuffSize( Integer.parseInt( jadBuffer ) );
            }
            else
            {
                LogMan.setBuffSize( 20 );
            }
        }
        else
        {
            LogMan.setBuffSize( 20 );
        }

        String jadLevel = getAppProperty( "LOG_LEVEL" );
        if ( jadLevel != null )
        {
            LogMan.setLevel( LogUtils.getLevelInt( jadLevel.toUpperCase() ) );
        }
        else
        {
            LogMan.setLevel( LogMan.LEVEL );
        }

        String jadIncludeTags = getAppProperty( "LOG_INCLUDE_TAGS" );
        if ( jadIncludeTags != null )
        {
            LogMan.setIncludeTags( jadIncludeTags );
        }
        else
        {
            LogMan.setIncludeTags( "" );
        }

        String jadExcludeTags = getAppProperty( "LOG_EXCLUDE_TAGS" );
        if ( jadExcludeTags != null )
        {
            LogMan.setExcludeTags( jadExcludeTags );
        }
        else
        {
            LogMan.setExcludeTags( "" );
        }

        String jadIncludeMessages = getAppProperty( "LOG_INCLUDE_MESSAGES" );
        if ( jadIncludeMessages != null )
        {
            LogMan.setIncludeMessages( jadIncludeMessages );
        }
        else
        {
            LogMan.setIncludeMessages( "" );
        }

        String jadExcludeMessages = getAppProperty( "LOG_EXCLUDE_MESSAGES" );
        if ( jadExcludeMessages != null )
        {
            LogMan.setExcludeMessages( jadExcludeMessages );
        }
        else
        {
            LogMan.setExcludeMessages( "" );
        }
    }    
    
    public SplashScreenView getSplashScreenView()
    {
        if ( splashScreenView == null )
        {
            splashScreenView = new SplashScreenView( this );
        }
        return splashScreenView;
    }

    public void setSplashScreenView( SplashScreenView splashScreenView )
    {
        this.splashScreenView = splashScreenView;
    }

    public WaitingView getWaitingView()
    {
        if ( waitingView == null )
        {
            waitingView = new WaitingView( this );
        }
        return waitingView;
    }

    public void setWaitingView( WaitingView waitingView )
    {
        this.waitingView = waitingView;
    }

    public LoginView getLoginView()
    {
        if ( loginView == null )
        {
            loginView = new LoginView( this );
        }
        return loginView;
    }

    public OrgUnitSelectView getOrgUnitSelectView()
    {
        if ( orgUnitSelectView == null )
        {
            orgUnitSelectView = new OrgUnitSelectView( this );
        }
        return orgUnitSelectView;
    }

    public void setOrgUnitSelectView( OrgUnitSelectView orgUnitSelectView )
    {
        this.orgUnitSelectView = orgUnitSelectView;
    }

    public void setLoginView( LoginView loginView )
    {
        this.loginView = loginView;
    }

    public PinView getPinView()
    {
        if ( pinView == null )
        {
            pinView = new PinView( this );
        }
        return pinView;
    }

    public void setPinView( PinView pinView )
    {
        this.pinView = pinView;
    }

    public SettingView getSettingView()
    {
        if ( settingView == null )
        {
            settingView = new SettingView( this );
        }
        return settingView;
    }

    public void setSettingView( SettingView settingView )
    {
        this.settingView = settingView;
    }

    public UpdateNewVersionView getUpdateNewVersionView()
    {
        if ( updateNewVersionView == null )
        {
            updateNewVersionView = new UpdateNewVersionView( this );
        }
        return updateNewVersionView;
    }

    public void setUpdateNewVersionView( UpdateNewVersionView updateNewVersionView )
    {
        this.updateNewVersionView = updateNewVersionView;
    }

    public static boolean isDownloading()
    {
        return isDownloading;
    }

    public static void setDownloading( boolean isDownloading )
    {
        DHISMIDlet.isDownloading = isDownloading;
    }

    public OrgUnit getCurrentOrgUnit()
    {
        return currentOrgUnit;
    }

    public void setCurrentOrgUnit( OrgUnit currentOrgUnit )
    {
        this.currentOrgUnit = currentOrgUnit;
    }

    public static boolean isFirstTimeLogIn()
    {
        return isFirstTimeLogIn;
    }

    public static void setFirstTimeLogIn( boolean isFirstTimeLogIn )
    {
        DHISMIDlet.isFirstTimeLogIn = isFirstTimeLogIn;
    }

    public AlertBoxView getAlertBoxView( String message, String title )
    {
        if ( alertBoxView == null )
        {
            alertBoxView = new AlertBoxView( message, title );
        }
        alertBoxView.setMessage( message );
        alertBoxView.setTitleText( title );
        return alertBoxView;
    }

    public void setAlertBoxView( AlertBoxView alertBoxView )
    {
        this.alertBoxView = alertBoxView;
    }
}
