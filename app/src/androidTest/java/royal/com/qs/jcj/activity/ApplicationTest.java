package royal.com.qs.jcj.activity;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.test.ApplicationTestCase;

import royal.com.qs.jcj.db.dao.BlackNumberDBOpenHelper;
import royal.com.qs.jcj.db.dao.BlackNumberDao;
import royal.com.qs.jcj.domian.BlackNumber;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {

    private SQLiteDatabase db;
    private BlackNumberDBOpenHelper helper;
    private BlackNumberDao dao;

    public ApplicationTest() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        helper = new BlackNumberDBOpenHelper(getContext());
        db = helper.getReadableDatabase();
        dao = new BlackNumberDao(getContext());
    }

    public void testInsert() {
        BlackNumber blackNumber = new BlackNumber("13000000000", "1");
        dao.insert(blackNumber);
    }

    public void testUpdate() {
        dao.update("18971063242","2");
    }

    public void testFindAll() {
        dao.findAll();
    }

    public void testDelete() {
        final boolean delete = dao.delete("18971063242");

    }
}