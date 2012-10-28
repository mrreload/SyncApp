/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syncapp;

/**
 *
 * @author mrreload
 */
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Receiver implements Runnable {

    public void run() {
        try {
            Receiver.RcvFile();
        } catch (Exception ex) {
            Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void RcvFile() throws Exception {
        String separ = ",,";
        int bytesRead;
        int current = 0;
        String szFileOutPath = "C:\\tmp\\";
        String[] szElements;
        ServerSocket serverSocket = null;
        serverSocket = new ServerSocket(13267);

        while (true) {
            Socket clientSocket = null;
            clientSocket = serverSocket.accept();

            InputStream in = clientSocket.getInputStream();

            DataInputStream clientData = new DataInputStream(in);

            szElements = clientData.readUTF().split(separ);

            String fileName = szElements[0];
            String szTitle = szElements[1];
            String szSHA = szElements[2];
            String szFinalFile = szFileOutPath + fileName;
            OutputStream output = new FileOutputStream(szFinalFile);
            long size = clientData.readLong();
            System.out.println("Receiving: " + fileName + " With Size: " + size + " Title: " + szTitle + " SHA256: " + szSHA);
            byte[] buffer = new byte[1024];
            while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                output.write(buffer, 0, bytesRead);
                size -= bytesRead;
                //System.out.println("received: " + bytesRead);
            }
            System.out.println("Received: " + fileName);
            // Closing the FileOutputStream handle
            output.close();
            System.out.println(Receiver.verify("SHA", szSHA, SHACheckSum.getSHA(szFinalFile)));
        }
    }

    private static boolean verify(String szType, String szOrgData, String szNewData) {
        boolean blCheck = false;
        if (!szNewData.equals(szOrgData)) {
            blCheck = false;
        } else if (szNewData.equals(szOrgData)) {
            blCheck = true;
        }
        return blCheck;
    }
}
