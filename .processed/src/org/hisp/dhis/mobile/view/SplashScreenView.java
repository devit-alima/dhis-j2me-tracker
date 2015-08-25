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

import javax.microedition.rms.RecordStoreException;

import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.recordstore.SettingRecordStore;

import com.sun.lwuit.Image;

public class SplashScreenView
    extends AbstractView
{
    private static final String CLASS_TAG = "SplashScreenView";
    
    public static String LOGO_IMAGE = "/dhis2_logo.png";

    private Image logo;

    SplashScreen splashScreen;

    AbstractView nextView;

    public SplashScreenView( DHISMIDlet dhisMIDlet )
    {
        super( dhisMIDlet );
    }

    public void prepareView()
    {
        // TODO Auto-generated method stub
    }

    public void showView()
    {
        SettingRecordStore settingStore = null;

        try
        {
            settingStore = new SettingRecordStore();
            if ( settingStore.get( SettingRecordStore.PIN ).equals( "" ) )
            {
                this.dhisMIDlet.getPinView().preparePinFormForFirstTime();
                nextView = this.dhisMIDlet.getLoginView();
            }
            else
            {
                this.dhisMIDlet.getPinView().preparePinFormForSecondTime();
                nextView = this.dhisMIDlet.getPinView();
            }

            splashScreen = new SplashScreen( getLogo() );
            splashScreen.show();

            long start = System.currentTimeMillis();

            long difference = System.currentTimeMillis() - start;

            // Show splash view for at least 3 seconds
            Thread.sleep( Math.max( 0, 3000 - difference ) );

            nextView.showView();
        }
        catch ( RecordStoreException e )
        {
            LogMan.log( "UI," + CLASS_TAG, e );
            e.printStackTrace();
        }
        catch ( Exception e )
        {
            LogMan.log( "UI," + CLASS_TAG, e );
            e.printStackTrace();
        }

    }

    public Image getLogo()
    {
        if ( logo == null )
        {
            try
            {
                logo = Image.createImage( LOGO_IMAGE );
            }
            catch ( java.io.IOException e )
            {
                LogMan.log( "UI," + CLASS_TAG, e );
                e.printStackTrace();
            }
        }
        return logo;
    }

}
