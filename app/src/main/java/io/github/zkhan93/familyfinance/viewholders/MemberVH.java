package io.github.zkhan93.familyfinance.viewholders;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.Member;

/**
 * Created by zeeshan on 7/7/17.
 */

public class MemberVH extends RecyclerView.ViewHolder {

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.email)
    TextView email;
    @BindView(R.id.timestamp)
    TextView timestamp;

    @BindView(R.id.avatar)
    ImageView avatar;
    @BindView(R.id.state)
    ImageView state;

    private Context context;

    public MemberVH(View itemView) {
        super(itemView);
        context = itemView.getContext();
        ButterKnife.bind(this, itemView);
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
