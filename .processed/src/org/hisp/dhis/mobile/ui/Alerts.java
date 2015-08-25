package org.hisp.dhis.mobile.ui;

/*
 * Copyright (c) 2004-2010, University of Oslo All rights reserved.
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

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;

public class Alerts
{
    private static Alert successAlert;

    private static Alert errorAlert;

    public static Alert getSuccessAlert( String title, String message )
    {
        if ( successAlert == null )
        {
            successAlert = new Alert( Text.ALERT(), null, null, AlertType.INFO );
            successAlert.setTimeout( Alert.FOREVER );
        }
        successAlert.setTitle( title );
        successAlert.setString( message );

        return successAlert;
    }

    public static Alert getErrorAlert( String title, String msg )
    {
        if ( errorAlert == null )
        {
            errorAlert = new Alert( null );
            errorAlert.setType( AlertType.ERROR );
            errorAlert.setTimeout( Alert.FOREVER );
        }

        errorAlert.setTitle( title );
        errorAlert.setString( msg );

        return errorAlert;
    }

    public static Alert getInfoAlert( String title, String msg )
    {
        Alert alert = new Alert( title );
        alert.setString( msg );
        alert.setType( AlertType.INFO );
        alert.setTimeout( Alert.FOREVER );
        return alert;
    }

    public static Alert getConfirmAlert( String title, String msg, CommandListener listener )
    {

        Alert alert = new Alert( title, msg, null, AlertType.CONFIRMATION );
        alert.addCommand( new Command( Text.YES(), Command.OK, 0 ) );
        alert.addCommand( new Command( Text.NO(), Command.CANCEL, 0 ) );
        alert.setCommandListener( listener );
        return alert;
    }

}
