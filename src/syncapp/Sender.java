package syncapp;

/**
 *
 * @author mrreload
 */
import java.io.*;
import java.net.Socket;

public class Sender {
//    public static String szFile;
//    public void run() {
//        try {
//            Sender.SndFile();
//        } catch (IOException ex) {
//            Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    public static String fullHash;
    public static String OrgFileName;
    private static String Title;
    private static String Year;
    public static boolean BadFile;
    static boolean servStatus;

    public static void SndFile(String szHost, String szType, String szFile, int iCurrentFile, int iTotalFile) throws IOException, Exception {
        String sep = ",,";
        int iPort = Integer.parseInt(Config.readProp("remote.port", "sync.conf"));
        System.out.println("Sending file " + szFile + " to " + szHost);
        try (Socket sock = new Socket(szHost, iPort)) {
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
            dos.writeUTF(szType + sep + myFile.getName() + sep + Title + sep + szSHA + sep + iCurrentFile + sep + iTotalFile + sep + fullHash + sep + OrgFileName);

            dos.writeLong(mybytearray.length);
            dos.write(mybytearray, 0, mybytearray.length);
            dos.flush();
        }
    }

    public static void SendList(String szHost, String szType, String[] szList) throws IOException, Exception {
//        File file = new File(szDir);
//        File[] files = file.listFiles();
        for (int i = 0; i < szList.length; i++) {
//            System.out.println(szList[i]);
            SndFile(szHost, szType, szList[i], i, szList.length);
        }

    }

    public static String[] getList(String szDir) {

        File file = new File(szDir);
        File[] files = file.listFiles();
        String[] szFiles = new String[files.length];
        for (int fileInList = 0; fileInList < files.length; fileInList++) {
            szFiles[fileInList] = files[fileInList].toString();
        }
        return szFiles;
    }

    public static void SendMain(String szFile, String szWorkFolder) throws FileNotFoundException, IOException, Exception {
//        SplitMan.FileSplitter(szFile, szWorkFolder);
        fullHash = SHACheckSum.getSHA(szFile);
        OrgFileName = new File(szFile).getName();
        Year = "2012";
        Title = "The Avengers";
//        Sender.SendList(SplitMan.FileSplitter(szFile, szWorkFolder));
        SndMSG("192");
        SndMSG("F");
    }

    public static void SndMSG(String szMSG) throws IOException, Exception {
        
        
        
        int iPort = Integer.parseInt(Config.readProp("remote.port", "sync.conf"));
        
        try (Socket sock = new Socket(Config.readProp("remote.host", "sync.conf"), iPort)) {
            
            OutputStream os = sock.getOutputStream();

            //Sending file name and file size to the server
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeUTF(szMSG);
            
            dos.flush();
            dos.close();
        }
    }
    public static void reSender(String szFile, int iCurrentFile, int iTotalFile) throws InterruptedException{
        BadFile = false;
        while (!BadFile) {
            Thread.sleep(5000);
        } 
        
    }
    public static void servReady() throws IOException, Exception {
        Thread.sleep(5000);
        while (!servStatus) {
            Sender.SndMSG("ACK");
            Thread.sleep(2000);
        }
            
            
        
       
        
    }
    
}
