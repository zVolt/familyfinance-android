package io.github.zkhan93.familyfinance.viewholders;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Calendar;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.Member;

/**
 * Created by zeeshan on 7/7/17.
 */

public class MemberVH extends RecyclerView.ViewHolder {

    TextView name;
    TextView email;
    TextView timestamp;

    ImageView avatar;
    ImageView state;

    private final Context context;

    public MemberVH(View itemView) {
        super(itemView);
        context = itemView.getContext();
        name = itemView.findViewById(R.id.name);
        email = itemView.findViewById(R.id.email);
        timestamp = itemView.findViewById(R.id.timestamp);

        avatar = itemView.findViewById(R.id.avatar);
        state = itemView.findViewById(R.id.state);
    }

    public void setMember(Member member) {
        name.setText(member.getName());
        email.setText(member.getEmail());
        if (member.getProfilePic() != null && member.getProfilePic().trim().length() > 0)
            Glide.with(context)
                    .load(member.getProfilePic())
                    .apply(RequestOptions
                        .circleCropTransform()
                        .placeholder(R.drawable.ic_person_grey_600_24dp))
                    .into(avatar);
        timestamp.setText(DateUtils.getRelativeTimeSpanString(member.getWasPresentOn()));

        //if member presence is latest to 2 minutes show him green else orange
        if (member.getWasPresentOn() >= Calendar.getInstance().getTimeInMillis() - (2 * 60 * 60)) {
            state.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.circle_green));
            timestamp.setText(context.getString(R.string.online));
        }
        else
            state.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.circle_orange));

    }
}
