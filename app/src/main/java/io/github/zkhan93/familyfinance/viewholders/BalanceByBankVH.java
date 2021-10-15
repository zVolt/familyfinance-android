package io.github.zkhan93.familyfinance.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;

import androidx.recyclerview.widget.RecyclerView;
import io.github.zkhan93.familyfinance.R;

/**
 * Created by zeeshan on 11/26/17.
 */

public class BalanceByBankVH extends RecyclerView.ViewHolder implements ValueEventListener {
    ImageView avatar;
    TextView balance;

    private DatabaseReference imageRef;

    public BalanceByBankVH(View itemView) {
        super(itemView);
        avatar = itemView.findViewById(R.id.avatar);
        balance = itemView.findViewById(R.id.balance);
        imageRef = FirebaseDatabase.getInstance().getReference("images").child("banks");
    }

    public void setItem(String bankId, float amount) {
        if (bankId == null) return;
        imageRef.child(bankId).addListenerForSingleValueEvent(this);
        balance.setText(NumberFormat.getCurrencyInstance().format(amount));
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot == null) return;
        String imageUrl = dataSnapshot.getValue(String.class);
        if (imageUrl == null || imageUrl.isEmpty()) return;
        Glide.with(avatar).load(imageUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(avatar);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
