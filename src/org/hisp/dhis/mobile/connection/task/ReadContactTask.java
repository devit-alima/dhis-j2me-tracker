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

import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.pim.Contact;
import javax.microedition.pim.ContactList;
import javax.microedition.pim.PIM;
import javax.microedition.pim.PIMException;

import org.hisp.dhis.mobile.log.LogMan;

/**
 * @author Nguyen Kim Lai
 * 
 * @version ReadContactTask.java 10:19:30 AM Mar 21, 2013 $
 */
public class ReadContactTask
    extends AbstractTask
{
    private static final String CLASS_TAG = "ReadContactTask";

    private static PIM pim;

    public static boolean readCompleted = false;

    public void run()
    {
        LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "Starting ReadContactTask..." );
        /*ConnectionManager.getDhisMIDlet().getCurrentContactView().prepareView();

        Hashtable currentContacts = readContacts();

        Enumeration enumeration = currentContacts.keys();

        int numb = 1;

        while ( enumeration.hasMoreElements() )
        {
            String contactDetail = (String) enumeration.nextElement();
            CurrentContactView.contactListForm.append( "(" + numb + ")" + contactDetail + "\r\n" );
            numb++;
        }

        ConnectionManager.getDhisMIDlet().getCurrentContactView().showView();*/
    }

    public static synchronized Hashtable readContacts()
    {
        LogMan.log( LogMan.INFO, "Network," + CLASS_TAG, "reading contacts..." );

        Hashtable listOfPreviousContact = new Hashtable();

        ContactList contacts = null;

        Enumeration enumeration = null;

        Contact contact;

        String contactName = "";

        String contactNo = "";

        pim = PIM.getInstance();

        // String[] lists = pim.listPIMLists( pim.CONTACT_LIST );

        try
        {
            contacts = (ContactList) pim.openPIMList( pim.CONTACT_LIST, pim.READ_WRITE );
        }
        catch ( Exception e )
        {
            LogMan.log( "Network,"+CLASS_TAG, e );
        }

        try
        {
            enumeration = contacts.items();
        }
        catch ( Exception e )
        {
            LogMan.log( "Network,"+CLASS_TAG, e );
        }
        while ( enumeration.hasMoreElements() )
        {
            contact = (Contact) enumeration.nextElement();

            try
            {
                if ( contacts.isSupportedField( Contact.FORMATTED_NAME )
                    && (contact.countValues( Contact.FORMATTED_NAME ) > 0) )
                {
                    contactName = contact.getString( Contact.FORMATTED_NAME, 0 );
                }
            }
            catch ( Exception e )
            {
                LogMan.log( "Network,"+CLASS_TAG, e );
            }

            int phoneNos = contact.countValues( Contact.TEL );
            try
            {
                if ( contacts.isSupportedField( Contact.TEL ) && (contact.countValues( Contact.TEL ) > 0) )
                {
                    contactNo = contact.getString( contact.TEL, 0 );
                }
            }

            catch ( Exception e )
            {
                LogMan.log( "Network,"+CLASS_TAG, e );
            }
            listOfPreviousContact.put( contactName + "/" + contactNo, "" );

        }
        readCompleted = true;
        try
        {
            contacts.close();
        }
        catch ( PIMException e )
        {
            LogMan.log( "Network,PIMException,"+CLASS_TAG, e );
        }
        finally
        {
            contacts = null;
            enumeration = null;
            System.gc();
        }
        return listOfPreviousContact;
    }
}
