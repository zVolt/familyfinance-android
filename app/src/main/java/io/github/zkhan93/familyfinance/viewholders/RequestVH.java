package io.github.zkhan93.familyfinance.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.events.DeleteRequestEvent;
import io.github.zkhan93.familyfinance.events.FamilySetEvent;
import io.github.zkhan93.familyfinance.models.Request;

/**
 * Created by zeeshan on 16/7/17.
 */

public class RequestVH extends RecyclerView.ViewHolder implements View.OnClickListener {

    public static String TAG = RequestVH.class.getSimpleName();
    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd, MMM YY");
    @BindView(R.id.status)
    TextView status;
    @BindView(R.id.family_id)
    TextView familyId;
    @BindView(R.id.timestamp)
    TextView timestamp;
    @BindView(R.id.delete)
    ImageButton delete;

    private Request request;
    private WeakReference<ItemInteractionListener> itemInteractionListenerWeakReference;
    private Toast toast;

    public RequestVH(View itemView) {
        super(itemView);
        Context context = itemView.getContext();
        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(this);
        delete.setOnClickListener(this);
    }

    public RequestVH(View itemView, ItemInteractionListener itemInteractionListener) {
        this(itemView);
        itemInteractionListenerWeakReference = new WeakReference<>
                (itemInteractionListener);
    }

    public void setRequest(Request request) {
        this.request = request;
        status.setText(request.getBlocked() ? "Blocked" : request.getApproved() ? "Approved" :
                "Pending");
        familyId.setText(request.getFamilyId());
        timestamp.setText(DATE_FORMAT.format(new Date(request.getUpdatedOn())));
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.delete:
                if (itemInteractionListenerWeakReference == null)
                    EventBus.getDefault().post(new DeleteRequestEvent(familyId.getText().toString()
                            .trim()));
                else {
                    ItemInteractionListener itemInteractionListener =
                            itemInteractionListenerWeakReference.get();
                    if (itemInteractionListener == null)
                        return;
                    itemInteractionListener.deleteRequest(familyId.getText().toString().trim());
                }
                break;
            default:
                Log.d(TAG,"request clicked");

                    EventBus.getDefault().post(new FamilySetEvent(request));

        }
    }

    public interface ItemInteractionListener {
        void deleteRequest(String familyId);
    }
}
