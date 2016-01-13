package royal.com.qs.jcj.domian;

import java.io.Serializable;

/**
 * Created by jcj on 16/1/9.
 */
public class BlackNumber implements Serializable{
    private String number;
    private String mode;

    public BlackNumber(String number, String mode) {
        this.number = number;
        this.mode = mode;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
