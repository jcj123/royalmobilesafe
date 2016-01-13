package royal.com.qs.jcj.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import royal.com.qs.jcj.R;


public class SettingDialog extends RelativeLayout {
    TextView mTitle;
    TextView mDesp;

    public SettingDialog(Context context) {
        super(context);
        init();
    }

    public SettingDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SettingDialog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View.inflate(getContext(), R.layout.view_address_item, this);

        mTitle = (TextView) findViewById(R.id.tv_title);
        mDesp = (TextView) findViewById(R.id.tv_desp);
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setmDesp(String desp) {
        mDesp.setText(desp);
    }


}
