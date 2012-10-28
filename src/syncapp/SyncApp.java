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
        String szFile = "D:\\test.zip";
        
//            SplitMan.FileSplitter(szFile);
//            SplitMan.FileJoiner("C:\\tmp");
            System.out.println(SplitMan.getList("/home/mrreload/Downloads"));
        
        System.exit(0);
        // Create the object with the run() method
        //Runnable tSend = new Sender();
        Runnable tRcv = new Receiver();
// Create the thread supplying it with the runnable object
        //Thread t1 = new Thread(tSend);
        Thread t2 = new Thread(tRcv);
// Start the thread
        //t1.start();
        t2.start();
        try {
            Sender.SndFile(szFile);
            Sender.SndFile("D:\\Dell_WinXPSP3_SATA.iso");
        } catch (IOException ex) {
            Logger.getLogger(SyncApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(SyncApp.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    }
}
