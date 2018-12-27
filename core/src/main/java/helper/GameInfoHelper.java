package helper;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.VerRsrc.VS_FIXEDFILEINFO;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Scanner;


public class GameInfoHelper {
    public static final String PRODUCTNAME = "ProductName",
                               FILEVERSION = "FileVersion";

    /*
    public static String[] getFileInfo(String filePath){
        String[] fileInfo = new String[2];
        IntByReference dwDummy = new IntByReference();
        dwDummy.setValue(0);

        int versionlength = com.sun.jna.platform.win32.Version.INSTANCE.GetFileVersionInfoSize(filePath, dwDummy);

        byte[] bufferarray = new byte[versionlength];
        if(bufferarray.length == 0){
            String[] str = {"",""};
            return str;
        }
        Pointer lpData = new Memory(bufferarray.length);
        PointerByReference lplpBuffer = new PointerByReference();
        PointerByReference lplpBufferName = new PointerByReference();
        IntByReference puLen = new IntByReference();
        IntByReference puLenName = new IntByReference();

        boolean fileInfoResult = com.sun.jna.platform.win32.Version.INSTANCE.GetFileVersionInfo(filePath, 0, versionlength, lpData);
        boolean verQueryVal = com.sun.jna.platform.win32.Version.INSTANCE.VerQueryValue(lpData, "\\", lplpBuffer, puLen);
        boolean verQueryValName = com.sun.jna.platform.win32.Version.INSTANCE.VerQueryValue(lpData, "\\StringFileInfo\\040904e4\\ProductName", lplpBufferName, puLenName);

        VS_FIXEDFILEINFO lplpBufStructure = new VS_FIXEDFILEINFO(lplpBuffer.getValue());
        lplpBufStructure.read();

        int v1 = (lplpBufStructure.dwFileVersionMS).intValue() >> 16;
        int v2 = (lplpBufStructure.dwFileVersionMS).intValue() & 0xffff;
        int v3 = (lplpBufStructure.dwFileVersionLS).intValue() >> 16;
        int v4 = (lplpBufStructure.dwFileVersionLS).intValue() & 0xffff;
        fileInfo[0] = v1 + "." + v2 + "." + v3 + "." + v4;

        int nameLen = new Integer(puLenName.getValue());
        char[] charBuf = new char[nameLen];
        lplpBufferName.getValue().read(0, charBuf,0,nameLen);
        fileInfo[1] = new String(charBuf);

        return fileInfo;
    }
    */

    private static String getInfo(String info, String filePath){
        IntByReference dwDummy = new IntByReference();
        dwDummy.setValue(0);

        int versionlength = com.sun.jna.platform.win32.Version.INSTANCE.GetFileVersionInfoSize(filePath, dwDummy);

        byte[] bufferarray = new byte[versionlength];
        Pointer lpData = new Memory(bufferarray.length);
        PointerByReference lplpBuffer = new PointerByReference();
        IntByReference puLen = new IntByReference();

        boolean fileInfoResult = com.sun.jna.platform.win32.Version.INSTANCE.GetFileVersionInfo(filePath, 0, versionlength, lpData);
        boolean verQueryVal = com.sun.jna.platform.win32.Version.INSTANCE.VerQueryValue(lpData, "\\StringFileInfo\\040904e4\\" + info, lplpBuffer, puLen);

        int infoLen = new Integer(puLen.getValue());
        char[] charBuf = new char[infoLen];
        lplpBuffer.getValue().read(0, charBuf,0,infoLen);
        return new String(charBuf);
    }

    //Todo
    public static String getVersion(String filePath){
        String root = GameFolderHelper.getRootFolder(filePath);
        return getInfo(FILEVERSION, root);
    }

    public static String fileVersion(String file, String query){
        String root = GameFolderHelper.getRootFolder(file);
        File vFile = new File(root);
        System.out.println(vFile.getAbsolutePath());
        Scanner scr = null;
        try {
            scr = new Scanner(vFile);
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

    public static String dateVersion(String filePath){
        String root = GameFolderHelper.getRootFolder(filePath);
        File file = new File(root);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        return sdf.format(file.lastModified());
    }

    private GameInfoHelper(){}
}
