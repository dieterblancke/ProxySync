package be.dieterblancke.proxysync.common.redis.codec;

import io.lettuce.core.codec.RedisCodec;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class StringToByteArrayCodec implements RedisCodec<String, byte[]>
{

    private static final byte[] EMPTY = new byte[0];

    @Override
    public String decodeKey( final ByteBuffer byteBuffer )
    {
        final byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get( bytes );

        return new String( bytes, StandardCharsets.UTF_8 );
    }

    @Override
    public byte[] decodeValue( final ByteBuffer byteBuffer )
    {
        int remaining = byteBuffer.remaining();
        if ( remaining == 0 )
        {
            return EMPTY;
        }
        else
        {
            byte[] b = new byte[remaining];
            byteBuffer.get( b );
            return b;
        }
    }

    @Override
    public ByteBuffer encodeKey( final String s )
    {
        return ByteBuffer.wrap( s.getBytes( StandardCharsets.UTF_8 ) );
    }

    @Override
    public ByteBuffer encodeValue( final byte[] value )
    {
        return value == null ? ByteBuffer.wrap( EMPTY ) : ByteBuffer.wrap( value );
    }
}
