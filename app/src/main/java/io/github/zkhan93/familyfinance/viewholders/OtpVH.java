package io.github.zkhan93.familyfinance.viewholders;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.Member;
import io.github.zkhan93.familyfinance.models.Otp;

/**
 * Created by zeeshan on 7/7/17.
 */

public class OtpVH extends RecyclerView.ViewHolder {
    public static String TAG = OtpVH.class.getSimpleName();

    @BindView(R.id.receiver)
    ImageView receiver;
    @BindView(R.id.number)
    TextView number;
    @BindView(R.id.content)
    TextView content;
    @BindView(R.id.timestamp)
    TextView timestamp;
    @BindView(R.id.claim)
    TextView claim;

    public OtpVH(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setOtp(Otp otp) {
        Member from = otp.getFrom();
        String url;
        if (from != null && from.getProfilePic() != null) {
            url = from.getProfilePic();

            Glide.with(receiver.getContext())
                    .load(url)
                    .apply(RequestOptions
                            .circleCropTransform()
                            .placeholder(R.drawable.ic_person_grey_600_24dp))
                    .into(receiver);
        }
        Member claimedBy = otp.getClaimedby();
        if (claimedBy != null && claimedBy.getProfilePic()!=null) {
            claim.setVisibility(View.VISIBLE);
            url = claimedBy.getProfilePic();
            Glide.with(claim.getContext())
                    .load(url)
                    .apply(RequestOptions.circleCropTransform())
                    .into(new SimpleTarget<Drawable>(50, 50) {
                        @Override
                        public void onResourceReady(Drawable drawable, Transition<? super Drawable>
                                transition) {
                            claim.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null,
                                    null);
                        }
                    });
        }else{
            claim.setVisibility(View.GONE);
        }

        number.setText(otp.getNumber());
        content.setText(otp.getContent());
        timestamp.setText(DateUtils.getRelativeTimeSpanString(otp.getTimestamp()));
    }
}
