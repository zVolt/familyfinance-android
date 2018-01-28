package io.github.zkhan93.familyfinance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.adapters.CredentialListAdapter;
import io.github.zkhan93.familyfinance.models.Credential;
import io.github.zkhan93.familyfinance.viewholders.CredentialVH;


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

    @BindView(R.id.passwords)
    RecyclerView credentialList;

    private String familyId;


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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_credential, container, false);
        ButterKnife.bind(this, rootView);
        CredentialListAdapter credentialListAdapter = new CredentialListAdapter(getActivity()
                .getApplicationContext(), this, familyId);
        credentialList.setLayoutManager(new LinearLayoutManager(getActivity()
                .getApplicationContext()));
        credentialList.setAdapter(credentialListAdapter);
        return rootView;
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
}
