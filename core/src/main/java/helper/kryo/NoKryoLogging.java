package helper.kryo;

import com.esotericsoftware.minlog.Log;

public final class NoKryoLogging extends Log.Logger {
    @Override
    protected void print(String message) { }
}
