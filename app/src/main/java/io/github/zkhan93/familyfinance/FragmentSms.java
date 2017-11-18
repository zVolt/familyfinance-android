package io.github.zkhan93.familyfinance;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.adapters.OtpListAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentSms.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentSms#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSms extends Fragment implements OtpListAdapter.ItemInsertedListener {
    public static final String TAG = FragmentSms.class.getSimpleName();
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_FAMILY_ID = "familyId";


    private OnFragmentInteractionListener mListener;
    private String familyId;

    @BindView(R.id.list)
    RecyclerView otpsList;

    public FragmentSms() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentSms.
     */

    public static FragmentSms newInstance(String familyId) {
        FragmentSms fragment = new FragmentSms();
        Bundle args = new Bundle();
        args.putString(ARG_FAMILY_ID, familyId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            familyId = getArguments().getString(ARG_FAMILY_ID, null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_otps, container, false);
        ButterKnife.bind(this, rootView);
        OtpListAdapter otpListAdapter = new OtpListAdapter((App) getActivity().getApplication(), familyId, this);
        otpsList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        otpsList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        otpsList.scrollToPosition(0);
        otpsList.setAdapter(otpListAdapter);
        return rootView;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {

    }

    @Override
    public void onItemAdded(int position) {
        if (otpsList != null)
            otpsList.smoothScrollToPosition(position);
    }
}
