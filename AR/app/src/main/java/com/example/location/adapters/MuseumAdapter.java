package com.example.location.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.location.R;
import com.example.location.interfaces.OnItemClickListener;
import com.example.location.model.Museum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MuseumAdapter extends RecyclerView.Adapter<MuseumAdapter.MyViewHolder> implements Filterable {
    ArrayList<Museum> arr;
    ArrayList<Museum> museumAll;
    private OnItemClickListener onItemClickListener;

    public MuseumAdapter(ArrayList<Museum> arr, OnItemClickListener onItemClickListener) {
        this.arr = arr;
        this.onItemClickListener = onItemClickListener;
        museumAll = new ArrayList<>();
        museumAll.addAll(arr);
    }

    @NonNull
    @Override
    public MuseumAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_museum, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MuseumAdapter.MyViewHolder holder, int position) {
        holder.getDataBind(arr.get(position), holder.itemView.getContext());
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Museum> filteredList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0 || charSequence == "") {
                filteredList.addAll(museumAll);
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            arr.clear();
            arr.addAll((Collection<? extends Museum>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView bg;
        TextView title;
        TextView description;
        Museum museum;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            bg = itemView.findViewById(R.id.background);
            description = itemView.findViewById(R.id.description);
            title = itemView.findViewById(R.id.title);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation myAnim = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.click);
                    itemView.startAnimation(myAnim);
                    onItemClickListener.onItemClick(museum);
                }
            });
        }

        public void getDataBind(Museum museum, Context context) {
            this.museum = museum;
            if (museum.getImage() != null && !museum.getImage().isEmpty()) {
                Glide.with(context).load(museum.getImage()).placeholder(R.drawable.noimage).error(R.drawable.noimage).into(bg);
            } else {
                bg.setImageDrawable(context.getResources().getDrawable(R.drawable.noimage));
            }

            title.setText("Museum UMG");
            description.setText(museum.getDescription());
        }
    }
}
