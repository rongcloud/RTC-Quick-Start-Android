package cn.rongcloud.sealcall;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.rtc.stream.MediaType;
import io.rong.signalingkit.RCSCall;
import io.rong.signalingkit.callmanager.IRCSReceivedCallListener;
import io.rong.signalingkit.RCSCallClient;
import io.rong.signalingkit.RCSCallCommon;
import io.rong.signalingkit.callmanager.RCSCallManager;
import io.rong.signalingkit.RCSCallSession;
import io.rong.common.RLog;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private String userId;
    private ImageView moreImage;
    private Button multiChatButton;
    private RecyclerView contactList;
    private static final int REQUEST_CODE_SINGLE_AUDIO = 101;
    private static final int REQUEST_CODE_SINGLE_VIDEO = 102;
    private static final int REQUEST_CODE_MULTI_AUDIO = 103;
    private static final int REQUEST_CODE_MULTI_VIDEO = 104;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        userId = getIntent().getStringExtra("userId");
//        this.setTitle(userId);

        moreImage = findViewById(R.id.seal_more);
        moreImage.setOnClickListener(this);

        contactList = findViewById(R.id.contact_list);
        contactList.setLayoutManager(new LinearLayoutManager(this));

        final ContactListAdapter adapter = new ContactListAdapter(getOtherContacts());
        contactList.setAdapter(adapter);
        RCSCallClient.getInstance().init(getApplicationContext());
        IRCSReceivedCallListener callListener = new IRCSReceivedCallListener() {
            @Override
            public void onReceivedCall(final RCSCallSession callSession) {
                RLog.d(TAG, "onReceivedCall");
                RCSCallClient.getInstance().startVoIPActivity(MainActivity.this, callSession, true);
            }
        };
        RCSCallClient.getInstance().setReceivedCallListener(callListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.seal_more:
                MorePopWindow morePopWindow = new MorePopWindow(MainActivity.this);
                morePopWindow.setOptionClickRunnable(startMultiRTCDialog);
                morePopWindow.showPopupWindow(moreImage);
                break;
            default:
                break;
        }
    }

    public Runnable startMultiRTCDialog = new Runnable() {
        @Override
        public void run() {
            if (MorePopWindow.MediaType == MediaType.AUDIO) {
                starSelectForResult(REQUEST_CODE_MULTI_AUDIO);
            } else if (MorePopWindow.MediaType == MediaType.VIDEO) {
                starSelectForResult(REQUEST_CODE_MULTI_VIDEO);
            }
        }
    };

    private void starSelectForResult(int requestCode) {
        Intent intent = new Intent(this, SelectUserActivity.class);
        intent.putStringArrayListExtra(SelectUserActivity.ALL_USERS, getOtherIds());
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            ArrayList<String> invited = data.getStringArrayListExtra(SelectUserActivity.SELECTED_USERS);
            if (invited == null || invited.size() <= 0)
                return;
            switch (requestCode) {
                case REQUEST_CODE_SINGLE_AUDIO:
                    RCSCall.startSingleCall(this, invited.get(0), RCSCallCommon.CallMediaType.AUDIO);
                    break;
                case REQUEST_CODE_SINGLE_VIDEO:
                    RCSCall.startSingleCall(this, invited.get(0), RCSCallCommon.CallMediaType.VIDEO);
                    break;
                case REQUEST_CODE_MULTI_AUDIO:
                    RCSCall.startMultiCall(this, invited, RCSCallCommon.CallMediaType.AUDIO);
                    break;
                case REQUEST_CODE_MULTI_VIDEO:
                    RCSCall.startMultiCall(this, invited, RCSCallCommon.CallMediaType.VIDEO);
                    break;
            }
        }
    }

    /**
     * @return 测试的其他的用户Id
     */
    private ArrayList<String> getOtherIds() {
        if (TextUtils.isEmpty(userId)) {
            return null;
        }
        ArrayList<String> userIdList = new ArrayList<>();
        for (UserProfile id : LoginActivity.getUserTokens()) {
            if (!userId.equals(id.getUserId())) {
                userIdList.add(id.getUserId());
            }
        }
        return userIdList;
    }

    private List<UserProfile> getOtherContacts() {
        if (TextUtils.isEmpty(userId)) {
            return null;
        }
        List<UserProfile> userIdList = new ArrayList<>();
        for (UserProfile id : LoginActivity.getUserTokens()) {
            if (!userId.equals(id.getUserId())) {
                userIdList.add(id);
            }
        }
        return userIdList;
    }


    private class ContactListAdapter extends RecyclerView.Adapter<ContactViewHolder> implements View.OnClickListener {
        private List<UserProfile> data;
        private UserProfile selected;

        public ContactListAdapter(List<UserProfile> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            View view = inflater.inflate(R.layout.contact_list_item, viewGroup, false);
            return new MainActivity.ContactViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ContactViewHolder contactViewHolder, int i) {
            String userId = data.get(i).getUserId();
            contactViewHolder.contactIdTextView.setText(userId);
            contactViewHolder.itemView.setTag(userId);
            contactViewHolder.singleAudioButton.setTag(userId);
            contactViewHolder.singleVideoButton.setTag(userId);
            contactViewHolder.singleAudioButton.setOnClickListener(this);
            contactViewHolder.singleVideoButton.setOnClickListener(this);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }


        @Override
        public void onClick(View v) {
            String userId = (String) v.getTag();
            if (TextUtils.isEmpty(userId))
                return;

            if (v.getId() == R.id.single_audio_button) {
                RCSCall.startSingleCall(MainActivity.this, userId, RCSCallCommon.CallMediaType.AUDIO);

            } else if (v.getId() == R.id.single_video_button) {
                RCSCall.startSingleCall(MainActivity.this, userId, RCSCallCommon.CallMediaType.VIDEO);
            }
        }
    }

    private class ContactViewHolder extends RecyclerView.ViewHolder {
        private TextView contactIdTextView;
        private Button singleAudioButton;
        private Button singleVideoButton;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            this.contactIdTextView = itemView.findViewById(R.id.contact_id_text_view);
            this.singleAudioButton = itemView.findViewById(R.id.single_audio_button);
            this.singleVideoButton = itemView.findViewById(R.id.single_video_button);
        }
    }
}
