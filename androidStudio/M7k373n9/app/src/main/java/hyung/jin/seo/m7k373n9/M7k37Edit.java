package hyung.jin.seo.m7k373n9;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



public class M7k37Edit extends Activity implements OnClickListener {

    TextView key;
    EditText value, confirm;
    Button save, cancel;

    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);
        key = (TextView) findViewById(R.id.editKeyField);
        value = (EditText) findViewById(R.id.editValueField);
        confirm = (EditText) findViewById(R.id.editConfirmField);
        save = (Button) findViewById(R.id.editSaveButton);
        cancel = (Button) findViewById(R.id.editCancelButton);
        save.setOnClickListener(this);
        cancel.setOnClickListener(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String item = bundle.getString(M7k37Constants.EDIT_LIST);
        String[] selected = M7k37Util.stringSplit(item);

        key.setText(selected[0].trim());
//		value.setText(M7k37Util.showPassword(selected[1].trim()));
//		confirm.setText(M7k37Util.showPassword(selected[1].trim()));
        value.setText(selected[1].trim());
        confirm.setText(selected[1].trim());
    }
    @Override
    public void onClick(View v) {
        if(v==save)
        {
            Log.i(M7k37Constants.HEADER, "Edit button");
            String newKey = key.getText().toString().trim();
            String newValue = value.getText().toString().trim();
            String newConfirm = confirm.getText().toString().trim();
            if(newValue.equals(newConfirm))
            {
                savePreferences(newKey, newValue);
                Log.i(M7k37Constants.HEADER, key + " - " + value + " is now saved");
                M7k37Util.shout(getApplicationContext(), "[" + newKey + "] is now updated");
                Intent back = new Intent(this, M7k37Main.class);
                back.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(back);
                finish();
            }else{
                M7k37Util.shout(getApplicationContext(), "Please Make Sure Same Confirm Value");
            }

        }else if(v==cancel){
            Log.i(M7k37Constants.HEADER, "Cancel button");
            Intent back = new Intent(this, M7k37Main.class);
            back.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(back);
            finish();
        }
    }


    private void savePreferences(String name, String value)
    {
        SharedPreferences pref = getSharedPreferences(M7k37Constants.PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(name, value);
        editor.commit();
    }


}
