/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syncapp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 *
 * @author mrreload
 */
public class Config {
    private final static Logger CFGLOG = Logger.getLogger(Config.class.getName());
    
    public static String readProp(String prop, String daFile) {
        String propval = null;
        Properties configFl = new Properties();
        try {
            FileInputStream fin = new FileInputStream(daFile);
            configFl.load(fin);

            propval = configFl.getProperty(prop);
            fin.close();
        } catch (FileNotFoundException noFile) {
            CFGLOG.severe(noFile.getMessage());
        } catch (IOException io) {
            CFGLOG.severe(io.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return propval;

    }
    public static void writeProp(String property, String szs, String cfgFile) {
        boolean blSet = false;
        Properties configFile = new Properties();
        try {
            FileInputStream fin = new FileInputStream(cfgFile);
            configFile.load(fin);
            configFile.setProperty(property, szs);
            FileOutputStream fout = new FileOutputStream(cfgFile);
            configFile.store(fout, null);
            fin.close();
            fout.close();
            blSet = true;
        } catch (FileNotFoundException noFile) {
            CFGLOG.severe(noFile.getMessage());
            blSet = false;
        } catch (IOException io) {
            CFGLOG.severe(io.getMessage());
            blSet = false;
        }

    }
}
