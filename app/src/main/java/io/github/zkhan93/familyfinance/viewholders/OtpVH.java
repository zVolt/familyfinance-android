package io.github.zkhan93.familyfinance.viewholders;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;

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
    @BindView(R.id.name)
    TextView name;
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
        if (from != null && from.getName() != null)
            name.setText(from.getName());
        else
            name.setText("Unknown");
        number.setText(otp.getNumber());
        content.setText(otp.getContent());
        timestamp.setText(DateUtils.getRelativeTimeSpanString(otp.getTimestamp()));
    }
}
