package io.github.zkhan93.familyfinance;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.github.zkhan93.familyfinance.models.Wallet;
import io.github.zkhan93.familyfinance.viewholders.WalletVH;


public class FragmentWallets extends Fragment implements WalletVH.ItemInteractionListener,
        SearchView.OnQueryTextListener{

    public static final String TAG = FragmentWallets.class.getSimpleName();
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_FAMILY_ID = "familyId";

    public FragmentWallets() {
        // Required empty public constructor
    }

    public static FragmentWallets newInstance(String familyId) {
        FragmentWallets fragment = new FragmentWallets();
        Bundle args = new Bundle();
        args.putString(ARG_FAMILY_ID, familyId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        textView.setText("Fragment Wallets Card");
        return textView;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void delete(Wallet wallet) {

    }

    @Override
    public void edit(Wallet wallet) {

    }

    @Override
    public void onView(Wallet wallet) {

    }

    @Override
    public void onLongPress(Wallet wallet) {

    }
}
