package org.hisp.dhis.mobile.view;

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

import java.io.IOException;
import java.util.Hashtable;

import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotFoundException;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.recordstore.OrgUnitRecordStore;
import org.hisp.dhis.mobile.ui.Text;
import org.hisp.dhis.mobile.util.Properties;

import com.sun.lwuit.Command;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.TextField;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;

public class LoginView
    extends AbstractView
    implements ActionListener
{
    private static final String CLASS_TAG = "LoginView";

    private static String DEFAULT_DHIS_URL = "http://localhost:8080/dhis";

    private static String DEFAULT_USERNAME = "long";

    private static String DEFAULT_PASSWORD = "District1";

    public static final String MOBILE_PATH = "api/mobile/2.10/LWUIT";

    private Form loginForm;

    private TextField loginServerUrlTextField;

    private TextField loginUserNameTextField;

    private TextField loginPasswordTextField;

    public LoginView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
    }

    private void login()
    {
        ConnectionManager.init( dhisMIDlet, buildURL( this.getLoginServerUrlTextField().getText() ), this
            .getLoginUserNameTextField().getText(), this.getLoginPasswordTextField().getText(), "en-GB", null );
        dhisMIDlet.getWaitingView().showView();
        try
        {
            OrgUnitRecordStore.deleteAllOrgUnit();
        }
        catch ( RecordStoreNotFoundException e )
        {
            LogMan.log( "UI," + CLASS_TAG, e );
            e.printStackTrace();
        }
        catch ( RecordStoreException e )
        {
            LogMan.log( "UI," + CLASS_TAG, e );
            e.printStackTrace();
        }
        ConnectionManager.login();
    }

    public static String buildURL( String url )
    {
        url += (url.endsWith( "/" ) ? "" : "/") + MOBILE_PATH;
        return url;
    }

    public void prepareView()
    {
        // TODO Auto-generated method stub
    }

    public void showView()
    {
        this.getLoginForm().show();
    }

    public Form getLoginForm()
    {
        if ( loginForm == null )
        {
            loginForm = new Form( Text.LOGIN() );
            loginForm.setLayout( new BoxLayout( BoxLayout.Y_AXIS ) );
            loginForm.setTransitionOutAnimator( CommonTransitions.createSlide( CommonTransitions.SLIDE_HORIZONTAL,
                true, 200 ) );
            loginForm.addComponent( new Label( Text.USERNAME() ) );
            loginForm.addComponent( getLoginUserNameTextField() );
            loginForm.addComponent( new Label( Text.PASSWORD() ) );
            loginForm.addComponent( getLoginPasswordTextField() );
            loginForm.addComponent( new Label( Text.URL() ) );
            loginForm.addComponent( getLoginServerUrlTextField() );

            if ( LogMan.isEnabled() )
            {
                loginForm.addCommand( new Command( Text.LOG() ) );
            }
            loginForm.addCommand( new Command( Text.EXIT() ) );
            loginForm.addCommand( new Command( Text.LOGIN() ) );
            loginForm.addCommandListener( this );
        }
        return loginForm;
    }

    public void setLoginForm( Form loginForm )
    {
        this.loginForm = loginForm;
    }

    public void actionPerformed( ActionEvent ae )
    {
        if ( ae.getCommand().getCommandName().equals( Text.LOGIN() ) )
        {
            login();
        }
        else if ( ae.getCommand().getCommandName().equals( Text.LOG() ) )
        {
            LogMan.showLogMonitorScreen();
        }
        else if ( ae.getCommand().getCommandName().equals( Text.EXIT() ) )
        {
            Dialog dialog = new Dialog( Text.WARNING() );
            dialog.setDialogType( Dialog.TYPE_CONFIRMATION );
            dialog.addComponent( new TextArea( Text.EXIT_CONFIRM() ) );
            dialog.addCommandListener( this );
            dialog.addCommand( new Command( Text.NO() ) );
            dialog.addCommand( new Command( Text.YES() ) );
            dialog.show();
        }
        else if ( ae.getCommand().getCommandName().equals( Text.YES() ) )
        {
            dhisMIDlet.exitMIDlet();
        }
        else
        {
            this.showView();
        }

    }

    public TextField getLoginServerUrlTextField()
    {
        if ( loginServerUrlTextField == null )
        {
            try
            {
                Hashtable props = Properties.load( "/properties/app.properties" );
                if ( props.get( "server.url" ) != null )
                {
                    DEFAULT_DHIS_URL = (String) props.get( "server.url" );
                }
            }
            catch ( IOException e )
            {
                LogMan.log( "UI," + CLASS_TAG, e );
                e.printStackTrace();
            }
            loginServerUrlTextField = new TextField( DEFAULT_DHIS_URL );
        }
        return loginServerUrlTextField;
    }

    public void setLoginServerUrlTextField( TextField loginServerUrlTextField )
    {
        this.loginServerUrlTextField = loginServerUrlTextField;
    }

    public TextField getLoginUserNameTextField()
    {
        if ( loginUserNameTextField == null )
        {
            try
            {
                Hashtable props = Properties.load( "/properties/app.properties" );
                if ( props.get( "username" ) != null )
                {
                    DEFAULT_USERNAME = (String) props.get( "username" );
                }
            }
            catch ( IOException e )
            {
                LogMan.log( "UI," + CLASS_TAG, e );
                e.printStackTrace();
            }
            loginUserNameTextField = new TextField( DEFAULT_USERNAME );
        }
        return loginUserNameTextField;
    }

    public void setLoginUserNameTextField( TextField loginUserNameTextField )
    {
        this.loginUserNameTextField = loginUserNameTextField;
    }

    public TextField getLoginPasswordTextField()
    {
        if ( loginPasswordTextField == null )
        {
            try
            {
                Hashtable props = Properties.load( "/properties/app.properties" );
                if ( props.get( "password" ) != null )
                {
                    DEFAULT_PASSWORD = (String) props.get( "password" );
                }
            }
            catch ( IOException e )
            {
                LogMan.log( "UI," + CLASS_TAG, e );
                e.printStackTrace();
            }
            loginPasswordTextField = new TextField( DEFAULT_PASSWORD );
            loginPasswordTextField.setConstraint( TextField.PASSWORD | TextField.URL );
        }
        return loginPasswordTextField;
    }

    public void setLoginPasswordTextField( TextField loginPasswordTextField )
    {
        this.loginPasswordTextField = loginPasswordTextField;
    }

}
