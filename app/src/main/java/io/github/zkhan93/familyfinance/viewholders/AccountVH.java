package io.github.zkhan93.familyfinance.viewholders;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.Account;
import io.github.zkhan93.familyfinance.models.Member;

/**
 * Created by zeeshan on 7/7/17.
 */

public class AccountVH extends RecyclerView.ViewHolder implements PopupMenu
        .OnMenuItemClickListener, View.OnClickListener, View.OnLongClickListener {
    public static final String TAG = AccountVH.class.getSimpleName();
    @BindView(R.id.account_holder)
    TextView name;
    @BindView(R.id.account_number)
    TextView accountNumber;
    @BindView(R.id.bank)
    ImageView bank;
    @BindView(R.id.ifsc)
    TextView ifsc;
    @BindView(R.id.balance)
    TextView balance;
    @BindView(R.id.updated_by)
    ImageView updatedBy;
    @BindView(R.id.updated_on)
    TextView updatedOn;
    @BindView(R.id.menu)
    ImageButton menu;

    private Context context;
    private ItemInteractionListener itemInteractionListener;
    private PopupMenu popup;
    private Account account;
    private ValueEventListener bankImageLinkListener;

    {
        bankImageLinkListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String url = dataSnapshot.getValue(String.class);
                if (url == null)
                    url = String.format("http://via.placeholder.com/200x200/f0f0f0/2c2c2c?text=%s",
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

    public AccountVH(View itemView, ItemInteractionListener itemInteractionListener) {
        super(itemView);
        context = itemView.getContext();
        ButterKnife.bind(this, itemView);
        this.itemInteractionListener = itemInteractionListener;

        popup = new PopupMenu(itemView.getContext(), menu);
        MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener(this);
        inflater.inflate(R.menu.account_menu, popup.getMenu());
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
        menu.setOnClickListener(this);
    }

    public void setAccount(Account account) {
        this.account = account;
        name.setText(account.getAccountHolder());
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
        switch (view.getId()) {
            case R.id.menu:
                popup.show();
                return;
            default:
                if (itemInteractionListener != null)
                    itemInteractionListener.view(account);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (itemInteractionListener != null) {
            itemInteractionListener.onLongPress(account);
            return true;
        } else
            return false;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                itemInteractionListener.delete(account);
                return true;
            case R.id.action_edit:
                itemInteractionListener.edit(account);
                return true;
            default:
                return false;
        }
    }

    public interface ItemInteractionListener {
        void delete(Account account);

        void edit(Account account);

        void view(Account account);

        void onLongPress(Account account);
    }
}
