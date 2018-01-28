package io.github.zkhan93.familyfinance;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.models.Credential;
import io.github.zkhan93.familyfinance.models.CredentialType;
import io.github.zkhan93.familyfinance.util.Util;

/**
 * Created by zeeshan on 1/28/18.
 */

public class DialogFragmentViewCredential extends DialogFragment implements DialogInterface
        .OnClickListener, ValueEventListener {

    public static final String TAG = DialogFragmentViewCredential.class.getSimpleName();

    static String ARG_CRED = "credential";

    @BindView(R.id.username)
    TextView username;
    @BindView(R.id.password)
    TextView password;
    @BindView(R.id.description)
    TextView description;

    TextView typeName;

    ImageView typeIcon;


    private Credential credential;
    private DatabaseReference credRef;
    private View.OnClickListener clickToCopyClistener;

    {
        clickToCopyClistener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view instanceof TextView) {
                    String text = ((TextView) view).getText().toString();
                    ClipboardManager clipboardManager = (ClipboardManager) getActivity()
                            .getSystemService(Context.CLIPBOARD_SERVICE);
                    if (clipboardManager == null) return;
                    clipboardManager.setPrimaryClip(ClipData.newPlainText("", text));
                    Util.Log.d(TAG, "text copied %s", text);
                }
            }
        };
    }

    public DialogFragmentViewCredential() {
        credRef = FirebaseDatabase.getInstance().getReference();
    }

    public static DialogFragmentViewCredential getInstance(Credential credential) {
        DialogFragmentViewCredential dialog = new DialogFragmentViewCredential();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_CRED, credential);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            credential = bundle.getParcelable(ARG_CRED);
        }
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout
                .dialog_view_credential, null, false);
        View titleView = LayoutInflater.from(getActivity()).inflate(R.layout
                .dialog_view_credential_title, null, false);
        typeName = titleView.findViewById(R.id.typeName);
        typeIcon = titleView.findViewById(R.id.typeIcon);
        ButterKnife.bind(this, rootView);

        username.setText(credential.getUsername());
        username.setOnClickListener(clickToCopyClistener);
        password.setOnClickListener(clickToCopyClistener);
        password.setText(credential.getPassword());
        if (credential.getDescription() == null || credential.getDescription().isEmpty()) {
            description.setVisibility(View.GONE);
        } else {
            description.setVisibility(View.VISIBLE);
            description.setText(credential.getDescription());
        }
        credRef.child("credentialTypes").child(credential.getTypeId())
                .addListenerForSingleValueEvent
                        (this);
        builder.setView(rootView).
                setCustomTitle(titleView);
                //setPositiveButton(android.R.string.ok, this);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot == null) return;
        CredentialType credentialType = dataSnapshot.getValue(CredentialType.class);
        if (credentialType == null) return;
        typeName.setText(credentialType.getName());
        if (URLUtil.isValidUrl(credentialType.getIconUrl())) {
            Glide.with(typeIcon.getContext()).load(credentialType.getIconUrl()).into
                    (typeIcon);
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Util.Log.d(TAG, "onCancelled %s", databaseError.getMessage());
    }
}
