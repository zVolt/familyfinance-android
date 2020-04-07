package io.github.zkhan93.familyfinance;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.zkhan93.familyfinance.util.Util;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentHome extends Fragment {
    private static final String TAG = FragmentHome.class.getSimpleName();


    private String familyId;


    public FragmentHome() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        familyId = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getString(R.string.pref_family_id), null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @OnClick({R.id.summary, R.id.ccards, R.id.dcards, R.id.accounts, R.id.messages, R.id.members, R.id.credentials})
    void onCardClicked(View view) {
        Util.Log.d(TAG, "card clicked %d", view.getId());
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.pref_family_id), familyId);
        NavController navController = Navigation.findNavController(getActivity(),
                R.id.nav_host_fragment);
        navController.navigate(view.getId(), bundle);
    }
}
