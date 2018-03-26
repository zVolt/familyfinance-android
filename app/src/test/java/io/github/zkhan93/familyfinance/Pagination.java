package io.github.zkhan93.familyfinance;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import io.github.zkhan93.familyfinance.adapters.OtpListAdapter;
import io.github.zkhan93.familyfinance.models.DaoSession;
import io.github.zkhan93.familyfinance.models.MemberDao;

import static org.mockito.Mockito.*;

/**
 * Created by zeeshan on 25/3/18.
 */

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
        App app = Mockito.mock(App.class);
        Mockito.when(daoSession.getMemberDao()).thenReturn(memberDao);
        Mockito.when(app.getDaoSession()).thenReturn(daoSession);
        OtpListAdapter otp = new OtpListAdapter(app, "", null);
    }
}
