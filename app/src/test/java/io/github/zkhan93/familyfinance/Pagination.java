package io.github.zkhan93.familyfinance;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import io.github.zkhan93.familyfinance.adapters.OtpListAdapter;
import io.github.zkhan93.familyfinance.models.DaoSession;
import io.github.zkhan93.familyfinance.models.MemberDao;

/**
 * Created by zeeshan on 25/3/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class Pagination {
    @Mock
    private DaoSession daoSession;
    @Mock
    private App app;
    @Mock
    private MemberDao memberDao;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void loadingFirstPage() {
//        App app = Mockito.mock(App.class);
//        FirebaseDatabase mockFirebase = Mockito.mock(FirebaseDatabase.class);
//        Mockito.when(FirebaseDatabase.getInstance()).thenReturn(mockFirebase);
//        Mockito.when(daoSession.getMemberDao()).thenReturn(memberDao);
//        Mockito.when(app.getDaoSession()).thenReturn(daoSession);
//        OtpListAdapter otp = new OtpListAdapter(app, "", null);
    }
}
