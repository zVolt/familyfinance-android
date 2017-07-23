package io.github.zkhan93.familyfinance;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.greendao.query.Query;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.models.Account;
import io.github.zkhan93.familyfinance.models.CCard;
import io.github.zkhan93.familyfinance.models.DaoSession;
import io.github.zkhan93.familyfinance.tasks.LoadFromDbTask;
import io.github.zkhan93.familyfinance.util.Constants;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentSummary.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentSummary#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSummary extends Fragment {
    public static final String TAG = FragmentSummary.class.getSimpleName();
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_FAMILY_ID = "familyId";

    @BindView(R.id.grand_total)
    public TextView grandTotal;
    @BindView(R.id.account_total)
    public TextView accountTotal;
    @BindView(R.id.consumed_cc_limit)
    public TextView consumedCCTotal;
    @BindView(R.id.remaining_cc_limit)
    public TextView remainingCCTotal;
    @BindView(R.id.account_title)
    public TextView accountTitle;
    @BindView(R.id.card_title)
    public TextView cardTitle;

    @BindView(R.id.next_payment1)
    public View nextPayment1;
    @BindView(R.id.next_payment2)
    public View nextPayment2;
    @BindView(R.id.next_payment3)
    public View nextPayment3;

    private String familyId;
    private OnFragmentInteractionListener mListener;
    private DaoSession daoSession;
    private float amountTotal, amountConsumedCC, amountRemainingCC, amountAccount;

    public FragmentSummary() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentSummary.
     */

    public static FragmentSummary newInstance(String familyId) {
        FragmentSummary fragment = new FragmentSummary();
        Bundle args = new Bundle();
        args.putString(ARG_FAMILY_ID, familyId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            familyId = getArguments().getString(ARG_FAMILY_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_summary, container, false);
        ButterKnife.bind(this, rootView);
        daoSession = ((App) getActivity().getApplication()).getDaoSession();
        if (savedInstanceState == null)
            refreshData();
        else {
            amountTotal = savedInstanceState.getFloat("amountTotal", amountTotal);
            amountAccount = savedInstanceState.getFloat("amountAccount", amountAccount);
            amountConsumedCC = savedInstanceState.getFloat("amountConsumedCC", amountConsumedCC);
            amountRemainingCC = savedInstanceState.getFloat("amountRemainingCC", amountRemainingCC);
            familyId = savedInstanceState.getString("familyId", familyId);
            consumedCCTotal.setText(NumberFormat.getCurrencyInstance().format
                    (amountConsumedCC));
            remainingCCTotal.setText(NumberFormat.getCurrencyInstance().format
                    (amountRemainingCC));
            accountTotal.setText(NumberFormat.getCurrencyInstance().format
                    (amountAccount));
            grandTotal.setTextColor(ContextCompat.getColor(getActivity(), amountTotal >= 0 ?
                    R.color.md_green_400 : R.color.md_red_400));
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        Query<CCard> cardQuery = daoSession.getCCardDao().queryBuilder().build();
        new LoadFromDbTask<>(cardQuery, new LoadFromDbTask.Listener<CCard>() {
            @Override
            public void onLoadTaskComplete(List<CCard> data) {
                int cardCount = data.size();
                cardTitle.setText(String.format(Locale.ENGLISH, "%d Cards", cardCount));
                amountRemainingCC = 0;
                amountConsumedCC = 0;
                for (CCard cCard : data) {
                    amountConsumedCC += cCard.getConsumedLimit();
                    amountRemainingCC += cCard.getRemainingLimit();
                }
                consumedCCTotal.setText(NumberFormat.getCurrencyInstance().format
                        (amountConsumedCC));
                remainingCCTotal.setText(NumberFormat.getCurrencyInstance().format
                        (amountRemainingCC));
                amountTotal = amountAccount - amountConsumedCC;
                grandTotal.setText(NumberFormat.getCurrencyInstance().format(amountTotal));
                grandTotal.setTextColor(ContextCompat.getColor(getActivity(), amountTotal >= 0 ?
                        R.color.md_green_400 : R.color.md_red_400));
                Collections.sort(data, CCard.BY_PAYMENT_DATE);
                View[] views = new View[]{
                        nextPayment1,
                        nextPayment2, nextPayment3
                };
                TextView txtView;
                CCard cCard;
                String str;
                for (int i = 0; i < views.length && i < data.size(); i++) {
                    cCard = data.get(i);
                    txtView = ButterKnife.findById(views[i], R.id.amount);
                    txtView.setText(NumberFormat.getCurrencyInstance().format(cCard
                            .getConsumedLimit()));
                    txtView = ButterKnife.findById(views[i], R.id.card_name);
                    
                    str = String.format("%s - %s", cCard.getCardholder(), cCard.getBank());
                    txtView.setText(str);
                    txtView = ButterKnife.findById(views[i], R.id.date);
                    txtView.setText(Constants.PAYMENT_DATE.format(cCard.getPaymentDate()));
                }

            }
        }).execute();
        Query<Account> accountQuery = daoSession.getAccountDao().queryBuilder().build();
        new LoadFromDbTask<>(accountQuery, new LoadFromDbTask.Listener<Account>() {
            @Override
            public void onLoadTaskComplete(List<Account> data) {
                int accountCount = data.size();
                amountAccount = 0;
                for (Account account : data) {
                    amountAccount += account.getBalance();
                }
                accountTitle.setText(String.format(Locale.ENGLISH, "%d Accounts", accountCount));
                accountTotal.setText(NumberFormat.getCurrencyInstance().format
                        (amountAccount));
                amountTotal = amountAccount - amountConsumedCC;
                grandTotal.setText(NumberFormat.getCurrencyInstance().format
                        (amountTotal));
                grandTotal.setTextColor(ContextCompat.getColor(getActivity(), amountTotal >= 0 ?
                        R.color.md_green_400 : R.color.md_red_400));
            }
        }).execute();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putFloat("amountTotal", amountTotal);
        outState.putFloat("amountAccount", amountAccount);
        outState.putFloat("amountConsumedCC", amountConsumedCC);
        outState.putFloat("amountRemainingCC", amountRemainingCC);
        outState.putString("familyId", familyId);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {

    }
}
