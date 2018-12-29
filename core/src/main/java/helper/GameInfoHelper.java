package helper;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.VerRsrc.VS_FIXEDFILEINFO;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.Scanner;


public class GameInfoHelper {

    public static String getVersion(Properties properties){
        switch (properties.getProperty("version.format")){
            case "file": return fileVersion(properties.getProperty("version.file"), properties.getProperty("version.query"));
            case "date": return dateVersion(properties.getProperty("exe.file"));
            default: return getVersion(properties.getProperty("exe.file"));
        }
    }

    private static String fileVersion(String file, String query){
        String absolutePath = GameFolderHelper.getAbsolutePath(file);
        if(absolutePath.equals("")) return "";
        File versionFile = new File(absolutePath);
        Scanner scr = null;
        try {
            scr = new Scanner(versionFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while(scr.hasNextLine()){
            String line = scr.nextLine();
            if(line.contains(query)){
                return line.substring(query.length());
            }
        }
        return "";
    }

    private static String dateVersion(String filePath){
        String absolutePath = GameFolderHelper.getAbsolutePath(filePath);
        if(absolutePath.equals("")) return "";
        File file = new File(absolutePath);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        return sdf.format(file.lastModified());
    }

    private static String getVersion(String filePath){
        String absolutePath = GameFolderHelper.getAbsolutePath(filePath);
        if(absolutePath.equals("")) return "";
        IntByReference dwDummy = new IntByReference();
        dwDummy.setValue(0);

        int versionlength = com.sun.jna.platform.win32.Version.INSTANCE.GetFileVersionInfoSize(absolutePath, dwDummy);

        byte[] bufferarray = new byte[versionlength];
        Pointer lpData = new Memory(bufferarray.length);
        PointerByReference lplpBuffer = new PointerByReference();
        IntByReference puLen = new IntByReference();

        boolean fileInfoResult = com.sun.jna.platform.win32.Version.INSTANCE.GetFileVersionInfo(absolutePath, 0, versionlength, lpData);
        boolean verQueryVal = com.sun.jna.platform.win32.Version.INSTANCE.VerQueryValue(lpData, "\\", lplpBuffer, puLen);
        //boolean verQueryVal = com.sun.jna.platform.win32.Version.INSTANCE.VerQueryValue(lpData, "\\StringFileInfo\\040904e4\\" + info, lplpBuffer, puLen);


        VS_FIXEDFILEINFO lplpBufStructure = new VS_FIXEDFILEINFO(lplpBuffer.getValue());
        lplpBufStructure.read();

        int v1 = (lplpBufStructure.dwFileVersionMS).intValue() >> 16;
        int v2 = (lplpBufStructure.dwFileVersionMS).intValue() & 0xffff;
        int v3 = (lplpBufStructure.dwFileVersionLS).intValue() >> 16;
        int v4 = (lplpBufStructure.dwFileVersionLS).intValue() & 0xffff;
        return v1 + "." + v2 + "." + v3 + "." + v4;
    }

    private GameInfoHelper(){}
}
