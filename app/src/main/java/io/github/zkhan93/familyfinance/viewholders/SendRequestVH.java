package io.github.zkhan93.familyfinance.viewholders;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.Request;

/**
 * Created by zeeshan on 16/7/17.
 */

public class SendRequestVH extends RecyclerView.ViewHolder implements View.OnClickListener {

    public static String TAG = SendRequestVH.class.getSimpleName();
    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd, MMM yy hh:mm a", Locale.US);
    @BindView(R.id.status)
    TextView status;
    @BindView(R.id.family_id)
    TextView familyId;
    @BindView(R.id.timestamp)
    TextView timestamp;
    @BindView(R.id.delete)
    ImageButton delete;

    private Request request;
    private ItemInteractionListener itemInteractionListener;

    public SendRequestVH(View itemView) {
        super(itemView);
        Context context = itemView.getContext();
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(this);
        delete.setOnClickListener(this);
    }

    public SendRequestVH(View itemView, ItemInteractionListener itemInteractionListener) {
        this(itemView);
        this.itemInteractionListener = itemInteractionListener;
    }

    public void setRequest(Request request) {
        this.request = request;
        status.setText(request.getBlocked() ? "Blocked" :
                request.getApproved() ? "Approved" : "Pending");
        familyId.setText(request.getFamilyId());
        timestamp.setText(DATE_FORMAT.format(new Date(request.getUpdatedOn())));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.delete:
                itemInteractionListener.deleteRequest(request);
                break;
            default:
                itemInteractionListener.switchFamily(request);
        }
    }

    public interface ItemInteractionListener {
        void deleteRequest(Request request);

        void switchFamily(Request request);
    }
}
