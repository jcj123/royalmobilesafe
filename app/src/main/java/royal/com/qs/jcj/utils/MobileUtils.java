package royal.com.qs.jcj.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by jcj on 15/12/17.
 */
public class MobileUtils {

    public static String readSteam(InputStream inputStream) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int len = 0;
        byte[] bytes = new byte[1024];
        if((len= inputStream.read(bytes))!=-1){
            bos.write(bytes,0,len);
        }
        String s = bos.toString();
        bos.close();
        inputStream.close();
        return s;
    }

}
