package be.dieterblancke.proxysync.common.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class StreamUtil
{

    private StreamUtil()
    {
    }

    public static String streamToString( final InputStream is )
    {
        if ( is == null )
        {
            return null;
        }

        try ( InputStreamReader isr = new InputStreamReader( is );
              BufferedReader br = new BufferedReader( isr ) )
        {
            final StringBuilder sb = new StringBuilder();
            String str;
            while ( ( str = br.readLine() ) != null )
            {
                sb.append( str ).append( System.getProperty( "line.separator" ) );
            }
            return sb.toString();
        }
        catch ( Exception exception )
        {
            exception.printStackTrace();
            return null;
        }
    }
}

