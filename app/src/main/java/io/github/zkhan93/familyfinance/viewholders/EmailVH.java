package io.github.zkhan93.familyfinance.viewholders;

import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.Email;

/**
 * Created by zeeshan on 10/29/17.
 */

public class EmailVH extends RecyclerView.ViewHolder {

    TextView subject;
    TextView to;
    TextView from;
    TextView timestamp;

    public EmailVH(View itemView) {
        super(itemView);
        subject = itemView.findViewById(R.id.subject);
        to = itemView.findViewById(R.id.to);
        from = itemView.findViewById(R.id.from);
        timestamp = itemView.findViewById(R.id.timestamp);
    }

    public void setEmail(Email email) {
        subject.setText(email.getSubject());
        to.setText(email.getTo());
        from.setText(email.getFrom());
        timestamp.setText(DateUtils.getRelativeTimeSpanString(email.getTimestamp()));
    }
}

