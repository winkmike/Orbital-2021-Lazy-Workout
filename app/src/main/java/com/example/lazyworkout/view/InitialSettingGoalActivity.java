package com.example.lazyworkout.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lazyworkout.R;
import com.example.lazyworkout.util.Constant;
import com.example.lazyworkout.util.Database;
import com.example.lazyworkout.util.Util;
import com.google.firebase.auth.FirebaseAuth;

public class InitialSettingGoalActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView backArrow;
    private TextView skip;
    private AutoCompleteTextView setGoalInput;
    private String[] goal = {"4.0 km", "4.5 km", "5.0 km", "5.5 km", "6.0 km", "6.5 km", "7.0 km", "7.5 km",
            "8.0 km", "8.5 km", "9.0 km", "9.5 km", "10.0 km"};
    private Button btn;

    Database db = new Database();
    private String uid = FirebaseAuth.getInstance().getUid();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_setting_goal);

        initViews();
    }

    private void initViews() {
        setGoalInput = findViewById(R.id.initialGoalInput);  //TODO: revamp set goal input
        btn = findViewById(R.id.initialGoalBtn);

        setGoalInput.setText(Constant.DEFAULT_GOAL + " km");

        ArrayAdapter<String> goalArray = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Constant.GOAL_SETTING);
        setGoalInput.setAdapter(goalArray);
        setGoalInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                float goal = Util.getGoal(item);
                db.updateGoal(goal);
                getSharedPreferences(uid, Context.MODE_PRIVATE).edit()
                        .putFloat("goal", (float) goal).commit();
            }
        });

        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case (R.id.initialGoalBtn):
                startActivity(new Intent(this, InitialSettingStepActivity.class));

                break;

            default:
                break;
        }
    }
}