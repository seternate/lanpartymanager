package helper;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.VerRsrc.VS_FIXEDFILEINFO;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;



public class GameInfoHelper {

    public static String[] getFileInfo(String filePath){
        String[] fileInfo = new String[2];
        IntByReference dwDummy = new IntByReference();
        dwDummy.setValue(0);

        int versionlength = com.sun.jna.platform.win32.Version.INSTANCE.GetFileVersionInfoSize(filePath, dwDummy);

        byte[] bufferarray = new byte[versionlength];
        Pointer lpData = new Memory(bufferarray.length);
        PointerByReference lplpBuffer = new PointerByReference();
        PointerByReference lplpBufferName = new PointerByReference();
        IntByReference puLen = new IntByReference();
        IntByReference puLenName = new IntByReference();

        //DON'T DELETE, THEY ARE NEEDED
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

}
