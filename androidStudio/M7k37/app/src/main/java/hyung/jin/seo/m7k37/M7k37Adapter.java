package hyung.jin.seo.m7k37;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class M7k37Adapter extends ArrayAdapter {

	private Context context;
	private int layout;
	private ArrayList item;
	private int size;
	public boolean isActivated; 
	private int wholeHeight;
	private int itemHeight;
	private boolean firstCheckHeight;
	
	public M7k37Adapter(Context context, int layout, ArrayList item) {
		super(context, layout, item);
		this.context = context;
		this.layout = layout;
		this.item = item;
		size = item.size();
		firstCheckHeight = true;
		display();
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		if(firstCheckHeight)
		{
			calculateHeight();
			firstCheckHeight = false;
		}
		if(convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(layout, null);
			LinearLayout llView = (LinearLayout) convertView.findViewById(R.id.encapsulate);
			llView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, itemHeight));
		}
		// Get each item
		M7k37Menu menu = (M7k37Menu) item.get(position);
		// Make all enable
		areAllItemsEnabled();
		
		
		ImageView image = (ImageView) convertView.findViewById(R.id.list_image);
		TextView tt = (TextView) convertView.findViewById(R.id.toptext);
        TextView bt = (TextView) convertView.findViewById(R.id.bottomtext);
        
        
         if (tt != null){
             tt.setText(menu.getName());                            
         }
         if(bt != null){
             bt.setText(menu.getDesc());
         }
		
        // Make cyan in enable; otherwise gray
        
		if(isEnabled(position))
		{
			image.setImageResource(menu.getIcon());
			tt.setTextColor(Color.parseColor("#000080"));
			bt.setTextColor(Color.parseColor("#0000FF"));
		}else{
			image.setImageBitmap(M7k37Util.grayScaleImage(BitmapFactory.decodeResource(context.getResources(), menu.getIcon())));
			tt.setTextColor(Color.GRAY);
			bt.setTextColor(Color.GRAY);
		}
		
//		Log.i(M7k37Constants.HEADER, "item[" + position + "] is enabled : " + isEnabled(position));
		return convertView;
	}
	
	
	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}

	/**
	 * 
	 */
	@Override
	public boolean isEnabled(int position) {
		if((!isActivated)&&(position < (size-1)))// except Master
		{
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * Measure the whole screen size
	 */
	public void display()
	{
		Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		wholeHeight = display.getHeight();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		wholeHeight = metrics.heightPixels;
	}
	
	/**
	 * In order to get the whole screen size, calculate "actual height = whole height - title bar"  
	 */
	public void calculateHeight()
	{
		Rect rectgle = new Rect();
		Window window = ((Activity)context).getWindow(); 
		window.getDecorView().getWindowVisibleDisplayFrame(rectgle); 
		int contentViewHeight = window.findViewById(Window.ID_ANDROID_CONTENT).getHeight();
		wholeHeight = contentViewHeight; 
		itemHeight = (int)(wholeHeight/size);
	}
	
}
