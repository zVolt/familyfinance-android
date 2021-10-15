package io.github.zkhan93.familyfinance;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.adapters.AddonCardListAdapter;
import io.github.zkhan93.familyfinance.models.AddonCard;
import io.github.zkhan93.familyfinance.models.CCard;
import io.github.zkhan93.familyfinance.models.CCardDao;
import io.github.zkhan93.familyfinance.models.DaoSession;
import io.github.zkhan93.familyfinance.util.Util;

public class CardDetailActivity extends AppCompatActivity {
    public static String TAG = CardDetailActivity.class.getSimpleName();

    @BindView(R.id.cards)
    RecyclerView cards;

    @BindView(R.id.card_type)
    TextView cardType;
    @BindView(R.id.phone_number)
    TextView phoneNumber;
    @BindView(R.id.bill_date)
    TextView billDate;
    @BindView(R.id.userid)
    TextView userID;

    @BindView(R.id.password)
    TextView password;
    @BindView(R.id.card_limit)
    TextView cardLimit;

    private CCard ccard;
    private CCardDao cCardDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_view_ccard);
        ButterKnife.bind(this);
        cCardDao = ((App) getApplication()).getDaoSession().getCCardDao();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            ccard = bundle.getParcelable("card");
            if (ccard != null)
                ccard.__setDaoSession((DaoSession) cCardDao.getSession());
        }

        Util.Log.d(TAG, "ccard received: %s", ccard.toString());
        setCardDetails();
        setUpCards();
    }

    private void setUpCards() {
        List<CCard> cardItems = new ArrayList<CCard>();
        cardItems.add(ccard);
        for (AddonCard ac : ccard.getAddonCards()) {
            cardItems.add(new CCard(ac.getNumber(), ac.getName(), ccard.getBank(), ac.getName(),
                    null, null, String.valueOf(ac.getCvv()), ac.getPhoneNumber(),
                    null, ccard.getType(), 0, ac.getExpiresOn(), ccard.getPaymentDay(),
                    ccard.getBillingDay(), ccard.getMaxLimit(), ccard.getConsumedLimit(), ac.getUpdatedByMemberId()));
        }
        AddonCardListAdapter adapter = new AddonCardListAdapter(null, cards);
        adapter.setItems(cardItems);
        cards.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        cards.setAdapter(adapter);
        SnapHelper snap = new PagerSnapHelper();
        snap.attachToRecyclerView(cards);
    }

    private void setCardDetails() {
        cardType.setText("Primary");

        phoneNumber.setText(ccard.getNumber());

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, ccard.getBillingDay());
        if (cal.before(Calendar.getInstance().getTime())) {
            cal.add(Calendar.MONTH, 1);
        }

        billDate.setText(cal.getTime().toString());
        userID.setText(ccard.getUserid());
        password.setText(ccard.getPassword());
        cardLimit.setText(String.valueOf(ccard.getMaxLimit()));
    }
}
