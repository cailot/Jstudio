package hyung.jin.seo.m7k373n9;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class M7k37Util {

    /**
     * It returns saved key-value as String[]
     * @param map
     * @return
     */
    public static String[] mapToString(Map map)
    {
        if(map==null) return new String[]{};
        int size = map.size();
        String[] nodeList = new String[size];
        Set keys = map.keySet();
        Iterator iter = keys.iterator();
        int start = 0;
        while(iter.hasNext())
        {
            String key = (String) iter.next();
            String value = (String) map.get(key);
            nodeList[start] = key + " : " + value;
            start++;
        }
        return nodeList;
    }

    /**
     * It returns saved key-value as List
     * @param map
     * @return
     */
    public static List mapToList(Map map)
    {
        if(map==null) return new ArrayList();
        List nodeList = new ArrayList();
        Set keys = map.keySet();
        Iterator iter = keys.iterator();
        while(iter.hasNext())
        {
            String key = (String) iter.next();
            String value = (String) map.get(key);
            nodeList.add(key + " : " + value);
        }
        return nodeList;
    }


    /**
     * Simply split string by ':'
     * @param str
     * @return
     */
    public static String[] stringSplit(String str)
    {
        if(str.contains(M7k37Constants.SEPARATOR))
        {
            return str.split(M7k37Constants.SEPARATOR);
        }else{
            return new String[]{"",""};
        }
    }


    /**
     * Look up typed-word and check it belongs to keys array
     * @param keys
     * @param input
     * @return
     */
    public static String getContained(String[] keys, String input)
    {
        String contained = null;
        if((keys==null) || (keys.length==0) || (input==null) || (input.length()==0)) return null;
        for(int i=0; i<keys.length; i++)
        {
            if(input.contains(keys[i]))
            {
                contained = keys[i];
                break;
            }
        }
        return contained;
    }

    /**
     * Return string displaying only first & last character
     * @param pass
     * @return
     */
    public static String showPassword(String pass)
    {
        if(pass==null || pass.length()==0)
        {
            return "";
        }else if(pass.length()==1){
            return "*";
        }else if(pass.length()==2){
            return "**";
        }else{
            char first = pass.charAt(0);
            char last = pass.charAt(pass.length()-1);
            String body = "";
            for(int i=1; i<=pass.length()-2; i++)
            {
                body += "*";
            }
            return (first + body + last);
        }
    }

    /**
     * Return keys as String[]
     * @param map
     * @return
     */
    public static String[] getKeysFromMap(Map map)
    {
        Set keySet = map.keySet();
        int size = keySet.size();
        String[] keys = new String[size];
        int cnt = 0;
        Iterator iter = keySet.iterator();
        while(iter.hasNext())
        {
            keys[cnt] = iter.next().toString();
            cnt++;
        }
        return keys;
    }

    /**
     * Helper to determine if a given character code is alphabetic.
     */
    public static boolean isAlphabet(int code) {
        if (Character.isLetter(code)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Toast message on screen
     * @param context
     * @param msg
     */
    public static void shout(Context context, String msg)
    {
        TextView tv = new TextView(context);
        tv.setText(msg);
        tv.setHeight(80);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(16);
        tv.setTextColor(Color.CYAN);
        LinearLayout ll = new LinearLayout(context);
        ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        ll.setBackgroundResource(R.drawable.toast);
        ll.setPadding(30, 0, 30, 0);
        ll.setGravity(Gravity.CENTER);
        ll.addView(tv);
        Toast t = Toast.makeText(context, "", Toast.LENGTH_LONG);
        t.setGravity(Gravity.CENTER, 0, 0);
        t.setView(ll);
        t.show();
    }

    /**
     * Convert string to hex
     * @param data
     * @return
     */
    public static String originalToHex(byte[] data)
    {
        return data!=null ? Hex.encodeHexString(data) : null ;
    }

    /**
     * Convert hex to string
     * @param hex
     * @return
     * @throws DecoderException
     */
    public static String hexToOriginal(String hex) throws DecoderException
    {
        return hex!=null ? new String(Hex.decodeHex(hex.toCharArray())) : null;
    }

    /**
     * Encrypt via AES
     * @param key
     * @param str
     * @return
     * @throws Exception
     */
    public static String encryptString(String key, String str) throws Exception
    {
        SecretKeySpec skeySpec = new SecretKeySpec(Hex.decodeHex(key.toCharArray()), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        return new String(cipher.doFinal(str.getBytes()));
    }

    /**
     * Decrypt via AEX
     * @param key
     * @param str
     * @return
     * @throws Exception
     */
    public static String decryptString(String key, String str) throws Exception
    {
        SecretKeySpec skeySpec = new SecretKeySpec(Hex.decodeHex(key.toCharArray()), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        return new String(cipher.doFinal(str.getBytes()));
    }


    public static Bitmap grayScaleImage(Bitmap src) {
        // constant factors
        final double GS_RED = 0.299;
        final double GS_GREEN = 0.587;
        final double GS_BLUE = 0.114;

        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        // pixel information
        int A, R, G, B;
        int pixel;

        // get image size
        int width = src.getWidth();
        int height = src.getHeight();

        // scan through every single pixel
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get one pixel color
                pixel = src.getPixel(x, y);
                // retrieve color of all channels
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                // take conversion up to one single value
                R = G = B = (int)(GS_RED * R + GS_GREEN * G + GS_BLUE * B);
                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        // return final image
        return bmOut;
    }
}