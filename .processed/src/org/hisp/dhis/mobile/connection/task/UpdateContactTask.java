/*
 * Copyright (c) 2004-2012, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.mobile.connection.task;

import java.io.DataInputStream;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.pim.Contact;
import javax.microedition.pim.ContactList;
import javax.microedition.pim.PIM;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;

import org.hisp.dhis.mobile.connection.ConnectionManager;
import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.NameBasedMIDlet;

/**
 * @author Nguyen Kim Lai
 * 
 * @version UpdateContactTask.java 10:14:20 AM Mar 21, 2013 $
 */

public class UpdateContactTask
    extends AbstractTask
{
    private static final String CLASS_TAG = "UpdateContactTask";
    private NameBasedMIDlet nameBasedMIDlet;

    private static Alert check;

    private Random randPh = new Random();

    private PIM pim;

    private String nameToWrite;

    private String phoneToWrite;

    public void run()
    {
        LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Starting UpdateContactTask..." );
        
        this.nameBasedMIDlet = (NameBasedMIDlet) ConnectionManager.getDhisMIDlet();
        DataInputStream dataInputStream = null;
        Vector contactList = new Vector();
        try
        {
            dataInputStream = this.download();

            boolean stop = false;

            while ( !stop )
            {
                try
                {
                    String singleContact = dataInputStream.readUTF();
                    contactList.addElement( singleContact );
                }
                catch ( Exception e )
                {
                    stop = true;
                }
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        writeContacts( contactList );
        contactList = null;
        System.gc();
        nameBasedMIDlet.getAlertBoxView( "Update contact from server successfully", "Caution" ).showView();
    }

    public synchronized void writeContacts( Vector contactList )
    {
        int tc = 0;

        try
        {
            Hashtable currentContacts = ReadContactTask.readContacts();
            ContactList contacts = null;
            boolean isExist = false;
            pim = PIM.getInstance();
            try
            {
                contacts = (ContactList) pim.openPIMList( PIM.CONTACT_LIST, PIM.READ_WRITE );
            }
            catch ( PIMException e )
            {
                // An error occurred
                return;
            }
            for ( int i = 0; i < 10; i++ )
            {
                String contactDetail = (String) contactList.elementAt( i );

                if ( currentContacts.size() > 0 )
                {
                    if ( currentContacts.containsKey( contactDetail ) )
                    {
                        isExist = true;
                    }
                    if ( isExist == false )
                    {
                        this.nameToWrite = contactDetail.substring( 0, contactDetail.indexOf( "/" ) );
                        this.phoneToWrite = contactDetail.substring( nameToWrite.length() + 1, contactDetail.length() );
                        writeData( contacts, nameToWrite, phoneToWrite );
                    }
                    else
                    {
                        isExist = false;
                    }
                }
                else
                {
                    this.nameToWrite = contactDetail.substring( 0, contactDetail.indexOf( "/" ) );
                    this.phoneToWrite = contactDetail.substring( nameToWrite.length() + 1, contactDetail.length() );
                    writeData( contacts, nameToWrite, phoneToWrite );
                }

            }
            /*
             * check = new Alert( "Added", "Added ", null,
             * AlertType.CONFIRMATION ); check.setTimeout( Alert.FOREVER );
             * PIMWrite.display.setCurrent( check );
             */
            try
            {
                contacts.close();
            }
            catch ( PIMException e )
            {
            }

        }
        catch ( Exception e )
        {
            String errorMessage = e + " ; tc=" + tc;
            check = new Alert( "Response", errorMessage, null, AlertType.CONFIRMATION );
            check.setTimeout( Alert.FOREVER );
        }
    }

    public void writeData( ContactList contacts, String nameToWrite, String phoneToWrite )
    {
        Contact contact = contacts.createContact();

        String[] name = new String[contacts.stringArraySize( Contact.NAME )];

        if ( contacts.isSupportedField( Contact.TEL ) )
        {
            contact.addString( Contact.TEL, Contact.ATTR_MOBILE, phoneToWrite );
        }

        if ( contacts.isSupportedArrayElement( Contact.NAME, Contact.NAME_FAMILY ) )
        {
            name[Contact.NAME_FAMILY] = nameToWrite;
        }

        else if ( contacts.isSupportedArrayElement( Contact.NAME, Contact.NAME_GIVEN ) )
        {
            name[Contact.NAME_GIVEN] = nameToWrite;
        }
        else if ( contacts.isSupportedArrayElement( Contact.NAME, Contact.NAME_OTHER ) )
        {
            name[Contact.NAME_OTHER] = nameToWrite;
        }

        contact.addStringArray( Contact.NAME, PIMItem.ATTR_NONE, name );

        try
        {
            contact.commit();
        }
        catch ( PIMException e )
        {
            e.printStackTrace();
        }
    }

}
