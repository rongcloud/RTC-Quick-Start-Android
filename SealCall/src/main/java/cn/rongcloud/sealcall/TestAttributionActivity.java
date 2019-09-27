package cn.rongcloud.sealcall;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.rong.imlib.RongIMClient;
import io.rong.signal.core.RCSignalingClient;
import io.rong.signal.enums.RCSignalingType;
import io.rong.signal.infos.RCSignalingChannelAttributeChangedNotification;
import io.rong.signal.infos.RCSignalingMemberAttributeChangedNotification;
import io.rong.signal.core.RCSignalingChannelInfo;
import io.rong.signal.infos.RCSignalingChannelMember;
import io.rong.signal.infos.RCSignalingEventInfo;
import io.rong.signal.interfaces.RCSignalingResultCallback;
import io.rong.signal.interfaces.RCSignalingChannelQueryResult;
import io.rong.signal.interfaces.RCSignalingMemberQueryResult;
import io.rong.signal.interfaces.RCSignalingEventObserver;

/**
 * 房间属性测试
 */
public class TestAttributionActivity extends Activity implements View.OnClickListener, RCSignalingEventObserver {
    private static final String TAG = "TestAttributionActivity";
    private String roomId = "attributeRoom";
    private int i;
    private Button create;
    private Button join;
    private Button leave;
    private Button setRoomData;
    private Button getRoomData;
    private Button deleteRoomData;
    private Button setMemberData;
    private Button getMemberData;
    private Button deleteMemberData;
    private TextView roomAttribute;
    private TextView userAttribute;
    private EditText roomIdET;
    private int r = 0;
    private int u = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_attribute);
        roomIdET = findViewById(R.id.roomId);
        create = findViewById(R.id.create_room);
        join = findViewById(R.id.join_room);
        leave = findViewById(R.id.leave_room);
        setRoomData = findViewById(R.id.set_room_data);
        getRoomData = findViewById(R.id.get_room_data);
        deleteRoomData = findViewById(R.id.delete_room_data);
        setMemberData = findViewById(R.id.set_member_data);
        getMemberData = findViewById(R.id.get_member_data);
        deleteMemberData = findViewById(R.id.delete_member_data);
        roomAttribute = findViewById(R.id.room_attribute);
        userAttribute = findViewById(R.id.user_attribute);
        create.setOnClickListener(this);
        join.setOnClickListener(this);
        leave.setOnClickListener(this);
        setRoomData.setOnClickListener(this);
        getRoomData.setOnClickListener(this);
        deleteRoomData.setOnClickListener(this);
        setMemberData.setOnClickListener(this);
        getMemberData.setOnClickListener(this);
        deleteMemberData.setOnClickListener(this);
        RCSignalingClient.getInstance().addEventObserver(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_room:
                roomId = roomIdET.getText().toString();
                RCSignalingClient.getInstance().joinChannel(roomId, new RCSignalingResultCallback() {
                    @Override
                    public void onSuccess() {
                        updateRoomAttribute();
                        Toast.makeText(TestAttributionActivity.this, "创建成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(int errorCode) {
                        Toast.makeText(TestAttributionActivity.this, "创建失败", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.join_room:
                roomId = roomIdET.getText().toString();
                RCSignalingClient.getInstance().joinChannel(roomId, new RCSignalingResultCallback() {
                    @Override
                    public void onSuccess() {
                        updateRoomAttribute();
                        Toast.makeText(TestAttributionActivity.this, "加入成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(int errorCode) {
                        Toast.makeText(TestAttributionActivity.this, "加入失败", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.leave_room:
                r = 0;
                u = 0;
                roomId = roomIdET.getText().toString();
                RCSignalingClient.getInstance().leaveChannel(roomId, new RCSignalingResultCallback() {
                    @Override
                    public void onSuccess() {
                        updateRoomAttribute();
                        Toast.makeText(TestAttributionActivity.this, "离开成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(int errorCode) {
                        Toast.makeText(TestAttributionActivity.this, "离开失败", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.set_room_data:
                HashMap<String, String> attributeSet = new HashMap<>();
                ++r;
                attributeSet.put("setKey " + RongIMClient.getInstance().getCurrentUserId() + r, "setValue " + r);
                Log.i(TAG, "setRoom r = " + r);
                RCSignalingClient.getInstance().setChannelAttributes(attributeSet, true, new RCSignalingResultCallback() {

                    @Override
                    public void onSuccess() {
                        updateRoomAttribute();
                        Toast.makeText(TestAttributionActivity.this, "设置房间属性成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(int errorCode) {
                        Toast.makeText(TestAttributionActivity.this, "设置房间属性失败", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.get_room_data:
                //todo
                RCSignalingClient.getInstance().getChannelAttributes(new ArrayList<String>(), new RCSignalingChannelQueryResult() {

                    @Override
                    public void onSuccess(Map<String, String> channelAttributes) {
                        updateRoomAttribute();
                        Toast.makeText(TestAttributionActivity.this, "获取房间属性成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(int errorCode) {
                        Toast.makeText(TestAttributionActivity.this, "获取房间属性失败", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.delete_room_data:
                List<String> deletes = new ArrayList<>();
                if (!TextUtils.isEmpty(roomId)) {
                    RCSignalingChannelInfo channelInfo = RCSignalingClient.getInstance().getChannelInfo();
                    if (channelInfo != null) {
                        Map<String, String> map = channelInfo.getChannelAttribute();
                        if (map != null && !map.isEmpty()) {
                            String key = new ArrayList<>(map.keySet()).get(0);
                            deletes.add(key);
                        }
                    }
                }
                if (deletes.isEmpty()) {
                    Toast.makeText(TestAttributionActivity.this, "已经没有房间属性存在", Toast.LENGTH_SHORT).show();
                    return;
                }
                RCSignalingClient.getInstance().deleteChannelAttributes(deletes, true, new RCSignalingResultCallback() {

                    @Override
                    public void onSuccess() {
                        updateRoomAttribute();
                        Toast.makeText(TestAttributionActivity.this, "删除房间属性成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(int errorCode) {
                        Toast.makeText(TestAttributionActivity.this, "删除房间属性成功", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.set_member_data:
                HashMap<String, String> myAttribute = new HashMap<>();
                ++u;
                myAttribute.put("memberKey " + u, "memeberValue " + u);
                Log.i(TAG, "setMember u = " + u);
                RCSignalingClient.getInstance().setMemberAttributes(myAttribute, true, new RCSignalingResultCallback() {
                    @Override
                    public void onSuccess() {
                        updateRoomAttribute();
                        Toast.makeText(TestAttributionActivity.this, "删除自己属性成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(int errorCode) {
                        Toast.makeText(TestAttributionActivity.this, "删除自己属性成功", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.get_member_data:
                List<String> userIds = new ArrayList<>(RCSignalingClient.getInstance().getChannelInfo().getMembersAttributeMap().keySet());
                RCSignalingClient.getInstance().getMembersAttributes(userIds, new RCSignalingMemberQueryResult() {

                    @Override
                    public void onSuccess(List<RCSignalingChannelMember> membersAttributes) {
                        updateRoomAttribute();
                        Toast.makeText(TestAttributionActivity.this, "获取用户属性成功", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailed(int errorCode) {
                        Toast.makeText(TestAttributionActivity.this, "获取用户属性失败", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.delete_member_data:
                List<String> memberDeletes = new ArrayList<>();
                memberDeletes.add("memberKey " + u--);
                if (u == -1) {
                    u = 0;
                }
                Log.i(TAG, "deleteRoom r = " + u);
                RCSignalingClient.getInstance().deleteMemberAttributes(memberDeletes, true, new RCSignalingResultCallback() {

                    @Override
                    public void onSuccess() {
                        updateRoomAttribute();
                        Toast.makeText(TestAttributionActivity.this, "删除用户属性成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(int errorCode) {
                        Toast.makeText(TestAttributionActivity.this, "删除用户属性失败", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    public void onReceiveSignalingAction(RCSignalingEventInfo info) {
        //收到信令消息
        Log.i(TAG, "onReceiveSignalingAction info " + info);
        if (info.getSignalingType() == RCSignalingType.LEAVE || info.getSignalingType() == RCSignalingType.JOIN) {
            updateRoomAttribute();
        }
    }

    @Override
    public void onReceiveChannelAttributeChanged(RCSignalingChannelAttributeChangedNotification notification) {
        Log.i(TAG, "onReceiveChannelAttributeChanged notification " + notification);
        updateRoomAttribute();
    }

    @Override
    public void onReceiveMemberAttributeChanged(RCSignalingMemberAttributeChangedNotification notification) {
        Log.i(TAG, "onReceiveMemberAttributeChanged notification " + notification);
        updateRoomAttribute();
    }

    @Override
    public void onConnectStatusChanged(RongIMClient.ConnectionStatusListener.ConnectionStatus status) {

    }


    private void updateRoomAttribute() {
        RCSignalingChannelInfo roomAttributeInfo = RCSignalingClient.getInstance().getChannelInfo();
        if (roomAttributeInfo == null) {
            roomAttribute.setText("");
            userAttribute.setText("");
        } else {
            Map<String, String> room = roomAttributeInfo.getChannelAttribute();
            Map<String, RCSignalingChannelMember> membersAttributeMap = roomAttributeInfo.getMembersAttributeMap();
            roomAttribute.setText("roomId: " + roomAttributeInfo.getChannelId() + "\n" + getMapString(room));
            userAttribute.setText("users:\n" + getUserListString(membersAttributeMap));
        }
    }

    private String getUserListString(Map<String, RCSignalingChannelMember> memberAttributeMap) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, RCSignalingChannelMember> entry : memberAttributeMap.entrySet()) {
            builder.append("userId: " + entry.getKey() + "\n")
                    .append(getMapString(entry.getValue().getAttribute())).append('\n');
        }
        return builder.toString();
    }

    private String getMapString(Map<String, String> map) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.append(entry.getKey() + ": " + entry.getValue() + "\n");
        }
        return builder.toString();
    }
}
