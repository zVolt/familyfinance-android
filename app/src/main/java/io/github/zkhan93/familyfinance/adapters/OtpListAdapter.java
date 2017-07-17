package io.github.zkhan93.familyfinance.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.github.zkhan93.familyfinance.App;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.Otp;
import io.github.zkhan93.familyfinance.tasks.LoadFromDbTask;
import io.github.zkhan93.familyfinance.viewholders.OtpVH;

/**
 * Created by zeeshan on 8/7/17.
 */

public class OtpListAdapter extends RecyclerView.Adapter<OtpVH> implements LoadFromDbTask.Listener<Otp> {
    public static final String TAG = OtpListAdapter.class.getSimpleName();
    ArrayList<Otp> otps;

    public OtpListAdapter(App app) {
        this.otps = new ArrayList<>();
        new LoadFromDbTask<>(app.getDaoSession().getOtpDao(), this).execute();
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

    @Override
    public void onLoadTaskComplete(List<Otp> data) {
        otps.clear();
        otps.addAll(data);
        notifyDataSetChanged();
    }
}
