package test.com.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import test.com.R;


/**
 * Created by zhangsixia on 18/4/23.
 */

public class Left_Adapter extends RecyclerView.Adapter<Left_Adapter.MyHolder> {

    private ArrayList<String> dateList = new ArrayList<>();
    private Context mContext;

    public Left_Adapter(Context context){
        this.mContext = context;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.left_item_layout, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        holder.textView.setText(dateList.get(position));
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public void setDataList(ArrayList<String> dateList){
        this.dateList.clear();
        this.dateList.addAll(dateList);
        notifyDataSetChanged();
    }

    static class MyHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        public MyHolder(View itemView) {
            super(itemView);
            textView = (TextView)itemView.findViewById(R.id.layer_num);
        }

    }
}
