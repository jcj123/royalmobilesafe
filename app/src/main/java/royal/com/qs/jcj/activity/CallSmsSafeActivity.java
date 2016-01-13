package royal.com.qs.jcj.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import royal.com.qs.jcj.R;
import royal.com.qs.jcj.db.dao.BlackNumberDao;
import royal.com.qs.jcj.domian.BlackNumber;

public class CallSmsSafeActivity extends Activity {

    private ListView lv_blacknumber;
    private List<BlackNumber> numbers = new ArrayList<BlackNumber>();
    private BlackNumberDao dao;
    private BlackNumberAdapter adapter;

    //每一页的条目数
    private int everyPage = 20;
    //开始的位置
    private int startPos = 0;

    private LinearLayout layout;
    private int totalNumber;//总条目数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_sms_safe);
        initUI();

        initData();
    }

    private void initData() {
        dao = new BlackNumberDao(this);

        new Thread() {
            @Override
            public void run() {
                totalNumber = dao.getTotalNumber();

                if(numbers == null) {
                    numbers = dao.findByGroup(startPos, everyPage);
                } else {
                    numbers.addAll(dao.findByGroup(startPos,everyPage));
                }
                handler.sendEmptyMessage(1);

            }
        }.start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            layout.setVisibility(View.INVISIBLE);

            if(adapter==null) {
                adapter = new BlackNumberAdapter(CallSmsSafeActivity.this,
                        R.layout.list_items_blacknumber, numbers);
                lv_blacknumber.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }

        }
    };

    private void initUI() {
        layout = (LinearLayout) findViewById(R.id.ll_pb);
        layout.setVisibility(View.VISIBLE);
        lv_blacknumber = (ListView) findViewById(R.id.lv_blacknumber);

        lv_blacknumber.setOnScrollListener(new AbsListView.OnScrollListener() {
            /**
             * 滑动停止时调用该方法
             * @param view
             * @param scrollState
             */
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_IDLE:
                        final int lastVisiblePosition = lv_blacknumber.getLastVisiblePosition();
                        System.out.println(lastVisiblePosition);

                        if (lastVisiblePosition >= numbers.size()-1) {
                            startPos += everyPage;
                            if (startPos>=totalNumber) {
                                Toast.makeText(CallSmsSafeActivity.this,"没有更多数据了亲",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                            initData();
                        }

                        break;
                }


            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    class BlackNumberAdapter extends ArrayAdapter<BlackNumber> {

        private int resourceId;

        public BlackNumberAdapter(Context context, int resource, List<BlackNumber> objects) {
            super(context, resource, objects);
            resourceId = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            BlackNumber blackNumber = numbers.get(position);
            View view = View.inflate(CallSmsSafeActivity.this, resourceId, null);
            ViewHolder holder = new ViewHolder();
            holder.tv_number = (TextView) view.findViewById(R.id.tv_number);
            holder.tv_mode = (TextView) view.findViewById(R.id.tv_mode);
            view.setTag(holder);
            if (convertView != null) {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }

            TextView tv_number = holder.tv_number;
            TextView tv_mode = holder.tv_mode;
            tv_number.setText(blackNumber.getNumber());
            String mode = blackNumber.getMode();
            if (mode.equals("1")) {
                tv_mode.setText("来电拦截+短信");
            } else if (mode.equals("2")) {
                tv_mode.setText("电话拦截");
            } else if (mode.equals("3")) {
                tv_mode.setText("短信拦截");
            }
            return view;
        }

        class ViewHolder {
            TextView tv_number;
            TextView tv_mode;
        }

        public void addNumber(View view) {

        }
    }
}
