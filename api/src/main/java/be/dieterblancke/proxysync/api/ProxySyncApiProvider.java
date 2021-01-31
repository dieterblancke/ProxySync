package be.dieterblancke.proxysync.api;

public class ProxySyncApiProvider
{
    private static ProxySyncApi api;

    public static ProxySyncApi getApi()
    {
        return api;
    }

    public static void setApiInstance( final ProxySyncApi instance )
    {
        if ( api != null )
        {
            return;
        }
        api = instance;
    }
}
