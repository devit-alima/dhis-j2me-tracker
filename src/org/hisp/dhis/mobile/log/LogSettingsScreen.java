package org.hisp.dhis.mobile.log;

import javax.microedition.lcdui.*;

/**
 * 
 * @author Paul Mark Castillo
 */
public class LogSettingsScreen
    extends Form
    implements CommandListener
{

    /**
     *
     */
    ChoiceGroup enabled = new ChoiceGroup( "Enabled/Disabled: ", ChoiceGroup.POPUP );

    /**
     *
     */
    ChoiceGroup levels = new ChoiceGroup( "Level: ", ChoiceGroup.POPUP );

    /**
     *
     */
    TextField bufferSize = new TextField( "Buffer Size: ", "", 50, TextField.NUMERIC );

    /**
     *
     */
    TextField includeTags = new TextField( "Tags (Include): ", "", 256, TextField.ANY );

    /**
     *
     */
    TextField excludeTags = new TextField( "Tags (Exclude): ", "", 256, TextField.ANY );

    /**
     * 
     */
    TextField includeMessages = new TextField( "Message (Include): ", "", 256, TextField.ANY );

    /**
     * 
     */
    TextField excludeMessages = new TextField( "Message (Exclude): ", "", 256, TextField.ANY );

    /**
     *
     */
    Command saveCommand = new Command( "Save", Command.OK, 0 );

    /**
     *
     */
    Command backCommand = new Command( "Back", Command.BACK, 1 );

    /**
     *
     */
    public LogSettingsScreen()
    {
        super( "Log Settings" );

        enabled.append( "ENABLED", null );
        enabled.append( "DISABLED", null );
        append( enabled );

        levels.append( "DISABLED", null ); // 0
        levels.append( "DEBUG", null ); // 1
        levels.append( "INFO", null ); // 2
        levels.append( "DEV", null ); // 3
        levels.append( "WARN", null ); // 4
        levels.append( "ERROR", null ); // 5
        levels.append( "FATAL", null ); // 6
        append( levels );

        append( bufferSize );
        append( includeTags );
        append( excludeTags );
        append( includeMessages );
        append( excludeMessages );

        addCommand( saveCommand );
        addCommand( backCommand );
        setCommandListener( this );

    }

    /**
     *
     */
    public void refreshSettings()
    {
        if ( LogMan.isEnabled() )
        {
            selectChoice( enabled, "ENABLED" );
        }
        else
        {
            selectChoice( enabled, "DISABLED" );
        }

        switch ( LogMan.getLevel() )
        {
        case LogMan.DISABLED:
            selectChoice( levels, "DISABLED" );
            break;
        case LogMan.DEBUG:
            selectChoice( levels, "DEBUG" );
            break;
        case LogMan.INFO:
            selectChoice( levels, "INFO" );
            break;
        case LogMan.DEV:
            selectChoice( levels, "DEV" );
            break;
        case LogMan.WARN:
            selectChoice( levels, "WARN" );
            break;
        case LogMan.ERROR:
            selectChoice( levels, "ERROR" );
            break;
        case LogMan.FATAL:
            selectChoice( levels, "FATAL" );
            break;
        }

        String bufferSizeStr = LogMan.getBuffSize() + "";
        int bufferSizeStrCount = bufferSizeStr.length();
        if ( bufferSizeStrCount > bufferSize.getMaxSize() )
        {
            bufferSize.setMaxSize( bufferSizeStrCount );
        }
        bufferSize.setString( bufferSizeStr );

        String includeTagsStr = LogMan.getIncludeTagsStr();
        int includeTagsStrCount = includeTagsStr.length();
        if ( includeTagsStrCount > includeTags.getMaxSize() )
        {
            includeTags.setMaxSize( includeTagsStrCount );
        }
        includeTags.setString( includeTagsStr );

        String excludeTagsStr = LogMan.getExcludeTagsStr();
        int excludeTagsStrCount = excludeTagsStr.length();
        if ( excludeTagsStrCount > excludeTags.getMaxSize() )
        {
            excludeTags.setMaxSize( excludeTagsStr.length() );
        }
        excludeTags.setString( excludeTagsStr );

        String includeMessagesStr = LogMan.getIncludeMessagesStr();
        int includeMessagesStrCount = includeMessagesStr.length();
        if ( includeMessagesStrCount > includeMessages.getMaxSize() )
        {
            includeMessages.setMaxSize( includeMessagesStrCount );
        }
        includeMessages.setString( includeMessagesStr );

        String excludeMessagesStr = LogMan.getExcludeMessagesStr();
        int excludeMessagesStrCount = excludeMessagesStr.length();
        if ( excludeMessagesStrCount > excludeMessages.getMaxSize() )
        {
            excludeMessages.setMaxSize( excludeMessagesStrCount );
        }
        excludeMessages.setString( excludeMessagesStr );
    }

    /**
     * 
     * @param choice
     * @param str
     */
    private void selectChoice( ChoiceGroup choice, String str )
    {
        int levelsCount = choice.size();
        for ( int i = 0; i < levelsCount; i++ )
        {
            if ( choice.getString( i ).equals( str ) )
            {
                choice.setSelectedIndex( i, true );
            }
        }
    }

    /**
     *
     */
    public void saveCommand()
    {
        if ( checkBufferSize() )
        {
            System.out.println( "[LOG] Saving settings..." );
            String enable = enabled.getString( enabled.getSelectedIndex() );
            if ( enable.equals( "ENABLED" ) )
            {
                LogMan.setEnabled( true );
            }
            else if ( enable.equals( "DISABLED" ) )
            {
                LogMan.setEnabled( false );
            }

            String level = levels.getString( levels.getSelectedIndex() );
            if ( level.equals( "DISABLED" ) )
            {
                LogMan.setLevel( LogMan.DISABLED );
            }
            else if ( level.equals( "DEBUG" ) )
            {
                LogMan.setLevel( LogMan.DEBUG );
            }
            else if ( level.equals( "INFO" ) )
            {
                LogMan.setLevel( LogMan.INFO );
            }
            else if ( level.equals( "DEV" ) )
            {
                LogMan.setLevel( LogMan.DEV );
            }
            else if ( level.equals( "WARN" ) )
            {
                LogMan.setLevel( LogMan.WARN );
            }
            else if ( level.equals( "ERROR" ) )
            {
                LogMan.setLevel( LogMan.ERROR );
            }
            else if ( level.equals( "FATAL" ) )
            {
                LogMan.setLevel( LogMan.FATAL );
            }

            LogMan.setBuffSize( Integer.parseInt( bufferSize.getString() ) );
            LogMan.setIncludeTags( includeTags.getString() );
            LogMan.setExcludeTags( excludeTags.getString() );
            LogMan.setIncludeMessages( includeMessages.getString() );
            LogMan.setExcludeMessages( excludeMessages.getString() );
            LogMan.refreshLogs();

            backCommand();
        }
    }

    /**
     * 
     * @return
     */
    private boolean checkBufferSize()
    {
        try
        {
            int size = Integer.parseInt( bufferSize.getString() );
            if ( size > 0 )
            {
                return true;
            }
            else
            {
                LogMan.getDisplay().setCurrent(
                    new Alert( "Alert", "Buffer Size should be higher than zero.", null, AlertType.ERROR ) );
                return false;
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            LogMan.getDisplay().setCurrent( new Alert( "Alert", "Buffer Size in invalid", null, AlertType.ERROR ) );
            return false;
        }
    }

    /**
     *
     */
    public void backCommand()
    {
        LogMan.showPreviousScreen();
    }

    /**
     * 
     * @param c
     * @param d
     */
    public void commandAction( Command c, Displayable d )
    {
        if ( c == saveCommand )
        {
            saveCommand();
        }
        else if ( c == backCommand )
        {
            backCommand();
        }
    }
}
