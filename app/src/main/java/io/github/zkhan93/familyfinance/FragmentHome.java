package io.github.zkhan93.familyfinance;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import io.github.zkhan93.familyfinance.util.Util;
import io.github.zkhan93.familyfinance.vm.AppState;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentHome extends Fragment {
    private static final String TAG = FragmentHome.class.getSimpleName();


    private String familyId;

    private View.OnClickListener cardClickListener;


    public FragmentHome() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        familyId =
                PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getString(R.string.pref_family_id), null);
        cardClickListener = view -> {

            Util.Log.d(TAG, "card clicked %d", view.getId());
            AppState appState = new ViewModelProvider(requireActivity()).get(AppState.class);
            appState.disableFab();
            NavController navController = Navigation.findNavController(requireActivity(),
                    R.id.nav_host_fragment);
            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.pref_family_id), familyId);
            navController.navigate(view.getId(), bundle);
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        int[] viewIds = new int[]{
                R.id.ccards,
                R.id.summary,
                R.id.dcards,
                R.id.accounts,
                R.id.messages,
                R.id.members,
                R.id.credentials
        };
        for (int rid : viewIds)
            rootView.findViewById(rid).setOnClickListener(cardClickListener);
        return rootView;
    }

}
