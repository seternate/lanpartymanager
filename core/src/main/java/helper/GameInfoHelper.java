package helper;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.VerRsrc.VS_FIXEDFILEINFO;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import entities.Game;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.Scanner;

/**
 * <code>Helper class</code> to get the local informations from any {@link Game}.
 * <p>
 * No object can be created from this class, because it only functions as a <code>helper class</code>.
 */
public abstract class GameInfoHelper {
    /**
     * Determines the version from a {@link Game} dependent on the field <code>version.format</code> in the {@link Properties}
     * file of the {@link Game}.
     *
     * @param game {@link Game} to get the version from.
     *
     * @return {@link String} representation of the version from the <code>game</code> dependent on format.
     */
    public static String getVersion(Game game){
        Properties properties = game.getProperties();
        switch (properties.getProperty("version.format")){
            case "file": return fileVersion(properties.getProperty("version.file"), properties.getProperty("version.query"));
            case "date": return dateVersion(properties.getProperty("exe.file"));
            default: return getVersion(properties.getProperty("exe.file"));
        }
    }
    /**
     * Determines the version of a {@link Game} by searching a file with a query and returning the {@link String} right
     * after the query statement.
     *
     * @param file relative path to the file, which holds the version information.
     * @param query to find the version. (e.g. "ExtVersion=")
     *
     * @return {@link String} representation of the version found after the <code>query</code> statement. Empty
     * {@link String} if the <code>file</code> doesn't exists or wrong <code>query</code> used.
     *
     * @see Game
     */
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
    /**
     * Determines the version of a file or directory by returning the {@link String} representation of the date the
     * file or directory was last modified.
     *
     * @param filePath <code>.exe-File</code> relative path of a {@link Game}.
     *
     * @return {@link String} representation of the date where the file or directory was last modified.
     *
     * @see Game
     */
    private static String dateVersion(String filePath){
        String absolutePath = GameFolderHelper.getAbsolutePath(filePath);
        if(absolutePath.equals("")) return "";
        File file = new File(absolutePath);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        return sdf.format(file.lastModified());
    }

    /**
     * Determines the version of a file by returning its FileVersion from Windows.
     *
     * @param filePath relative path of an <code>.exe-File</code>.
     *
     * @return {@link String} representation of the FileVersion information returned with {@link com.sun.jna.platform.win32.Version}.
     */
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
}
