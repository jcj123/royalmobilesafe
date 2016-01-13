package royal.com.qs.jcj.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by jcj on 15/12/28.
 */
public class AddressDao {
    private static String path = "data/data/royal.com.qs.jcj.royalmobilesafe/files/address.db";

    /**
     * 根据号码获取归属地信息
     *
     * @param phone
     * @return
     */
    public static String getAddress(String phone) {
        String address = "未知号码";
        //获取数据库连接对象
        final SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openDatabase
                (path, null, SQLiteDatabase.OPEN_READONLY);
        // 手机号码特点: 1 + (3,4,5,6,7,8) + (9位数字)
        // 正则表达式
        if(phone.matches("^1[3-8]\\d{9}$")) {
            final Cursor cursor = sqLiteDatabase.rawQuery("select location from data2 where id=" +
                    "(select outkey from data1 where id=?)",new String[]{phone.substring(0, 7)});
            if(cursor.moveToNext()) {
                address = cursor.getString(0);
            }
        }else if (phone.matches("^\\d+$")) {// 匹配数字
            switch (phone.length()) {
                case 3:
                    address = "报警电话";
                    break;
                case 4:
                    address = "模拟器";
                    break;
                case 5:
                    address = "客服电话";
                    break;
                case 7:
                case 8:
                    address = "本地电话";
                    break;
                default:
                    // 01088881234
                    // 048388888888
                    if (phone.startsWith("0") && phone.length() > 10) {// 有可能是长途电话
                        // 有些区号是4位,有些区号是3位(包括0)

                        // 先查询4位区号
                        Cursor cursor = sqLiteDatabase.rawQuery(
                                "select location from data2 where area =?",
                                new String[] { phone.substring(1, 4) });

                        if (cursor.moveToNext()) {
                            address = cursor.getString(0);
                        } else {
                            cursor.close();

                            // 查询3位区号
                            cursor = sqLiteDatabase.rawQuery(
                                    "select location from data2 where area =?",
                                    new String[] { phone.substring(1, 3) });

                            if (cursor.moveToNext()) {
                                address = cursor.getString(0);
                            }

                            cursor.close();
                        }
                    }
                    break;
            }
        }
        sqLiteDatabase.close();
        return address;
    }
}
