package io.github.zkhan93.familyfinance.viewholders;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.Email;

/**
 * Created by zeeshan on 10/29/17.
 */

public class EmailVH extends RecyclerView.ViewHolder {

    @BindView(R.id.subject)
    TextView subject;
    @BindView(R.id.to)
    TextView to;
    @BindView(R.id.from)
    TextView from;
    @BindView(R.id.timestamp)
    TextView timestamp;

    public EmailVH(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setEmail(Email email) {
        subject.setText(email.getSubject());
        to.setText(email.getTo());
        from.setText(email.getFrom());
        timestamp.setText(DateUtils.getRelativeTimeSpanString(email.getTimestamp()));
    }
}

