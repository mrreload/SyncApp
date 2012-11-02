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
    private static boolean RemoteCanSend;
    static String szSHAFull;
    static ArrayList<String[]> badFiles;

    private static void Listen() throws Exception {
        String separ = ",,";
        String[] szElements;

        System.out.println("Starting Server: on " + iSock);
        sSock = new ServerSocket(iSock);
        while (true) {
            iCurrentChunk = 0;
            iTotalChunk = 0;
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
            if (badFiles != null) {
                String[] badList = new String[6];
                for (int i = 0; i < badFiles.size(); i++) {
                    badList = badFiles.get(i);
                    for (int j = 0; i < badList.length; j++) {
                        System.out.println(badList[j]);
                    }
                }
            }

            if ((iCurrentChunk == iTotalChunk) && iTotalChunk != 0 && iCurrentChunk > 0) {
                System.out.println("We can Assemble now");
                new Thread(new Runnable() {

                    public void run() {
                        String szOutFileFinal = null;
                        File[] szFileList = null;
                        try {
                            szFileList = (File[]) alFiles.toArray(new File[0]);
                            alFiles.clear();
                            String szOutFolder = Config.readProp("output.folder", "sync.conf");
                            if (!new File(szOutFolder).exists()) {
                                new File(szOutFolder).mkdirs();
                            }

                            szOutFileFinal = szOutFolder + File.separatorChar + (new File(Server.szOrgFileName).getName());
                            SplitMan.FileJoiner(szFileList, szOutFileFinal);
//                    System.out.println("Back to Listen");
                        } catch (Exception ex) {
                            Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        if (szSHAFull.equals(SHACheckSum.getSHA(szOutFileFinal))) {
                            System.out.println("CheckSums match");

                            szFileList = null;

                            Sender.SndMSG("COMPLETE");
                            RemoteCanSend = false;
                        } else {
                            System.out.println(szSHAFull);
                            System.out.println(SHACheckSum.getSHA(szOutFileFinal));
                        }
                    }
                }).start();
            }

        }
    }

    static boolean verifyHash(String szOrgData, String szNewData) {
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
            if (!RemoteCanSend) {
                Sender.SndMSG("READY");
            } else {
                Sender.SndMSG("WAIT");
            }
        }

        if (szMSG[0].equals("READY")) {
            System.out.println("READY Message received from Server");
            Sender.RemoteCanReceive = true;
            for (int i = 0; i < szMSG.length; i++) {
//            System.out.println(szMSG[i]);
            }
        }

        if (szMSG[0].equals("COMPLETE")) {
            System.out.println("COMPLETE Message received from Client");
            RemoteCanSend = false;
            //REQinProgress = false;
            for (int i = 0; i < szMSG.length; i++) {
//            System.out.println(szMSG[i]);
            }
        }

        if (szMSG[0].equals("WAIT")) {
            System.out.println("Received an WAIT, Remote Server is busy Sending already");
            Sender.RemoteCanReceive = false;
            for (int i = 0; i < szMSG.length; i++) {
//            System.out.println(szMSG[i]);
            }
        }

        if (szMSG[0].equals("REQ")) {
            System.out.println("Received a REQ from: " + cSock.getInetAddress().getHostAddress());
            for (int i = 0; i < szMSG.length; i++) {
//                System.out.println(szMSG[i]);
            }
            if (!Sender.senderBusy) {
                Sender.senderBusy = true;
                Request.reqFile(cSock.getInetAddress().getHostAddress(), szMSG);
            } else {
                Sender.SndMSG("WAIT,,Server Busy");
            }
        }

        if (szMSG[0].equals("REQCHUNK")) {
            System.out.println("Received a single Chunk request from: " + cSock.getInetAddress().getHostAddress());
            for (int i = 0; i < szMSG.length; i++) {
//                System.out.println(szMSG[i]);
            }
            if (!Sender.senderBusy) {
                Sender.senderBusy = true;
                Request.reqFile(cSock.getInetAddress().getHostAddress(), szMSG);
            } else {
                Sender.SndMSG("WAIT,,Server Busy");
            }
        }

        if (szMSG[0].equals("FIL")) {
//            System.out.println("Received a FIL " + szMSG.length);

//            for (int i = 0; i < szMSG.length; i++) {
////            Server.rcvFile2(szMSG);
//            }

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

    public static void rcvFile2(String[] szFileInfo) {

        int bytesRead;

        boolean blReceive = true;
        if (alFiles == null) {
            alFiles = new ArrayList<>();
        }
        if (badFiles == null) {
            badFiles = new ArrayList<>();
        }
        while (blReceive) {
//            InputStream in = null;
            try {
                Sender.RemoteCanReceive = false;
                in = cSock.getInputStream();
                DataInputStream clientData = new DataInputStream(in);
                String fileName = szFileInfo[1];
                String szSHA = szFileInfo[2];
                int index = Integer.parseInt(szFileInfo[3]);
                iCurrentChunk = index + 1;
                iTotalChunk = Integer.parseInt(szFileInfo[4]);
                szSHAFull = szFileInfo[5];
                szOrgFileName = szFileInfo[6];
                String szFileOutPath = Config.readProp("server.tmp", "sync.conf") + File.separatorChar + szSHAFull;
                if (!new File(szFileOutPath).exists()) {
                    new File(szFileOutPath).mkdirs();
                }
                String szCurrentChunk = szFileOutPath + File.separatorChar + fileName;
                OutputStream output = new FileOutputStream(szCurrentChunk);
                long size = clientData.readLong();
                System.out.println("Receiving: " + szCurrentChunk + " Size: " + size + " Chunk#: " + iCurrentChunk);
                byte[] buffer = new byte[1024];
                while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                    output.write(buffer, 0, bytesRead);
                    size -= bytesRead;
                }
                //                System.out.println("Received: " + fileName + " " + size);
                clientData.close();
                output.flush();
                output.close();
                // Add file to received collection, must check for matching source file
                if (Server.verifyHash(szSHA, SHACheckSum.getSHA(szCurrentChunk))) {

                    alFiles.add(index, new File(szCurrentChunk));
                    

                } else {
                    //add logic to re-send corrupt chunk
                    System.out.println("Chunk is BAD!");
                    badFiles.ensureCapacity(index);
                    badFiles.add(szFileInfo);

                }
                blReceive = false;
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    in.close();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }
}
