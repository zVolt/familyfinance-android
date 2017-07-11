package io.github.zkhan93.familyfinance;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.adapters.MemberListAdapter;
import io.github.zkhan93.familyfinance.models.Member;
import io.github.zkhan93.familyfinance.util.Constants;
import io.github.zkhan93.familyfinance.viewholders.MemberVH;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentMembers#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentMembers extends Fragment implements MemberVH.ItemInteractionListener {

    public static final String TAG = FragmentMembers.class.getSimpleName();

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";


//    private String mParam1;
//    private String mParam2;

    @BindView(R.id.list)
    RecyclerView membersList;

    private MemberListAdapter memberListAdapter;

    private OnFragmentInteractionListener mListener;

    public FragmentMembers() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentMembers.
     */

    public static FragmentMembers newInstance() {
        FragmentMembers fragment = new FragmentMembers();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_members, container, false);
        ButterKnife.bind(this, rootView);
        memberListAdapter = new MemberListAdapter((App) getActivity().getApplication(), this);
        membersList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext
                ()));
        membersList.setAdapter(memberListAdapter);
        return rootView;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            //call the interface methods
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


    @Override
    public void toggleSms(Member member) {
        Log.d(TAG, "toggleSms: " + member.toString());
        member.setCanRecieveSms(!member.getCanRecieveSms());
        ((App) getActivity().getApplication()).getDaoSession().getMemberDao().update(member);
        memberListAdapter.notifyItemChanged(member);
    }

    @Override
    public void remove(Member member) {
        Log.d(TAG, "remove: " + member.toString());
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {

    }
}
