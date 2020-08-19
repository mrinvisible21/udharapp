package com.example.udhar.MemberPayments;

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
import com.example.udhar.AllPendingPayments.PendingPayments;
import com.example.udhar.R;
import com.example.udhar.ViewDetails;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MemberPaymetAdapter extends RecyclerView.Adapter<MemberPaymetAdapter.MyViewHolder> implements Filterable {

    private List<MemberPayments> mList;
    private List<MemberPayments> mListFiltered;
    public Context context;

    public MemberPaymetAdapter(Context context, List<MemberPayments> mList) {
        this.context = context;
        this.mList = mList;
        this.mListFiltered = mList;
        notifyItemChanged(0, mListFiltered.size());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_member_payments,parent,false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.title.setText(mListFiltered.get(position).getAmount_title());
        holder.status.setText(mListFiltered.get(position).getStatus());
        DecimalFormat formatter = new DecimalFormat("#,##,##,##,###");
        holder.amount.setText("\u20B9 " + formatter.format(Integer.parseInt(mListFiltered.get(position).getRemaining_amount())));

        if(mListFiltered.get(position).getRemaining_amount().equalsIgnoreCase("0")){
            holder.amount.setText("\u20B9 " + formatter.format(Integer.parseInt("0")));
        }
        else if(mListFiltered.get(position).getRemaining_amount().equalsIgnoreCase(" ")){
            holder.amount.setText("\u20B9 " + formatter.format(Integer.parseInt("0")));

        }

        if (mListFiltered.get(position).getStatus().equalsIgnoreCase("Pending")){
            holder.memberimage.setBackgroundResource(R.drawable.ic_unpaid);
        }
        else if(mListFiltered.get(position).getStatus().equalsIgnoreCase("Paid")){
            holder.memberimage.setBackgroundResource(R.drawable.ic_paid);
        }



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
                    List<MemberPayments> filteredList = new ArrayList<>();
                    for (MemberPayments schools : mList) {
               if (schools.getAmount_title().toLowerCase().contains(charString.toLowerCase())) {
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
                mListFiltered = (ArrayList<MemberPayments>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title,status,amount;
        ImageView memberimage;
        LinearLayout linearLayout;

        public MyViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.title);
            status = (TextView) view.findViewById(R.id.status);
            amount = (TextView) view.findViewById(R.id.amount);
            memberimage=(ImageView)view.findViewById(R.id.image);
            linearLayout=(LinearLayout) view.findViewById(R.id.linearlayout);

        }
    }

}
