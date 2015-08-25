package org.hisp.dhis.mobile.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

public class ProgramInstance
    implements DataStreamSerializable
{

    private int id;

    private int patientId;

    private int programId;

    private String name;

    // status active = 0
    // status complete = 1
    // status canceled = 2
    private int status;

    private String dateOfEnrollment;

    private String dateOfIncident;

    private Vector programStageInstances = new Vector();

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public int getPatientId()
    {
        return patientId;
    }

    public void setPatientId( int patientId )
    {
        this.patientId = patientId;
    }

    public int getProgramId()
    {
        return programId;
    }

    public void setProgramId( int programId )
    {
        this.programId = programId;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus( int status )
    {
        this.status = status;
    }

    public String getDateOfEnrollment()
    {
        return dateOfEnrollment;
    }

    public void setDateOfEnrollment( String dateOfEnrollment )
    {
        this.dateOfEnrollment = dateOfEnrollment;
    }

    public String getDateOfIncident()
    {
        return dateOfIncident;
    }

    public void setDateOfIncident( String dateOfIncident )
    {
        this.dateOfIncident = dateOfIncident;
    }

    public Vector getProgramStageInstances()
    {
        return programStageInstances;
    }

    public void setProgramStageInstances( Vector programStageInstances )
    {
        this.programStageInstances = programStageInstances;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public void serialize( DataOutputStream dataOutputStream )
        throws IOException
    {
        dataOutputStream.writeInt( this.getId() );
        dataOutputStream.writeInt( this.getPatientId() );
        dataOutputStream.writeInt( this.getProgramId() );
        dataOutputStream.writeUTF( this.getName() );
        dataOutputStream.writeInt( this.getStatus() );
        dataOutputStream.writeUTF( this.getDateOfEnrollment() );
        dataOutputStream.writeUTF( this.getDateOfIncident() );

        dataOutputStream.writeInt( programStageInstances.size() );
        for ( int i = 0; i < programStageInstances.size(); i++ )
        {
            ProgramStage programStageInstance = (ProgramStage) programStageInstances.elementAt( i );
            programStageInstance.serialize( dataOutputStream );
        }

    }

    public void deSerialize( DataInputStream dataInputStream )
        throws IOException
    {
        this.setId( dataInputStream.readInt() );
        this.setPatientId( dataInputStream.readInt() );
        this.setProgramId( dataInputStream.readInt() );
        this.setName( dataInputStream.readUTF() );
        this.setStatus( dataInputStream.readInt() );
        this.setDateOfEnrollment( dataInputStream.readUTF() );
        this.setDateOfIncident( dataInputStream.readUTF() );

        // Read programstage instance
        int programStageInstanceSize = dataInputStream.readInt();
        for ( int i = 0; i < programStageInstanceSize; i++ )
        {
            ProgramStage programStageInstance = new ProgramStage();
            programStageInstance.deSerialize( dataInputStream );
            programStageInstances.addElement( programStageInstance );
        }
    }

}
