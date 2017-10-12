package io.github.zkhan93.familyfinance.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.greendao.query.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import io.github.zkhan93.familyfinance.App;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.events.InsertEvent;
import io.github.zkhan93.familyfinance.models.AddonCard;
import io.github.zkhan93.familyfinance.models.AddonCardDao;
import io.github.zkhan93.familyfinance.models.BaseModel;
import io.github.zkhan93.familyfinance.models.CCard;
import io.github.zkhan93.familyfinance.models.CCardDao;
import io.github.zkhan93.familyfinance.tasks.InsertTask;
import io.github.zkhan93.familyfinance.tasks.LoadFromDbTask;
import io.github.zkhan93.familyfinance.viewholders.AddonCardVH;
import io.github.zkhan93.familyfinance.viewholders.CCardVH;
import io.github.zkhan93.familyfinance.viewholders.EmptyVH;
import io.github.zkhan93.familyfinance.viewholders.FooterVH;

/**
 * Created by zeeshan on 8/7/17.
 */

public class CCardListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        LoadFromDbTask
                .Listener<CCard>, InsertTask.Listener<BaseModel>, ChildEventListener,
        ValueEventListener {
    public static final String TAG = CCardListAdapter.class.getSimpleName();
    private ArrayList<CCard> ccards;
    private CCardVH.ItemInteractionListener itemInteractionListener;
    private AddonCardVH.ItemInteractionListener addonCardInteractionListener;
    private String familyId;
    private DatabaseReference ccardRef;
    private CCardDao cCardDao;
    private AddonCardDao addonCardDao;
    private boolean ignoreChildEvent;

    public CCardListAdapter(App app, String familyId, CCardVH.ItemInteractionListener
            itemInteractionListener, AddonCardVH.ItemInteractionListener
                                    addonCardInteractionListener) {
        cCardDao = app.getDaoSession().getCCardDao();
        addonCardDao = app.getDaoSession().getAddonCardDao();
        this.ccards = new ArrayList<>();
        this.itemInteractionListener = itemInteractionListener;
        this.addonCardInteractionListener = addonCardInteractionListener;
        this.familyId = familyId;
        ccardRef = FirebaseDatabase.getInstance().getReference("ccards").child(familyId);
        ignoreChildEvent = true;
        Query<CCard> query = cCardDao.queryBuilder().orderDesc(CCardDao.Properties.UpdatedOn)
                .build();
        new LoadFromDbTask<>(query, this).execute();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.EMPTY)
            return new EmptyVH(LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .listitem_empty, parent, false),"blankCCard");
        else if (viewType == ITEM_TYPE.FOOTER)
            return new FooterVH(LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .listitem_footer, parent, false));
        else
            return new CCardVH(LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .listitem_ccard, parent, false), itemInteractionListener,
                    addonCardInteractionListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == ITEM_TYPE.NORMAL)
            ((CCardVH) holder).setCCard(ccards.get(position));
    }

    @Override
    public int getItemCount() {
        return ccards.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (ccards.size() == 0)
            return ITEM_TYPE.EMPTY;
        return position == ccards.size() ? ITEM_TYPE.FOOTER : ITEM_TYPE.NORMAL;
    }

    public interface ITEM_TYPE {
        int NORMAL = 0;
        int FOOTER = 1;
        int EMPTY = 2;
    }

    @Override
    public void onLoadTaskComplete(List<CCard> data) {
//        Log.d(TAG, "loaded: " + data.toString());
        ccards.clear();
        ccards.addAll(data);
        Collections.sort(ccards, CCard.BY_UPDATED_ON);
        notifyDataSetChanged();
        ccardRef.addListenerForSingleValueEvent(this);
        ccardRef.addChildEventListener(this);
    }

    public boolean addOrUpdate(CCard newCcard, List<AddonCard> addonCards) {
        int position = 0;
        boolean found = false;
        ListIterator<CCard> itr = ccards.listIterator();
        CCard oldCcard;
        while (itr.hasNext()) {
            oldCcard = itr.next();
            if (oldCcard.getNumber().trim().equals(newCcard.getNumber().trim())) {
                found = true;
                oldCcard.updateFrom(newCcard); // fetch the item from database again
                cCardDao.insertOrReplace(oldCcard);
                addonCardDao.queryBuilder().where(AddonCardDao.Properties.MainCardNumber.eq
                        (oldCcard.getNumber())).buildDelete()
                        .executeDeleteWithoutDetachingEntities();
                addonCardDao.insertOrReplaceInTx(addonCards);
                oldCcard = cCardDao.load(oldCcard.getNumber());
                itr.set(oldCcard);
                notifyItemChanged(position);
                break;
            }
            position++;
        }
        if (found) {
            oldCcard = ccards.remove(position);
            ccards.add(0, oldCcard);
            notifyItemMoved(position, 0);
        }
        if (!found) {
            addonCardDao.insertOrReplaceInTx(addonCards);
            cCardDao.insertOrReplace(newCcard);
            newCcard = cCardDao.load(newCcard.getNumber());
            ccards.add(0, newCcard);
            notifyItemInserted(0);
        }
        return found;
    }

    public boolean addOrUpdate(CCard newCcard) {
        int position = 0;
        boolean found = false;
        ListIterator<CCard> itr = ccards.listIterator();
        CCard oldCcard;
        while (itr.hasNext()) {
            oldCcard = itr.next();
            if (oldCcard.getNumber().trim().equals(newCcard.getNumber().trim())) {
                found = true;
                oldCcard.updateFrom(newCcard); // fetch the item from database again
                cCardDao.insertOrReplace(oldCcard);
                oldCcard = cCardDao.load(oldCcard.getNumber());
                itr.set(oldCcard);
                notifyItemChanged(position);
                break;
            }
            position++;
        }
        if (found) {
            oldCcard = ccards.remove(position);
            ccards.add(0, oldCcard);
            notifyItemMoved(position, 0);
        }
        if (!found) {
            cCardDao.insertOrReplace(newCcard);
            newCcard = cCardDao.load(newCcard.getNumber());
            ccards.add(0, newCcard);
            notifyItemInserted(0);
        }
        return found;
    }

    public void registerForEvent() {
        EventBus.getDefault().register(this);
    }

    public void unregisterForEvent() {
        EventBus.getDefault().unregister(this);
    }

    public void deleteCcard(String cardNumber) {
        ListIterator<CCard> itr = ccards.listIterator();
        int position = 0;
        while (itr.hasNext()) {
            if (itr.next().getNumber().trim().equals(cardNumber.trim())) {
                itr.remove();
                notifyItemRemoved(position);
                ccardRef.child(cardNumber).setValue(null);
                break;
            }
            position++;
        }
    }

    public void deleteCcard(String cardNumber, boolean isAddon) {
        if (!isAddon) {
            deleteCcard(cardNumber);
            return;
        }
        ListIterator<AddonCard> itr;
        AddonCard addonCard;
        int position = 0;
        for (CCard cCard : ccards) {
            if (cCard.getAddonCards() == null || cCard.getAddonCards().size() == 0) continue;
            itr = cCard.getAddonCards().listIterator();
            while (itr.hasNext()) {
                addonCard = itr.next();
                if (addonCard.getNumber().trim().equals(cardNumber.trim())) {
                    itr.remove();
                    notifyItemChanged(position);
                    ccardRef.child(cCard.getNumber()).child("addonCards").child(addonCard
                            .getNumber()).setValue(null);
                    return;
                }
            }
            position++;
        }
    }

    @Subscribe()
    public void onCcardEvent(InsertEvent<CCard> insertEvent) {
        if (insertEvent.getItems() == null)
            return;
        for (CCard cCard : insertEvent.getItems()) {
            if (cCard != null)
                addOrUpdate(cCard);
        }
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        if (ignoreChildEvent || !dataSnapshot.exists())
            return;
        CCard cCard = dataSnapshot.getValue(CCard.class);
        if (cCard == null) return;
        List<AddonCard> addonCards = new ArrayList<>();
        AddonCard addonCard;
        for (DataSnapshot ads : dataSnapshot.child("addonCards").getChildren()) {
            addonCard = ads.getValue(AddonCard.class);
            if (addonCard != null)
                addonCards.add(addonCard);
        }
        addOrUpdate(cCard, addonCards);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        if (!dataSnapshot.exists())
            return;
        CCard cCard = dataSnapshot.getValue(CCard.class);
        if (cCard == null)
            return;
        List<AddonCard> addonCards = new ArrayList<>();
        AddonCard addonCard;
        for (DataSnapshot ads : dataSnapshot.child("addonCards").getChildren()) {
            addonCard = ads.getValue(AddonCard.class);
            if (addonCard != null)
                addonCards.add(addonCard);
        }
        addOrUpdate(cCard, addonCards);
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        if (!dataSnapshot.exists())
            return;
        CCard cCard = dataSnapshot.getValue(CCard.class);
        if (cCard == null)
            return;
        ListIterator<CCard> itr = ccards.listIterator();
        CCard oldcCard;
        int position = 0;
        boolean found = false;
        while (itr.hasNext()) {
            oldcCard = itr.next();
            if (oldcCard.getNumber().trim().equals(cCard.getNumber().trim())) {
                oldcCard.delete();
                itr.remove();
                found = true;
                break;
            }
            position++;
        }
        if (found) {
            notifyItemRemoved(position);
        }
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        //no shit givven
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d(TAG, "operation cancelled" + databaseError.getMessage());
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (!dataSnapshot.exists())
            return;
        CCard cCard;
        AddonCard addonCard;
        List<CCard> ccards = new ArrayList<>();
        List<AddonCard> addonCards = new ArrayList<>();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            cCard = ds.getValue(CCard.class);
            if (cCard == null) continue;
            ccards.add(cCard);
            for (DataSnapshot ads : ds.child("addonCards").getChildren()) {
                addonCard = ads.getValue(AddonCard.class);
                if (addonCard == null) continue;
                addonCard.setMainCardNumber(cCard.getNumber());
                addonCards.add(addonCard);
            }
        }
//        Log.d(TAG, "fetched: " + ccards.toString());
        new InsertTask<>(addonCardDao, this, true).execute(addonCards.toArray(new
                AddonCard[addonCards.size()]));
        new InsertTask<>(cCardDao, this, true).execute(ccards.toArray(new CCard[ccards.size()]));
    }

    @Override
    public void onInsertTaskComplete(List items) {
        if (items == null || items.size() == 0)
            return;
        if (items.get(0) instanceof CCard) {
            ccards.clear();
            ccards.addAll(items);
            Collections.sort(ccards, CCard.BY_UPDATED_ON);
            notifyDataSetChanged();
            ignoreChildEvent = false;
        }
    }
}
