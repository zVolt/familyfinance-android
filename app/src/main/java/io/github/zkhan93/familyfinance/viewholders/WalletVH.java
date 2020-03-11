package io.github.zkhan93.familyfinance.viewholders;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import io.github.zkhan93.familyfinance.models.Wallet;

public class WalletVH extends RecyclerView.ViewHolder implements PopupMenu
        .OnMenuItemClickListener, View.OnClickListener, View.OnLongClickListener {

    public WalletVH(View itemView, @NonNull CCardVH.ItemInteractionListener itemInteractionListener) {
        super(itemView);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    public interface ItemInteractionListener {

        void delete(Wallet wallet);

        void edit(Wallet wallet);

        void onView(Wallet wallet);

        void onLongPress(Wallet wallet);

    }
}
