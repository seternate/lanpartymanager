package helper;

import com.esotericsoftware.minlog.Log;

public class NoKryoLogging extends Log.Logger {
    @Override
    protected void print(String message) {
        //DO NOTHING
    }
}
