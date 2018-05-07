package test.com;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import test.com.library.SeatTable;

/**
 * Created by zhangsixia on 18/4/24.
 */

public class SeatTableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seat_layout);
        seatTableWay();
    }
    public SeatTable seatTableView;
    public void seatTableWay(){
        ArrayList<String> lineNumbers = new ArrayList<>();
        for(int i= 0;i<30;i++){
            int num = i+1;
            lineNumbers.add(""+num);
        }
        seatTableView = (SeatTable) findViewById(R.id.seatView);

        seatTableView.setData(30,2,3);
    }
}
