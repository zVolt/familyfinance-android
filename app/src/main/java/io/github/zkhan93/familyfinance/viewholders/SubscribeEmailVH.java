package io.github.zkhan93.familyfinance.viewholders;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.callbacks.SubscribeEmailCallback;

/**
 * Created by zeeshan on 10/29/17.
 */

public class SubscribeEmailVH extends RecyclerView.ViewHolder {
    private final SubscribeEmailCallback subscribeEmailCallback;

    public SubscribeEmailVH(View itemView, SubscribeEmailCallback subscribeEmailCallback) {
        super(itemView);
        this.subscribeEmailCallback = subscribeEmailCallback;
        itemView.findViewById(R.id.subscribe).setOnClickListener(view -> {
            subscribeEmailCallback.onSubscribeEmail();
        });
    }


}
