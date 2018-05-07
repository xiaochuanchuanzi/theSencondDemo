package test.com.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import test.com.R;
import test.com.myapplication.scrollview.BodyScrollView;
import test.com.myapplication.scrollview.LeftAndTop;
import test.com.myapplication.scrollview.LeftScrollView;
import test.com.myapplication.scrollview.MapsCature;
import test.com.myapplication.scrollview.TopScrollView;
import test.com.myapplication.selfwordview.SelfGroup;
import test.com.recyclerview.Body_Adapter;
import test.com.recyclerview.Left_Adapter;
import test.com.recyclerview.NEW_Body_Adapter;
import test.com.recyclerview.Top_Adapter;

/**
 * Created by zhangsixia on 18/4/19.
 */

public class SelfWordActivity extends AppCompatActivity{


    private RecyclerView mRecyclerView_LEFT,mRecyclerView_TOP,mRecyclerView_BODY;
    private Left_Adapter mLeft_Adapter;
    private Top_Adapter mTop_Adapter;
    private Body_Adapter mBody_Adapter;
    private ArrayList<String> leftList;
    private ArrayList<String> topList;
    private ArrayList<ArrayList<String>> bodyList;
    private NEW_Body_Adapter NEW_mBody_Adapter;
    private BodyScrollView mBodyScrollView;
    private TopScrollView mTopScrollView;
    private LeftScrollView mLeftScrollView;
    private LeftAndTop mLeftAndTop;
    private MapsCature mMapsCature;
    private SelfGroup mSelfGroup;
    private Button mButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_layout);
        mBodyScrollView = findViewById(R.id.body);

        mSelfGroup = findViewById(R.id.body_group);
        mTopScrollView = findViewById(R.id.top);
        mLeftScrollView = findViewById(R.id.left);
        mLeftAndTop = findViewById(R.id.left_and_top);
        mMapsCature = findViewById(R.id.fature_map);


        mLeftScrollView.addListener(mSelfGroup);
        mTopScrollView.addListener(mSelfGroup);
        mMapsCature.setListener(mSelfGroup);
        //timerMessure();

        mSelfGroup.setmOnItemsClickListener(new SelfGroup.OnItemsClickListener() {
            @Override
            public void OnitemListener(View view, int position) {
                for(int i= 0;i < mSelfGroup.getChildCount();i++){
                    if(view == mSelfGroup.getChildAt(i)){
                        if(view.isSelected()){
                            view.setSelected(false);
                            view.setBackgroundColor(Color.WHITE);
                            Toast.makeText(SelfWordActivity.this,"取消了"+position,Toast.LENGTH_SHORT).show();
                        }else{
                            view.setSelected(true);
                            view.setBackgroundColor(Color.RED);
                            Toast.makeText(SelfWordActivity.this,"点击了"+position,Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        mSelfGroup.getChildAt(i).setSelected(false);
                        mSelfGroup.getChildAt(i).setBackgroundColor(Color.WHITE);
                    }
                }
            }
        });
        //initLeftRecyclerView();
        //initTopRecyclerView();
        //NEW_initBodyRecyclerView();
        //seatTableWay();
    }

   /* public void initLeftRecyclerView(){
        leftList = new ArrayList<>();
        initData();
        mRecyclerView_LEFT = findViewById(R.id.left_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView_LEFT.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL); //加上这句 就可以设置水平方向
        mRecyclerView_LEFT.setLayoutManager(layoutManager);
        mLeft_Adapter = new Left_Adapter(SelfWordActivity.this);
        mRecyclerView_LEFT.setAdapter(mLeft_Adapter);
        mLeft_Adapter.setDataList(leftList);

    }
    public void initData(){
        for(int i=0;i<50;i++){
            int num = i+1;
            leftList.add(""+num);
        }
    }

    public void initTopRecyclerView(){
        topList = new ArrayList<>();
        initData2();
        mRecyclerView_TOP = findViewById(R.id.top_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView_TOP.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.HORIZONTAL));
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL); //加上这句 就可以设置水平方向
        mRecyclerView_TOP.setLayoutManager(layoutManager);
        mTop_Adapter = new Top_Adapter(SelfWordActivity.this);
        mRecyclerView_TOP.setAdapter(mTop_Adapter);
        mTop_Adapter.setDataList(topList);
    }
    public void initData2(){
        for(int i=0;i<50;i++){
            int num = i+1;
            topList.add(""+num);
        }
    }

   *//* public void initBodyRecyclerView(){
        bodyList = new ArrayList<>();
        initData3();
        mRecyclerView_BODY = findViewById(R.id.body_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView_BODY.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.HORIZONTAL));
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL); //加上这句 就可以设置水平方向
        mRecyclerView_BODY.setLayoutManager(layoutManager);
        mBody_Adapter = new Body_Adapter(SelfWordActivity.this);
        mRecyclerView_BODY.setAdapter(mBody_Adapter);
        mBody_Adapter.setDataList(bodyList);
    }*//*

   *//* public void initData3(){
        for(int i=0;i<100;i++){
            int num = i+1;
            bodyList.add(""+num);
        }
    }*//*

    public void initData3(){
        for(int i=0;i<100;i++){
            ArrayList<String> alist = new ArrayList<>();
            for(int j= 0;j<100;j++){
                int num = j+1;
                alist.add(""+num);
            }
            bodyList.add(alist);
        }
    }


    public void NEW_initBodyRecyclerView(){
        bodyList = new ArrayList<>();
        initData3();
        mRecyclerView_BODY = findViewById(R.id.body_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView_BODY.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL); //加上这句 就可以设置水平方向
        mRecyclerView_BODY.setLayoutManager(layoutManager);
        NEW_mBody_Adapter = new NEW_Body_Adapter(SelfWordActivity.this);
        mRecyclerView_BODY.setAdapter(NEW_mBody_Adapter);
        NEW_mBody_Adapter.setDataList(bodyList);
    }





    public SeatTable seatTableView;
    public void seatTableWay(){
        ArrayList<String> lineNumbers = new ArrayList<>();
        for(int i= 0;i<30;i++){
            int num = i+1;
            lineNumbers.add(""+num);
        }
        seatTableView = (SeatTable) findViewById(R.id.seatView);
        seatTableView.setScreenName("8号厅荧幕");//设置屏幕名称
        seatTableView.setMaxSelected(3);//设置最多选中
        seatTableView.setLineNumbers(lineNumbers);


        seatTableView.setSeatChecker(new SeatTable.SeatChecker() {
            @Override
            public boolean isValidSeat(int row, int column) {
                if(column==2) {
                    return false;
                }
                return true;
            }
            @Override
            public boolean isSold(int row, int column) {
                if(row==6&&column==6){
                    return true;
                }
                return false;
            }
            @Override
            public void checked(int row, int column) {

            }
            @Override
            public void unCheck(int row, int column) {

            }
            @Override
            public String[] checkedSeatTxt(int row, int column) {
                return null;
            }
        });
        seatTableView.setData(10,15);
    }*/

    public void timerMessure(){
        formatTime(1510876800, 1511395200);
    }
    long mDay;
    long mHour;
    long mMin;
    long mSecond;// 天 ,小时,分钟,秒
    private void formatTime(long startTime, long endTime) {
        long time = (endTime - startTime) / 1000;
        mSecond = (int) (time % 60);
        mMin = (int) (time / 60 % 60);
        mHour = (int) (time / 3600);
        mDay = (int) (time / 3600 / 24);

        /** 倒计时60秒，一次1秒 */
        CountDownTimer timer = new CountDownTimer(60*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.i("TTTTAAG",computeTime());
            }
            @Override
            public void onFinish() {
                Log.i("TTTTAAG","倒计时完毕了");
            }
        }.start();
        //timer.cancel();
    }
    /**
     * 倒计时计算
     */
    private String computeTime() {
        String time = "";
        mSecond--;
        if (mSecond < 0) {
            mMin--;
            mSecond = 59;
            if (mMin < 0) {
                mMin = 59;
                mHour--;
                if (mHour < 0) {
                    // 倒计时结束
                    mHour = 23;
                    mDay--;
                }
            }
        }
        time = "抢购中本场还剩" + mDay + "天" + mHour + "时" + mMin + "分" + mSecond + "秒";
        return time;
    }

}
