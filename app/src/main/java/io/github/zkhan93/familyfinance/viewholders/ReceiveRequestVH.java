package io.github.zkhan93.familyfinance.viewholders;

import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.Request;

/**
 * Created by zeeshan on 22/7/17.
 */

public class ReceiveRequestVH extends RecyclerView.ViewHolder implements PopupMenu
        .OnMenuItemClickListener {
    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd, MMM hh:mm a", Locale.US);
    @BindView(R.id.avatar)
    ImageView avatar;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.email)
    TextView email;
    @BindView(R.id.status)
    TextView status;
    @BindView(R.id.timestamp)
    TextView timestamp;
    @BindView(R.id.show_options)
    ImageButton showOptions;

    private Request request;
    private PopupMenu popupMenu;
    private ItemInteractionListener itemInteractionListener;

    public ReceiveRequestVH(View itemView, ItemInteractionListener itemInteractionListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.itemInteractionListener = itemInteractionListener;
        popupMenu = new PopupMenu(itemView.getContext(), showOptions);
        popupMenu.inflate(R.menu.receive_request_menu);
        popupMenu.setOnMenuItemClickListener(this);
    }

    public void setRequest(Request request) {
        this.request = request;
        name.setText(request.getName());
        email.setText(request.getEmail());
        if (request.getProfilePic() != null && request.getProfilePic().trim().length() > 0)
            Glide.with(avatar).load(request.getProfilePic()).apply(RequestOptions
                    .circleCropTransform()).into(avatar);
        status.setText(
                request.getBlocked() ? " Blocked" :
                        request.getApproved() ? "Approved" : "Pending");
        timestamp.setText(DateUtils.getRelativeTimeSpanString(request.getUpdatedOn()));
    }

    @OnClick(R.id.show_options)
    public void showOptions(View view) {
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_approve:
                itemInteractionListener.approve(request);
                return true;
            case R.id.action_block:
                itemInteractionListener.block(request);
                return true;
            case R.id.action_revoke:
                itemInteractionListener.revoke(request);
                return true;
            case R.id.action_unblock:
                itemInteractionListener.unblock(request);
                return true;
            default:
                return false;
        }
    }

    public interface ItemInteractionListener {
        void approve(Request request);

        void block(Request request);

        void revoke(Request request);

        void unblock(Request request);
    }
}
