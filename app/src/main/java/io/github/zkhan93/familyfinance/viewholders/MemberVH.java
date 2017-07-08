package io.github.zkhan93.familyfinance.viewholders;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.helpers.MemberItemActionClbk;
import io.github.zkhan93.familyfinance.models.Member;

/**
 * Created by zeeshan on 7/7/17.
 */

public class MemberVH extends RecyclerView.ViewHolder {

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.email)
    TextView email;

    @BindView(R.id.sms)
    ImageButton toggleSms;
    @BindView(R.id.remove)
    ImageButton remove;

    private String memberId;
    private MemberItemActionClbk memberItemActionClbk;
    private Context context;

    public MemberVH(View itemView, MemberItemActionClbk memberItemActionClbk) {
        super(itemView);
        context = itemView.getContext();
        this.memberItemActionClbk = memberItemActionClbk;
        ButterKnife.bind(this, itemView);
    }

    public void setMember(Member member) {
        memberId = member.getId();
        name.setText(member.getName());
        email.setText(member.getEmail());
        toggleSms.setImageDrawable(ContextCompat.getDrawable(context, member.isCanRecieveSms() ?
                R.drawable
                        .ic_sms_teal_500_24dp : R.drawable.ic_sms_grey_500_24dp));
    }

    @OnClick({R.id.remove, R.id.sms})
    public void onClick(ImageButton button) {
        switch (button.getId()) {
            case R.id.remove:
                memberItemActionClbk.removeMember(memberId);
                break;
            case R.id.sms:
                memberItemActionClbk.toggleMemberSms(memberId);
                break;
        }
    }
}
