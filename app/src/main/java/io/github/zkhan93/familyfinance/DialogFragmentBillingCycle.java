package io.github.zkhan93.familyfinance;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.util.Util;

/**
 * Created by zeeshan on 11/25/17.
 */

public class DialogFragmentBillingCycle extends DialogFragment implements
        DialogInterface.OnClickListener,
        NumberPicker.OnValueChangeListener {
    public static final String TAG = DialogFragmentBillingCycle.class.getSimpleName();

    private static final String ARG_BILLING_DAY = "billing_day";
    private static final String ARG_PAYMENT_DAY = "payment_day";

    @BindView(R.id.billing_day)
    NumberPicker billingDay;
    @BindView(R.id.payment_day)
    NumberPicker paymentDay;
    @BindView(R.id.billing_cycle)
    TextView billingCycle;

    private int intBillingDay, intPaymentDay;
    private OnBillingCycleSelectListener onBillingCycleSelectListener;

    public static DialogFragmentBillingCycle getInstance(OnBillingCycleSelectListener
                                                                 onBillingCycleSelectListener,
                                                         int billingDay, int paymentDay) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_BILLING_DAY, billingDay);
        bundle.putInt(ARG_PAYMENT_DAY, paymentDay);

        DialogFragmentBillingCycle fragment = new DialogFragmentBillingCycle();
        fragment.setArguments(bundle);
        fragment.onBillingCycleSelectListener = onBillingCycleSelectListener;

        return fragment;
    }

    public static DialogFragmentBillingCycle getInstance(OnBillingCycleSelectListener
                                                                 onBillingCycleSelectListener) {
        return getInstance(onBillingCycleSelectListener, 1, 21);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            intBillingDay = bundle.getInt(ARG_BILLING_DAY, 1);
            intPaymentDay = bundle.getInt(ARG_PAYMENT_DAY, 21);
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout
                .dialog_billingcycle, null, false);
        ButterKnife.bind(this, rootView);
        billingDay.setMinValue(1);
        billingDay.setMaxValue(31);
        billingDay.setValue(intBillingDay);
        billingDay.setOnValueChangedListener(this);

        paymentDay.setMinValue(1);
        paymentDay.setMaxValue(31);
        paymentDay.setValue(intPaymentDay);
        paymentDay.setOnValueChangedListener(this);

        billingCycle.setText(Util.
                getBillingCycleString(billingDay.getValue(),
                        paymentDay.getValue(), "%s - %s"));

        builder.setTitle(R.string.title_billing_cycle)
                .setView(rootView).setPositiveButton(android.R.string.ok, this)
                .setNegativeButton(android.R.string.cancel, this);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                if (onBillingCycleSelectListener != null)
                    onBillingCycleSelectListener.onBillingCycleSelect(billingDay.getValue(),
                            paymentDay.getValue());
                break;
        }
    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
        billingCycle.setText(Util.
                getBillingCycleString(billingDay.getValue(),
                        paymentDay.getValue(), "%s - %s"));
    }

    public interface OnBillingCycleSelectListener {
        void onBillingCycleSelect(int billingDay, int paymentDay);
    }
}
