package hyung.jin.seo.m7k37;


import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Map;


public class M7k37Main extends ListActivity {
	private int editSelected = 0;
	private boolean[] deleteSelected;
	private M7k37Adapter adapter;
	
	private boolean isEditIntent; // indicating whether onStop() needs to keep editable or not
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
		// If it jumps to M7k37Edit, it should keep editable when coming back
		if(isEditIntent)
		{
			adapter.isActivated = true; // keep editable
			isEditIntent = false; // set value to false for next use
		}else{
			adapter.isActivated = false;
		}
		adapter.notifyDataSetChanged();
		Log.i(M7k37Constants.HEADER,
				"Adapter activated menu is now set to false onStop()");
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Log.i(M7k37Constants.HEADER, "=== onConfigurationChanged is called !!! ===");
		
		if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) // portrait 
		{ 
			Log.i(M7k37Constants.HEADER, "=== Configuration.ORIENTATION_PORTRAIT !!! ===");
			//adapter.isActivated = adapter.isActivated;
			//adapter.notifyDataSetChanged();
		}else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) { // landscape
			Log.i(M7k37Constants.HEADER, "=== Configuration.ORIENTATION_LANDSCAPE !!! ===");
			//adapter.isActivated = adapter.isActivated;
			//adapter.notifyDataSetChanged();
		}
	}
	
	
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ArrayList list = new ArrayList();
		list.add(new M7k37Menu(R.drawable.ic_menu_list, "List", "Display the whole list"));
		list.add(new M7k37Menu(R.drawable.ic_menu_add, "Add", "Enable you to add more keyword"));
		list.add(new M7k37Menu(R.drawable.ic_menu_edit, "Edit", "Enable you to modify existing keyword"));
		list.add(new M7k37Menu(R.drawable.ic_menu_remove, "Remove", "Remove saved keywords"));
		list.add(new M7k37Menu(R.drawable.ic_menu_master, "Master", "Only master can access"));
		
		adapter = new M7k37Adapter(this, R.layout.list_row, list);
		adapter.isActivated = false;
		setListAdapter(adapter);

		ActionBar bar = getActionBar();
//		Log.i(M7k37Constants.HEADER, "=== HEIGHT ===" + bar.getHeight());
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0099FF")));
		
		// ///////////////////////////////////////////////////////////////
		// Run just once when installed
		// //////////////////////////////////////////////////////////////
		if (!runBefore()) {
			showMasterPinDialog();
			saveRunHistory(); // set flag to false never to run again
			adapter.isActivated = true; // enable all menu
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here.
		int id = item.getItemId();
		switch(id)
		{
			case R.id.action_about :
				showAbout();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		switch (position) {
		case M7k37Constants.LIST:
			showListDialog();
			break;
		case M7k37Constants.ADD:
			showAddDialog();
			break;
		case M7k37Constants.EDIT:
			showEditDialog();
			break;
		case M7k37Constants.DELETE:
			showDeleteDialog();
			break;
		case M7k37Constants.MASTER:
			showMasterDialog();
			break;
		}
	}
	
	
	/**
	 * Display dialog to register master pin
	 */
	private void showMasterPinDialog() {
		LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
		final View view = li.inflate(R.layout.master_input, (ViewGroup) findViewById(R.id.masterInputLayout));
		AlertDialog.Builder builder = new AlertDialog.Builder(M7k37Main.this);
		builder.setView(view);
		builder.setTitle(M7k37Constants.DIALOG_INDENT + M7k37Constants.MASTER_REGISTER_TITLE);
		builder.setIcon(R.drawable.ic_menu_master);
		
		builder.setPositiveButton("Set PIN", null);
		
		final AlertDialog alertDialog = builder.create();
		alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			
			@Override
			public void onShow(DialogInterface dialog) {
				Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
				button.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						
						EditText masterValue = (EditText) view.findViewById(R.id.masterInputValue);
						EditText confirmValue = (EditText) view.findViewById(R.id.masterInputConfirm);
						
						String value = StringUtils.defaultString(masterValue.getText().toString());
						String confirm = StringUtils.defaultString(confirmValue.getText().toString());
						
						if (!value.equals(confirm)) {
							M7k37Util.shout(getApplicationContext(), "Input values are not identical");
							masterValue.setText("");
							confirmValue.setText("");
							masterValue.requestFocus();
						}else if("".equals(value)&&"".equals(confirm)){
							M7k37Util.shout(getApplicationContext(), "Enter 4 digits for Master Key");
							masterValue.setText("");
							confirmValue.setText("");
							masterValue.requestFocus();
						}else{
							saveMasterPin(value);
							M7k37Util.shout(getApplicationContext(), "Master key is now set to " + value);
							Log.i(M7k37Constants.HEADER, " Master key is set to "
									+ value + " is now saved");
							alertDialog.dismiss();
						}
					}
				});
			}
		});
		alertDialog.show();
	}
	
	
	

	/**
	 * Display list of key-value
	 */
	private void showListDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(M7k37Constants.DIALOG_INDENT + M7k37Constants.LIST_TITLE);
		builder.setIcon(R.drawable.ic_menu_list);
		Map saved = getPreferences();
		String[] keys = M7k37Util.getKeysFromMap(saved);
		String[] nodeList = new String[keys.length];
		for (int i = 0; i < nodeList.length; i++) {
			nodeList[i] = keys[i] + " : "
					+ M7k37Util.showPassword(getPreferences(keys[i]));
		}
		ArrayAdapter adap = new ArrayAdapter(this,
				android.R.layout.select_dialog_item, nodeList);
		builder.setAdapter(adap, null);
		builder.setPositiveButton("Confirm",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						Log.i(M7k37Constants.HEADER, "List is now checked");
					}
				});
		builder.create().show();
	}
	
	
	/**
	 * Add new password
	 */
	private void showAddDialog(){
		LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
		final View view = li.inflate(R.layout.add, (ViewGroup) findViewById(R.id.addLayout));
		AlertDialog.Builder builder = new AlertDialog.Builder(M7k37Main.this);
		builder.setView(view);
		builder.setTitle(M7k37Constants.DIALOG_INDENT + M7k37Constants.ADD_TITLE);
		builder.setIcon(R.drawable.ic_menu_add);
		
		builder.setPositiveButton("Add", null);
		builder.setNegativeButton("Close", null);
		
		final AlertDialog alertDialog = builder.create();
		alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			
			@Override
			public void onShow(DialogInterface dialog) {
				Button addButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
				addButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						EditText keyValue = (EditText) view.findViewById(R.id.addKeyField);
						EditText addValue = (EditText) view.findViewById(R.id.addValueField);
						EditText confirmValue = (EditText) view.findViewById(R.id.addConfirmField);
						
						String key = StringUtils.defaultString(keyValue.getText().toString());
						String value = StringUtils.defaultString(addValue.getText().toString());
						String confirm = StringUtils.defaultString(confirmValue.getText().toString());
						
						if (value.equals(confirm)) {
							savePreferences(key, value);
							Log.i(M7k37Constants.HEADER, key + " - " + value
									+ " is now saved");
							M7k37Util.shout(getApplicationContext(), "[" + key + "] is now mapped to " + value);
							alertDialog.dismiss();
						}else{
							M7k37Util.shout(getApplicationContext(), "Input values are not identical");
							addValue.setText("");
							confirmValue.setText("");
							addValue.requestFocus();
						}
					}
				}); // Add Button
				
				Button cancelButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
				cancelButton.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						alertDialog.dismiss();
					}
				}); // Cancel Button
				
			}
		});
		alertDialog.show();
	}
	
	
	/**
	 * Update existing password
	 */
	private void showEditDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(M7k37Constants.DIALOG_INDENT + M7k37Constants.EDIT_TITLE);
		builder.setIcon(R.drawable.ic_menu_edit);
		Map saved = getPreferences();
		String[] keys = M7k37Util.getKeysFromMap(saved);
		final String[] nodeList = new String[keys.length];
		for (int i = 0; i < nodeList.length; i++) {
			nodeList[i] = keys[i] + " : "
					+ M7k37Util.showPassword(getPreferences(keys[i]));
		}
		builder.setSingleChoiceItems(nodeList, 0,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						editSelected = which;
						Log.i(M7k37Constants.HEADER, which + " is selected");
					}
				})
				.setPositiveButton("Edit",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Log.i(M7k37Constants.HEADER, editSelected
										+ " is selected with Edit button");
								// Change screen via Intent
								Intent intent = new Intent(M7k37Main.this,
										M7k37Edit.class);
								intent.putExtra(M7k37Constants.EDIT_LIST,
										nodeList[editSelected]);
								editSelected = 0;
								
								// keep editable state when coming back to Main
								isEditIntent = true;
								startActivity(intent);
							}
						})
				.setNegativeButton("Close",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Log.i(M7k37Constants.HEADER,
										editSelected
												+ " is selected and will close with Close button");
								editSelected = 0;
								dialog.dismiss();
							}
						});
		builder.show();
	}
	
	
	/**
	 * Delete password
	 */
	private void showDeleteDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(M7k37Constants.DIALOG_INDENT + M7k37Constants.REMOVE_TITLE);
		builder.setIcon(R.drawable.ic_menu_remove);
		Map saved = getPreferences();
		String[] keys = M7k37Util.getKeysFromMap(saved);
		final String[] nodeList = new String[keys.length];
		for (int i = 0; i < nodeList.length; i++) {
			nodeList[i] = keys[i] + " : "
					+ M7k37Util.showPassword(getPreferences(keys[i]));
		}

		deleteSelected = new boolean[nodeList.length];
		builder.setMultiChoiceItems(nodeList, null,
				new DialogInterface.OnMultiChoiceClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
						Log.i(M7k37Constants.HEADER, which + " is " + isChecked
								+ " in showDeleteDialog()");
						deleteSelected[which] = !deleteSelected[which];
					}
				})
				.setPositiveButton("Delete",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Log.i(M7k37Constants.HEADER, which
										+ " is selected in showDeleteDialog()");
								int count = 0;
								for (int i = 0; i < deleteSelected.length; i++) {
									Log.i(M7k37Constants.HEADER, i + " : "
											+ deleteSelected[i]);
									if (deleteSelected[i] == true) {
										String[] record = M7k37Util
												.stringSplit(nodeList[i]);
										removePreferences(record[0].trim());
										count++;
									}
								}
								M7k37Util.shout(getApplicationContext(), count
										+ " information deleted");
								dialog.dismiss();
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Log.i(M7k37Constants.HEADER, "Close button");
								dialog.dismiss();
							}
						});
		builder.show();
	}
	
	/**
	 * Verify master access
	 */
	private void showMasterDialog() {
		LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
		final View view = li.inflate(R.layout.master_check, (ViewGroup) findViewById(R.id.masterCheckLayout));
		AlertDialog.Builder builder = new AlertDialog.Builder(M7k37Main.this);
		builder.setView(view);
		builder.setTitle(M7k37Constants.DIALOG_INDENT + M7k37Constants.MASTER_CHECK_TITLE);
		builder.setIcon(R.drawable.ic_menu_master);
		
		builder.setPositiveButton("Confirm", null);
		builder.setNegativeButton("Close", null);
		
		final AlertDialog alertDialog = builder.create();
		alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			
			@Override
			public void onShow(DialogInterface dialog) {
				Button addButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
				addButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						EditText masterValue = (EditText) view.findViewById(R.id.masterField);
						
						String pin = StringUtils.defaultString(masterValue.getText().toString());
						
						if(pin.equals(getMasterPin()))
						{
							M7k37Util.shout(getApplicationContext(), "Master PIN is CORRECT");
							//adapter.isActivated = !adapter.isActivated;
							adapter.isActivated = true;
							
							// need to refresh getView()
							adapter.notifyDataSetChanged();
							Log.i(M7k37Constants.HEADER, "I am going to refresh ListView from M7k37MasterCheckDialog");
							
							alertDialog.dismiss();
						}else{
							M7k37Util.shout(getApplicationContext(), "Master PIN is INCORRECT");
							masterValue.setText("");
						}
					}
				}); // Confirm Button
				
				Button cancelButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
				cancelButton.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						alertDialog.dismiss();
					}
				}); // Cancel Button
				
			}
		});
		alertDialog.show();
	}
	
	 /**
		 * Display 'About' dialog
		 */
		private void showAbout() 
		{
	        // Inflate the about message contents
	        View messageView = getLayoutInflater().inflate(R.layout.about, null, false);
	 
	        // When linking text, force to always use default color. This works
	        // around a pressed color state bug.
	        TextView textView = (TextView) messageView.findViewById(R.id.about_credits);
	        int defaultColor = textView.getTextColors().getDefaultColor();
	        textView.setTextColor(defaultColor);
	 
	        AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setIcon(R.drawable.ic_menu_about);
	        builder.setTitle(R.string.app_name);
	        builder.setView(messageView);
	        builder.create();
	        builder.show();
		 }

	/**
	 * Return password mapped to specific key
	 * 
	 * @param name
	 * @return
	 */
	private String getPreferences(String name) {
		SharedPreferences pref = getSharedPreferences(M7k37Constants.PREF,
				Activity.MODE_PRIVATE);
		String value = pref.getString(name, "");
		return value;
	}

	/**
	 * Return Map as collection of password
	 * 
	 * @return
	 */
	private Map getPreferences() {
		SharedPreferences pref = getSharedPreferences(M7k37Constants.PREF,
				MODE_PRIVATE);
		Map all = pref.getAll();
		return all;
	}

	
	/**
	 * Save key-value into Preferences
	 * @param name
	 * @param value
	 */
	private void savePreferences(String name, String value) {
		SharedPreferences pref = getSharedPreferences(M7k37Constants.PREF, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(name, value);
		editor.commit();
	}


	/**
	 * Remove password mapped to specific key
	 * 
	 * @param name
	 */
	private void removePreferences(String name) {
		SharedPreferences pref = getSharedPreferences(M7k37Constants.PREF,
				MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.remove(name);
		editor.commit();
	}

	/**
	 * Clear all password
	 */
	private void removeAllPreferences() {
		SharedPreferences pref = getSharedPreferences(M7k37Constants.PREF,
				MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.clear();
		editor.commit();
	}

	/**
	 * Set flag to indicate master pin set already done
	 */
	private void saveRunHistory() {
		M7k37Preferences pref = new M7k37Preferences(this);
		pref.put(M7k37Constants.ALREADY_RUN, true);
	}

	/**
	 * Check whether already master pin saved
	 * 
	 * @return
	 */
	private boolean runBefore() {
		M7k37Preferences pref = new M7k37Preferences(this);
		return pref.getValue(M7k37Constants.ALREADY_RUN, false);
	}
	
	/**
	 * Get master pin
	 * @return
	 */
	private String getMasterPin() {
		M7k37Preferences pref = new M7k37Preferences(this);
		return pref.getValue(M7k37Constants.MASTER_PIN, "");
	}
	
	/**
	 * Save master pin
	 * @param value
	 */
	private void saveMasterPin(String value) {
		M7k37Preferences pref = new M7k37Preferences(this);
		pref.put(M7k37Constants.MASTER_PIN, value);
	}
}