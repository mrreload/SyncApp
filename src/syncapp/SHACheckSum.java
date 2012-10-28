/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syncapp;

import java.io.FileInputStream;
import java.security.MessageDigest;

public class SHACheckSum {

    public static String getSHA(String szFileIn) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        FileInputStream fis = new FileInputStream(szFileIn.replaceAll("\"", ""));

        byte[] dataBytes = new byte[1024];

        int nread = 0;
        while ((nread = fis.read(dataBytes)) != -1) {
            md.update(dataBytes, 0, nread);
        }
        //;
        byte[] mdbytes = md.digest();

        //convert the byte to hex format method 1
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mdbytes.length; i++) {
            sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
        }

//        System.out.println("Hex format : " + sb.toString());
        return sb.toString();
        //convert the byte to hex format method 2
//        StringBuilder hexString = new StringBuilder();
//        for (int i = 0; i < mdbytes.length; i++) {
//            hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
//        }
//
//        System.out.println("Hex format : " + hexString.toString());
    }
}
