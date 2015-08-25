package org.hisp.dhis.mobile.log;

import java.util.Enumeration;
import java.util.Vector;

/**
 * 
 * @author Paul Mark Castillo
 */
public class LogUtils
{

    /**
     * 
     * @param logTags
     * @return
     */
    public static boolean isValidIncludeTags( Vector logTags )
    {
        Vector includeTags = LogMan.getIncludeTags();

        if ( !includeTags.isEmpty() )
        {
            if ( logTags != null && logTags.size() > 0 )
            {

                // This validates if it includes the needed tags
                Enumeration includeTagsEnu = includeTags.elements();
                while ( includeTagsEnu.hasMoreElements() )
                {
                    String includeTag = (String) includeTagsEnu.nextElement();

                    boolean found = false;
                    Enumeration logTagEnu = logTags.elements();
                    while ( logTagEnu.hasMoreElements() )
                    {
                        String tag = (String) logTagEnu.nextElement();
                        if ( includeTag.equals( tag ) )
                        {
                            found = true;
                        }
                    }
                    if ( !found )
                    {
                        return false;
                    }
                }
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return true;
        }
    }

    /**
     * 
     * @param logTags
     * @return
     */
    public static boolean isValidExcludeTags( Vector logTags )
    {
        Vector excludeTags = LogMan.getExcludeTags();

        if ( !excludeTags.isEmpty() )
        {
            if ( logTags != null && logTags.size() > 0 )
            {

                // This validates if it excludes the specified tags
                Enumeration excludeTagsEnu = excludeTags.elements();
                while ( excludeTagsEnu.hasMoreElements() )
                {
                    String excludeTag = (String) excludeTagsEnu.nextElement();

                    Enumeration logTagEnu = logTags.elements();
                    while ( logTagEnu.hasMoreElements() )
                    {
                        String tag = (String) logTagEnu.nextElement();
                        if ( excludeTag.equals( tag ) )
                        {
                            return false;
                        }
                    }
                }
                return true;
            }
            else
            {
                return true;
            }
        }
        else
        {
            return true;
        }
    }

    /**
     * 
     * @param message
     * @return
     */
    public static boolean isValidIncludeMessage( String message )
    {
        Vector includeMessages = LogMan.getIncludeMessages();

        if ( !includeMessages.isEmpty() )
        {
            if ( message != null && !message.equals( "" ) )
            {
                Enumeration includeMessageEnu = includeMessages.elements();
                while ( includeMessageEnu.hasMoreElements() )
                {
                    String includeMessage = (String) includeMessageEnu.nextElement();
                    if ( message.indexOf( includeMessage ) > -1 )
                    {
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return true;
        }
    }

    /**
     * 
     * @param message
     * @return
     */
    public static boolean isValidExcludeMessage( String message )
    {
        Vector excludeMessages = LogMan.getExcludeMessages();

        if ( !excludeMessages.isEmpty() )
        {
            if ( message != null && !message.equals( "" ) )
            {
                Enumeration excludeMessageEnu = excludeMessages.elements();
                while ( excludeMessageEnu.hasMoreElements() )
                {
                    String excludeMessage = (String) excludeMessageEnu.nextElement();
                    if ( message.indexOf( excludeMessage ) > -1 )
                    {
                        return false;
                    }
                }
                return true;
            }
            else
            {
                return true;
            }
        }
        else
        {
            return true;
        }
    }

    /**
     * 
     * @param tags
     * @return
     */
    public static String toStringTags( Vector tags, String separator )
    {
        StringBuffer sb = new StringBuffer();

        if ( tags != null && !tags.isEmpty() )
        {
            Enumeration enu = tags.elements();
            while ( enu.hasMoreElements() )
            {
                String tag = (String) enu.nextElement();
                if ( sb.length() > 0 )
                {
                    sb.append( separator );
                }
                sb.append( tag );
            }
        }

        return sb.toString();
    }

    /**
     * 
     * @param tags
     * @return
     */
    public static Vector toVector( String tags )
    {
        Vector vt = new Vector();

        if ( tags != null && !tags.equals( "" ) )
        {
            String tagsStr[] = split( tags, "," );
            int tagsStrLength = tagsStr.length;
            for ( int i = 0; i < tagsStrLength; i++ )
            {
                vt.addElement( tagsStr[i] );
            }
        }

        return vt;
    }

    /**
     * 
     * @param log
     * @return
     */
    public static String composeLog( Log log, boolean includeTimeDay )
    {
        String timeDay = log.getTimeDay();
        String level = LogUtils.getLevelName( log.getLevel() );
        String tags = "[" + LogUtils.toStringTags( log.getTags(), "|" ) + "]";
        String message = log.getMessage();
        String logStr = level + " " + tags + " " + message;

        if ( includeTimeDay )
        {
            logStr = timeDay + " " + logStr;
        }
        
        return logStr;
    }

    /**
     *
     */
    public static void clearConsole()
    {
        for ( int i = 0; i < 50; i++ )
        {
            System.out.println();
        }
    }

    /**
     * 
     * @param level
     * @return
     */
    public static String getLevelName( int level )
    {
        switch ( level )
        {
        case LogMan.DISABLED:
            return "DISABLED";
        case LogMan.DEBUG:
            return "DEBUG";
        case LogMan.INFO:
            return "INFO";
        case LogMan.DEV:
            return "DEV";
        case LogMan.WARN:
            return "WARN";
        case LogMan.ERROR:
            return "ERROR";
        case LogMan.FATAL:
            return "FATAL";
        default:
            return "";
        }
    }

    /**
     * 
     * @param level
     * @return
     */
    public static int getLevelInt( String level )
    {
        if ( level != null )
        {
            if ( level.equals( "DISABLED" ) )
            {
                return -1;
            }
            else if ( level.equals( "DEBUG" ) )
            {
                return 0;
            }
            else if ( level.equals( "INFO" ) )
            {
                return 1;
            }
            else if ( level.equals( "DEV" ) )
            {
                return 2;
            }
            else if ( level.equals( "WARN" ) )
            {
                return 3;
            }
            else if ( level.equals( "ERROR" ) )
            {
                return 4;
            }
            else if ( level.equals( "FATAL" ) )
            {
                return 5;
            }
            else
            {
                return -1;
            }
        }
        else
        {
            return -1;
        }
    }

    /**
     * 
     * @param original
     * @param separator
     * @return
     */
    public static String[] split( String original, String separator )
    {
        Vector nodes = new Vector();
        // Parse nodes into vector
        int index = original.indexOf( separator );
        while ( index >= 0 )
        {
            nodes.addElement( original.substring( 0, index ) );
            original = original.substring( index + separator.length() );
            index = original.indexOf( separator );
        }
        // Get the last node
        nodes.addElement( original );

        // Create parse string array
        String[] result = new String[nodes.size()];
        if ( nodes.size() > 0 )
        {
            for ( int loop = 0; loop < nodes.size(); loop++ )
            {
                result[loop] = ((String) nodes.elementAt( loop )).trim();
            }
        }
        return result;
    }
}
