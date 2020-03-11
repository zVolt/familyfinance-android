package io.github.zkhan93.familyfinance.viewholders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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


    public EmptyVH(View itemView,@NonNull String imageChild) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        Log.d(TAG, "empty VH created");
        FirebaseDatabase.getInstance()
                .getReference("images")
                .child(imageChild)
                .addListenerForSingleValueEvent(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot != null) {
            String url = dataSnapshot.getValue(String.class);
            Glide.with(image).load(url).into(image);
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d(TAG, databaseError.toString());
    }
}
