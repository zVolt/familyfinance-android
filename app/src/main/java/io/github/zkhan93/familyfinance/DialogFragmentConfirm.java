package io.github.zkhan93.familyfinance;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import org.greenrobot.eventbus.EventBus;

import io.github.zkhan93.familyfinance.events.DeleteConfirmedEvent;
import io.github.zkhan93.familyfinance.models.BaseModel;

/**
 * Created by zeeshan on 15/7/17.
 */

public class DialogFragmentConfirm<T extends BaseModel> extends DialogFragment implements
        DialogInterface
                .OnClickListener {

    public static String ARG_TITLE = "title";
    public static String ARG_ITEM = "item";
    public static final String TAG = DialogFragmentConfirm.class.getSimpleName();

    private T item;
    private String title;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args == null) {
            title = "Are you sure?";
        } else {
            title = args.getString(ARG_TITLE, "Are you sure?");
            item = args.getParcelable(ARG_ITEM);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setPositiveButton(android.R.string.yes, this)
                .setNegativeButton(android.R.string.no, this);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                EventBus.getDefault().post(new DeleteConfirmedEvent<>(item));
                break;
        }
    }

}
