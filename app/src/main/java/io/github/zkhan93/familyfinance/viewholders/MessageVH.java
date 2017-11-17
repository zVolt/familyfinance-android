package io.github.zkhan93.familyfinance.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.App;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.DaoSession;
import io.github.zkhan93.familyfinance.models.Message;

/**
 * Created by zeeshan on 11/13/17.
 */

public class MessageVH extends RecyclerView.ViewHolder {
    @BindView(R.id.header)
    View header;
    @BindView(R.id.sender_avatar)
    ImageView senderAvatar;
    @BindView(R.id.sender_name)
    TextView senderName;
    @BindView(R.id.content)
    TextView content;
    @BindView(R.id.timestamp)
    TextView timestamp;

    private String meId;

    public MessageVH(View itemView, String meId) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.meId = meId;
    }

    public void setMessage(Message message) {
        if (message.getSenderId().equals(meId)) {
            header.setVisibility(View.GONE);
        } else {
            header.setVisibility(View.VISIBLE);
            senderName.setText(message.getSender().getName());
            Glide.with(senderAvatar.getContext()).load(message.getSender().getProfilePic()).apply(RequestOptions.circleCropTransform()).into
                    (senderAvatar);
        }
        content.setText(message.getContent());
        timestamp.setText(DateUtils.getRelativeTimeSpanString(timestamp.getContext(), message
                .getTimestamp(), true));
    }
}
