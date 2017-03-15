package xyz.jayadev.marvel;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Jayadev on 12/08/16.
 */
public class Utils {

    public String ExecAsync() {
        return "";

    }
    public static Boolean isAvailable() {
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1  www.google.com");
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal==0);
            if(reachable){
                return reachable;
            }
            else{
                //nothing
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
        return false;
    }

    public static String md5(String ts) {
        try {
            String s = ts + Config.PRIVATE_KEY + Config.PUBLIC_KEY;
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}


