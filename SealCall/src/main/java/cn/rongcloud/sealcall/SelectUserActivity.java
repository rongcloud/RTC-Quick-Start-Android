package cn.rongcloud.sealcall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import cn.rongcloud.sealcall.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SelectUserActivity extends Activity {
    private static final String TAG = "SelectUserActivity";
    public static final String ALL_USERS = "all_users";
    public static final String SELECTED_USERS = "selected_users";
    private RecyclerView recyclerView;
    private Button confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        confirm = findViewById(R.id.confirm);
        List<String> allUsers = getIntent().getStringArrayListExtra(ALL_USERS);
        final SelectAdapter adapter = new SelectAdapter(allUsers);
        recyclerView.setAdapter(adapter);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putStringArrayListExtra(SELECTED_USERS, adapter.getCheckedList());
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }


    private class SelectAdapter extends RecyclerView.Adapter<BaseViewHolder> implements View.OnClickListener {
        private List<String> data;
        private Set<String> checkedList = new HashSet<>();

        public SelectAdapter(List<String> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            View view = inflater.inflate(R.layout.select_recycler_item, viewGroup, false);
            return new BaseViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BaseViewHolder baseViewHolder, int i) {
            String userid = data.get(i);
            baseViewHolder.checkBox.setChecked(checkedList.contains(userid));
            baseViewHolder.textView.setText(userid);
            baseViewHolder.itemView.setTag(userid);
            baseViewHolder.itemView.setOnClickListener(this);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @Override
        public void onClick(View v) {
            String userId = (String) v.getTag();
            if (checkedList.contains(userId)) {
                checkedList.remove(userId);
            } else {
                checkedList.add(userId);
            }
            notifyDataSetChanged();
        }

        public ArrayList<String> getCheckedList() {
            return new ArrayList<>(checkedList);
        }
    }

    private class BaseViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;
        private TextView textView;

        public BaseViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
            textView = itemView.findViewById(R.id.user_id);
        }
    }
}
