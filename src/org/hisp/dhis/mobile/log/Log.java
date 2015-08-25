package org.hisp.dhis.mobile.log;

import java.util.Date;
import java.util.Vector;

/**
 * 
 * @author Paul Mark Castillo
 */
public class Log
{

    /**
     *
     */
    private String timeDay;

    /**
     *
     */
    private int level;

    /**
     *
     */
    private Vector tags = new Vector();

    /**
     *
     */
    private String message;

    /**
     * 
     */
    public Log()
    {
        long current = System.currentTimeMillis();
        Date d = new Date( current );
        timeDay = d.toString();
    }

    /**
     * 
     * @param tag
     */
    public void addTag( String tag )
    {
        if ( tag != null && !tag.equals( "" ) )
        {
            tags.addElement( tag );
        }
    }

    /**
     * 
     * @param tags
     */
    public void addTags( String tags )
    {
        if ( tags != null && !tags.equals( "" ) )
        {
            String tagArray[] = LogUtils.split( tags, "," );
            int tagArrayCount = tagArray.length;
            for ( int i = 0; i < tagArrayCount; i++ )
            {
                addTag( tagArray[i] );
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="GETTER/SETTER METHODS">
    /**
     * @return the timeDay
     */
    public String getTimeDay()
    {
        return timeDay;
    }

    /**
     * @param timeDay the timeDay to set
     */
    public void setTimeDay( String timeDay )
    {
        this.timeDay = timeDay;
    }

    /**
     * @return the level
     */
    public int getLevel()
    {
        return level;
    }

    /**
     * @param level the level to set
     */
    public void setLevel( int level )
    {
        this.level = level;
    }

    /**
     * @return the tags
     */
    public Vector getTags()
    {
        return tags;
    }

    /**
     * @param tags the tags to set
     */
    public void setTags( Vector tags )
    {
        if ( tags != null )
        {
            this.tags = tags;
        }
    }

    /**
     * @return the message
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage( String message )
    {
        this.message = message;
    }
    // </editor-fold>
}
