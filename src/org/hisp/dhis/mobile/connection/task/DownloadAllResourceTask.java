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

import javax.microedition.rms.RecordStoreException;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;
import org.hisp.dhis.mobile.model.MobileModel;
import org.hisp.dhis.mobile.model.OrgUnit;
import org.hisp.dhis.mobile.recordstore.ProgramRecordStore;
import org.hisp.dhis.mobile.recordstore.RelationshipTypeRecordStore;

public class DownloadAllResourceTask
    extends AbstractTask
{
    private static final String CLASS_TAG = "DownloadAllResourceTask";

    private MobileModel mobileModel = new MobileModel();

    private Vector orgUnitVector;

    public DownloadAllResourceTask( Vector orgUnitVector )
    {
        this.orgUnitVector = orgUnitVector;
    }

    public void run()
    {
        LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Starting DownloadAllResourceTask..." );

        DHISMIDlet.setDownloading( true );

        for ( int i = 0; i < orgUnitVector.size(); i++ )
        {
            OrgUnit orgUnit = (OrgUnit) orgUnitVector.elementAt( i );
            this.processDownload( orgUnit );
        }

        DHISMIDlet.setDownloading( false );

        NameBasedMIDlet nameBasedMIDlet = (NameBasedMIDlet) ConnectionManager.getDhisMIDlet();
        nameBasedMIDlet.getTrackingMainMenuView().showView();

    }

    private void processDownload( OrgUnit orgUnit )
    {
        DataInputStream inputStream = null;
        ConnectionManager.setUrl( orgUnit.getDownloadAllUrl() );
        LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Downloading programs..." + ConnectionManager.getUrl() );

        try
        {
            inputStream = this.download();
            this.handleDownloadAllResource( inputStream );
            ProgramRecordStore.savePrograms( mobileModel.getPrograms() );
            RelationshipTypeRecordStore.deleteAllRelationshipType();
            RelationshipTypeRecordStore.saveRelationshipTypes( mobileModel.getRelationshipTypes() );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
            LogMan.log( "Network," + CLASS_TAG, e );
        }
        catch ( RecordStoreException e )
        {
            e.printStackTrace();
            LogMan.log( "Network," + CLASS_TAG, e );
        }
        finally
        {
            try
            {
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

    private void handleDownloadAllResource( DataInputStream inputStream )
        throws IOException
    {
        mobileModel.deSerialize( inputStream );
    }

    public MobileModel getMobileModel()
    {
        return mobileModel;
    }

    public void setMobileModel( MobileModel mobileModel )
    {
        this.mobileModel = mobileModel;
    }

    public Vector getOrgUnitVector()
    {
        return orgUnitVector;
    }

    public void setOrgUnitVector( Vector orgUnitVector )
    {
        this.orgUnitVector = orgUnitVector;
    }
}
