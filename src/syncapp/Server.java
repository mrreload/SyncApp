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

    private static void Listen() throws Exception {
        String separ = ",,";

        String[] szElements;
        sSock = new ServerSocket(iSock);

        System.out.println("Starting Receiver: ");
        while (true) {
            cSock = sSock.accept();

            in = cSock.getInputStream();
            DataInputStream clientData = new DataInputStream(in);

            szElements = clientData.readUTF().split(separ);
            if (szElements.length > 0) {
                //in.close();
                clientData.close();
                parseMSG(szElements);
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
            System.out.println("Received a REQ");
            for (int i = 0; i < szMSG.length; i++) {
                System.out.println(szMSG[i]);
            }
            Request.reqFile(szMSG);
        }
        if (szMSG[0].equals("FIL")) {
            System.out.println("Received a FIL");
            for (int i = 0; i < szMSG.length; i++) {
            Server.rcvFile2(szMSG);
            }
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

    public static void rcvFile(String[] szElements) throws FileNotFoundException, IOException, Exception {
        int bytesRead;
        String szFileOutPath = "/home/mrreload/temp";
        String szCurrentChunk;
        String szSHAFull;
         in = cSock.getInputStream();
        DataInputStream clientData = new DataInputStream(in);

        ArrayList<File> alFiles = new ArrayList<>();

        if (szElements.length == 8) {
            String fileName = szElements[0];
            String szTitle = szElements[1];
            String szSHA = szElements[2];
            int index = Integer.parseInt(szElements[3]);
            int iCurrentNum = index + 1;
            int iTotalNum = Integer.parseInt(szElements[4]);
            szSHAFull = szElements[5];
            String szOrgFileName = szElements[6];
            String szYear = szElements[7];
            szCurrentChunk = szFileOutPath + fileName;
            try (OutputStream output = new FileOutputStream(szCurrentChunk)) {
                long size = clientData.readLong();
                System.out.println("Receiving: " + fileName + " With Size: " + size + " Title: " + szTitle + " " + iCurrentNum + "of" + iTotalNum);
                byte[] buffer = new byte[1024];
                while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                    output.write(buffer, 0, bytesRead);
                    size -= bytesRead;
                    //System.out.println("received: " + bytesRead);
                }
            }
            // Add file to received collection, must check for matching source file

            if (Server.verifyHash(szSHA, SHACheckSum.getSHA(szCurrentChunk))) {
                System.out.println("Chunk is good!");
                alFiles.add(index, new File(szCurrentChunk));

            } else {
                //add logic to re-send corrupt file
            }
            if (iCurrentNum == iTotalNum) {
                System.out.println("all parts received");
                //attempt to assemble file
                File[] szFileList = (File[]) alFiles.toArray(new File[0]);
                SplitMan.FileJoiner(szFileList, "C:\\" + szOrgFileName);

                System.out.println("SHA 256 verfied, file is good. " + Server.verifyHash(szSHAFull, SHACheckSum.getSHA("C:\\" + szOrgFileName)));
                alFiles = null;

            }
        }
    }

    public static void rcvFile2(String[] szFileInfo) throws IOException, Exception {
        String separ = ",,";
        int bytesRead;
        int current = 0;
        String szFileOutPath = "/home/mrreload/temp";
        if (!new File(szFileOutPath).exists()) {
            new File(szFileOutPath).mkdirs();
        }
        String[] szElements;
//        ServerSocket serverSocket = null;
//        serverSocket = new ServerSocket(13267);

        while (true) {
            cSock = null;
            cSock = sSock.accept();

            in = cSock.getInputStream();

            DataInputStream clientData = new DataInputStream(in);

            szElements = clientData.readUTF().split(separ);

            String fileName = szElements[1];
            String szTitle = szElements[2];
            String szSHA = szElements[3];
            int index = Integer.parseInt(szElements[4]);
            int iCurrentNum = index + 1;
            int iTotalNum = Integer.parseInt(szElements[5]);
            String szSHAFull = szElements[6];
            String szFinalFile = szFileOutPath + File.separatorChar + fileName;
            try (OutputStream output = new FileOutputStream(szFinalFile)) {
                long size = clientData.readLong();
                System.out.println("Receiving: " + szFinalFile + " With Size: " + size + " SHA256: " + szSHA);
                byte[] buffer = new byte[1024];
                while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                    output.write(buffer, 0, bytesRead);
                    size -= bytesRead;
                    //System.out.println("received: " + bytesRead);
                }
                System.out.println("Received: " + fileName);
            }
            System.out.println(Server.verifyHash(szSHA, SHACheckSum.getSHA(szFinalFile)));
            
        }
    
    }
}
