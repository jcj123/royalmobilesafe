package royal.com.qs.jcj.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import royal.com.qs.jcj.R;

/**
 * 引导页3
 */
public class Setup3Activity extends BaseSetupActivity {
    private Button choose_contact_bt;
    private EditText safe_phone;
    private SharedPreferences mPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setup3);
        safe_phone = (EditText) findViewById(R.id.enter_safe_phone);
        mPref = getSharedPreferences("config",MODE_PRIVATE);
        final String phone = mPref.getString("safe_phone", "");

        safe_phone.setText(phone);

        choose_contact_bt = (Button) findViewById(R.id.choose_contacts);
        choose_contact_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Setup3Activity.this,ContactsActivity.class);
                startActivityForResult(intent,1);
            }
        });
    }
    public void moveToPrevious() {
        startActivity(new Intent(this,Setup2Activity.class));
        finish();
        overridePendingTransition(R.anim.transtate_previous_in,R.anim.transtate_previous_out);
    }

    public void moveToNext() {
        startActivity(new Intent(this,Setup4Activity.class));

        final String s = safe_phone.getText().toString();
        mPref.edit().putString("safe_phone",s).commit();
        finish();
        overridePendingTransition(R.anim.transtate_in,R.anim.transtate_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK) {
            String phone = data.getStringExtra("phone");
            phone = phone.replaceAll("-", "").replaceAll(" ", "");
            safe_phone.setText(phone);
        }
    }
}
