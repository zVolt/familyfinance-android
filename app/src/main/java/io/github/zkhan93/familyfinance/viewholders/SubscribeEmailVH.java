package io.github.zkhan93.familyfinance.viewholders;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.callbacks.SubscribeEmailCallback;

/**
 * Created by zeeshan on 10/29/17.
 */

public class SubscribeEmailVH extends RecyclerView.ViewHolder {
    private SubscribeEmailCallback subscribeEmailCallback;

    public SubscribeEmailVH(View itemView, SubscribeEmailCallback subscribeEmailCallback) {
        super(itemView);
        this.subscribeEmailCallback = subscribeEmailCallback;
        ButterKnife.bind(this, itemView);
    }

    @OnClick(R.id.subscribe)
    public void onSubscribe(View button) {
        subscribeEmailCallback.onSubscribeEmail();
    }
}
