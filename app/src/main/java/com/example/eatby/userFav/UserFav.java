package com.example.eatby.userFav;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.example.eatby.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;


public class UserFav extends AppCompatActivity {

    private RecyclerView userFavList;
    private FirestoreRecyclerAdapter adapter;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private Button clearAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_fav);
        userFavList = findViewById(R.id.userFavRecycler);
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        String userID = fAuth.getCurrentUser().getUid();
        clearAll = findViewById(R.id.clearAllBtn);
        Query query = fStore.collection("userFavorites").document(userID).collection("restaurantList");
        FirestoreRecyclerOptions<UserFavFireStore> options = new FirestoreRecyclerOptions.Builder<UserFavFireStore>()
                .setQuery(query,UserFavFireStore.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<UserFavFireStore,FavViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull FavViewHolder holder, int position, @NonNull @NotNull UserFavFireStore model) {
                holder.rName.setText(model.getName());
                Glide.with(UserFav.this).load(model.getImageUrl()).apply((new RequestOptions()).transforms(new Transformation[]{(Transformation)(new CenterCrop()), (Transformation)(new RoundedCorners(20))})).into(holder.rImage);
                holder.ratingBar.setRating(model.getRating());
                holder.rAddress.setText(model.getAddress());
                holder.rDistance.setText(model.getDistance());
                holder.rCategory.setText(model.getCategory());
                holder.rPrice.setText(model.getPrice());
                holder.getDirection.setOnClickListener(v -> {
                    String address = model.getAddress();
                    Uri u = Uri.parse("http://maps.google.co.in/maps?q=" + address);
                    Intent startMaps = new Intent(Intent.ACTION_VIEW,u);
                    startActivity(startMaps);
                });
                holder.callRestaurant.setOnClickListener(v->{
                    String number=model.getPhoneNumber();
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:"+number));

                    if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivity(callIntent);
                });
                holder.getMenu.setOnClickListener(v->{
                    String website = model.getName().replaceAll("[^a-zA-Z]+","").toLowerCase();
                    Uri u = Uri.parse("https://"+website+".com");
                    Intent intent = new Intent(Intent.ACTION_VIEW,u);
                    startActivity(intent);
                });
                holder.deleteFromFavorite.setOnClickListener(v-> {
                    AlertDialog.Builder alert = new AlertDialog.Builder(UserFav.this);
                    alert.setTitle("Delete").
                            setMessage("Remove from favorites?").
                            setNegativeButton("No",null).
                            setPositiveButton("Yes", (dialog, which) -> fStore.collection("userFavorites").document(userID).collection("restaurantList").
                                    whereEqualTo("name",model.getName()).get().addOnSuccessListener(queryDocumentSnapshots -> {
                                        WriteBatch batch = fStore.batch();
                                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                                        for(DocumentSnapshot snapshot: snapshotList){
                                            batch.delete(snapshot.getReference());
                                        }
                                        batch.commit().addOnSuccessListener(unused -> Log.d("TAG","Deletion Done"));
                                AlertDialog.Builder innerBuilder = new AlertDialog.Builder(UserFav.this);
                                innerBuilder.setMessage("Item Deleted").setNegativeButton("Ok",null);
                                AlertDialog innerAlert = innerBuilder.create();
                                innerAlert.show();
                                    }));
                    AlertDialog builder = alert.create();
                    builder.show();
                });
            }

            @NonNull
            @NotNull
            @Override
            public FavViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_favorite_list,parent,false);
                return new FavViewHolder(view);
            }

        };

        userFavList.setHasFixedSize(true);
        userFavList.setLayoutManager(new LinearLayoutManager(this));
        userFavList.setAdapter(adapter);

        clearAll.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(UserFav.this);
            builder.setTitle("Delete All").setMessage("Remove everything?").
            setNegativeButton("No",null).
            setPositiveButton("Yes", (dialog, which) -> fStore.collection("userFavorites").document(userID).collection("restaurantList").get().addOnCompleteListener(task -> {
                for (QueryDocumentSnapshot snapshot: Objects.requireNonNull(task.getResult()))
                    fStore.collection("userFavorites").document(userID).collection("restaurantList").document(snapshot.getId()).delete();
                AlertDialog.Builder innerBuilder = new AlertDialog.Builder(UserFav.this);
                innerBuilder.setMessage("List has been cleared").setNegativeButton("Ok",null);
                AlertDialog innerAlert = innerBuilder.create();
                innerAlert.show();
            }));
            AlertDialog alert = builder.create();
            alert.show();
        });

    }
    private static class FavViewHolder extends RecyclerView.ViewHolder{

        private final TextView rName,rAddress,rDistance,rCategory,rPrice;
        private final ImageView rImage;
        private final RatingBar ratingBar;
        private final ImageButton getDirection, callRestaurant, getMenu;
        private final FloatingActionButton deleteFromFavorite;
        public FavViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            rName = itemView.findViewById(R.id.tvName);
            rImage = itemView.findViewById(R.id.imageView);
            rAddress = itemView.findViewById(R.id.tvAddress);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            rDistance = itemView.findViewById(R.id.tvDistance);
            rCategory = itemView.findViewById(R.id.tvCategory);
            rPrice = itemView.findViewById(R.id.tvPrice);
            getDirection = itemView.findViewById(R.id.getDirection);
            callRestaurant = itemView.findViewById(R.id.callPhone);
            getMenu = itemView.findViewById(R.id.getMenu);
            deleteFromFavorite = itemView.findViewById(R.id.deleteFromFav);
        }
    }
    @Override
    protected void onStop(){
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart(){
        super.onStart();
        adapter.startListening();
    }
}