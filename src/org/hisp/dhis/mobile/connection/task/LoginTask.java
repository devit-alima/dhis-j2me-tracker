package org.hisp.dhis.mobile.connection.task;

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

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Vector;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.model.OrgUnit;
import org.hisp.dhis.mobile.recordstore.OrgUnitRecordStore;
import org.hisp.dhis.mobile.recordstore.SettingRecordStore;
import org.hisp.dhis.mobile.ui.Text;
import org.hisp.dhis.mobile.util.RecordStoreUtil;

public class LoginTask
    extends AbstractTask
{
    private static final String CLASS_TAG = "LoginTask";

    public static OrgUnit orgUnit;

    public static DataInputStream inputStream;

    public static String updateNewVersionUrl;

    public static double serverVersion;

    public static Vector orgUnitVector;

    public LoginTask()
    {
        orgUnit = new OrgUnit();
        orgUnitVector = new Vector();
    }

    public void run()
    {
        LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Starting LoginTask..." );
        try
        {
            inputStream = this.download();
            SettingRecordStore settingRecordStore = new SettingRecordStore();
            String clientVersion = settingRecordStore.get( SettingRecordStore.CLIENT_VERSION );

            if ( clientVersion == "" )
            {
                settingRecordStore.put( SettingRecordStore.CLIENT_VERSION, "2.12" );
                settingRecordStore.save();
            }
            serverVersion = getServerVersion();
            boolean isNewVersionAvailable = orgUnit.checkNewVersion( serverVersion );

            if ( isNewVersionAvailable == true )
            {
                ConnectionManager.getDhisMIDlet().getUpdateNewVersionView().setServerVersion( serverVersion );
                ConnectionManager.getDhisMIDlet().getUpdateNewVersionView().showView();
            }
            else
            {
                handleLogIn( inputStream );
            }
        }
        catch ( Exception e )
        {
            if ( e.getMessage().equalsIgnoreCase( "TCP open" ) )
            {
                ConnectionManager.getDhisMIDlet().getAlertBoxView( "Server has error, please try later", "TCP open" )
                    .showView();
                ConnectionManager.getDhisMIDlet().getLoginView().showView();
            }
            else if ( e.getMessage().equalsIgnoreCase( "Invalid username or password" ) )
            {
                ConnectionManager.getDhisMIDlet().getAlertBoxView( "Invalid username or password", "Alert" ).showView();
                ConnectionManager.getDhisMIDlet().getLoginView().showView();
            }
            else if ( e.getMessage().equalsIgnoreCase( "Connection not found" ) )
            {
                ConnectionManager.getDhisMIDlet().getAlertBoxView( "Invalid server location", "Alert" ).showView();
                ConnectionManager.getDhisMIDlet().getLoginView().showView();
            }
            else
            {
                e.printStackTrace();
                LogMan.log( "Network,Authentication3," + CLASS_TAG, e );
                ConnectionManager.getDhisMIDlet().getAlertBoxView( "Server encountered an unexpected problem", "Alert" ).showView();
                ConnectionManager.getDhisMIDlet().getLoginView().showView();
            }
        }
        finally
        {
            try
            {
                if ( inputStream != null )
                    inputStream.close();
            }
            catch ( IOException e )
            {
                e.printStackTrace();
                LogMan.log( "Network," + CLASS_TAG, e );
            }
            System.gc();
        }
    }

    public static void handleLogIn( DataInputStream dis )
        throws Exception
    {
        LogMan.log( LogMan.INFO, "Network,Authentication4," + CLASS_TAG, "Handling Login" );

        try
        {
            int size = dis.readInt();
            LogMan.log( LogMan.INFO, "Network,Authentication,OrgUnit" + CLASS_TAG, "Organization Unit Size: " + size );

            if ( size == 0 )
            {
                throw new IOException( Text.NO_ORGUNIT_ERROR() );
            }
            else
            {
                for ( int i = 0; i < size; i++ )
                {
                    LogMan.log( LogMan.DEBUG, "Network,Authentication,OrgUnit" + CLASS_TAG, "Processing OrgUnit #" + (i + 1) );
                    OrgUnit orgUnit = new OrgUnit();
                    orgUnit.deSerialize( dis );
                    orgUnitVector.addElement( orgUnit );
                    OrgUnitRecordStore.saveOrgUnit( orgUnit );
                }
                RecordStoreUtil.clearRecordStore( SettingRecordStore.PIN );
                ConnectionManager.getDhisMIDlet().getPinView().preparePinFormForFirstTime();
                ConnectionManager.getDhisMIDlet().getPinView().showView();
            }
            orgUnitVector = null;
            orgUnit = null;
            System.gc();
        } catch (Exception e) {
            LogMan.log( "Network,Authentication1," + CLASS_TAG, e );
        }
        finally
        {
            try
            {
                if ( dis != null )
                    dis.close();
                System.gc();
            }
            catch ( IOException ioe )
            {
                ioe.printStackTrace();
                LogMan.log( "Network,Authentication2," + CLASS_TAG, ioe );
            }

        }

    }

    public OrgUnit getOrgUnit()
    {
        return orgUnit;
    }

    public static DataInputStream getInputStream()
    {
        return inputStream;
    }

    public static void setInputStream( DataInputStream inputStream )
    {
        LoginTask.inputStream = inputStream;
    }

    public double getServerVersion()
    {
        double result = 0;
        try
        {
            result = inputStream.readDouble();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        return result;
    }

}
