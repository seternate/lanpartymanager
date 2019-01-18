package helper;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.VerRsrc.VS_FIXEDFILEINFO;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import entities.Game;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Scanner;

public abstract class GameInfoHelper {

    public static String getVersion(Game game){
        switch(game.getVersion().format){
            case "file": return getVersionFile(game.getVersion().file, game.getVersion().query);
            case "exe": return getVersionExe(game.getExeFileRelative());
            default: return "";
        }
    }

    private static String getVersionFile(String file, String query){
        String absolutePath = GameFolderHelper.getAbsolutePath(file);
        if(absolutePath== null)
            return null;
        File versionFile = new File(absolutePath);
        Scanner scr = null;
        try {
            scr = new Scanner(versionFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while(Objects.requireNonNull(scr).hasNextLine()){
            String line = scr.nextLine();
            if(line.contains(query)){
                return line.substring(query.length());
            }
        }
        return null;
    }

    private static String getVersionExe(String exePath){
        String absolutePath = GameFolderHelper.getAbsolutePath(exePath);
        if(absolutePath == null) return null;
        IntByReference dwDummy = new IntByReference();
        dwDummy.setValue(0);

        int versionlength = com.sun.jna.platform.win32.Version.INSTANCE.GetFileVersionInfoSize(absolutePath, dwDummy);

        //noinspection MismatchedReadAndWriteOfArray
        byte[] bufferarray = new byte[versionlength];
        Pointer lpData = new Memory(bufferarray.length);
        PointerByReference lplpBuffer = new PointerByReference();
        IntByReference puLen = new IntByReference();

        com.sun.jna.platform.win32.Version.INSTANCE.GetFileVersionInfo(absolutePath, 0, versionlength, lpData);
        com.sun.jna.platform.win32.Version.INSTANCE.VerQueryValue(lpData, "\\", lplpBuffer, puLen);

        VS_FIXEDFILEINFO lplpBufStructure = new VS_FIXEDFILEINFO(lplpBuffer.getValue());
        lplpBufStructure.read();

        int v1 = (lplpBufStructure.dwFileVersionMS).intValue() >> 16;
        int v2 = (lplpBufStructure.dwFileVersionMS).intValue() & 0xffff;
        int v3 = (lplpBufStructure.dwFileVersionLS).intValue() >> 16;
        int v4 = (lplpBufStructure.dwFileVersionLS).intValue() & 0xffff;
        return v1 + "." + v2 + "." + v3 + "." + v4;
    }

}
