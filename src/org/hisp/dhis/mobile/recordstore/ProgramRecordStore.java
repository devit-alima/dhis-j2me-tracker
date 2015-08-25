package org.hisp.dhis.mobile.recordstore;

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

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;

import org.hisp.dhis.mobile.log.LogMan;
import org.hisp.dhis.mobile.midlet.DHISMIDlet;
import org.hisp.dhis.mobile.model.Model;
import org.hisp.dhis.mobile.model.ModelList;
import org.hisp.dhis.mobile.model.OrgUnit;
import org.hisp.dhis.mobile.model.Patient;
import org.hisp.dhis.mobile.model.Program;
import org.hisp.dhis.mobile.model.ProgramInstance;
import org.hisp.dhis.mobile.model.ProgramStage;
import org.hisp.dhis.mobile.recordstore.filter.ProgramFilter;
import org.hisp.dhis.mobile.util.SerializationUtil;

public class ProgramRecordStore
{
    public static final String CLASS_TAG = "ProgramRecordStore";
    
    public static final String PROGRAM_DB = "PROGRAM";

    public static boolean savePrograms( Vector programVector )
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        for ( int i = 0; i < programVector.size(); i++ )
        {
            Program program = (Program) programVector.elementAt( i );
            if ( !saveProgram( program ) )
            {
                return false;
            }
        }
        return true;
    }

    public static Vector getProgramByType( int type )
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        Vector programs = new Vector();
        RecordStore recordStore = null;
        RecordEnumeration recordEnumeration = null;

        try
        {
            recordStore = RecordStore.openRecordStore( PROGRAM_DB, true );
            recordEnumeration = recordStore.enumerateRecords( null, null, false );
            while ( recordEnumeration.hasNextElement() )
            {
                Program program = new Program();
                SerializationUtil.deSerialize( program, recordEnumeration.nextRecord() );
                if ( program.getType() == type )
                {
                    programs.addElement( program );
                }
            }

        }
        finally
        {
            recordEnumeration.destroy();
            recordStore.closeRecordStore();
            System.gc();
        }

        return programs;
    }

    public static Vector getNonAnonymousPrograms()
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        Vector programs = new Vector();
        RecordStore recordStore = null;
        RecordEnumeration recordEnumeration = null;

        try
        {
            recordStore = RecordStore.openRecordStore( PROGRAM_DB, true );
            recordEnumeration = recordStore.enumerateRecords( null, null, false );
            while ( recordEnumeration.hasNextElement() )
            {
                Program program = new Program();
                SerializationUtil.deSerialize( program, recordEnumeration.nextRecord() );
                if ( program.getType() != 3 )
                {
                    programs.addElement( program );
                }
            }

        }
        finally
        {
            recordEnumeration.destroy();
            recordStore.closeRecordStore();
            System.gc();
        }

        return programs;
    }

    public static boolean saveProgram( Program program )
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        RecordStore recordStore = null;
        RecordEnumeration recordEnumeration = null;
        ProgramFilter programFilter = new ProgramFilter();
        programFilter.setProgramID( program.getId() );

        try
        {
            recordStore = RecordStore.openRecordStore( PROGRAM_DB, true );
            recordEnumeration = recordStore.enumerateRecords( programFilter, null, false );
            byte[] bite = SerializationUtil.serialize( program );
            if ( recordEnumeration.numRecords() > 0 )
            {
                int id = recordEnumeration.nextRecordId();
                recordStore.setRecord( id, bite, 0, bite.length );
            }
            else
            {
                recordStore.addRecord( bite, 0, bite.length );
            }
        }
        finally
        {
            recordEnumeration.destroy();
            recordStore.closeRecordStore();
            System.gc();
        }

        return true;
    }

    public static boolean saveProgram( Program program, OrgUnit orgUnit )
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        RecordStore recordStore = null;
        RecordEnumeration recordEnumeration = null;
        ProgramFilter programFilter = new ProgramFilter();
        programFilter.setProgramID( program.getId() );

        try
        {
            recordStore = RecordStore.openRecordStore( PROGRAM_DB + orgUnit.getId(), true );
            recordEnumeration = recordStore.enumerateRecords( programFilter, null, false );
            byte[] bite = SerializationUtil.serialize( program );
            if ( recordEnumeration.numRecords() > 0 )
            {
                int id = recordEnumeration.nextRecordId();
                recordStore.setRecord( id, bite, 0, bite.length );
            }
            else
            {
                recordStore.addRecord( bite, 0, bite.length );
            }
        }
        finally
        {
            recordEnumeration.destroy();
            recordStore.closeRecordStore();
            System.gc();
        }

        return true;
    }

    public static Program getProgram( int programID )
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        RecordStore recordStore = null;
        RecordEnumeration recordEnumeration = null;
        ProgramFilter programFilter = new ProgramFilter();
        programFilter.setProgramID( programID );

        try
        {
            recordStore = RecordStore.openRecordStore( PROGRAM_DB, true );
            recordEnumeration = recordStore.enumerateRecords( programFilter, null, false );

            if ( recordEnumeration.numRecords() > 0 )
            {
                Program program = new Program();
                SerializationUtil.deSerialize( program, recordEnumeration.nextRecord() );
                return program;
            }

        }
        finally
        {
            recordEnumeration.destroy();
            recordStore.closeRecordStore();
            System.gc();
        }
        return null;
    }

    public static ProgramStage getProgramStage( int programStageID, int programID )
    {
        Program program = null;
        try
        {
            program = ProgramRecordStore.getProgram( programID );

            for ( int i = 0; i < program.getProgramStages().size(); i++ )
            {
                if ( programStageID == ((ProgramStage) program.getProgramStages().elementAt( i )).getId() )
                {
                    return (ProgramStage) program.getProgramStages().elementAt( i );
                }
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            LogMan.log( "RMS," + CLASS_TAG, e );
        }
        return null;
    }

    /**
     * 
     * @return
     * @throws RecordStoreFullException
     * @throws RecordStoreNotFoundException
     * @throws RecordStoreException
     * @throws IOException
     */
    public static ModelList getCurrentPrograms()
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        ModelList modelList = new ModelList();
        RecordStore recordStore = null;
        RecordEnumeration recordEnumeration = null;
        try
        {
            recordStore = RecordStore.openRecordStore( PROGRAM_DB, true );
            recordEnumeration = recordStore.enumerateRecords( null, null, false );

            while ( recordEnumeration.hasNextElement() )
            {
                Program program = new Program();
                Model model = new Model();

                SerializationUtil.deSerialize( program, recordEnumeration.nextRecord() );
                model.setId( program.getId() );
                model.setName( program.getName() );
                modelList.getModels().addElement( model );
            }
        }
        finally
        {
            recordEnumeration.destroy();
            recordStore.closeRecordStore();
            System.gc();
        }
        return modelList;
    }

    public static Vector getAllProgram()
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        Vector programs = new Vector();
        RecordStore recordStore = null;
        RecordEnumeration recordEnumeration = null;

        try
        {
            recordStore = RecordStore.openRecordStore( PROGRAM_DB, true );
            recordEnumeration = recordStore.enumerateRecords( null, null, false );
            while ( recordEnumeration.hasNextElement() )
            {
                Program program = new Program();
                SerializationUtil.deSerialize( program, recordEnumeration.nextRecord() );
                programs.addElement( program );
            }

        }
        finally
        {
            recordEnumeration.destroy();
            recordStore.closeRecordStore();
            System.gc();
        }

        return programs;
    }

    public static Vector getEnrollablePrograms( Patient patient )
        throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException
    {
        Hashtable alreadyEnrollPrograms = new Hashtable();
        Vector enrollablePrograms = new Vector();
        for ( int i = 0; i < patient.getCompletedPrograms().size(); i++ )
        {
            ProgramInstance programInstance = (ProgramInstance) patient.getCompletedPrograms().elementAt( i );
            alreadyEnrollPrograms.put( String.valueOf( programInstance.getProgramId() ), "" );
        }

        for ( int i = 0; i < patient.getEnrollmentPrograms().size(); i++ )
        {
            ProgramInstance programInstance = (ProgramInstance) patient.getEnrollmentPrograms().elementAt( i );
            alreadyEnrollPrograms.put( String.valueOf( programInstance.getProgramId() ), "" );
        }

        for ( int i = 0; i < getAllProgram().size(); i++ )
        {
            Program program = (Program) getAllProgram().elementAt( i );
            if ( alreadyEnrollPrograms.get( String.valueOf( program.getId() ) ) == null )
            {
                enrollablePrograms.addElement( program );
            }
        }

        return enrollablePrograms;
    }
}
