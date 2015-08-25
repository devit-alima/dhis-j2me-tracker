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
import java.util.Vector;

import javax.microedition.lcdui.Alert;
import javax.microedition.rms.RecordStoreException;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.recordstore.OrgUnitRecordStore;
import org.hisp.dhis.mobile.recordstore.SettingRecordStore;
import org.hisp.dhis.mobile.ui.Text;
import org.hisp.dhis.mobile.util.Properties;
import org.hisp.dhis.mobile.util.RecordStoreUtil;

import com.sun.lwuit.Button;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Font;
import com.sun.lwuit.Form;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.TextField;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;

public class PinView
    extends AbstractView
    implements ActionListener
{
    private static final String CLASS_TAG = "PinView";
    
    private static final int MAX_PIN_SIZE = 4;

    private Form pinForm;

    private TextField pinFormTextField;

    private Alert reinitConfirmAlert;

    private TextArea pinHint;

    private Button btnEnterPin;

    private SettingRecordStore settingRecordStore;

    public PinView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
    }

    private void checkPin()
    {
        try
        {
            String currentPin = getSettingRecordStore().get( SettingRecordStore.PIN );
            String inputPin = this.getPinFormTextField().getText().trim();

            if ( currentPin.equalsIgnoreCase( DHISMIDlet.BLANK ) )
            {
                this.handleInitializationProcess( inputPin );
                DHISMIDlet.setFirstTimeLogIn( true );
            }
            else
            {
                this.handleSecondTimeProcess( currentPin, inputPin );
                DHISMIDlet.setFirstTimeLogIn( false );
            }
        }
        catch ( RecordStoreException e )
        {
            LogMan.log( "UI," + CLASS_TAG, e );
            e.printStackTrace();
        }
        finally
        {
            System.gc();
        }

    }

    private void handleSecondTimeProcess( String currentPin, String inputPin )
        throws RecordStoreException
    {
        if ( currentPin.equalsIgnoreCase( inputPin ) )
        {
            this.update();
        }
        else
        {
            dhisMIDlet.getAlertBoxView( Text.INVALID_PIN(), Text.ERROR() ).showView();
        }
    }

    private void handleInitializationProcess( String inputPin )
    {
        if ( inputPin.length() < 4 )
        {
            dhisMIDlet.getAlertBoxView( Text.ENTER_PIN(), Text.ERROR() ).showView();
        }
        else
        {
            dhisMIDlet.getWaitingView().showView();
            Vector orgUnitVector = null;
            try
            {
                orgUnitVector = OrgUnitRecordStore.loadAllOrgUnit();
            }
            catch ( RecordStoreException e )
            {
                LogMan.log( "UI," + CLASS_TAG, e );
                e.printStackTrace();
            }
            catch ( IOException e )
            {
                LogMan.log( "UI," + CLASS_TAG, e );
                e.printStackTrace();
            }
            ConnectionManager.downloadAllResource( orgUnitVector );
            this.saveInitSetting();
        }
    }

    private void saveInitSetting()
    {
        try
        {
            Hashtable props = Properties.load( "/properties/app.properties" );
            String serverPhoneNumber = "";
            if ( props.get( "server.phonenumber" ) != null )
            {
                serverPhoneNumber = (String) props.get( "server.phonenumber" );
            }

            settingRecordStore.put( SettingRecordStore.SERVER_URL, dhisMIDlet.getLoginView()
                .getLoginServerUrlTextField().getText() );
            settingRecordStore.put( SettingRecordStore.USERNAME, dhisMIDlet.getLoginView().getLoginUserNameTextField()
                .getText() );
            settingRecordStore.put( SettingRecordStore.PASSWORD, dhisMIDlet.getLoginView().getLoginPasswordTextField()
                .getText() );
            settingRecordStore.put( SettingRecordStore.PIN, this.getPinFormTextField().getText().trim() );
            settingRecordStore.put( SettingRecordStore.SERVER_PHONE_NUMBER, serverPhoneNumber );
            settingRecordStore.save();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            LogMan.log( "UI," + CLASS_TAG, e );
        }

    }

    public void prepareView()
    {
    }

    public void navigate()
    {
        if ( DHISMIDlet.isDownloading() )
        {
            dhisMIDlet.getWaitingView().showView();
        }
        else
        {
            NameBasedMIDlet namebasedMIDlet = (NameBasedMIDlet) dhisMIDlet;
            namebasedMIDlet.getTrackingMainMenuView().showView();
        }
    }

    public void update()
        throws RecordStoreException
    {
        if ( this.dhisMIDlet instanceof NameBasedMIDlet )
        {
            ConnectionManager.init( dhisMIDlet, "", settingRecordStore.get( SettingRecordStore.USERNAME ),
                settingRecordStore.get( SettingRecordStore.PASSWORD ), DHISMIDlet.DEFAULT_LOCALE, null );
            ((NameBasedMIDlet) dhisMIDlet).getTrackingMainMenuView().showView();
        }
    }

    public void showView()
    {
        this.getPinForm().show();
    }

    public void preparePinFormForFirstTime()
    {
        dhisMIDlet.getPinView().getPinHint().setText( Text.CREATE_PIN_SUGGESTION() );
    }

    public void preparePinFormForSecondTime()
    {
        dhisMIDlet.getPinView().getPinHint().setText( Text.CHECK_PIN_SUGGESTION() );
    }

    public Form getPinForm()
    {
        if ( pinForm == null )
        {
            pinForm = new Form( Text.ENTER_PIN() );
            pinForm.setLayout( new BoxLayout( BoxLayout.Y_AXIS ) );
            pinForm.setTransitionOutAnimator( CommonTransitions.createSlide( CommonTransitions.SLIDE_HORIZONTAL, true,
                200 ) );
            pinForm.addComponent( getPinHint() );
            pinForm.addComponent( getPinFormTextField() );
            pinFormTextField.requestFocus();
            pinForm.addComponent( getBtnEnterPin() );

            pinForm.addCommand( new Command( Text.BACK() ) );
            // pinForm.addCommand( new Command( "Reinit" ) );
            pinForm.addCommandListener( this );
        }
        return pinForm;
    }

    public void setPinForm( Form pinForm )
    {
        this.pinForm = pinForm;
    }

    public TextField getPinFormTextField()
    {
        if ( pinFormTextField == null )
        {
            pinFormTextField = new TextField();
            pinFormTextField.setMaxSize( MAX_PIN_SIZE );
            pinFormTextField.setConstraint( TextField.NUMERIC );
            pinFormTextField.setInputModeOrder( new String[] { "123" } );
        }
        return pinFormTextField;
    }

    public void setPinFormTextField( TextField pinFormTextField )
    {
        this.pinFormTextField = pinFormTextField;
    }

    public Alert getReinitConfirmAlert()
    {
        if ( this.reinitConfirmAlert == null )
        {
            // reinitConfirmAlert = Alerts.getConfirmAlert( Text.MESSAGE(),
            // Text.REINIT_MESSAGE(), this );
        }
        return reinitConfirmAlert;
    }

    public void setReinitConfirmAlert( Alert reinitConfirmAlert )
    {
        this.reinitConfirmAlert = reinitConfirmAlert;
    }

    public void actionPerformed( ActionEvent ae )
    {
        if ( ae.getCommand().getCommandName().equals( "Reinit" ) )
        {
            // checkPin();
        }
        else
        {
            RecordStoreUtil.clearAllRecordStore();
            dhisMIDlet.getLoginView().showView();
        }
    }

    public TextArea getPinHint()
    {
        if ( pinHint == null )
        {
            pinHint = new TextArea();
            pinHint.setEditable( false );
        }
        return pinHint;
    }

    public void setPinHint( TextArea pinHint )
    {
        this.pinHint = pinHint;
    }

    public Button getBtnEnterPin()
    {
        if ( btnEnterPin == null )
        {
            btnEnterPin = new Button( Text.ENTER() );
            btnEnterPin.addActionListener( new ActionListener()
            {
                public void actionPerformed( ActionEvent ae )
                {
                    checkPin();
                }
            } );
            btnEnterPin.getUnselectedStyle().setAlignment( Component.CENTER );
            btnEnterPin.getSelectedStyle().setAlignment( Component.CENTER );
            btnEnterPin.getPressedStyle().setAlignment( Component.CENTER );
            btnEnterPin.getUnselectedStyle().setFont(
                Font.createSystemFont( Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM ) );
            btnEnterPin.getSelectedStyle().setFont(
                Font.createSystemFont( Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM ) );
            btnEnterPin.getPressedStyle().setFont(
                Font.createSystemFont( Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM ) );
        }
        return btnEnterPin;
    }

    public void setBtnEnterPin( Button btnEnterPin )
    {
        this.btnEnterPin = btnEnterPin;
    }

    public SettingRecordStore getSettingRecordStore()
    {
        if ( settingRecordStore == null )
        {
            try
            {
                settingRecordStore = new SettingRecordStore();
            }
            catch ( RecordStoreException e )
            {
                LogMan.log( "UI," + CLASS_TAG, e );
                e.printStackTrace();
            }
        }
        return settingRecordStore;
    }

}
