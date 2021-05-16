package io.github.zkhan93.familyfinance.viewholders;

import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import androidx.recyclerview.widget.RecyclerView;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.Message;

/**
 * Created by zeeshan on 11/13/17.
 */

public class MessageVH extends RecyclerView.ViewHolder {
    View header;
    ImageView senderAvatar;
    TextView senderName;
    TextView content;
    TextView timestamp;

    private String meId;

    public MessageVH(View itemView, String meId) {
        super(itemView);
        header = itemView.findViewById(R.id.header);
        senderAvatar = itemView.findViewById(R.id.sender_avatar);
        senderName = itemView.findViewById(R.id.sender_name);
        content = itemView.findViewById(R.id.content);
        timestamp = itemView.findViewById(R.id.timestamp);
        this.meId = meId;
    }

    public void setMessage(Message message) {
        if (message.getSenderId().equals(meId)) {
            header.setVisibility(View.GONE);
        } else {
            header.setVisibility(View.VISIBLE);
            senderName.setText(message.getSender().getName());
            Glide.with(senderAvatar.getContext())
                    .load(message.getSender().getProfilePic())
                    .apply(RequestOptions
                            .circleCropTransform()
                            .placeholder(R.drawable.ic_person_grey_600_24dp))
                    .into(senderAvatar);
        }
        content.setText(message.getContent());
        timestamp.setText(DateUtils.getRelativeTimeSpanString(timestamp.getContext(), message
                .getTimestamp(), true));
    }
}
