package io.github.zkhan93.familyfinance.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.Member;

/**
 * Created by zeeshan on 11/19/17.
 */

public class FilterSMSMemberVH extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView name;
    ImageView avatar;
    private Context context;
    private ItemInteractionListener itemInteractionListener;
    private Member member;

    public FilterSMSMemberVH(View itemView, ItemInteractionListener itemInteractionListener) {
        super(itemView);
        itemView.setOnClickListener(this);
        this.itemInteractionListener = itemInteractionListener;
        context = itemView.getContext();
        name = itemView.findViewById(R.id.name);
        avatar = itemView.findViewById(R.id.avatar);
    }

    public void setMember(@NonNull Member member) {
        this.member = member;
        name.setText(member.getName());
        avatar.setContentDescription(member.getName());
        if (member.getProfilePic() != null && member.getProfilePic().trim().length() > 0)
            Glide.with(context)
                    .load(member.getProfilePic())
                    .apply(RequestOptions
                            .circleCropTransform()
                            .placeholder(R.drawable.ic_person_grey_600_24dp))
                    .into(avatar);

    }

    @Override
    public void onClick(View view) {
        if (itemInteractionListener != null)
            itemInteractionListener.onFilterMemberClicked(member);
    }

    public interface ItemInteractionListener {
        void onFilterMemberClicked(Member member);
    }
}
