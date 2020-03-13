package com.rtcdemo;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.rongcloud.rtc.RTCErrorCode;
import cn.rongcloud.rtc.RongRTCEngine;
import cn.rongcloud.rtc.callback.JoinRoomUICallBack;
import cn.rongcloud.rtc.callback.RongRTCResultUICallBack;
import cn.rongcloud.rtc.core.RendererCommon;
import cn.rongcloud.rtc.engine.view.RongRTCVideoView;
import cn.rongcloud.rtc.events.RongRTCEventsListener;
import cn.rongcloud.rtc.room.RongRTCRoom;
import cn.rongcloud.rtc.stream.MediaType;
import cn.rongcloud.rtc.stream.local.RongRTCCapture;
import cn.rongcloud.rtc.stream.remote.RongRTCAVInputStream;
import cn.rongcloud.rtc.user.RongRTCLocalUser;
import cn.rongcloud.rtc.user.RongRTCRemoteUser;
import cn.rongcloud.rtc.utils.FinLog;
import io.rong.imlib.model.Message;

public class MainActivity extends Activity implements RongRTCEventsListener, View.OnClickListener {
    private static final String TAG = "MainActivity";
    private RongRTCVideoView localVideoView;
    private LinearLayout remoteContainer;
    private String mRoomId = "quickStartDemoRoom";
    private RongRTCRoom mRongRTCRoom;
    private RongRTCLocalUser mLocalUser;
    private Button button;
    private RelativeLayout localContainer;
    private TextView exchangeView;
    /**
     * 记录点击切换屏幕方向时应使用的值。
     */
    int orientationValue = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        initView();
        joinRoom();
    }

    private void initView() {
        localVideoView = RongRTCEngine.getInstance().createVideoView(this);
        //设置视频的填充模式：SCALE_ASPECT_FIT - 代表显示完整视频，但是会留有黑边 ； 默认是 SCALE_ASPECT_FILL - 铺满父控件，裁剪超出边界部分
        localVideoView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        localVideoView.setOnClickListener(this);
        localContainer = (RelativeLayout) findViewById(R.id.local_container);
        addToLocalContainer(localVideoView);


        remoteContainer = (LinearLayout) findViewById(R.id.remotes);
        button = (Button) findViewById(R.id.finish);
        button.setVisibility(View.GONE);
        button.setOnClickListener(this);
        exchangeView = (TextView) findViewById(R.id.exchange_orientation);
        exchangeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRequestedOrientation(orientationValue);
            }
        });
    }

    private void addToLocalContainer(RongRTCVideoView videoView) {
        RelativeLayout.LayoutParams bigParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //设置控件居中显示
        bigParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        //设置视频视图可以被上层其他视频覆盖
        videoView.setZOrderMediaOverlay(false);
        localContainer.removeAllViews();
        localContainer.addView(videoView, bigParams);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 加入横屏要处理的代码
            orientationValue = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // 加入竖屏要处理的代码
            orientationValue = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        }
    }

    /**
     * 加入房间
     */
    private void joinRoom() {
        RongRTCEngine.getInstance().joinRoom(mRoomId, new JoinRoomUICallBack() {
            @Override
            protected void onUiSuccess(RongRTCRoom rongRTCRoom) {
                Toast.makeText(MainActivity.this, "加入房间成功", Toast.LENGTH_SHORT).show();
                mRongRTCRoom = rongRTCRoom;
                mLocalUser = rongRTCRoom.getLocalUser();
                RongRTCCapture.getInstance().setRongRTCVideoView(localVideoView); //设置本地预览视图
                RongRTCCapture.getInstance().startCameraCapture();       //开始采集数据
                setEventListener();                                      //设置监听
                addRemoteUsersView();
                subscribeAll();                                          //订阅资源
                publishDefaultStream();                                  //发布资源
            }

            @Override
            protected void onUiFailed(RTCErrorCode rtcErrorCode) {
                Toast.makeText(MainActivity.this, "加入房间失败 rtcErrorCode：" + rtcErrorCode, Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * 注册监听
     */
    private void setEventListener() {
        if (mRongRTCRoom != null) {
            mRongRTCRoom.registerEventsListener(this);
        }
    }

    private void removeListener() {
        if (mRongRTCRoom != null) {
            mRongRTCRoom.unRegisterEventsListener(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeListener();
        RongRTCEngine.getInstance().quitRoom(mRoomId, new RongRTCResultUICallBack() {
            @Override
            public void onUiSuccess() {
                Toast.makeText(MainActivity.this, "离开房间成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUiFailed(RTCErrorCode rtcErrorCode) {
                Toast.makeText(MainActivity.this, "离开房间失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 添加远端用户View
     */
    private void addRemoteUsersView() {
        if (mRongRTCRoom != null) {
            for (RongRTCRemoteUser remoteUser : mRongRTCRoom.getRemoteUsers().values()) {
                for (RongRTCAVInputStream inputStream : remoteUser.getRemoteAVStreams()) {
                    if (inputStream.getMediaType() == MediaType.VIDEO) {
                        inputStream.setRongRTCVideoView(getNewVideoView());
                    }
                }
            }
        }
    }

    /**
     * 订阅所有当前在房间发布资源的用户
     */
    private void subscribeAll() {
        if (mRongRTCRoom != null) {
            for (RongRTCRemoteUser remoteUser : mRongRTCRoom.getRemoteUsers().values()) {
                remoteUser.subscribeAVStream(remoteUser.getRemoteAVStreams(), new RongRTCResultUICallBack() {
                    @Override
                    public void onUiSuccess() {
                        Toast.makeText(MainActivity.this, "订阅资源成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onUiFailed(RTCErrorCode rtcErrorCode) {
                        Toast.makeText(MainActivity.this, "订阅资源成功", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }
    }

    /**
     * 发布资源
     */
    private void publishDefaultStream() {
        if (mLocalUser != null) {
            mLocalUser.publishDefaultAVStream(new RongRTCResultUICallBack() {
                @Override
                public void onUiSuccess() {
                    Toast.makeText(MainActivity.this, "发布资源成功", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onUiFailed(RTCErrorCode rtcErrorCode) {
                    Toast.makeText(MainActivity.this, "发布资源失败", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private RongRTCVideoView getNewVideoView() {
        Log.i(TAG, "getNewVideoView()");
        RongRTCVideoView videoView = RongRTCEngine.getInstance().createVideoView(this);
        videoView.setOnClickListener(this);
        //设置视频显示完整内容
        videoView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        videoView.setZOrderMediaOverlay(true);
        videoView.setZOrderOnTop(true);
        remoteContainer.addView(videoView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return videoView;
    }


    @Override
    public void onRemoteUserPublishResource(RongRTCRemoteUser rongRTCRemoteUser, List<RongRTCAVInputStream> list) {
        for (RongRTCAVInputStream inputStream : rongRTCRemoteUser.getRemoteAVStreams()) {
            if (inputStream.getMediaType() == MediaType.VIDEO) {
                inputStream.setRongRTCVideoView(getNewVideoView());
            }
        }
        rongRTCRemoteUser.subscribeAVStream(rongRTCRemoteUser.getRemoteAVStreams(), new RongRTCResultUICallBack() {
            @Override
            public void onUiSuccess() {
                Toast.makeText(MainActivity.this, "订阅成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUiFailed(RTCErrorCode rtcErrorCode) {
                Toast.makeText(MainActivity.this, "订阅失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRemoteUserAudioStreamMute(RongRTCRemoteUser rongRTCRemoteUser, RongRTCAVInputStream rongRTCAVInputStream, boolean b) {

    }

    @Override
    public void onRemoteUserVideoStreamEnabled(RongRTCRemoteUser rongRTCRemoteUser, RongRTCAVInputStream rongRTCAVInputStream, boolean b) {

    }

    @Override
    public void onRemoteUserUnpublishResource(RongRTCRemoteUser rongRTCRemoteUser, List<RongRTCAVInputStream> list) {

    }

    @Override
    public void onUserJoined(RongRTCRemoteUser rongRTCRemoteUser) {

    }

    @Override
    public void onUserLeft(RongRTCRemoteUser rongRTCRemoteUser) {
        for (RongRTCAVInputStream inputStream : rongRTCRemoteUser.getRemoteAVStreams()) {
            if (inputStream.getMediaType() == MediaType.VIDEO) {
                remoteContainer.removeView(inputStream.getRongRTCVideoView());
            }
        }
    }

    @Override
    public void onUserOffline(RongRTCRemoteUser rongRTCRemoteUser) {

    }

    @Override
    public void onVideoTrackAdd(String s, String s1) {

    }

    @Override
    public void onFirstFrameDraw(String s, String s1) {

    }

    @Override
    public void onLeaveRoom() {

    }

    @Override
    public void onReceiveMessage(Message message) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.finish) {
            quit();
            finish();
        } else if (v instanceof RongRTCVideoView) {
            int index = -1;
            for (int i = 0; i < remoteContainer.getChildCount(); i++) {
                RongRTCVideoView videoView = (RongRTCVideoView) remoteContainer.getChildAt(i);
                FinLog.e(TAG,"touched = " +v+" :  "+videoView);
                if (videoView == v) {
                    index = i;
                }
            }
            if (index != -1) {
                RongRTCVideoView big = (RongRTCVideoView) localContainer.getChildAt(0);
                localContainer.removeAllViews();
                remoteContainer.addView(big, index, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                remoteContainer.removeView(v);
                addToLocalContainer((RongRTCVideoView) v);
            }
        }
    }

    @Override
    public void onBackPressed() {
        quit();
        super.onBackPressed();

    }

    private void quit() {
        RongRTCEngine.getInstance().quitRoom(mRongRTCRoom.getRoomId(), new RongRTCResultUICallBack() {
            @Override
            public void onUiSuccess() {
                Toast.makeText(MainActivity.this, "离开房间成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUiFailed(RTCErrorCode rtcErrorCode) {
                Toast.makeText(MainActivity.this, "离开房间失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
