package test.com.myapplication.selfwordview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import test.com.R;

/**
 * Created by zhangsixia on 18/4/27.
 */

public class SelfGroupActivity extends AppCompatActivity {


    private SelfGroup mSelfGroup;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.self_group_layout);
        mSelfGroup = findViewById(R.id.self_group);


    }
}
