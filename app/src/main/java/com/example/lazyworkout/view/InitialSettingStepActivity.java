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

public class InitialSettingStepActivity extends AppCompatActivity implements View.OnClickListener {

    private AutoCompleteTextView setStepInput;
    private Button btn;

    Database db = new Database();
    private String uid = FirebaseAuth.getInstance().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_setting_step);

        initViews();
    }

    private void initViews() {
        setStepInput = findViewById(R.id.initialStepInput);  //TODO: revamp set step input
        btn = findViewById(R.id.initialStepBtn);

        setStepInput.setText((int) (Constant.DEFAULT_STEP_SIZE * 100000) + " cm");

        ArrayAdapter<String> stepArray = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Constant.STEP_SETTING);
        setStepInput.setAdapter(stepArray);
        setStepInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                float stepSize = Util.getStepsize(item);
                db.updateStepsize(stepSize);
                getSharedPreferences(uid, Context.MODE_PRIVATE).edit()
                        .putFloat("step_size", (float) stepSize).commit();
            }
        });

        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case (R.id.initialStepBtn):
                startActivity(new Intent(this, TimePickerActivity.class));
                break;

            default:
                break;
        }
    }
}