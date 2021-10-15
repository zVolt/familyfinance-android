package io.github.zkhan93.familyfinance;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import io.github.zkhan93.familyfinance.adapters.BalanceByBankAdapter;
import io.github.zkhan93.familyfinance.models.Account;
import io.github.zkhan93.familyfinance.models.CCard;
import io.github.zkhan93.familyfinance.util.Constants;


/**
 * A {@link Fragment} subclass.
 * Use the {@link FragmentSummary#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSummary extends Fragment {
    public static final String TAG = FragmentSummary.class.getSimpleName();
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_FAMILY_ID = "familyId";

    public TextView grandTotal;
    public TextView accountTotal;
    public TextView consumedCCTotal;
    public TextView totalCCTotal;

    public TextView accountTitle;
    public TextView cardTitle;

    public ProgressBar cardLimitbar;

    public View nextPayment1;
    public View nextPayment2;
    public View nextPayment3;

    public RecyclerView balanceBybankList;

    private String familyId;
    boolean ccLoaded, accountLoaded;
    private float amountTotal, amountConsumedCC, amountRemainingCC, amountAccount;
    private List<CCard> cCards;
    private List<Account> accounts;
    private ChildEventListener ccardChildEventListener, accountChildEventListener;
    private int count = 0;
    public FragmentSummary() {
        // Required empty public constructor
        accounts = new ArrayList<>();
        cCards = new ArrayList<>();
        ccardChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                CCard cCard = dataSnapshot.getValue(CCard.class);
                if (cCard == null) return;
                cCard.setNumber(dataSnapshot.getKey());
                cCards.add(cCard);
                amountConsumedCC += cCard.getConsumedLimit();
                amountRemainingCC += cCard.getRemainingLimit();
                if (ccLoaded) recalculate();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                CCard oldCard = null;
                CCard cCard = dataSnapshot.getValue(CCard.class);
                if (cCard == null) return;
                cCard.setNumber(dataSnapshot.getKey());
                ListIterator<CCard> itr = cCards.listIterator();
                while (itr.hasNext()) {
                    oldCard = itr.next();
                    if (oldCard.getNumber().equals(cCard.getNumber())) {
                        itr.set(cCard);
                        amountConsumedCC -= oldCard.getConsumedLimit();
                        amountRemainingCC -= oldCard.getRemainingLimit();
                        amountConsumedCC += cCard.getConsumedLimit();
                        amountRemainingCC += cCard.getRemainingLimit();
                        break;
                    }
                }
                if (ccLoaded) recalculate();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                CCard cCard = dataSnapshot.getValue(CCard.class);
                if (cCard == null) return;
                cCard.setNumber(dataSnapshot.getKey());
                CCard oldCard;
                ListIterator<CCard> itr = cCards.listIterator();
                while (itr.hasNext()) {
                    oldCard = itr.next();
                    if (oldCard.getNumber().equals(cCard.getNumber())) {
                        itr.remove();
                        amountConsumedCC -= oldCard.getConsumedLimit();
                        amountRemainingCC -= oldCard.getRemainingLimit();
                        break;
                    }
                }
                if (ccLoaded) recalculate();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        accountChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Account account = dataSnapshot.getValue(Account.class);
                if (account == null) return;
                account.setAccountNumber(dataSnapshot.getKey());
                accounts.add(account);
                amountAccount += account.getBalance();
                if (accountLoaded) recalculate();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Account oldAccount = null;
                Account account = dataSnapshot.getValue(Account.class);
                if (account == null) return;
                account.setAccountNumber(dataSnapshot.getKey());
                ListIterator<Account> itr = accounts.listIterator();
                while (itr.hasNext()) {
                    oldAccount = itr.next();
                    if (oldAccount.getAccountNumber().equals(account.getAccountNumber())) {
                        itr.set(account);
                        amountAccount -= oldAccount.getBalance();
                        amountAccount += account.getBalance();
                        break;
                    }
                }
                if (accountLoaded) recalculate();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Account oldAccount;
                Account account = dataSnapshot.getValue(Account.class);
                if (account == null) return;
                account.setAccountNumber(dataSnapshot.getKey());
                ListIterator<Account> itr = accounts.listIterator();
                while (itr.hasNext()) {
                    oldAccount = itr.next();
                    if (oldAccount.getAccountNumber().equals(account.getAccountNumber())) {
                        itr.remove();
                        amountAccount -= oldAccount.getBalance();
                        break;
                    }
                }
                if (accountLoaded) recalculate();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
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
        if (familyId == null) {
            familyId =
                    PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(ARG_FAMILY_ID, null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_summary, container, false);

        grandTotal = rootView.findViewById(R.id.grand_total);
        accountTotal = rootView.findViewById(R.id.account_total);
        consumedCCTotal = rootView.findViewById(R.id.consumed_cc_limit);
        totalCCTotal = rootView.findViewById(R.id.total_cc_limit);
        accountTitle = rootView.findViewById(R.id.account_title);
        cardTitle = rootView.findViewById(R.id.card_title);
        cardLimitbar = rootView.findViewById(R.id.card_limit_bar);
        nextPayment1 = rootView.findViewById(R.id.next_payment1);
        nextPayment2 = rootView.findViewById(R.id.next_payment2);
        nextPayment3 = rootView.findViewById(R.id.next_payment3);
        balanceBybankList = rootView.findViewById(R.id.balance_by_bank);

        if (savedInstanceState != null) {
            amountTotal = savedInstanceState.getFloat("amountTotal", amountTotal);
            amountAccount = savedInstanceState.getFloat("amountAccount", amountAccount);
            amountConsumedCC = savedInstanceState.getFloat("amountConsumedCC", amountConsumedCC);
            amountRemainingCC = savedInstanceState.getFloat("amountRemainingCC", amountRemainingCC);
            familyId = savedInstanceState.getString(getString(R.string.pref_family_id), familyId);
            consumedCCTotal.setText(NumberFormat.getCurrencyInstance().format
                    (amountConsumedCC));
            totalCCTotal.setText(NumberFormat.getCurrencyInstance().format
                    (amountRemainingCC + amountConsumedCC));
            accountTotal.setText(NumberFormat.getCurrencyInstance().format
                    (amountAccount));
            grandTotal.setTextColor(ContextCompat.getColor(getActivity(), amountTotal >= 0 ?
                    R.color.md_green_400 : R.color.md_red_400));
        }
        balanceBybankList.setAdapter(new BalanceByBankAdapter(familyId));
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        accountLoaded = ccLoaded = false;
        cCards.clear();
        accounts.clear();
        amountAccount = amountConsumedCC = amountRemainingCC = 0;
        registerChildListeners();
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterChildListeners();
    }

    private void unregisterChildListeners() {
        FirebaseDatabase.getInstance().getReference("ccards").child(familyId)
                .removeEventListener(ccardChildEventListener);
        FirebaseDatabase.getInstance().getReference("accounts").child(familyId)
                .removeEventListener(accountChildEventListener);
    }

    private void registerChildListeners() {

        FirebaseDatabase.getInstance().getReference("ccards").child(familyId)
                .addChildEventListener(ccardChildEventListener);
        FirebaseDatabase.getInstance().getReference("ccards").child(familyId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "count=cards: " + dataSnapshot.getChildrenCount() + "=" +
                                cCards.size());
                        recalculate();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        FirebaseDatabase.getInstance().getReference("accounts").child(familyId)
                .addChildEventListener(accountChildEventListener);
        FirebaseDatabase.getInstance().getReference("accounts").child(familyId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "count=account: " + dataSnapshot.getChildrenCount() + "=" +
                                accounts.size());
                        recalculate();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void recalculate() {
        Log.d(TAG, "count=" + ++count);
        int cardCount = cCards.size();
        cardTitle.setText(String.format(Locale.ENGLISH, "%d Cards", cardCount));

        consumedCCTotal.setText(NumberFormat.getCurrencyInstance().format
                (amountConsumedCC));
        cardLimitbar.setMax((int) (amountRemainingCC + amountConsumedCC));
        cardLimitbar.setProgress((int) amountConsumedCC);
        totalCCTotal.setText(NumberFormat.getCurrencyInstance().format
                (amountRemainingCC + amountConsumedCC));
        amountTotal = amountAccount - amountConsumedCC;
        grandTotal.setText(NumberFormat.getCurrencyInstance().format(amountTotal));
        grandTotal.setTextColor(ContextCompat.getColor(getActivity(), amountTotal >= 0 ?
                R.color.md_green_400 : R.color.md_red_400));

        View[] views = new View[]{
                nextPayment1,
                nextPayment2, nextPayment3
        };
        for (View view : views)
            view.setVisibility(View.GONE);
        TextView txtView;
        CCard cCard;
        String str;
        Collections.sort(cCards, CCard.BY_PAYMENT_DATE);
        View view;
        for (int i = 0; i < views.length && i < cCards.size(); i++) {
            cCard = cCards.get(i);
            view = views[i];
            txtView = view.findViewById(R.id.amount);
            txtView.setText(NumberFormat.getCurrencyInstance().format(cCard
                    .getConsumedLimit()));
            txtView = view.findViewById(R.id.card_name);

            str = String.format("%s - %s", cCard.getCardholder(), cCard.getBank());
            txtView.setText(str);
            txtView = view.findViewById(R.id.date);
            txtView.setText(Constants.PAYMENT_DATE.format(cCard.getPaymentDate()));
            views[i].setVisibility(View.VISIBLE);
        }

        //for accounts
        accountTitle.setText(String.format(Locale.ENGLISH, "%d Accounts", accounts.size()));
        accountTotal.setText(NumberFormat.getCurrencyInstance().format
                (amountAccount));
        amountTotal = amountAccount - amountConsumedCC;
        grandTotal.setText(NumberFormat.getCurrencyInstance().format
                (amountTotal));
        grandTotal.setTextColor(ContextCompat.getColor(getActivity(), amountTotal >= 0 ?
                R.color.md_green_400 : R.color.md_red_400));

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
}
