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
        String szFile = "/home/mrreload/amd.run";
        String szWorkFolder = "/home/mrreload/temp";
        Server.iSock = 13267;
//        try {
//            SplitMan.FileSplitter(szFile);
//            SplitMan.FileJoiner(SplitMan.getList("C:\\tmp"));
//            System.out.println(SplitMan.getList("C:\\tmp").length);
//           
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(SyncApp.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(SyncApp.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        System.exit(0);
        // Create the object with the run() method
        //Runnable tSend = new Sender();
        Runnable tRcv = new Server();
// Create the thread supplying it with the runnable object
        //Thread t1 = new Thread(tSend);
        Thread t2 = new Thread(tRcv);
// Start the thread
        //t1.start();
        t2.start();
        try {
//            SplitMan.FileSplitter("D:\\Dell_WinXPSP3_SATA.iso", "C:\\tmp");
//            Sender.SendList("localhost", "FIL", Sender.getList("C:\\tmp\\filesync"));
            
            Sender.SndMSG(Config.readProp("remote.host", "sync.conf"), "REQ,," + szFile + ",,0");
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
