package royal.com.qs.jcj.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import royal.com.qs.jcj.R;

/**
 * 读取联系人界面
 */
public class ContactsActivity extends ActionBarActivity {
    private ListView contacts_lv;
    private  List<Map<String, String>> readContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        contacts_lv = (ListView) findViewById(R.id.contacts_lv);
        readContacts = readContacts();
        contacts_lv.setAdapter(new SimpleAdapter(this, readContacts, R.layout.list_contacts_item
                , new String[]{"name", "phone"}, new int[]{R.id.name_tv, R.id.phone_tv}));

        contacts_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String phone = readContacts.get(position).get("phone");
                final Intent intent = new Intent();
                intent.putExtra("phone",phone);
                setResult(RESULT_OK,intent);
                //关闭当前页面才能触发此点击响应事件，所以用finish关闭当前页面
                finish();
            }
        });
    }


    private List<Map<String, String>> readContacts() {
        final ContentResolver cr = getContentResolver();
        Uri rawContactsUri = Uri
                .parse("content://com.android.contacts/raw_contacts");
        Uri dataUri = Uri.parse("content://com.android.contacts/data");
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
// 从raw_contacts中读取联系人的id("contact_id")
        Cursor rawContactsCursor = getContentResolver().query(rawContactsUri,
                new String[] { "contact_id" }, null, null, null);
        if (rawContactsUri != null) {
            while (rawContactsCursor.moveToNext()) {
                final String contact_id = rawContactsCursor.getString(0);
                // 根据contact_id从data表中查询出相应的电话号码和联系人名称, 实际上查询的是视图view_data
                Cursor dataCursor = getContentResolver().query(dataUri,
                        new String[]{"data1", "mimetype"}, "contact_id=?",
                        new String[]{contact_id}, null);
                if (dataCursor != null) {
                    Map<String, String> map = new HashMap<String, String>();

                    while (dataCursor.moveToNext()) {
                        final String data1 = dataCursor.getString(0);
                        final String mimetype = dataCursor.getString(1);
                        if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
                            map.put("phone", data1);
                        } else if ("vnd.android.cursor.item/name"
                                .equals(mimetype)) {
                            map.put("name", data1);
                        }
                    }
                    list.add(map);
                    dataCursor.close();
                }
            }
            rawContactsCursor.close();
        }

        return list;
    }
}
