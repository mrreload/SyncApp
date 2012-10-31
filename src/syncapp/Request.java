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
 * @author marc.hoaglin
 */
public class Request {
static String FILE;
static String CHUNK;
static String szRemoteHost;

    public static void reqFile(String szHost, String[] szFile) throws Exception {
      szRemoteHost = szHost;
            //Format:
            //REQ, FileName, chunk#
            System.out.println(szRemoteHost + " Client requested file: " + szFile[1] + " chunk: " + szFile[2]);
            FILE = szFile[1];
            CHUNK = szFile[2];
            
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Sender.fullHash = SHACheckSum.getSHA(FILE);
                        Sender.OrgFileName = FILE;
                        Sender.SendList(szRemoteHost, "FIL", SplitMan.FileSplitter(FILE, Config.readProp("sender.tmp", "sync.conf")));
                    } catch (IOException ex) {
                        Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }).start();
            System.out.println("loop is running...");
            //Server.rcvFile2(szFile);
//            Sender.SendList(SplitMan.FileSplitter(szFile[1], "/home/mrreload/tmp"));
        } 
    
}
