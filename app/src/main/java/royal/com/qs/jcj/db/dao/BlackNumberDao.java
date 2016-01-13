package royal.com.qs.jcj.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import royal.com.qs.jcj.domian.BlackNumber;

/**
 * Created by jcj on 16/1/9.
 */
public class BlackNumberDao {
    private BlackNumberDBOpenHelper helper;
    private SQLiteDatabase db;


    public BlackNumberDao(Context context) {
        helper = new BlackNumberDBOpenHelper(context);
    }

    public void insert(BlackNumber blackNumber) {
        String number = blackNumber.getNumber();
        String mode = blackNumber.getMode();
        db = helper.getWritableDatabase();

        for (int i = 0; i <= 300; i++) {
            ContentValues values = new ContentValues();
            values.put("number", Long.parseLong(number) + i);
            Random r = new Random();
            int m = r.nextInt(3) + 1;
            values.put("mode", m + "");
            long result = db.insert("blacknumber", null, values);
        }
        db.close();

    }

    public boolean update(String number, String mode) {
        ContentValues values = new ContentValues();
        values.put("mode", mode);
        db = helper.getReadableDatabase();
        final int result = db.update("blacknumber", values, "number = ?", new String[]{number});
        db.close();
        if (result != 0) {
            return true;
        }
        return false;
    }

    public boolean delete(String number) {
        db = helper.getReadableDatabase();
        final int result = db.delete("blacknumber", "number=?", new String[]{number});
        db.close();
        if (result != 0) {
            return true;
        }
        return false;
    }

    /**
     * 查询所有数据
     *
     * @return
     */
    public List<BlackNumber> findAll() {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<BlackNumber> numbers = new ArrayList<BlackNumber>();
        db = helper.getReadableDatabase();
        final Cursor cursor = db.query("blacknumber", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String number = cursor.getString(cursor.getColumnIndex("number"));
            String mode = cursor.getString(cursor.getColumnIndex("mode"));
            final BlackNumber blackNumber = new BlackNumber(number, mode);
            numbers.add(blackNumber);
        }
        cursor.close();
        db.close();

        return numbers;
    }

    /**
     * 分页查询
     *
     * @param startPage 起始页
     * @param everyPage 每一页显示的条数
     * @return
     */
    public List<BlackNumber> findByPage(int startPage, int everyPage) {
        List<BlackNumber> blackNumbers = new ArrayList<BlackNumber>();
        db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select number,mode from blacknumber limit ? offset ?",
                new String[]{String.valueOf(everyPage), String.valueOf(startPage * everyPage)});
        while (cursor.moveToNext()) {
            final String number = cursor.getString(0);
            final String mode = cursor.getString(1);
            BlackNumber blackNumber = new BlackNumber(number, mode);
            blackNumbers.add(blackNumber);
        }
        return blackNumbers;
    }

    /**
     *
     * @param startPos  开始的位置（条数）
     * @param everyPage 每一次刷新出来的条数
     * @return
     */
    public List<BlackNumber> findByGroup(int startPos , int everyPage) {
        List<BlackNumber> blackNumbers = new ArrayList<BlackNumber>();
        db = helper.getReadableDatabase();
        final Cursor cursor = db.rawQuery("select number,mode from blacknumber limit ? offset ?",
                new String[]{String.valueOf(everyPage), String.valueOf(startPos)
        });

        while (cursor.moveToNext()) {
            final String number = cursor.getString(0);
            final String mode = cursor.getString(1);
            final BlackNumber blackNumber = new BlackNumber(number, mode);
            blackNumbers.add(blackNumber);
        }

        return blackNumbers;
    }

    public int getTotalNumber() {
        db = helper.getReadableDatabase();
        final Cursor cursor = db.rawQuery("select count(*) from blacknumber", null);
        int count = 0;
        while (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        return count;
    }
}
