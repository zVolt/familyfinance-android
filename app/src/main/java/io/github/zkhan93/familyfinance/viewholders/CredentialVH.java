package io.github.zkhan93.familyfinance.viewholders;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.Credential;

/**
 * Created by zeeshan on 1/26/18.
 */

public class CredentialVH extends RecyclerView.ViewHolder implements View.OnClickListener, View
        .OnLongClickListener {
    private TextView username;
    private TextView description;
    private CredentialInteraction credentialInteraction;
    private Credential credential;

    public CredentialVH(View itemView, CredentialInteraction credentialInteraction) {
        super(itemView);
        this.credentialInteraction = credentialInteraction;
        username = itemView.findViewById(R.id.username);
        description = itemView.findViewById(R.id.description);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    public void setView(Credential credential) {
        this.credential = credential;
        if (credential == null) return;
        if (username != null)
            username.setText(credential.getUsername());
        if (description != null) {
            if (credential.getDescription() == null || credential.getDescription().isEmpty())
                description.setVisibility(View.GONE);
            else
                description.setVisibility(View.VISIBLE);
            description.setText(credential.getDescription());
        }
    }

    @Override
    public void onClick(View view) {
        if (credentialInteraction == null || credential == null) return;
        credentialInteraction.onCredentialClicked(credential);
    }

    @Override
    public boolean onLongClick(View view) {
        if (credentialInteraction == null || credential == null) return false;
        credentialInteraction.onCredentialLongClicked(credential);
        return true;
    }

    public interface CredentialInteraction {
        void onCredentialClicked(Credential credential);

        void onCredentialLongClicked(Credential credential);
    }
}
