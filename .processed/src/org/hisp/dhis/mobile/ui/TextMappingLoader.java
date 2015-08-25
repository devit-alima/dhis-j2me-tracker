package org.hisp.dhis.mobile.ui;

import java.io.IOException;
import java.util.Hashtable;

import org.hisp.dhis.mobile.model.TextMapping;
import org.hisp.dhis.mobile.util.Properties;

public class TextMappingLoader
{

    private static final String PREFIX = "/properties/";

    private static final String SUFFIX = ".properties";

    public static TextMapping load( String locale )
        throws IOException
    {
        if ( locale.equalsIgnoreCase( "en-GB" ) )
        {
            return null;
        }
        else
        {
            String resourcePath = PREFIX + locale + SUFFIX;
            Hashtable textTable = Properties.load( resourcePath );
            TextMapping textMapping = new TextMapping();
            textMapping.setTextTable( textTable );
            return textMapping;
        }
    }

}
