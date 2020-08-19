package com.example.udhar.AllPendingPayments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.udhar.R;
import com.example.udhar.ViewDetails;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PendingPaymentAdapter extends RecyclerView.Adapter<PendingPaymentAdapter.MyViewHolder> implements Filterable {

    private List<PendingPayments> mList;
    private List<PendingPayments> mListFiltered;
    public Context context;

    public PendingPaymentAdapter(Context context, List<PendingPayments> mList) {
        this.context = context;
        this.mList = mList;
        this.mListFiltered = mList;
        notifyItemChanged(0, mListFiltered.size());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_home_list,parent,false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.membername.setText(mListFiltered.get(position).getM_name());
        holder.date.setText(mListFiltered.get(position).getEnd_date());
        DecimalFormat formatter = new DecimalFormat("#,##,##,##,###");
        holder.amount.setText("\u20B9 " + formatter.format(Integer.parseInt(mListFiltered.get(position).getRemaining_amount())));

        ColorGenerator generator=ColorGenerator.MATERIAL;
        Drawable drawable= TextDrawable.builder().buildRound(mListFiltered.get(position).getM_name().substring(0,1),generator.getRandomColor());
        holder.memberimage.setImageDrawable(drawable);


        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ViewDetails.class);
                intent.putExtra("id",mListFiltered.get(position).getId());
                intent.putExtra("u_id",mListFiltered.get(position).getU_id());
                intent.putExtra("m_id",mListFiltered.get(position).getM_id());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mList != null){ return mListFiltered.size();}
        else { return 0;}
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mListFiltered = mList;
                } else {
                    List<PendingPayments> filteredList = new ArrayList<>();
                    for (PendingPayments schools : mList) {
               if (schools.getM_name().toLowerCase().contains(charString.toLowerCase())||
                       schools.getM_name().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(schools);
                        }
                    }
                    mListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mListFiltered = (ArrayList<PendingPayments>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView membername,date,amount;
        ImageView memberimage;
        LinearLayout linearLayout;

        public MyViewHolder(View view) {
            super(view);

            membername = (TextView) view.findViewById(R.id.member_name);
            date = (TextView) view.findViewById(R.id.date);
            amount = (TextView) view.findViewById(R.id.amount);
            memberimage=(ImageView)view.findViewById(R.id.image);
            linearLayout=(LinearLayout) view.findViewById(R.id.linearlayout);

        }
    }

}
