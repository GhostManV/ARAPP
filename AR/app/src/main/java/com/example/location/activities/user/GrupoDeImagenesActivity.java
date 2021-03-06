package com.example.location.activities.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.location.R;
import com.example.location.activities.MainActivity;
import com.example.location.adapters.ParentImageAdapter;
import com.example.location.dummy.ImagenesLocales;
import com.example.location.dummy.GruposLocales;
import com.example.location.interfaces.OnItemClickListener;
import com.example.location.model.GrupoDeImagenes;
import com.example.location.model.Imagen;
import com.example.location.model.Museum;
import com.example.location.model.ParentImage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GrupoDeImagenesActivity extends AppCompatActivity implements OnItemClickListener {
DatabaseReference imageRef = FirebaseDatabase.getInstance().getReference("imagens");
    SharedPreferences sharedPreferences;

    Museum museum;
    List<GrupoDeImagenes> grupoDeImagenes = new ArrayList<>();
    ArrayList<Imagen> imagens = new ArrayList<>();
    ArrayList<Imagen> imagenes = new ArrayList<>();
    ArrayList<ParentImage> parentImages = new ArrayList<>();
    RecyclerView recyclerView;
    ActionBar actionBar;
    ProgressDialog dialog;
    ImagenesLocales img=new ImagenesLocales(1);
    List<Imagen> allimg=new ArrayList<>();

    ImageView imgPicture;
    //TextView textViewDesc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("App_settings", MODE_PRIVATE);
        museum = (Museum) getIntent().getSerializableExtra("museumData");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.activity_image_group);
        recyclerView = findViewById(R.id.recyclerview);
        actionBar = getSupportActionBar();
        actionBar.setTitle("MUSEO UMG");
        actionBar.setDisplayHomeAsUpEnabled(true);
        allimg=img.List();
        for (Imagen c:allimg) {
            imagenes.add(c);
            Log.d("->>>>>>>>>>", "Value is: " + c.getName());
        }
        setImageGroup();
        getImageList();
        setView();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();
    }

    private void setImageGroup() {
        if (museum.getImages() == null) {
            return;
        }
        GruposLocales imageGroupDummy = new GruposLocales();
        List<GrupoDeImagenes> groupList = imageGroupDummy.list();
        for (int i = 0; i < groupList.size(); i++) {
            if (museum.getImages().contains(groupList.get(i).getId())) {
                grupoDeImagenes.add(groupList.get(i));
            }
        }

        for (int i = 0; i < grupoDeImagenes.size(); i++) {
            ParentImage parentImage = new ParentImage(grupoDeImagenes.get(i).getId(), grupoDeImagenes.get(i).getName(), new ArrayList<>());
            parentImages.add(parentImage);
        }
    }

    private void getImageList() {
     /*ImagenesLocales img=new ImagenesLocales(1);
        List<Imagen> allimg=new ArrayList<>();
        allimg=img.List();
        imagens = new ArrayList<>();
        for (Imagen c:allimg) {

                imagens.add(c);

            Log.d("->>>>>>>>>>", "Value is: " + c.getName());
        }
        setAdapterPo();*/
        //Aqui jala las imagenes de la bd
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                imagens = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Imagen imagen = child.getValue(Imagen.class);
                    if (imagen != null) {
                        imagens.add(imagen);
                    }
                }
                setAdapterPo();
                Log.d("TAG IMGGRUPACTIVI", "Value is: " + dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        };
        imageRef.addValueEventListener(eventListener);
    }
//Funcion que pone la imagen segun la bd
    private void setView() {
        //textViewDesc = findViewById(R.id.textViewDesc);
        imgPicture = findViewById(R.id.imageView);
       // textViewDesc.setText(museum.getDescription());
        if (museum.getImage() != null && !museum.getImage().isEmpty()) {
            DisplayMetrics dimension = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dimension);
            float density  = getResources().getDisplayMetrics().density;
            float width = dimension.widthPixels/density+3;
            //Se setea la imagen del mueo donde salen los dinos

            Glide.with(this).load(museum.getImage()).override((int) width,255).placeholder(R.drawable.noimage).error(R.drawable.noimage).into(imgPicture);
        }
    }

    private void setAdapterPo() {
        for (int i = 0; i < imagenes.size(); i++) {
            for (int j = 0; j < 6; j++) {
                if (imagenes.get(i).getGroup().equals(parentImages.get(j).getId())) {
                    parentImages.get(j).addImage(imagenes.get(i));
                }
            }
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        ParentImageAdapter parentImageAdapter = new ParentImageAdapter(parentImages, this, this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(parentImageAdapter);
        dialog.dismiss();
    }

    public void saveFavorites(ArrayList<String> favorites) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> set = new HashSet<>();
        set.addAll(favorites);
        editor.putStringSet("FAVORITES", set);
        editor.apply();
    }

    public ArrayList<String> getFavorites() {
        ArrayList<String> favorites = new ArrayList<>();
        Set<String> set = sharedPreferences.getStringSet("FAVORITES", new HashSet<>());
        favorites.addAll(set);
        return favorites;
    }

    public void onFavoriteClick(Integer groupId) {
        ArrayList<String> favorites = getFavorites();
        if (favorites.contains(groupId.toString())) {
            favorites.remove(groupId.toString());
        }
        else {
            favorites.add(groupId.toString());
        }

        saveFavorites(favorites);
    }

    @Override
    public void onItemClick(Object o) {
        Imagen data = (Imagen) o;
        Intent intent = new Intent(this.getApplicationContext(), ImagenActivity.class);
        intent.putExtra("data", data);
        startActivity(intent);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnHome:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}