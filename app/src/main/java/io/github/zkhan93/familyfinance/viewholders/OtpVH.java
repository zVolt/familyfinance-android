package io.github.zkhan93.familyfinance.viewholders;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.Member;
import io.github.zkhan93.familyfinance.models.Otp;
import io.github.zkhan93.familyfinance.util.Constants;

/**
 * Created by zeeshan on 7/7/17.
 */

public class OtpVH extends RecyclerView.ViewHolder {
    @BindView(R.id.receiver)
    ImageView receiver;
    @BindView(R.id.number)
    TextView number;
    @BindView(R.id.content)
    TextView content;
    @BindView(R.id.timestamp)
    TextView timestamp;

    public OtpVH(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setOtp(Otp otp) {
        Member from = otp.getFrom();
        String url;
        if (from != null && from.getProfilePic() != null)
            url = from.getProfilePic();
        else
            url = "";//TOGO get gavatar of the email
        Glide.with(receiver.getContext()).load(url).apply(RequestOptions
                .circleCropTransform()).into(receiver);
        number.setText(otp.getNumber());
        content.setText(otp.getContent());
        timestamp.setText(DateUtils.getRelativeTimeSpanString(otp.getTimestamp()));
    }
}
