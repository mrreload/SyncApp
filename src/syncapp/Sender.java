package syncapp;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author mrreload
 */
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sender {
//    public static String szFile;
//    public void run() {
//        try {
//            Sender.SndFile();
//        } catch (IOException ex) {
//            Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    public static void SndFile(String szFile) throws IOException, Exception {
        String separ = ",,";
        Socket sock = new Socket("127.0.0.1", 13267);
        
        //File setup to send
        File myFile = new File(szFile);
        String szSHA = SHACheckSum.getSHA(szFile);
        byte[] mybytearray = new byte[(int) myFile.length()];

        FileInputStream fis = new FileInputStream(myFile);
        BufferedInputStream bis = new BufferedInputStream(fis);
        //bis.read(mybytearray, 0, mybytearray.length);

        DataInputStream dis = new DataInputStream(bis);
        dis.readFully(mybytearray, 0, mybytearray.length);

        OutputStream os = sock.getOutputStream();

        //Sending file name and file size to the server
        DataOutputStream dos = new DataOutputStream(os);
        dos.writeUTF(myFile.getName() + separ + "The Movie" + separ + szSHA);
        
        dos.writeLong(mybytearray.length);
        dos.write(mybytearray, 0, mybytearray.length);
        dos.flush();



        //Closing socket
        sock.close();
    }
}
