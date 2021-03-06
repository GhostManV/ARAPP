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
import com.example.location.interfaces.OnItemLongClick;
import com.example.location.model.Imagen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> implements Filterable {
    ArrayList<Imagen> arr;
    ArrayList<Imagen> imagenAll;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClick onItemLongClick;
    int type = 0;
    int isOnLongClick = 0;

    public ImageAdapter(ArrayList<Imagen> arr, OnItemClickListener onItemClickListener, int type) {
        this.arr = arr;
        this.onItemClickListener = onItemClickListener;
        this.type = type;
        imagenAll = new ArrayList<>();
        imagenAll.addAll(arr);
    }

    public ImageAdapter(ArrayList<Imagen> arr, OnItemClickListener onItemClickListener, OnItemLongClick onItemLongClick, int type, int isOnLongClick) {
        this.arr = arr;
        this.onItemClickListener = onItemClickListener;
        this.onItemLongClick = onItemLongClick;
        this.type = type;
        this.isOnLongClick = isOnLongClick;
    }

    @NonNull
    @Override
    public ImageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_full, parent, false);
        if (type == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_grid, parent, false);
        }
        if (type == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_horizontal, parent, false);
        }


        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.MyViewHolder holder, int position) {
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
            List<Imagen> filteredList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0 || charSequence == "") {
                filteredList.addAll(imagenAll);
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            arr.clear();
            arr.addAll((Collection<? extends Imagen>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView bg;
        TextView title;
        TextView description;
        Imagen imagen;

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
                    onItemClickListener.onItemClick(imagen);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (isOnLongClick == 1) {
                        onItemLongClick.onItemLongClick(imagen,itemView);
                    }
                    return true;
                }
            });
        }

        public void getDataBind(Imagen imagen, Context context) {

            this.imagen = imagen;
            title.setText(imagen.getName());
            description.setText(imagen.getDesc());

            if (imagen.getImage() != null) {
                bg.setImageDrawable(context.getResources().getDrawable(imagen.getImage()));
            } else if (imagen.getUrl() != null && !imagen.getUrl().isEmpty()) {
                Glide.with(context).load(imagen.getUrl()).placeholder(R.drawable.noimage).error(R.drawable.noimage).into(bg);
            }
        }
    }
}
