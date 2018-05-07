package test.com.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import test.com.R;

/**
 * Created by zhangsixia on 18/4/23.
 */

public class Item_Body_Adapter extends RecyclerView.Adapter<Item_Body_Adapter.MyHolder> {

    private ArrayList<String> dateList = new ArrayList<>();
    private Context mContext;

    public Item_Body_Adapter(Context context){
        this.mContext = context;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_body_layout, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        holder.textView1.setText(dateList.get(position));
    }

    /**
     * 配合父Adapter,重写此方法,可减少位移的出现,同时可以增加流畅度
     */
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        Log.i("TAG",dateList.size()+"");
        return dateList.size();
    }

    public void setDataList(ArrayList<String> dateList){
        this.dateList.clear();
        this.dateList.addAll(dateList);
        notifyDataSetChanged();
    }

    static class MyHolder extends RecyclerView.ViewHolder{
        public TextView textView1;
        public MyHolder(View itemView) {
            super(itemView);
            textView1 = (TextView)itemView.findViewById(R.id.layer_num1);
        }

    }
}
