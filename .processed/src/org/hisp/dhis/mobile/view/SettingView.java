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

import java.util.Vector;

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.TextField;
import javax.microedition.rms.RecordStoreException;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.OrgUnit;
import org.hisp.dhis.mobile.recordstore.SettingRecordStore;
import org.hisp.dhis.mobile.ui.Alerts;
import org.hisp.dhis.mobile.ui.Text;

public class SettingView
    extends AbstractView
    implements CommandListener
{
    private static final String CLASS_TAG = "SettingView";
    
    private Form settingForm;

    private Command settingFormSaveCommand;

    private Command settingExitCommand;

    private TextField settingServerURLTextField;

    private TextField settingUserNameTextField;

    private TextField settingPasswordTextField;

    private ChoiceGroup settingLocaleChoiceGroup;

    private TextField settingServerPhoneNumber;

    private Vector localeVector;

    private String currentLocale;

    public SettingView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
    }

    public void prepareView()
    {
        try
        {
            SettingRecordStore settingRecordStore = new SettingRecordStore();

            this.getSettingUserNameTextField().setString( settingRecordStore.get( SettingRecordStore.USERNAME ) );
            this.getSettingPasswordTextField().setString( settingRecordStore.get( SettingRecordStore.PASSWORD ) );
            this.getSettingServerURLTextField().setString( settingRecordStore.get( SettingRecordStore.SERVER_URL ) );
            this.getSettingServerPhoneNumber().setString(
                settingRecordStore.get( SettingRecordStore.SERVER_PHONE_NUMBER ) );

            if ( settingRecordStore.get( SettingRecordStore.LOCALE ).equals( DHISMIDlet.BLANK )
                || settingRecordStore.get( SettingRecordStore.LOCALE ) == null )
            {
                currentLocale = DHISMIDlet.DEFAULT_LOCALE;
            }
            else
            {
                currentLocale = settingRecordStore.get( SettingRecordStore.LOCALE );
            }

            for ( int i = 0; i < this.getLocaleVector().size(); i++ )
            {
                String localeName = (String) this.getLocaleVector().elementAt( i );
                if ( localeName.equalsIgnoreCase( currentLocale ) )
                {
                    this.getSettingLocaleChoiceGroup().setSelectedIndex( i, true );
                    return;
                }
            }

        }
        catch ( RecordStoreException e )
        {
            this.getDisplay().setCurrent( Alerts.getErrorAlert( Text.ERROR(), e.getMessage() ) );
            LogMan.log( "UI," + CLASS_TAG, e );
            e.printStackTrace();
        }
    }

    public void showView()
    {
        this.prepareView();
        this.switchDisplayable( null, this.getSettingForm() );
    }

    public void commandAction( Command command, Displayable displayable )
    {
        if ( command == this.settingExitCommand )
        {
            if ( this.dhisMIDlet instanceof NameBasedMIDlet )
            {
                NameBasedMIDlet nameBasedMIDlet = (NameBasedMIDlet) dhisMIDlet;
                nameBasedMIDlet.getTrackingMainMenuView().showView();
            }
        }
        else if ( command == this.settingFormSaveCommand )
        {
            try
            {
                Displayable nextDisplayable = null;
                String selectedLocale = (String) this.getLocaleVector().elementAt(
                    this.getSettingLocaleChoiceGroup().getSelectedIndex() );
                String userName = this.getSettingUserNameTextField().getString();
                String passWord = this.getSettingPasswordTextField().getString();

                SettingRecordStore settingRecordStore = new SettingRecordStore();
                settingRecordStore.put( SettingRecordStore.USERNAME, userName );
                settingRecordStore.put( SettingRecordStore.PASSWORD, passWord );
                settingRecordStore.put( SettingRecordStore.SERVER_URL, this.getSettingServerURLTextField().getString() );
                settingRecordStore.put( SettingRecordStore.SERVER_PHONE_NUMBER, this.getSettingServerPhoneNumber()
                    .getString() );
                settingRecordStore.put( SettingRecordStore.LOCALE, selectedLocale );
                settingRecordStore.save();

                if ( !currentLocale.equals( selectedLocale ) )
                {
                    dhisMIDlet.getWaitingView().showView();
                    OrgUnit orgUnit = dhisMIDlet.getCurrentOrgUnit();
                    ConnectionManager.init( dhisMIDlet, orgUnit.getDownloadAllUrl(), userName, passWord,
                        selectedLocale, orgUnit );
                    // ConnectionManager.updateContentLanguage();
                }
                else
                {
                    this.switchDisplayable( Alerts.getInfoAlert( Text.MESSAGE(), Text.SETTING_SAVED_MESSAGE() ),
                        nextDisplayable );
                }
            }
            catch ( Exception e )
            {
                LogMan.log( "UI," + CLASS_TAG, e );
                e.printStackTrace();
            }
        }
    }

    public Form getSettingForm()
    {
        if ( settingForm == null )
        {
            settingForm = new Form( Text.CONFIGURABLE_PARAMS(), new Item[] { getSettingServerURLTextField(),
                getSettingUserNameTextField(), getSettingPasswordTextField(), getSettingServerPhoneNumber(),
                getSettingLocaleChoiceGroup() } );
            settingForm.addCommand( getSettingFormSaveCommand() );
            settingForm.addCommand( getSettingExitCommand() );
            settingForm.setCommandListener( this );
        }
        return settingForm;
    }

    public void setSettingForm( Form settingForm )
    {
        this.settingForm = settingForm;
    }

    public Command getSettingFormSaveCommand()
    {
        if ( settingFormSaveCommand == null )
        {
            settingFormSaveCommand = new Command( Text.SAVE(), Command.SCREEN, 0 );
        }
        return settingFormSaveCommand;
    }

    public void setSettingFormSaveCommand( Command settingFormSaveCommand )
    {
        this.settingFormSaveCommand = settingFormSaveCommand;
    }

    public Command getSettingExitCommand()
    {
        if ( settingExitCommand == null )
        {
            settingExitCommand = new Command( Text.BACK(), Command.BACK, 0 );
        }
        return settingExitCommand;
    }

    public void setSettingExitCommand( Command settingExitCommand )
    {
        this.settingExitCommand = settingExitCommand;
    }

    public TextField getSettingServerURLTextField()
    {
        if ( settingServerURLTextField == null )
        {
            settingServerURLTextField = new TextField( Text.URL(), "", 64, TextField.URL );
        }
        return settingServerURLTextField;
    }

    public void setSettingServerURLTextField( TextField settingServerURLTextField )
    {
        this.settingServerURLTextField = settingServerURLTextField;
    }

    public TextField getSettingUserNameTextField()
    {
        if ( settingUserNameTextField == null )
        {
            settingUserNameTextField = new TextField( Text.USERNAME(), null, 32, TextField.ANY );
        }
        return settingUserNameTextField;
    }

    public void setSettingUserNameTextField( TextField settingUserNameTextField )
    {
        this.settingUserNameTextField = settingUserNameTextField;
    }

    public TextField getSettingPasswordTextField()
    {
        if ( settingPasswordTextField == null )
        {
            settingPasswordTextField = new TextField( Text.PASSWORD(), "", 32, TextField.ANY | TextField.PASSWORD );
        }
        return settingPasswordTextField;
    }

    public void setSettingPasswordTextField( TextField settingPasswordTextField )
    {
        this.settingPasswordTextField = settingPasswordTextField;
    }

    public ChoiceGroup getSettingLocaleChoiceGroup()
    {
        if ( settingLocaleChoiceGroup == null )
        {
            settingLocaleChoiceGroup = new ChoiceGroup( Text.LANGUAGE_LOCALE(), ChoiceGroup.POPUP );
            for ( int i = 0; i < this.getLocaleVector().size(); i++ )
            {
                settingLocaleChoiceGroup.append( ((String) this.getLocaleVector().elementAt( i )), null );
            }
        }
        return settingLocaleChoiceGroup;
    }

    public void setSettingLocaleChoiceGroup( ChoiceGroup settingLocaleChoiceGroup )
    {
        this.settingLocaleChoiceGroup = settingLocaleChoiceGroup;
    }

    public TextField getSettingServerPhoneNumber()
    {
        if ( settingServerPhoneNumber == null )
        {
            settingServerPhoneNumber = new TextField( Text.SERVER_PHONE_NUMBER(), "", 32, TextField.PHONENUMBER );
        }
        return settingServerPhoneNumber;
    }

    public void setSettingServerPhoneNumber( TextField settingServerPhoneNumber )
    {
        this.settingServerPhoneNumber = settingServerPhoneNumber;
    }

    public Vector getLocaleVector()
    {
        if ( localeVector == null )
        {
            localeVector = new Vector();
            localeVector.addElement( "en-GB" );
            localeVector.addElement( "fr-FR" );
            localeVector.addElement( "hi-IN" );
            localeVector.addElement( "pa-IN" );
            localeVector.addElement( "vi-VN" );
        }
        return localeVector;
    }

    public void setLocaleVector( Vector localeVector )
    {
        this.localeVector = localeVector;
    }

}
