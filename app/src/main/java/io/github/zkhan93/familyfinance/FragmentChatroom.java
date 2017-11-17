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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.zkhan93.familyfinance.adapters.MessageListAdapter;
import io.github.zkhan93.familyfinance.models.Message;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentChatroom.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentChatroom#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentChatroom extends Fragment {
    private static final String ARG_FAMILY_ID = "familyId";

    private String familyId, meId;
    private OnFragmentInteractionListener mListener;
    private MessageListAdapter messageListAdapter;

    @BindView(R.id.list)
    RecyclerView messages;
    @BindView(R.id.content)
    EditText content;
    @BindView(R.id.send)
    ImageButton send;

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
        meId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_chatroom, container, false);
        ButterKnife.bind(this, rootView);
        messages.setLayoutManager(new LinearLayoutManager(getActivity()));
        messageListAdapter = new MessageListAdapter(getActivity().getApplicationContext(),
                familyId);
        messages.setAdapter(messageListAdapter);
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
        } else {
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @OnClick(R.id.send)
    public void onSend(View view) {
        Message message = new Message();
        message.setContent(content.getText().toString());
        message.setTimestamp(Calendar.getInstance().getTimeInMillis());
        message.setSenderId(meId);
        FirebaseDatabase.getInstance().getReference("chats").child(familyId).push().setValue
                (message);
        content.setText("");
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
}
