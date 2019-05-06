package helper.kryo;

import com.esotericsoftware.minlog.Log;

/**
 * Disables all logging from the {@code KryoNet}.
 *
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
public final class NoKryoLogging extends Log.Logger {

    @Override
    protected void print(String message) { }

}
