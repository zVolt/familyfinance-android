package io.github.zkhan93.familyfinance;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.zkhan93.familyfinance.adapters.MessageListAdapter;
import io.github.zkhan93.familyfinance.models.Message;
import io.github.zkhan93.familyfinance.util.Util;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentChatroom.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentChatroom#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentChatroom extends Fragment implements MessageListAdapter.MessageListener {
    public static final String TAG = FragmentChatroom.class.getSimpleName();
    private static final String ARG_FAMILY_ID = "familyId";

    private String familyId, meId;
    private OnFragmentInteractionListener mListener;
    private int unreadMessageStartPosition, unreadMessageCount;

    @BindView(R.id.list)
    RecyclerView messages;
    @BindView(R.id.content)
    EditText content;
    @BindView(R.id.send)
    ImageButton send;
    @BindView(R.id.smily)
    ImageButton smily;
    @BindView(R.id.attach)
    ImageButton attach;
    @BindView(R.id.new_message)
    ImageButton newMessage;

    public FragmentChatroom() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param familyId Family Id.
     * @return A new instance of fragment FragmentChatroom.
     */
    public static FragmentChatroom newInstance(String familyId) {
        FragmentChatroom fragment = new FragmentChatroom();
        Bundle args = new Bundle();
        args.putString(ARG_FAMILY_ID, familyId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            familyId = getArguments().getString(ARG_FAMILY_ID);
        }
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser != null)
            meId = fbUser.getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_chatroom, container, false);
        ButterKnife.bind(this, rootView);
        messages.setLayoutManager(new LinearLayoutManager(getActivity()));
        MessageListAdapter messageListAdapter = new MessageListAdapter(getActivity()
                .getApplicationContext(),
                familyId, this);
        messages.setAdapter(messageListAdapter);
        newMessage.setVisibility(View.GONE);
        return rootView;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @OnClick({R.id.send, R.id.attach, R.id.smily})
    public void onSend(View view) {
        switch (view.getId()) {
            case R.id.send:
                String strContent = content.getText().toString().trim();
                if (strContent.isEmpty()) {
                    content.setText("");
                    return;
                }
                Message message = new Message();
                message.setContent(strContent);
                message.setTimestamp(Calendar.getInstance().getTimeInMillis());
                message.setSenderId(meId);
                FirebaseDatabase.getInstance().getReference("chats").child(familyId).push().setValue
                        (message);
                content.setText("");
                break;
            case R.id.smily:
            case R.id.attach:
                Toast.makeText(getContext(), "Will be available soon..", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onNewMessage(int position) {
        Util.Log.d(TAG, "%d %d", ((LinearLayoutManager) messages.getLayoutManager())
                .findLastCompletelyVisibleItemPosition(), position);
        if (((LinearLayoutManager) messages.getLayoutManager())
                .findLastCompletelyVisibleItemPosition() != position - 2) {
            //if last visible item is not at position-1
            if (unreadMessageStartPosition == -1) unreadMessageStartPosition = position;
            else unreadMessageCount += 1;
            showUnreadView();
        } else
            messages.smoothScrollToPosition(position);
    }

    private void showUnreadView() {
        newMessage.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.new_message)
    void onClick(View view) {
        if (unreadMessageStartPosition != 0)
            messages.smoothScrollToPosition(unreadMessageStartPosition);
        newMessage.setVisibility(View.GONE);
        unreadMessageStartPosition = -1;
    }
}
