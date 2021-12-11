package io.github.zkhan93.familyfinance;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.FirebaseDatabase;

import io.github.zkhan93.familyfinance.adapters.CredentialTypeSpinnerAdapter;
import io.github.zkhan93.familyfinance.models.Credential;
import io.github.zkhan93.familyfinance.models.CredentialType;
import io.github.zkhan93.familyfinance.util.TextWatcherProxy;
import io.github.zkhan93.familyfinance.util.Util;

/**
 * Created by zeeshan on 1/28/18.
 */

public class DialogFragmentCredential extends DialogFragment implements DialogInterface
        .OnClickListener, CredentialTypeSpinnerAdapter.OnLoadCompleteListener, AdapterView
        .OnItemSelectedListener {

    public static final String TAG = DialogFragmentCredential.class.getSimpleName();

    private static String ARG_CREDENTIAL = "credential";
    private static String ARG_FAMILY_ID = "familyId";

    EditText description;
    EditText username;
    EditText password;
    Spinner credentialTypeSpinner;

    private CredentialTypeSpinnerAdapter typeAdapter;
    private Credential credential;
    private String familyId;
    private TextWatcherProxy allFieldsTextChangeWatcher;

    {
        allFieldsTextChangeWatcher = new TextWatcherProxy() {
            @Override
            public void afterTextChanged(Editable editable) {
                syncPositiveButton();
            }
        };
    }

    public static DialogFragmentCredential getInstance(Credential credential, String familyId) {
        DialogFragmentCredential dialogFragmentCredential = new DialogFragmentCredential();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_CREDENTIAL, credential);
        bundle.putString(ARG_FAMILY_ID, familyId);
        dialogFragmentCredential.setArguments(bundle);
        return dialogFragmentCredential;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            credential = bundle.getParcelable(ARG_CREDENTIAL);
            familyId = bundle.getString(ARG_FAMILY_ID);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_credential,
                null, false);
        description = rootView.findViewById(R.id.description);
        username = rootView.findViewById(R.id.username);
        password = rootView.findViewById(R.id.password);
        credentialTypeSpinner = rootView.findViewById(R.id.type);

        typeAdapter = new CredentialTypeSpinnerAdapter(this);
        credentialTypeSpinner.setAdapter(typeAdapter);
        credentialTypeSpinner.setOnItemSelectedListener(this);
        builder.setView(rootView).setTitle(R.string.title_credentials_create)
                .setPositiveButton(android.R.string.ok, this)
                .setNegativeButton(android.R.string.cancel, this);

        if (credential != null) {
            builder.setNeutralButton(R.string.delete,this);
            if (!credential.getTypeId().equals("other")) {
                description.setVisibility(View.GONE);
            } else {
                description.setVisibility(View.VISIBLE);
            }
            username.setText(credential.getUsername());
            password.setText(credential.getPassword());
            description.setText(credential.getDescription());
        }
        username.addTextChangedListener(allFieldsTextChangeWatcher);
        password.addTextChangedListener(allFieldsTextChangeWatcher);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                if (credential == null)
                    credential = new Credential();
                credential.setUsername(username.getText().toString());
                credential.setPassword(password.getText().toString());
                credential.setDescription(description.getText().toString());
                String credId = credential.getId();
                if (credId == null || credId.isEmpty())
                    FirebaseDatabase.getInstance().getReference()
                            .child("credentials")
                            .child(familyId).push().setValue(credential);
                else
                    FirebaseDatabase.getInstance().getReference().child("credentials")
                            .child(familyId)
                            .child(credId).setValue(credential);
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                break;
            case DialogInterface.BUTTON_NEUTRAL:
                FirebaseDatabase.getInstance().getReference()
                        .child("credentials")
                        .child(familyId)
                        .child(credential.getId()).setValue(null);
                break;
        }
    }

    @Override
    public void OnCredentialTypesLoadComplete() {
        String typeIdToSelect = "other";
        if (credential != null && credentialTypeSpinner != null)
            typeIdToSelect = credential.getTypeId();
        if (typeIdToSelect.equals("other"))
            description.addTextChangedListener(allFieldsTextChangeWatcher);
        credentialTypeSpinner.setSelection(typeAdapter.getIndexOfCredentialType(typeIdToSelect));
        syncPositiveButton();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        CredentialType credentialType = (CredentialType) typeAdapter.getItem(position);
        if (credentialType == null) return;
        if (credential == null)
            credential = new Credential();
        credential.setTypeId(credentialType.getId());
        if (credentialType.getId().equals("other")) {
            description.setVisibility(View.VISIBLE);
            description.addTextChangedListener(allFieldsTextChangeWatcher);
        } else {
            description.setVisibility(View.GONE);
            description.removeTextChangedListener(allFieldsTextChangeWatcher);
        }
        syncPositiveButton();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Util.Log.d(TAG, "nothing Selected");
    }

    public void syncPositiveButton() {
        String usernameText = username.getText().toString();
        String passwordText = password.getText().toString();
        String descriptionText = description.getText().toString();
        String type = credential == null ? null : credential.getTypeId();
        if (type == null || type.isEmpty() || type.equals("other")) {
            if (descriptionText.isEmpty() ||
                    usernameText.isEmpty() ||
                    passwordText.isEmpty())
                ((AlertDialog) getDialog())
                        .getButton(DialogInterface.BUTTON_POSITIVE)
                        .setEnabled(false);
            else
                ((AlertDialog) getDialog())
                        .getButton(DialogInterface.BUTTON_POSITIVE)
                        .setEnabled(true);
        } else {
            if (usernameText.isEmpty() || passwordText.isEmpty())
                ((AlertDialog) getDialog())
                        .getButton(DialogInterface.BUTTON_POSITIVE)
                        .setEnabled(false);
            else
                ((AlertDialog) getDialog())
                        .getButton(DialogInterface.BUTTON_POSITIVE)
                        .setEnabled(true);
        }
    }
}
