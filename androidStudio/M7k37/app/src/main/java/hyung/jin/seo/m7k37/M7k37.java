package hyung.jin.seo.m7k37;

import hyung.jin.seo.m7k37.softkeyboard.SoftKeyboard;

import java.util.Arrays;
import java.util.Map;



import android.app.Activity;
import android.content.SharedPreferences;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;


/**
 * @author js278
 *
 */
public class M7k37 extends SoftKeyboard
{
	
	private String[] keys;
	
//	private boolean isActiviate = false;
	
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub 
		super.onCreate();
		Log.i(M7k37Constants.HEADER, "onCreate()");
	}



	/* (non-Javadoc)
	 * Save keys
	 * @see android.inputmethodservice.InputMethodService#onStartInput(android.view.inputmethod.EditorInfo, boolean)
	 */
	@Override
	public void onStartInput(EditorInfo attribute, boolean restarting) {
		// TODO Auto-generated method stub
		super.onStartInput(attribute, restarting);
		
		// initialise all keywords
		Map map = getPreferences();
		String[] keywords = M7k37Util.mapToString(map);
		keys = new String[keywords.length];
		for(int i=0; i<keywords.length; i++)
		{
			String[] words =  M7k37Util.stringSplit(keywords[i]);
			keys[i] = words[0].trim();
		}
		
		Log.i(M7k37Constants.HEADER, "onStartInput()");
	}


	
	
    
	/* This method is to look up and change user input to reserved word if exists
	 * @see hyung.jin.seo.m7k37.softkeyboard.SoftKeyboard#handleCharacter(int, int[])
	 */
	public void handleCharacter(int primary, int[] codes) {
		 super.handleCharacter(primary, codes);
    	///////////////////////////////////////////////////////////////
    	//
    	//   Logic for change keyword if saved
    	//
    	////////////////////////////////////////////////////////////////
    	
        InputConnection ic = getCurrentInputConnection(); 
   		String currentText = (String) ic.getExtractedText(new ExtractedTextRequest(), 0).text;
   		int length = currentText.length();
   		Log.i(M7k37Constants.HEADER, "Input Text :  " + currentText + "              length : " + length);
   		Log.i(M7k37Constants.HEADER, "handleCharacter() " + String.valueOf((char)primaryKeyCode) + Arrays.toString(codes));
   		
   		String search = M7k37Util.getContained(keys, currentText);
   		if((!isActiviate)||(search==null)||(search.length()==0))
   		{
   			getCurrentInputConnection().commitText(String.valueOf((char)primaryKeyCode), 1);
   		}else{
   			ic.deleteSurroundingText(length, length);
   			getCurrentInputConnection().commitText(getPreferences(search), 1);
   		}
	        	
		
	}

	/**
	 * Retrieve all key-values
	 * @return
	 */
	private Map getPreferences()
	{
		SharedPreferences pref = getSharedPreferences(M7k37Constants.PREF, MODE_PRIVATE);
		Map all = pref.getAll();
		return all;
	}
	
	
	/**
	 * Retrieve value based on key
	 * @param name
	 * @return
	 */
	private String getPreferences(String name)
	{
		SharedPreferences pref = getSharedPreferences(M7k37Constants.PREF, Activity.MODE_PRIVATE);
		String value = pref.getString(name, "");
		return value;
	}
	
}
