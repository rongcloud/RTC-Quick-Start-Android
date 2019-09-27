package cn.rongcloud.sealcall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.rongcloud.rtc.media.http.HttpClient;
import cn.rongcloud.rtc.media.http.Request;
import cn.rongcloud.rtc.media.http.RequestMethod;
import cn.rongcloud.rtc.utils.FinLog;
import io.rong.imlib.RongIMClient;

public class LoginActivity extends Activity {
    private static final String TAG = "LoginActivity";
    public static final String KEY_TEST_MODEL = "key_test_model";
    public static final String MODEL_VOIP = "model_voip";
    public static final String MODEL_ATTRIBUTE = "model_attribute";
    private String testModel = MODEL_VOIP;
    private Button confirm;
    private RecyclerView recyclerView;
    private static UserProfile currentUser;
    private static List<UserProfile> userTokens;

    CountDownTimer timer;

    public static UserProfile getCurrentUser() {
        return currentUser;
    }

    public static List<UserProfile> getUserTokens() {
        return userTokens;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        testModel = getIntent().getStringExtra(KEY_TEST_MODEL);
        userTokens = buildUserTokenList();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        confirm = findViewById(R.id.loginBtn);

        final UserTokenListAdapter adapter = new UserTokenListAdapter(userTokens);
        recyclerView.setAdapter(adapter);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter.getSelectedUserToken() == null) {
                    return;
                }
                confirm.setEnabled(false);
                currentUser = adapter.getSelectedUserToken();
                getTokenForXQ();
            }
        });
    }

    private List<UserProfile> buildUserTokenList() {
        List<UserProfile> userTokens = new ArrayList<>();

        for (int i = 1; i <= 35; i++) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("SealCall_User");
            buffer.append(String.format("%02d", i));

            userTokens.add(new UserProfile(buffer.toString()));
        }
        return userTokens;
    }

    private void login(String token) {
        RongIMClient.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        confirm.setEnabled(true);
                        Toast.makeText(LoginActivity.this, "onTokenIncorrect", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onSuccess(final String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "onSuccess " + s);
                        Toast.makeText(LoginActivity.this, "onSuccess", Toast.LENGTH_SHORT).show();
                        if (TextUtils.isEmpty(testModel)) {
                            return;
                        }
                        if (testModel.equals(MODEL_ATTRIBUTE)) {
                            Intent intent = new Intent(LoginActivity.this, TestAttributionActivity.class);
                            startActivity(intent);
                        } else if (testModel.equals(MODEL_VOIP)) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("userId", s);
                            startActivity(intent);
                        }
                        finish();
                    }
                });
            }

            @Override
            public void onError(final RongIMClient.ErrorCode e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        confirm.setEnabled(true);
                        Toast.makeText(LoginActivity.this, "onError " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    private void getTokenForXQ() {
        String userName = getRandomString();

        StringBuilder params = new StringBuilder();
        params.append("userId=")
                .append(currentUser.getUserId())
                .append("&")
                .append("name=")
                .append(userName);
        long timestamp = System.currentTimeMillis();
        int nonce = (int) (Math.random() * 10000);
        String signature = "";
        try {
            signature = sha1(AppConfigure.DEMO_API_SECRET + nonce + timestamp);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        FinLog.d(TAG, "DEMO_SERVER: " + AppConfigure.DEMO_SERVER + " ,  " + "signature :" + signature + " ,  params : " + params.toString());
        Request request = new Request.Builder()
                .url(AppConfigure.DEMO_SERVER + "/user/getToken.json")
                .method(RequestMethod.POST)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Timestamp", String.valueOf(timestamp))
                .addHeader("Nonce", String.valueOf(nonce))
                .addHeader("Signature", signature)
                .addHeader("App-Key", AppConfigure.APP_KEY)
                .body(params.toString())
                .build();

        HttpClient.getDefault().request(request, new HttpClient.ResultCallback() {
            @Override
            public void onResponse(String result) {
                FinLog.d(TAG, "result :" + result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.optInt("code") == 200) {
                        Log.e(TAG, "userTokens.add(\n" +
                                "                new UserProfile(\"" + jsonObject.optString("userId") + "\", \"" + jsonObject.optString("token") + "\"));");
                        currentUser.setToken(jsonObject.optString("token"));
                        login(currentUser.getToken());
                    } else {
                        Log.e(TAG, "code not 200, code=" + jsonObject.optInt("code"));
                    }
                } catch (JSONException e) {
                }
            }


            @Override
            public void onFailure(int errorCode) {
                FinLog.e(TAG, "errorCode :" + errorCode);
                Toast.makeText(LoginActivity.this, "Error: " + errorCode, Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onError(IOException exception) {
                Log.e(TAG, "onError: " + exception.getMessage());
            }
        });
    }

    public static String sha1(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        md.update(data.getBytes());
        StringBuffer buf = new StringBuffer();
        byte[] bits = md.digest();
        for (int i = 0; i < bits.length; i++) {
            int a = bits[i];
            if (a < 0) a += 256;
            if (a < 16) buf.append("0");
            buf.append(Integer.toHexString(a));
        }
        return buf.toString();
    }

    private String getRandomString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        String generatedString = buffer.toString();

        return generatedString;
    }

    private class UserTokenListAdapter extends RecyclerView.Adapter<UserTokenViewHolder> implements View.OnClickListener {
        private List<UserProfile> data;
        private UserProfile selectedUserToken;

        public UserTokenListAdapter(List<UserProfile> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public UserTokenViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            View view = inflater.inflate(R.layout.user_token_item, viewGroup, false);
            return new UserTokenViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull UserTokenViewHolder baseViewHolder, int i) {
            String userId = data.get(i).getUserId();

            baseViewHolder.radioButton.setChecked(selectedUserToken == null ? false : TextUtils.equals(userId, selectedUserToken.getUserId()));
            baseViewHolder.textView.setText(userId);
            baseViewHolder.itemView.setTag(userId);
            baseViewHolder.itemView.setOnClickListener(this);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @Override
        public void onClick(View v) {
            String userId = (String) v.getTag();

            for (UserProfile token : this.data) {
                if (TextUtils.equals(token.getUserId(), userId)) {
                    this.selectedUserToken = token;
                }
            }
            notifyDataSetChanged();
        }

        public UserProfile getSelectedUserToken() {
            return selectedUserToken;
        }
    }

    private class UserTokenViewHolder extends RecyclerView.ViewHolder {
        private RadioButton radioButton;
        private TextView textView;

        public UserTokenViewHolder(@NonNull View itemView) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.checkbox);
            textView = itemView.findViewById(R.id.user_id);
        }
    }
}