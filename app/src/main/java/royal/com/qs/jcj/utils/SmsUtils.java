package royal.com.qs.jcj.utils;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;
import android.widget.ProgressBar;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by jcj on 16/1/26.
 * 短信备份工具类
 */
public class SmsUtils {

    public interface CallBack{
        public void before(int count);
        public void after(int progress);
    }

    public static boolean backupSms(Context context, CallBack callBack) {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            final ContentResolver cr = context.getContentResolver();
            Uri uri = Uri.parse("content://sms/");
            final Cursor cursor = cr.query(uri, new String[]{"address", "date", "type", "body"}
                    , null, null, null);
            //总共有多少组数据
            final int count = cursor.getCount();

           // pd.setMax(count);
           // pb.setMax(count);
            callBack.before(count);

            //进度条初始值为0
            int progress = 0;
            try {
                final File file = new File(Environment.getExternalStorageDirectory(), "backup.xml");
                final FileOutputStream fos = new FileOutputStream(file);

                final XmlSerializer xmlSerializer = Xml.newSerializer();
                xmlSerializer.setOutput(fos, "utf-8");
                xmlSerializer.startDocument("utf-8", true);
                xmlSerializer.startTag(null, "smss");
                xmlSerializer.attribute(null,"size",String.valueOf(count));

                while (cursor.moveToNext()) {
                    final String address = cursor.getString(0);
                    final String date = cursor.getString(1);
                    final String type = cursor.getString(2);
                    final String body = cursor.getString(3);
                    System.out.println("address====" + address);
                    System.out.println("date====" + date);
                    System.out.println("type====" + type);
                    System.out.println("body====" + body);

                    xmlSerializer.startTag(null, "sms");

                    xmlSerializer.startTag(null, "address");
                    xmlSerializer.text(address);
                    xmlSerializer.endTag(null, "address");
                    xmlSerializer.startTag(null, "date");
                    xmlSerializer.text(date);
                    xmlSerializer.endTag(null, "date");
                    xmlSerializer.startTag(null, "type");
                    xmlSerializer.text(type);
                    xmlSerializer.endTag(null, "type");
                    xmlSerializer.startTag(null, "body");
                    xmlSerializer.text(body);
                    xmlSerializer.endTag(null, "body");

                    xmlSerializer.endTag(null, "sms");

                    progress++;
                   // pd.setProgress(progress);
                 //   pb.setProgress(progress);
                    callBack.after(progress);
                    Thread.sleep(200);
                }
                cursor.close();

                xmlSerializer.endTag(null, "smss");
                xmlSerializer.endDocument();
                fos.flush();
                fos.close();

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }


        }

        return false;
    }
}
