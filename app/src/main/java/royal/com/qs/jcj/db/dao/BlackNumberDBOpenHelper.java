package royal.com.qs.jcj.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jcj on 16/1/9.
 */
public class BlackNumberDBOpenHelper extends SQLiteOpenHelper{

    public BlackNumberDBOpenHelper(Context context) {
        super(context, "blackNumber.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table blacknumber (_id integer primary key autoincrement," +
                " number varchar(20) , mode varchar(2))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
