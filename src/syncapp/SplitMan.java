/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syncapp;

import java.io.*;

/**
 *
 * @author marc.hoaglin
 */
public class SplitMan {

    public static void FileSplitter(String szFile) throws FileNotFoundException, IOException {
        FileInputStream fis = new FileInputStream(szFile);
        String fileName;
        int size = 1024 * 1024;
        byte buffer[] = new byte[size];

        int count = 0;
        while (true) {
            int i = fis.read(buffer, 0, size);
            if (i == -1) {
                break;
            }

            fileName = String.format("%s.part%09d", szFile, count);
            System.out.println(new File(fileName).getName());
            FileOutputStream fos = new FileOutputStream("C:\\tmp" + File.separatorChar + new File(fileName).getName());
            fos.write(buffer, 0, i);
            fos.flush();
            fos.close();

            ++count;
        }
        System.out.println(count);
    }

    public static void FileJoiner(String szDir) {
        // get number of files in temp folder
        System.out.println(new File(szDir).list().length);
        System.out.println(folderSize(new File(szDir)));
        System.out.println(new File("D:\\test.zip").length());
        

    }

    public static long folderSize(File directory) {
        long length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile()) {
                length += file.length();
            } else {
                length += folderSize(file);
            }
        }
        return length;
    }

    public static File[] getList(String szDir) {
        
        File file = new File(szDir);
        File[] files = file.listFiles();
        String[] szFiles = new String[files.length];
        for (int fileInList = 0; fileInList < files.length; fileInList++) {
            System.out.println(files[fileInList].toString());
            szFiles[fileInList] = files[fileInList].toString();
        }
        return files;
    }
}
