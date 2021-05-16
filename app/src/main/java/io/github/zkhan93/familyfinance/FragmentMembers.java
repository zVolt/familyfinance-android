package io.github.zkhan93.familyfinance;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.github.zkhan93.familyfinance.adapters.MemberListAdapter;
import io.github.zkhan93.familyfinance.models.Member;
import io.github.zkhan93.familyfinance.util.Util;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentMembers#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentMembers extends Fragment {

    public static final String TAG = FragmentMembers.class.getSimpleName();
    public static final int PERMISSION_REQUEST_CODE = 42;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_FAMILY_ID = "familyId";
    private static final String ARG_FAMILY_MODERATOR_ID = "familyModeratorId";


    private String familyId;

    RecyclerView membersList;

    private Member enableSmsFor;
    private MemberListAdapter memberListAdapter;

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
            familyId = getArguments().getString(ARG_FAMILY_ID, null);
        }
        if(familyId == null){
            familyId = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(ARG_FAMILY_ID, null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_members, container, false);
        membersList = rootView.findViewById(R.id.list);
        memberListAdapter = new MemberListAdapter((App) getActivity().getApplication(), familyId);
        membersList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext
                ()));
        membersList.setAdapter(memberListAdapter);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Util.isInternetConnected(getActivity().getApplicationContext())) {
            Log.d(TAG, "seeking presence");
            FirebaseDatabase.getInstance().getReference("family").child(familyId).child
                    ("checkPresence").setValue(ServerValue.TIMESTAMP);
        } else
            Log.d(TAG, "seeking presence condiftion failed");
    }

//    @Override
//    public void toggleSms(Member member) {
//        Log.d(TAG, "toggleSms: " + member.toString());
//        boolean setEnabled = !member.getSmsEnabled();
//        enableSmsFor = member;
//        if (setEnabled) {
//            int permissionCheck = checkSelfPermission(getActivity(), Manifest
//                    .permission.RECEIVE_SMS) & checkSelfPermission(getActivity(), Manifest
//                    .permission.READ_PHONE_STATE);
//
//            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
//                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest
//                        .permission.RECEIVE_SMS)) {
//                    //explain the need of this permission
//                    //todo show a dialog and then on positive show request permission
//                    Log.d(TAG, "lol we need it :D");
//                    requestPermissions(new String[]{
//                            Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_PHONE_STATE
//                    }, PERMISSION_REQUEST_CODE);
//
//                } else {
//                    requestPermissions(new String[]{
//                            Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_PHONE_STATE
//                    }, PERMISSION_REQUEST_CODE);
//                }
//            }
//        } else {
//            member.setSmsEnabled(false);
//            ((App) getActivity().getApplication()).getDaoSession().getMemberDao().update
//                    (member);
//            memberListAdapter.addOrUpdate(member);
//        }
//    }

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
}
