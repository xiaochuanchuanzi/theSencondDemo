package test.com.recyclerview;

import android.content.Context;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import test.com.R;


/**
 * Created by zhangsixia on 18/4/23.
 */

public class NEW_Body_Adapter extends RecyclerView.Adapter<NEW_Body_Adapter.MyHolder> {

    private ArrayList<ArrayList<String>> dateList = new ArrayList<>();
    private Context mContext;

    private Item_Body_Adapter item_body_adapter;

    public NEW_Body_Adapter(Context context){
        this.mContext = context;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.new_body_item_layout, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        holder.mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.HORIZONTAL));
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL); //加上这句 就可以设置水平方向
        //layoutManager.setAutoMeasureEnabled(true);
        holder.mRecyclerView.setLayoutManager(layoutManager);
        /**
         * 需要判断item中recyclerview是否已经设置适配器,重复设置会导致滑动不流畅
         */
        if(holder.mRecyclerView.getAdapter()==null) {
            item_body_adapter = new Item_Body_Adapter(mContext);
            holder.mRecyclerView.setAdapter(item_body_adapter);
        }
        item_body_adapter.setDataList(dateList.get(position));
    }

    /**
     * 重写此方法返回一个position的类型,可以在一定程度上减轻位移的出现
     */
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public void setDataList(ArrayList<ArrayList<String>> dateList){
        this.dateList.clear();
        this.dateList.addAll(dateList);
        notifyDataSetChanged();
    }

    static class MyHolder extends RecyclerView.ViewHolder{
        public RecyclerView mRecyclerView;
//        public LinearLayout mLinearLayout;
        public MyHolder(View itemView) {
            super(itemView);
//            mLinearLayout = itemView.findViewById(R.id.linear_layut);
//            mLinearLayout.setFocusable(true);
//            mLinearLayout.setFocusableInTouchMode(true);
            mRecyclerView = (RecyclerView)itemView.findViewById(R.id.item_body_list);
        }

    }
}
