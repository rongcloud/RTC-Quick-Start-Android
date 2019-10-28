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
                login(currentUser.getToken());
            }
        });
    }

    private List<UserProfile> buildUserTokenList() {
        List<UserProfile> userTokens = new ArrayList<>();

        userTokens.add(new UserProfile("SealCall_User01", "zifPhe4H3kwW4kksjL6p8UmcbyeYIrXSDa0nFvL2mH+ioB4/kU0OQmBRJUJN4DuSdpLdd8oZ2ggxflFdnDVDcUItg9kNrDgk"));
        userTokens.add(new UserProfile("SealCall_User02", "0zybiCjDVXnVJS42kF6o+c2yq+hfEluLjZ78E1qo4hFOWaZ199HaevLWjNaW0C7UdLNuv0zeq4ZYOmxputWQlXICSM+OO8m59N268qONaaM="));
        userTokens.add(new UserProfile("SealCall_User03", "u9XrzvWiGx6YL9Z2LrnG1LI6ZiT8q7s0UEaMPWY0lMzC3IAa8p6ZGUByjr9N8XReQ4tesdb9WTV4YE4Jvw9gLIBH3YVQJcRG5E0rL7nxYFQ="));
        userTokens.add(new UserProfile("SealCall_User04", "cxGrEItU0/o05EVQ4TksrbI6ZiT8q7s0UEaMPWY0lMzC3IAa8p6ZGUB30f9QwgKLGar5wmyp2Ax4YE4Jvw9gLKuH+T9ERAgK5E0rL7nxYFQ="));
        userTokens.add(new UserProfile("SealCall_User05", "xkDmw5buYilfiVhKxcKFKrI6ZiT8q7s0UEaMPWY0lMzC3IAa8p6ZGe6CFAWSk0/4AjOhMkg8m9Z4YE4Jvw9gLEgt0kcNxsa85E0rL7nxYFQ="));
        userTokens.add(new UserProfile("SealCall_User06", "9LY/deNjc0da8zN6m3RQFV4PgRGJ4lryRR/tEnSS1d3lkVz6ITObLgh8DhsCJN2DbLFcrLheElfwIheWLY3KWhIWi6PV/g/N"));
        userTokens.add(new UserProfile("SealCall_User07", "Guuv2wlSJXAU9CzqqulrUUmcbyeYIrXSDa0nFvL2mH+ioB4/kU0OQnb4OnccO+5bdpLdd8oZ2ggxflFdnDVDcVhanTqGr2i/"));
        userTokens.add(new UserProfile("SealCall_User08", "we9sctbIk7kO82btUL9+DM2yq+hfEluLjZ78E1qo4hFOWaZ199HaekyTr2s2+1UgNE+iOCOEEplYOmxputWQlXICSM+OO8m5IdPdKvjObUY="));
        userTokens.add(new UserProfile("SealCall_User09", "M29OWaCY4fVZFfDS5XHfILI6ZiT8q7s0UEaMPWY0lMzC3IAa8p6ZGdtKbV2NkhpsQ4tesdb9WTV4YE4Jvw9gLCEAuq4F/KPH5E0rL7nxYFQ="));
        userTokens.add(new UserProfile("SealCall_User10", "ZUsQAsJFVYpmohiybibkaUmcbyeYIrXSDa0nFvL2mH+ioB4/kU0OQmNtaKUhAMAVdpLdd8oZ2ggxflFdnDVDcTblkoqlCoIc"));
        userTokens.add(new UserProfile("SealCall_User11", "wDiG1jcALrPLOvvQz+wHErI6ZiT8q7s0UEaMPWY0lMzC3IAa8p6ZGSNM7We1SqtLJ6HdCJ0y3gp4YE4Jvw9gLO/JJASb2TY55E0rL7nxYFQ="));
        userTokens.add(new UserProfile("SealCall_User12", "ReM5ssIK6w9fiVhKxcKFKrI6ZiT8q7s0UEaMPWY0lMzC3IAa8p6ZGQVyHRx8zO/Gq58lwVEXC3Z4YE4Jvw9gLAuSv3/4twXf5E0rL7nxYFQ="));
        userTokens.add(new UserProfile("SealCall_User13", "wldCq8jIjrC2uxb/fkc1XEmcbyeYIrXSDa0nFvL2mH+ioB4/kU0OQiuljvwG3WCedpLdd8oZ2ggxflFdnDVDcVaN6otFzJ4I"));
        userTokens.add(new UserProfile("SealCall_User14", "w8bt9iT1JoFCbq0vvoICurI6ZiT8q7s0UEaMPWY0lMzC3IAa8p6ZGUir9WzG+1ir71jLn1LwskB4YE4Jvw9gLGSCU0R1a2775E0rL7nxYFQ="));
        userTokens.add(new UserProfile("SealCall_User15", "Umh0eQ/pofe56E74q3SQX7I6ZiT8q7s0UEaMPWY0lMzC3IAa8p6ZGWDv/v3ROUFM71jLn1LwskB4YE4Jvw9gLNat93v3ZkW55E0rL7nxYFQ="));
        userTokens.add(new UserProfile("SealCall_User16", "fgfeGm+ez/fZD9nJVm3sxLI6ZiT8q7s0UEaMPWY0lMzC3IAa8p6ZGZ0LhvqD+WMxJ6HdCJ0y3gp4YE4Jvw9gLNm76uPk1Evs5E0rL7nxYFQ="));
        userTokens.add(new UserProfile("SealCall_User17", "gZLgxWxKqRda8zN6m3RQFV4PgRGJ4lryRR/tEnSS1d3lkVz6ITObLklmK1dK35r7bLFcrLheElfwIheWLY3KWkTJamA6IAw4"));
        userTokens.add(new UserProfile("SealCall_User18", "1fDkf3c8v84p5gMLroYTpc2yq+hfEluLjZ78E1qo4hFOWaZ199Haesg51gmd8Arqz+joFGhyqf1YOmxputWQldYgRpmAzKkOIdPdKvjObUY="));
        userTokens.add(new UserProfile("SealCall_User19", "pOfcSOPVRHjoRtOxrVAL+c2yq+hfEluLjZ78E1qo4hFOWaZ199HaepbN+NDes/GpsivS+FIA1J1YOmxputWQldYgRpmAzKkO9OPuDeq6p4Q="));
        userTokens.add(new UserProfile("SealCall_User20", "xlPe0UGRsWzmlmiKnou6sc2yq+hfEluLjZ78E1qo4hFOWaZ199HaegSDEVtWhn5wG6gfhDNUAFpYOmxputWQlUU6lrwKeXPnDXrQANyidZ4="));
        userTokens.add(new UserProfile("SealCall_User21", "xU5vFrV1Vzbiynp+isQwErI6ZiT8q7s0UEaMPWY0lMzC3IAa8p6ZGT1MJUrScrwyGar5wmyp2Ax4YE4Jvw9gLCBDrE7E2e/L5E0rL7nxYFQ="));
        userTokens.add(new UserProfile("SealCall_User22", "p1duxWykuPqMOfDFixtV80mcbyeYIrXSDa0nFvL2mH+ioB4/kU0OQihMEKXY39DOdpLdd8oZ2ggxflFdnDVDcQFbGz/pFDb2"));
        userTokens.add(new UserProfile("SealCall_User23", "4ilETqgou/RZFfDS5XHfILI6ZiT8q7s0UEaMPWY0lMzC3IAa8p6ZGbxiOeRJVYFPxGR7iYJ7/654YE4Jvw9gLGF4mdBHKBZq5E0rL7nxYFQ="));
        userTokens.add(new UserProfile("SealCall_User24", "lUYny81oMJQ89ZI+M8uOELI6ZiT8q7s0UEaMPWY0lMzC3IAa8p6ZGbn1WJ5w7wrMAjOhMkg8m9Z4YE4Jvw9gLOBYtg4X63C25E0rL7nxYFQ="));
        userTokens.add(new UserProfile("SealCall_User25", "XqdRmZ7tCiw0fY46TqXbSrI6ZiT8q7s0UEaMPWY0lMzC3IAa8p6ZGabo9vxxQi+eJ6HdCJ0y3gp4YE4Jvw9gLGxX6oBLORVz5E0rL7nxYFQ="));
        userTokens.add(new UserProfile("SealCall_User26", "W+LCm/ScRKmgfP+ur20mBrI6ZiT8q7s0UEaMPWY0lMzC3IAa8p6ZGWw4Ui9tWoXCcIECCl/hnTR4YE4Jvw9gLElxsCx4lku45E0rL7nxYFQ="));
        userTokens.add(new UserProfile("SealCall_User27", "vciTKxq9WpW2uxb/fkc1XEmcbyeYIrXSDa0nFvL2mH+ioB4/kU0OQqQXpB5TKZZvdpLdd8oZ2ggxflFdnDVDcaXCigtXJ+pb"));
        userTokens.add(new UserProfile("SealCall_User28", "mc/445BWQkO2uxb/fkc1XEmcbyeYIrXSDa0nFvL2mH+ioB4/kU0OQhZawzbKGMIpdpLdd8oZ2ggxflFdnDVDcepZB+mctPPC"));
        userTokens.add(new UserProfile("SealCall_User29", "pPDXe8Vw3aM4/f9pXFw3nkmcbyeYIrXSDa0nFvL2mH+ioB4/kU0OQts47CGD6btVdpLdd8oZ2ggxflFdnDVDcct+RATcdK+T"));
        userTokens.add(new UserProfile("SealCall_User30", "Y837xBpF9lcMNLlDYM+24bI6ZiT8q7s0UEaMPWY0lMzC3IAa8p6ZGYdeUW5kJF7UQ4tesdb9WTV4YE4Jvw9gLDRFgMmVgCae5E0rL7nxYFQ="));
        userTokens.add(new UserProfile("SealCall_User31", "DzrOROOkr7rnHovtQHdQw82yq+hfEluLjZ78E1qo4hFOWaZ199HaeodhdG0sqhl8vOHaeG6eRw5YOmxputWQlUduBdTS1ttYtOrbWIBP8sA="));

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