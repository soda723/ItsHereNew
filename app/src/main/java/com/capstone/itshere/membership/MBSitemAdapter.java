package com.capstone.itshere.membership;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.itshere.R;
import com.google.api.Distribution;

import java.util.ArrayList;

public class MBSitemAdapter extends RecyclerView.Adapter<MBSitemAdapter.MBSItemHolder> {
    private ArrayList<MBSitem> arrayList;
    private Context context;

    public MBSitemAdapter(ArrayList<MBSitem> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public MBSItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.membership_item, parent, false);
        MBSItemHolder holder = new MBSItemHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MBSitemAdapter.MBSItemHolder holder, int position) {
        MBSitem myitem = arrayList.get(position);
        holder.tv_mbs_contents.setText(myitem.getContents());
        holder.ly_color.setBackgroundColor(myitem.getColor());

    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0 );
    }

    public class MBSItemHolder extends RecyclerView.ViewHolder{
        TextView tv_mbs_contents;
        ImageButton btn_delete, btn_pencil;
        LinearLayout ly_color;

        public MBSItemHolder(@NonNull View itemview){
            super(itemview);
            this.tv_mbs_contents = itemview.findViewById(R.id.mbsitem_name);
            this.btn_delete = itemview.findViewById(R.id.mbsitem_btn_delete);
            this.btn_pencil = itemview.findViewById(R.id.mbsitem_btn_pencil);
            this.ly_color = itemview.findViewById(R.id.mbsitem_ly_color);

            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            btn_pencil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }//MBSItemHolder
}
