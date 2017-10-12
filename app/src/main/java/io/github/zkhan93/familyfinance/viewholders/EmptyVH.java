package io.github.zkhan93.familyfinance.viewholders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.R;

/**
 * Created by zeeshan on 12/10/17.
 */

public class EmptyVH extends RecyclerView.ViewHolder implements ValueEventListener {
    public static final String TAG = EmptyVH.class.getSimpleName();
    @BindView(R.id.image)
    ImageView image;

    private String url;

    public EmptyVH(View itemView,@NonNull String childName) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        Log.d(TAG, "empty VH created");
        FirebaseDatabase.getInstance().getReference("images").child(childName)
                .addValueEventListener(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot != null) {
            url = dataSnapshot.getValue(String.class);
            Log.d(TAG, "loading " + url);
            Glide.with(image).load(url).into(image);
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d(TAG, databaseError.toString());
    }
}
