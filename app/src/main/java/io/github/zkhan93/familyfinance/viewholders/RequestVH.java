package io.github.zkhan93.familyfinance.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.events.CheckRequestEvent;
import io.github.zkhan93.familyfinance.events.DeleteRequestEvent;
import io.github.zkhan93.familyfinance.models.Request;
import io.github.zkhan93.familyfinance.tasks.LoadFromDbTask;

/**
 * Created by zeeshan on 16/7/17.
 */

public class RequestVH extends RecyclerView.ViewHolder {

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

    private Context context;
    private WeakReference<ItemInteractionListener> itemInteractionListenerWeakReference;

    public RequestVH(View itemView) {
        super(itemView);
        context = itemView.getContext();
        ButterKnife.bind(this, itemView);
    }

    public RequestVH(View itemView, ItemInteractionListener itemInteractionListener) {
        super(itemView);
        itemInteractionListenerWeakReference = new WeakReference<>
                (itemInteractionListener);
        context = itemView.getContext();
        ButterKnife.bind(this, itemView);
    }

    public void setRequest(Request request) {
        status.setText(request.getBlocked() ? "Blocked" : request.getApproved() ? "Approved" :
                "Pending");
        familyId.setText(request.getFamilyId());
        timestamp.setText(DATE_FORMAT.format(new Date(request.getUpdatedOn())));
    }

    @OnClick(R.id.delete)
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.check:
//                if (itemInteractionListenerWeakReference == null)
//                    EventBus.getDefault().post(new CheckRequestEvent(familyId.getText().toString()
//                            .trim()));
//                else {
//                    ItemInteractionListener itemInteractionListener =
//                            itemInteractionListenerWeakReference.get();
//                    if (itemInteractionListener == null)
//                        return;
//                    itemInteractionListener.checkRequest(familyId.getText().toString().trim());
//                }
//                break;
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
                Log.d(TAG, "click not implmented");
        }
    }

    public interface ItemInteractionListener {
        void deleteRequest(String familyId);

        void checkRequest(String familyId);
    }
}
