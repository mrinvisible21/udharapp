package com.example.udhar.AllMemberLists;

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
import com.example.udhar.MemberProfile;
import com.example.udhar.R;

import java.util.ArrayList;
import java.util.List;

public class AllMembersAdapter extends RecyclerView.Adapter<AllMembersAdapter.MyViewHolder> implements Filterable {

    private List<AllMember> mList;
    private List<AllMember> mListFiltered;
    public Context context;

    public AllMembersAdapter(Context context, List<AllMember> mList) {
        this.context = context;
        this.mList = mList;
        this.mListFiltered = mList;
        notifyItemChanged(0, mListFiltered.size());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_members,parent,false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.membername.setText(mListFiltered.get(position).getM_name());
        holder.mobile.setText(mListFiltered.get(position).getM_mobile());
        holder.address.setText(mListFiltered.get(position).getM_address());

        ColorGenerator generator=ColorGenerator.MATERIAL;
        Drawable drawable= TextDrawable.builder().buildRound(mListFiltered.get(position).getM_name().substring(0,1),generator.getRandomColor());
        holder.memberimage.setImageDrawable(drawable);


        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, MemberProfile.class);
                intent.putExtra("m_id",mListFiltered.get(position).getId());
                intent.putExtra("m_name",mListFiltered.get(position).getM_name());
                intent.putExtra("m_mobile",mListFiltered.get(position).getM_mobile());
                intent.putExtra("m_address",mListFiltered.get(position).getM_address());
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
                    List<AllMember> filteredList = new ArrayList<>();
                    for (AllMember schools : mList) {
               if (schools.getM_name().toLowerCase().contains(charString.toLowerCase())||
                       schools.getM_mobile().toLowerCase().contains(charString.toLowerCase())||
                        schools.getM_address().toLowerCase().contains(charString.toLowerCase())) {
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
                mListFiltered = (ArrayList<AllMember>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView membername,mobile,address;
        ImageView memberimage;
        LinearLayout linearLayout;

        public MyViewHolder(View view) {
            super(view);

            membername = (TextView) view.findViewById(R.id.membername);
            mobile = (TextView) view.findViewById(R.id.membermobile);
            address = (TextView) view.findViewById(R.id.memberaddress);
            memberimage=(ImageView)view.findViewById(R.id.image);
            linearLayout=(LinearLayout) view.findViewById(R.id.linearlayout);

        }
    }

}
