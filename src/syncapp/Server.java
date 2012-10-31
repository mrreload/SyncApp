/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syncapp;

/**
 *
 * @author mrreload
 */
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable {

    public void run() {
        try {
            Server.Listen();
        } catch (Exception ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    static ServerSocket sSock;
    public static int iSock;
    static Socket cSock;
    static InputStream in;
    static int iCurrentChunk;
    static int iTotalChunk;
    static ArrayList<File> alFiles;
    static String szOrgFileName;
    
    private static void Listen() throws Exception {
        String separ = ",,";
        String[] szElements;
        System.out.println("Starting Server: on " + iSock);
        sSock = new ServerSocket(iSock);
        while (true) {
            cSock = sSock.accept();
            in = cSock.getInputStream();
            DataInputStream clientData = new DataInputStream(in);
            szElements = clientData.readUTF().split(separ);
            if (szElements.length > 0) {
                //in.close();
//                clientData.close();
                parseMSG(szElements);
                szElements = null;

            }
            if ((iCurrentChunk == iTotalChunk) && iTotalChunk != 0 && iCurrentChunk > 0) {
                System.out.println("We can Assemble now");

                try {
                    File[] szFileList = (File[]) alFiles.toArray(new File[0]);
                    String szOutFolder = Config.readProp("output.folder", "sync.conf");
                    if (!new File(szOutFolder).exists()) {
                        new File(szOutFolder).mkdirs();
                    }
                    SplitMan.FileJoiner(szFileList, szOutFolder + File.separatorChar + (new File(Server.szOrgFileName).getName()));
                    System.out.println("Back to Listen");
                } catch (Exception ex) {
                    Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            
        }
    }

    private static boolean verifyHash(String szOrgData, String szNewData) {
        boolean blCheck = false;
        if (!szNewData.equals(szOrgData)) {
            blCheck = false;
        } else if (szNewData.equals(szOrgData)) {
            blCheck = true;
        }
        return blCheck;
    }

    private static void parseMSG(String[] szMSG) throws Exception {

        if (szMSG[0].equals("ACK")) {
            System.out.println("Received an ACK");
            for (int i = 0; i < szMSG.length; i++) {
//            System.out.println(szMSG[i]);
            }
        }
        if (szMSG[0].equals("REQ")) {
            System.out.println("Received a REQ from: " + cSock.getInetAddress().getHostAddress());
            for (int i = 0; i < szMSG.length; i++) {
//                System.out.println(szMSG[i]);
            }
            Request.reqFile(cSock.getInetAddress().getHostAddress(), szMSG);

        }
        if (szMSG[0].equals("FIL")) {
            System.out.println("Received a FIL " + szMSG.length);

            for (int i = 0; i < szMSG.length; i++) {
//            Server.rcvFile2(szMSG);
            }
            Server.rcvFile2(szMSG);
        }
        if (szMSG[0].equals("LST")) {
            System.out.println("Received a LST");
            for (int i = 0; i < szMSG.length; i++) {
//            System.out.println(szMSG[i]);
            }
        }
        if (szMSG[0].equals("XLST")) {
            System.out.println("Received a XLST");
            for (int i = 0; i < szMSG.length; i++) {
//            System.out.println(szMSG[i]);
            }
        }
    }

    public static void rcvFile2(String[] szFileInfo) throws IOException, Exception {
       
        int bytesRead;
        
        boolean blReceive = true;
        if (alFiles == null) {
            alFiles = new ArrayList<>();
        }
        while (blReceive) {

            in = cSock.getInputStream();

            DataInputStream clientData = new DataInputStream(in);

            String fileName = szFileInfo[1];
            String szTitle = szFileInfo[2];
            String szSHA = szFileInfo[3];
            int index = Integer.parseInt(szFileInfo[4]);
            iCurrentChunk = index + 1;
            iTotalChunk = Integer.parseInt(szFileInfo[5]);
            String szSHAFull = szFileInfo[6];
            szOrgFileName = szFileInfo[7];
            String szFileOutPath = Config.readProp("server.tmp", "sync.conf") + File.separatorChar + szSHAFull;
        if (!new File(szFileOutPath).exists()) {
            new File(szFileOutPath).mkdirs();
        }
            String szCurrentChunk = szFileOutPath + File.separatorChar + fileName;
            try (OutputStream output = new FileOutputStream(szCurrentChunk)) {
                long size = clientData.readLong();
                System.out.println("Receiving: " + szCurrentChunk + " Size: " + size + " Chunk#: " + iCurrentChunk);
                byte[] buffer = new byte[1024];
                while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
//                    System.out.println("Writing File..");
                    output.write(buffer, 0, bytesRead);
                    size -= bytesRead;
//                    System.out.println("received: " + bytesRead);
                }
                System.out.println("Received: " + fileName + " " + size);
            }
            // Add file to received collection, must check for matching source file

            if (Server.verifyHash(szSHA, SHACheckSum.getSHA(szCurrentChunk))) {
                System.out.println("Chunk is good!");
                alFiles.add(index, new File(szCurrentChunk));


            } else {
                //add logic to re-send corrupt chunk
            }
            blReceive = false;
        }

    }
}
