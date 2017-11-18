package io.github.zkhan93.familyfinance;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import io.github.zkhan93.familyfinance.adapters.FilterSmsMemberListAdapter;
import io.github.zkhan93.familyfinance.models.Member;
import io.github.zkhan93.familyfinance.util.Util;
import io.github.zkhan93.familyfinance.viewholders.FilterSMSMemberVH;

/**
 * Created by zeeshan on 11/19/17.
 */

public class DialogFragmentSmsFilter extends DialogFragment implements DialogInterface
        .OnClickListener, FilterSMSMemberVH.ItemInteractionListener {

    public static String ARG_FAMILY_ID = "familyId";
    public static final String TAG = DialogFragmentSmsFilter.class.getSimpleName();

    private String familyId;

    public static DialogFragmentSmsFilter newInstance(String familyId) {
        DialogFragmentSmsFilter dialogFragmentSmsFilter = new DialogFragmentSmsFilter
                ();
        Bundle args = new Bundle();
        args.putString(ARG_FAMILY_ID, familyId);
        dialogFragmentSmsFilter.setArguments(args);
        return dialogFragmentSmsFilter;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            familyId = args.getString(ARG_FAMILY_ID);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_filter_sms,
                null);
        RecyclerView membersList = rootView.findViewById(R.id.members);
        membersList.setAdapter(
                new FilterSmsMemberListAdapter((App) getActivity().getApplication(), familyId, this)
        );
        builder.setTitle(R.string.title_filter_sms)
                .setView(rootView)
                .setPositiveButton(R.string.clear, this)
                .setNegativeButton(android.R.string.cancel, this);

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which) {
        switch (which) {
            case DialogInterface.BUTTON_NEGATIVE:
                dismiss();
                break;
            case DialogInterface.BUTTON_POSITIVE:
                PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
                        .edit()
                        .remove("filter_sms_by_member").apply();
                dismiss();
                break;
        }
    }

    @Override
    public void onFilterMemberClicked(Member member) {
        //write to sharedPreference and fragment will listen to it
        PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
                .edit()
                .putString("filter_sms_by_member", member.getId())
                .apply();
        Util.Log.d(TAG, "filtering by %s", member.getId());
        dismiss();
    }
}
