package io.github.zkhan93.familyfinance.viewholders;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;

import io.github.zkhan93.familyfinance.models.BaseModel;
import io.github.zkhan93.familyfinance.util.ItemInteractionListener;

public abstract class BaseVH<T extends BaseModel> extends RecyclerView.ViewHolder{
    protected T item ;
    protected WeakReference<ItemInteractionListener<T>> itemInteractionListenerRef;

    public BaseVH(@NonNull View itemView, ItemInteractionListener<T> itemInteractionListener) {
        super(itemView);
        itemInteractionListenerRef = new WeakReference<>(itemInteractionListener);
    }

    public void setItem(T item) {
        this.item = item;
    }
}
