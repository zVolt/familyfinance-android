package io.github.zkhan93.familyfinance;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import org.greenrobot.eventbus.EventBus;

import io.github.zkhan93.familyfinance.events.DeleteConfirmedEvent;

/**
 * Created by zeeshan on 15/7/17.
 */

public class DialogFragmentConfirm<T> extends DialogFragment implements DialogInterface
        .OnClickListener {

    public static String ARGS_TITLE = "title";
    public static final String TAG = DialogFragmentConfirm.class.getSimpleName();

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String title;
        if (args == null)
            title = "Are you sure?";
        else
            title = args.getString(ARGS_TITLE, "Are you sure?");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setPositiveButton(android.R.string.yes, this).setNegativeButton(android.R.string
                .no, this);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                EventBus.getDefault().post(new DeleteConfirmedEvent<T>());
                break;
        }
    }

}
