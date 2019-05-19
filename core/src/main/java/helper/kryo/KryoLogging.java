package helper.kryo;

import com.esotericsoftware.minlog.Log;

import java.io.*;
import java.util.Date;

/**
 * Disables all logging from the {@code KryoNet}.
 *
 * @author Levin Jeck
 * @version 1.0
 * @since 1.0
 */
public final class KryoLogging extends Log.Logger {

    @Override
    public void log(int level, String category, String message, Throwable ex) {
        super.log(level, category, message, ex);
        StringBuilder builder = new StringBuilder(256);
        builder.append(new Date() + " ");
        builder.append("[");
        switch(level){
            case 1: builder.append("TRACE"); break;
            case 2: builder.append("DEBUG"); break;
            case 3: builder.append("INFO"); break;
            case 4: builder.append("WARN"); break;
            case 5: builder.append("ERROR"); break;
            default: builder.append("NONE"); break;
        }
        builder.append("] : ");
        builder.append(message + "\n");
        File logfile = new File("log-kryo.txt");
        try {
            BufferedWriter logstream = new BufferedWriter(new FileWriter(logfile, true));
            logstream.append(builder);
            logstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void print(String message) {

    }

}
