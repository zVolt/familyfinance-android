package io.github.zkhan93.familyfinance;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.adapters.AddonCardListAdapter;
import io.github.zkhan93.familyfinance.models.AddonCard;
import io.github.zkhan93.familyfinance.models.CCard;
import io.github.zkhan93.familyfinance.models.CCardDao;
import io.github.zkhan93.familyfinance.models.DaoSession;
import io.github.zkhan93.familyfinance.util.Util;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentCCardDetail#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCCardDetail extends Fragment {

    public static String TAG = FragmentCCardDetail.class.getSimpleName();
    private static final String ARG_CARD = "card";

    private CCard card;

    @BindView(R.id.cards)
    RecyclerView cards;

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

    @BindView(R.id.chart)
    LineChart chart;


    private CCardDao cCardDao;

    public FragmentCCardDetail() {
    }

    /**
     * @param card a credit card.
     * @return A new instance of fragment CCardDetailFragment.
     */
    public static FragmentCCardDetail newInstance(CCard card) {
        FragmentCCardDetail fragment = new FragmentCCardDetail();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CARD, card);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            card = getArguments().getParcelable(ARG_CARD);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        cCardDao = ((App) context.getApplicationContext()).getDaoSession().getCCardDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ccard_detail, container, false);
        ButterKnife.bind(this, rootView);

        Bundle bundle = getArguments();
        if (bundle != null) {
            card = bundle.getParcelable("card");
            if (card != null)
                card.__setDaoSession((DaoSession) cCardDao.getSession());
        }
        setCardDetails();
        setUpCards();
        setUpChart();
        return rootView;
    }

    private void setUpChart() {
        List<Entry> entries = new ArrayList<>();
        // turn your data into Entry objects
        entries.add(new Entry(1, 1334));
        entries.add(new Entry(2, 1543));
        entries.add(new Entry(3, 3543));
        entries.add(new Entry(4, 643));
        entries.add(new Entry(5, 4632));

        LineDataSet dataSet = new LineDataSet(entries, "Earning"); // add entries to dataset
        dataSet.setDrawFilled(false);
        dataSet.setValueTextSize(16);
        dataSet.setDrawValues(true);
        dataSet.setLineWidth(2f);
        dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        dataSet.setColor(getResources().getColor(R.color.md_green_500));
        dataSet.setDrawCircles(false);


        LineData lineData = new LineData();
        lineData.addDataSet(dataSet);
        Legend legend = chart.getLegend();
        legend.setEnabled(false);

        chart.setData(lineData);
        chart.setDescription(null);
        chart.setDrawGridBackground(false);
        chart.setDrawBorders(false);
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setTouchEnabled(false);
        chart.getXAxis().setSpaceMin(1);
        chart.getXAxis().setSpaceMax(1);
        chart.getXAxis().setDrawAxisLine(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                switch (String.valueOf(value)) {
                    case "1.0":
                        return "Jan";
                    case "2.0":
                        return "Feb";
                    case "3.0":
                        return "Mar";
                    case "4.0":
                        return "Apr";
                    case "5.0":
                        return "May";
                }
                return "";
            }
        });
        chart.getAxisLeft().setEnabled(false);
        chart.getAxisRight().setEnabled(false);

        chart.invalidate();
//        dataSet.setColor();
//        dataSet.setValueTextColor(...); // styling, ...
    }

    private void setUpCards() {
        List<CCard> cardItems = new ArrayList<CCard>();
        cardItems.add(card);
        for (AddonCard ac : card.getAddonCards()) {
            cardItems.add(new CCard(ac.getNumber(), ac.getName(), card.getBank(), ac.getName(),
                    null, null, String.valueOf(ac.getCvv()), ac.getPhoneNumber(),
                    null, card.getType(), 0, ac.getExpiresOn(), card.getPaymentDay(),
                    card.getBillingDay(), card.getMaxLimit(), card.getConsumedLimit(),
                    ac.getUpdatedByMemberId()));
        }
        AddonCardListAdapter adapter = new AddonCardListAdapter(null, cards);
        adapter.setItems(cardItems);
        cards.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        cards.setAdapter(adapter);
        SnapHelper snap = new PagerSnapHelper();
        snap.attachToRecyclerView(cards);
    }

    private void setCardDetails() {
        String UNSET = "Not Provided";
        String value = card.getPhoneNumber();
        if (value == null || value.isEmpty())
            value = UNSET;
        phoneNumber.setText(value);

        value = getNextBillDate();
        if (value == null || value.isEmpty())
            value = UNSET;
        billDate.setText(value);

        value = card.getUserid();
        if (value == null || value.isEmpty())
            value = UNSET;
        userID.setText(value);

        value = card.getPassword();
        if (value == null || value.isEmpty())
            value = UNSET;
        password.setText(value);

        value = NumberFormat.getCurrencyInstance().format(card.getMaxLimit());
        cardLimit.setText(value);
    }

    private String getNextBillDate() {
        if (card.getBillingDay() != -1) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, card.getBillingDay());
            if (cal.before(Calendar.getInstance().getTime())) {
                cal.add(Calendar.MONTH, 1);
            }
            return new SimpleDateFormat("dd MMM, yyyy", Locale.US).format(cal.getTime());
        }
        return null;
    }
}
