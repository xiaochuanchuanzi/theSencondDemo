package test.com.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import test.com.R;

/**
 * Created by zhangsixia on 18/5/3.
 */

public class IndexActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mButton1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index_layout);
        mButton1 = findViewById(R.id.button1);
        mButton1.setText("SelfWordActivity");
        mButton1.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button1:
                Intent intent = new Intent(IndexActivity.this,SelfWordActivity.class);
                startActivity(intent);
                break;
        }
    }
}
