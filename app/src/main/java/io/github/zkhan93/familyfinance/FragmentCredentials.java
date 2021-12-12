package io.github.zkhan93.familyfinance;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.github.zkhan93.familyfinance.adapters.CredentialListAdapter;
import io.github.zkhan93.familyfinance.models.Credential;
import io.github.zkhan93.familyfinance.util.Util;
import io.github.zkhan93.familyfinance.viewholders.CredentialVH;
import io.github.zkhan93.familyfinance.vm.AppState;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link FragmentCredentials#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCredentials extends Fragment implements CredentialVH.CredentialInteraction {
    public static final String TAG = FragmentCredentials.class.getSimpleName();

    private static final String ARG_FAMILY_ID = "familyId";

    RecyclerView credentialList;

    private String familyId;

    private AppState appState;

    public FragmentCredentials() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param familyId Parameter 1
     * @return A new instance of fragment FragmentCredentials.
     */
    public static FragmentCredentials newInstance(String familyId) {
        FragmentCredentials fragment = new FragmentCredentials();
        Bundle args = new Bundle();
        args.putString(ARG_FAMILY_ID, familyId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            familyId = getArguments().getString(ARG_FAMILY_ID);
        }
        if (familyId == null) {
            familyId =
                    PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(ARG_FAMILY_ID, null);
        }
        appState = new ViewModelProvider(requireActivity()).get(AppState.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_credential, container, false);
        credentialList = rootView.findViewById(R.id.passwords);
        CredentialListAdapter credentialListAdapter = new CredentialListAdapter(getActivity()
                .getApplicationContext(), this, familyId);
        credentialList.setLayoutManager(new LinearLayoutManager(getActivity()
                .getApplicationContext()));
        credentialList.setAdapter(credentialListAdapter);
        initFab();
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        initFab();
    }

    @Override
    public void onCredentialClicked(Credential credential) {
        DialogFragmentViewCredential.getInstance(credential).show(getActivity()
                .getSupportFragmentManager(), DialogFragmentViewCredential.TAG);
    }

    @Override
    public void onCredentialLongClicked(Credential credential) {
        DialogFragmentCredential.
                getInstance(credential, familyId).
                show(getActivity().getSupportFragmentManager(), DialogFragmentCredential.TAG);
    }

    private void initFab() {
        appState.enableFab(R.drawable.ic_add_white_24dp, TAG);
        appState.getFabAction().observe(getViewLifecycleOwner(), event -> {
            String id = event.getContentIfNotHandled();
            Util.Log.d(TAG, "fab click for: %s", id);
            if (id != null && id.equals(TAG))
                DialogFragmentCredential.getInstance(null, familyId)
                        .show(getParentFragmentManager(), DialogFragmentViewCard.TAG);
        });
    }
}
