/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syncapp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mrreload
 */
public class SyncApp {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String szRemoteHost;
        if (args.length >= 1) {
            szRemoteHost = args[0];
        } else {
            szRemoteHost = "localhost";
        }
        String szFile = "C:\\test.zip";
        String szFile2 = "C:\\cb.exe";
        String szFile3 = "C:\\test.pdf";
        String szFile4 = "C:\\Cisco.zip";
        String szWorkFolder = "/home/mrreload/temp";
        Server.iSock = 13267;

        Runnable tRcv = new Server();
// Create the Server thread supplying it with the runnable object

        Thread t2 = new Thread(tRcv);
// Start the thread

        t2.start();
        try {
            
            Sender.servReady();
            Sender.SndMSG("REQ,," + szFile + ",,0");
            Sender.servReady();
            Sender.SndMSG("REQ,," + szFile2 + ",,0");
//            
            Sender.servReady();
            Sender.SndMSG("REQ,," + szFile3 + ",,0");
            Sender.servReady();
            Sender.SndMSG("REQ,," + szFile4 + ",,0");
//            Sender.SndMSG("ACK,,Helo,,you,,We are listening");
//            Sender.SndMSG("FIL,,/home/mrreload/amd.run,,0");
//            Sender.SndMSG("LST,,Helo,,File System,,Receiving a list");
//            Sender.SndMSG("XLST,,Helo,,XBMC,,Receiving a XBMC List");
//            Sender.SendMain(szFile, szWorkFolder);
//            SplitMan.FileJoiner(SplitMan.getList("C:\\temp"), "C:\\temp\\testiso.iso");
        } catch (IOException ex) {
            Logger.getLogger(SyncApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(SyncApp.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
