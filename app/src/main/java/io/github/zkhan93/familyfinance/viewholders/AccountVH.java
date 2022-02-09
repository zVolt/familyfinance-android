package io.github.zkhan93.familyfinance.viewholders;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Date;

import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.Account;
import io.github.zkhan93.familyfinance.models.Member;
import io.github.zkhan93.familyfinance.util.ItemInteractionListener;

/**
 * Created by zeeshan on 7/7/17.
 */

public class AccountVH extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    public static final String TAG = AccountVH.class.getSimpleName();
    TextView name;
    TextView accountNumber;
    ImageView bank;
    TextView ifsc;
    TextView balance;
    ImageView updatedBy;
    TextView updatedOn;
    TextView bankName;

    private final Context context;
    private final ItemInteractionListener<Account> itemInteractionListener;
    private Account account;
    private ValueEventListener bankImageLinkListener;

    private void init() {
        bankImageLinkListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String url = dataSnapshot.getValue(String.class);
                if (url == null)
                    url = String.format("https://via.placeholder.com/200x200/f0f0f0/2c2c2c?text=%s",
                            dataSnapshot.getKey());
                Glide.with(context)
                        .load(url)
                        .apply(RequestOptions
                                .centerInsideTransform()
                                .placeholder(R.drawable.ic_bank_grey_600_18dp))
                        .into(bank);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "bank image loading cancelled");
            }
        };
    }

    public AccountVH(View itemView, ItemInteractionListener<Account> itemInteractionListener) {
        super(itemView);
        context = itemView.getContext();
        name = itemView.findViewById(R.id.account_holder);
        accountNumber = itemView.findViewById(R.id.account_number);
        bank = itemView.findViewById(R.id.bank);
        bankName = itemView.findViewById(R.id.bank_name);
        ifsc = itemView.findViewById(R.id.ifsc);
        balance = itemView.findViewById(R.id.balance);
        updatedBy = itemView.findViewById(R.id.updated_by);
        updatedOn = itemView.findViewById(R.id.updated_on);
        this.itemInteractionListener = itemInteractionListener;
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
        this.init();
    }

    public void setAccount(Account account) {
        this.account = account;
        name.setText(account.getAccountHolder());
        bankName.setText(account.getBank().toUpperCase());
        accountNumber.setText(account.getAccountNumber());
        FirebaseDatabase.getInstance()
                .getReference("images")
                .child("banks")
                .child(account.getBank().toUpperCase())
                .addListenerForSingleValueEvent(bankImageLinkListener);
        ifsc.setText(account.getIfsc());
        balance.setText(NumberFormat.getCurrencyInstance().format(account.getBalance()));

        Member _updatedBy = account.getUpdatedBy();

        if (_updatedBy != null && _updatedBy.getProfilePic() != null && !_updatedBy.getProfilePic
                ().isEmpty())
            Glide.with(context)
                    .load(_updatedBy.getProfilePic())
                    .apply(RequestOptions
                            .circleCropTransform()
                            .placeholder(R.drawable.ic_person_grey_600_24dp))
                    .into(updatedBy);

        Date _updatedOn = account.getUpdatedOn() == -1 ? null : new Date(account.getUpdatedOn());
        updatedOn.setText(_updatedOn == null ? "Never" : DateUtils.getRelativeTimeSpanString
                (_updatedOn.getTime()));
    }

    @Override
    public void onClick(View view) {
        if (itemInteractionListener != null)
            itemInteractionListener.view(account);

    }

    @Override
    public boolean onLongClick(View view) {
        if (itemInteractionListener != null) {
            itemInteractionListener.edit(account);
            return true;
        } else
            return false;
    }

}
