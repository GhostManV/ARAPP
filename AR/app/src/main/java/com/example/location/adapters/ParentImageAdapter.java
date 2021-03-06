package com.example.location.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.location.R;
import com.example.location.activities.user.GrupoDeImagenesActivity;
import com.example.location.interfaces.OnItemClickListener;
import com.example.location.model.ParentImage;

import java.util.ArrayList;

public class ParentImageAdapter extends RecyclerView.Adapter<ParentImageAdapter.MyViewHolder> {
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private OnItemClickListener onItemClickListener;
    ArrayList<ParentImage> parentImages = new ArrayList<>();
    GrupoDeImagenesActivity activity;

    public ParentImageAdapter(ArrayList<ParentImage> parentImages, OnItemClickListener onItemClickListener, GrupoDeImagenesActivity activity) {
        this.onItemClickListener = onItemClickListener;
        this.parentImages.addAll(parentImages);
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.parent_item_image_grid, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder parentViewHolder, int position) {

        ParentImage parentImage = parentImages.get(position);
        parentViewHolder.parentItemTitle.setText(parentImage.getName());
        parentViewHolder.groupId = parentImage.getId();


        LinearLayoutManager layoutManager = new LinearLayoutManager(parentViewHolder.childRecyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false);
        ImageAdapter childItemAdapter = new ImageAdapter((ArrayList) parentImage.getImagens(), onItemClickListener, 0);
        parentViewHolder.childRecyclerView.setLayoutManager(layoutManager);
        parentViewHolder.childRecyclerView.setAdapter(childItemAdapter);
        parentViewHolder.childRecyclerView.setRecycledViewPool(viewPool);
    }

    @Override
    public int getItemCount() {
        return parentImages.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView parentItemTitle;

        private RecyclerView childRecyclerView;
        Integer groupId;

        MyViewHolder(final View itemView) {
            super(itemView);

            parentItemTitle = itemView.findViewById(R.id.parent_item_title);

            childRecyclerView = itemView.findViewById(R.id.child_recyclerview);


        }
    }
}
