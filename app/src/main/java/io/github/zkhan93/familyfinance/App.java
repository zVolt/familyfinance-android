package io.github.zkhan93.familyfinance;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.greendao.database.Database;

import io.github.zkhan93.familyfinance.models.DaoMaster;
import io.github.zkhan93.familyfinance.models.DaoSession;
import io.github.zkhan93.familyfinance.util.Constants;

/**
 * Created by zeeshan on 9/7/17.
 */

public class App extends Application {
    public static final boolean ENCRYPTED = false;
    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, ENCRYPTED ?
                "ff-db-encrypted" : "ff-db");
        Database db = ENCRYPTED ? helper.getEncryptedWritableDb("super-secret") : helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
        Constants.generateDummyData(this);
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
