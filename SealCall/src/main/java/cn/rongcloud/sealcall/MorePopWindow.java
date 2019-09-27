package cn.rongcloud.sealcall;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import cn.rongcloud.rtc.stream.MediaType;


public class MorePopWindow extends PopupWindow {
    public static cn.rongcloud.rtc.stream.MediaType MediaType;
    private Runnable optionClickRunnable;

    @SuppressLint("InflateParams")
    public MorePopWindow(final Activity context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View content = inflater.inflate(R.layout.popupwindow_add, null);

        // 设置SelectPicPopupWindow的View
        this.setContentView(content);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);

        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimationPreview);


        RelativeLayout multi_audio = content.findViewById(R.id.multi_audio);
        multi_audio.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaType = MediaType.AUDIO;
                MorePopWindow.this.dismiss();
                runOptionClick();
            }
        });

        RelativeLayout multi_video = content.findViewById(R.id.multi_video);
        multi_video.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaType = MediaType.VIDEO;
                MorePopWindow.this.dismiss();
                runOptionClick();
            }
        });
    }

    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAsDropDown(parent, 0, 0);
        } else {
            this.dismiss();
        }
    }

    public void setOptionClickRunnable(Runnable runnable) {
        this.optionClickRunnable = runnable;
    }

    private void runOptionClick() {
        if (this.optionClickRunnable != null) {
            this.optionClickRunnable.run();
        }
    }
}
