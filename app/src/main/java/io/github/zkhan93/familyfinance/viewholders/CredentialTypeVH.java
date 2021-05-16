package io.github.zkhan93.familyfinance.viewholders;

import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import androidx.recyclerview.widget.RecyclerView;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.CredentialType;

/**
 * Created by zeeshan on 1/26/18.
 */

public class CredentialTypeVH extends RecyclerView.ViewHolder {
    private TextView name, count;
    private ImageView icon;
    private ImageView handler;
    private View itemView;

    public CredentialTypeVH(View itemView, View.OnClickListener groupClickListener) {
        super(itemView);
        this.name = itemView.findViewById(R.id.name);
        this.count = itemView.findViewById(R.id.count);
        icon = itemView.findViewById(R.id.icon);
        this.itemView = itemView;
        this.itemView.setOnClickListener(groupClickListener);
    }

    public void setView(CredentialType credentialType, int count) {
        if (credentialType == null) return;
        itemView.setTag(credentialType.getId());
        name.setText(credentialType.getName());
        this.count.setText(String.valueOf(count));
        if (URLUtil.isValidUrl(credentialType.getIconUrl())) {
            Glide.with(icon.getContext()).load(credentialType.getIconUrl()).into(icon);
        }
        //TODO: set handler
    }

    private void expandedView() {
    }

    private void collapsedView() {
    }
}
