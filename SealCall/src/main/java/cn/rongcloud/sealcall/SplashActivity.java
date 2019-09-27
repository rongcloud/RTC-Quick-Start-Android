package cn.rongcloud.sealcall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import static cn.rongcloud.sealcall.LoginActivity.KEY_TEST_MODEL;
import static cn.rongcloud.sealcall.LoginActivity.MODEL_ATTRIBUTE;
import static cn.rongcloud.sealcall.LoginActivity.MODEL_VOIP;

public class SplashActivity extends Activity implements View.OnClickListener {
    private Button startVoipBtn;
    private Button startAttributeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        startVoipBtn = findViewById(R.id.startVoip);
        startAttributeBtn = findViewById(R.id.startAttribute);
        startVoipBtn.setOnClickListener(this);
        startAttributeBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        switch (v.getId()) {
            case R.id.startVoip:
                intent.putExtra(KEY_TEST_MODEL, MODEL_VOIP);
                break;
            case R.id.startAttribute:
                intent.putExtra(KEY_TEST_MODEL, MODEL_ATTRIBUTE);
                break;
            default:
                break;
        }
        startActivity(intent);
    }
}
