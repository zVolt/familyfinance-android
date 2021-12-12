package io.github.zkhan93.familyfinance.viewholders;

import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import androidx.recyclerview.widget.RecyclerView;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.Member;
import io.github.zkhan93.familyfinance.models.Otp;
import io.github.zkhan93.familyfinance.util.Util;

/**
 * Created by zeeshan on 7/7/17.
 */

public class OtpVH extends RecyclerView.ViewHolder {
    public static String TAG = OtpVH.class.getSimpleName();

    ImageView receiver;
    TextView number;
    TextView content;
    TextView timestamp;
    TextView claim;

    private DatabaseReference fromRef;
    private final ValueEventListener senderValueListener;
    private final ValueEventListener claimerValueListener;
    private int asyncCallCount;
    private Member from, claimedBy;
    private Otp otp;

    {
        senderValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                asyncCallCount--;
                if (dataSnapshot == null || !dataSnapshot.exists()) return;
                from = dataSnapshot.getValue(Member.class);
                checkIfDataFetchComplete();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Util.Log.d(TAG, "onCancelled: %s", databaseError.getMessage());
                asyncCallCount--;
                checkIfDataFetchComplete();
            }
        };

        claimerValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                asyncCallCount--;
                if (dataSnapshot == null || !dataSnapshot.exists()) return;
                claimedBy = dataSnapshot.getValue(Member.class);
                checkIfDataFetchComplete();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Util.Log.d(TAG, "onCancelled: %s", databaseError.getMessage());
                asyncCallCount--;
                checkIfDataFetchComplete();
            }
        };
    }

    public OtpVH(View itemView, String familyId) {
        super(itemView);
        receiver = itemView.findViewById(R.id.receiver);
        number = itemView.findViewById(R.id.number);
        content = itemView.findViewById(R.id.content);
        timestamp = itemView.findViewById(R.id.timestamp);
        claim = itemView.findViewById(R.id.claim);
    }

    public void setOtp(Otp otp) {
        this.otp = otp;
        checkIfDataFetchComplete();
//        asyncCallCount = 1;
//        fromRef.child(otp.getId()).addListenerForSingleValueEvent(senderValueListener);
//        if (otp.getClaimedByMemberId() == null || otp.getClaimedByMemberId().isEmpty()) return;
//        asyncCallCount++;
//        fromRef.child(otp.getClaimedByMemberId()).addListenerForSingleValueEvent
//                (claimerValueListener);
    }

    private void checkIfDataFetchComplete() {
//        if (asyncCallCount > 0) return;
        String url;
        from = otp.getFrom();
        if (from != null && from.getProfilePic() != null) {
            url = from.getProfilePic();
            Glide.with(receiver.getContext())
                    .load(url)
                    .apply(RequestOptions
                            .circleCropTransform()
                            .placeholder(R.drawable.ic_person_grey_600_24dp))
                    .into(receiver);
        }
        claimedBy = otp.getClaimedby();
        if (claimedBy != null && claimedBy.getProfilePic() != null) {
            claim.setVisibility(View.VISIBLE);
            url = claimedBy.getProfilePic();
            Glide.with(claim.getContext())
                    .load(url)
                    .apply(RequestOptions.circleCropTransform())
                    .into(new SimpleTarget<Drawable>(50, 50) {
                        @Override
                        public void onResourceReady(Drawable drawable, Transition<? super
                                Drawable>
                                transition) {
                            claim.setCompoundDrawablesWithIntrinsicBounds(drawable, null,
                                    null,
                                    null);
                        }
                    });
        } else {
            claim.setVisibility(View.GONE);
        }

        number.setText(otp.getNumber());
        content.setText(otp.getContent());
        timestamp.setText(DateUtils.getRelativeTimeSpanString(otp.getTimestamp()));
    }
}
