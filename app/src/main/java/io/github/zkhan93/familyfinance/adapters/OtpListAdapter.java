package io.github.zkhan93.familyfinance.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.Account;
import io.github.zkhan93.familyfinance.models.Otp;
import io.github.zkhan93.familyfinance.viewholders.AccountVH;
import io.github.zkhan93.familyfinance.viewholders.OtpVH;

/**
 * Created by zeeshan on 8/7/17.
 */

public class OtpListAdapter extends RecyclerView.Adapter<OtpVH> {
    public static final String TAG = OtpListAdapter.class.getSimpleName();
    ArrayList<Otp> otps;

    public OtpListAdapter(ArrayList<Otp> opts) {
        this.otps = opts == null ? new ArrayList<Otp>() : opts;
    }

    @Override
    public OtpVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OtpVH(LayoutInflater.from(parent.getContext()).inflate(R.layout
                .listitem_otp, parent, false));
    }

    @Override
    public void onBindViewHolder(OtpVH holder, int position) {
        holder.setOtp(otps.get(position));
    }

    @Override
    public int getItemCount() {
        return otps.size();
    }

}
