package io.github.zkhan93.familyfinance.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.github.zkhan93.familyfinance.App;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.BaseModel;
import io.github.zkhan93.familyfinance.models.Message;
import io.github.zkhan93.familyfinance.models.MessageDao;
import io.github.zkhan93.familyfinance.tasks.InsertTask;
import io.github.zkhan93.familyfinance.tasks.LoadFromDbTask;
import io.github.zkhan93.familyfinance.viewholders.MessageVH;

/**
 * Created by zeeshan on 11/13/17.
 */

public class MessageListAdapter extends RecyclerView.Adapter<MessageVH> implements LoadFromDbTask
        .Listener<Message>, InsertTask.Listener<BaseModel>, ChildEventListener,
        ValueEventListener {
    private List<Message> messageList;
    private String meId;
    private String familyId;
    private MessageDao messageDao;
    private MessageListener messageListener;

    public MessageListAdapter(Context context, String familyId, MessageListener messageListener) {
        messageList = new ArrayList<>();
        meId = FirebaseAuth.getInstance().getUid();
        this.familyId = familyId;
        FirebaseDatabase.getInstance().getReference("chats").child(familyId).orderByChild
                ("timestamp").addChildEventListener(this);
        messageDao = ((App) context.getApplicationContext()).getDaoSession().getMessageDao();
        this.messageListener = messageListener;
    }

    public MessageListAdapter(Context context, String familyId) {
        this(context, familyId, null);
    }

    @Override
    public MessageVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == ITEM_TYPE.RECEIVING) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .listitem_chat_text_receive, parent, false);
        } else if (viewType == ITEM_TYPE.SENDING) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .listitem_chat_text_send, parent, false);
        }
        return new MessageVH(view, meId);
    }

    @Override
    public void onBindViewHolder(MessageVH holder, int position) {
        holder.setMessage(messageList.get(position));
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).getSenderId().equals(meId) ? ITEM_TYPE.SENDING :
                ITEM_TYPE.RECEIVING;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        if (dataSnapshot == null) return;
        Message message = dataSnapshot.getValue(Message.class);
        if (message == null) return;
        message.setId(dataSnapshot.getKey());
        message.setFamilyId(familyId);
        messageDao.insertOrReplace(message);
        messageList.add(message);
        notifyItemInserted(messageList.size());
        if (messageListener != null) messageListener.onNewMessage(messageList.size());
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onLoadTaskComplete(List<Message> data) {

    }

    @Override
    public void onInsertTaskComplete(List<BaseModel> items) {

    }

    interface ITEM_TYPE {
        int SENDING = 1;
        int RECEIVING = 0;
    }

    public interface MessageListener {
        void onNewMessage(int position);
    }
}
