package io.github.zkhan93.familyfinance;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import io.github.zkhan93.familyfinance.viewholders.MemberVH;

import static android.support.v4.content.ContextCompat.checkSelfPermission;


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
    public static final int PERMISSION_REQUEST_CODE = 42;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_FAMILY_ID = "familyId";
    private static final String ARG_FAMILY_MODERATOR_ID = "familyModeratorId";


    private String familyId;
    private String familyModeratorId;

    @BindView(R.id.list)
    RecyclerView membersList;

    private Member enableSmsFor;
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

    public static FragmentMembers newInstance(String familyId, String familyModeratorId) {
        FragmentMembers fragment = new FragmentMembers();
        Bundle args = new Bundle();
        args.putString(ARG_FAMILY_ID, familyId);
        args.putString(ARG_FAMILY_MODERATOR_ID, familyModeratorId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle args = getArguments();
            familyId = args.getString(ARG_FAMILY_ID, null);
            familyModeratorId = args.getString(ARG_FAMILY_MODERATOR_ID, null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_members, container, false);
        ButterKnife.bind(this, rootView);
        memberListAdapter = new MemberListAdapter((App) getActivity().getApplication(), familyId,
                this);
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
        boolean setEnabled = !member.getSmsEnabled();
        enableSmsFor = member;
        if (setEnabled) {
            int permissionCheck = checkSelfPermission(getActivity(), Manifest
                    .permission.RECEIVE_SMS) & checkSelfPermission(getActivity(), Manifest
                    .permission.READ_PHONE_STATE);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest
                        .permission.RECEIVE_SMS)) {
                    //explain the need of this permission
                    //todo show a dialog and then on positive show request permission
                    Log.d(TAG, "lol we need it :D");
                    requestPermissions(new String[]{
                            Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_PHONE_STATE
                    }, PERMISSION_REQUEST_CODE);

                } else {
                    requestPermissions(new String[]{
                            Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_PHONE_STATE
                    }, PERMISSION_REQUEST_CODE);
                }
            }
        } else {
            member.setSmsEnabled(false);
            ((App) getActivity().getApplication()).getDaoSession().getMemberDao().update
                    (member);
            memberListAdapter.addOrUpdate(member);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 1 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED && grantResults[1] == PackageManager
                        .PERMISSION_GRANTED) {
                    Log.d(TAG, "granted");
                    //granted
                    //todo: update firebase only
                    enableSmsFor.setSmsEnabled(true);
                    ((App) getActivity().getApplication()).getDaoSession().getMemberDao().update
                            (enableSmsFor);
                    memberListAdapter.addOrUpdate(enableSmsFor);
                } else {
                    //todo: show that permission rejected hence cannot share sms
                    Log.d(TAG, "rejected");
                }
                break;
        }
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
