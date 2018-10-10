package com.example.hahahaha.qrcodeadmin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



import java.util.List;

public class orderAdapter1 extends RecyclerView.Adapter<orderAdapter1.ViewHolder>{
    private List<XYValue> list;
    Context context;
    public orderAdapter1(Context context, List<XYValue> objects){
        this.context = context;
        this.list=objects;
    }


    @NonNull
    @Override
    public orderAdapter1.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.checkedqr_history,parent,false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull orderAdapter1.ViewHolder holder, int position) {
        holder.orderNo.setText(list.get(position).getQr_code());
        holder.orderItems.setText(list.get(position).getTimes());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView orderNo, orderItems, deliveryDate,cost;

        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            orderNo = itemView.findViewById(R.id.orderNo);
            orderItems = itemView.findViewById(R.id.orderItems);

        }
    }

}
