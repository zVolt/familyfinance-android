package io.github.zkhan93.familyfinance.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

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

    public MemberVH(View itemView, MemberItemActionClbk memberItemActionClbk) {
        super(itemView);
        this.memberItemActionClbk = memberItemActionClbk;
        ButterKnife.bind(this, itemView);
    }

    public void setMember(Member member) {
        memberId = member.getId();
        name.setText(member.getName());
        email.setText(member.getEmail());
    }

    @OnClick({R.id.remove, R.id.sms})
    public void onClick(ImageButton button) {
        switch (button.getId()) {
            case R.id.remove:
                memberItemActionClbk.remove(memberId);
                break;
            case R.id.sms:
                memberItemActionClbk.toggleSms(memberId);
                break;
        }
    }
}
