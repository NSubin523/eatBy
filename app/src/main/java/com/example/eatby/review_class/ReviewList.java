package com.example.eatby.review_class;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.eatby.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReviewList extends AppCompatActivity {
    private RecyclerView userReviewList;
    private FirestoreRecyclerAdapter adapter;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);

        userReviewList = findViewById(R.id.reviewListRecyclerView);
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        String userID = fAuth.getCurrentUser().getUid();
        Query query = fStore.collection("userReviews").document(userID).collection("reviewList");
        FirestoreRecyclerOptions<ReviewDb> options = new FirestoreRecyclerOptions.Builder<ReviewDb>()
                .setQuery(query,ReviewDb.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<ReviewDb,ReviewViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull ReviewViewHolder holder, int position, @NonNull @NotNull ReviewDb model) {
                holder.tvName.setText(model.getRestaurantName());
                Glide.with(ReviewList.this).load(model.getImage()).apply((new RequestOptions()).transforms(new Transformation[]{(Transformation)(new CenterCrop()), (Transformation)(new RoundedCorners(20))})).into(holder.tvImageUrl);
                holder.rBar.setRating(model.getUserRating());
                holder.tvReviewComment.setText("\""+model.getReview()+"\"");
                holder.deleteReview.setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ReviewList.this);
                    builder.setTitle("Delete").setMessage("Do you want to delete this review?").
                    setNegativeButton("No",null).
                    setPositiveButton("Yes", (dialog, which) -> fStore.collection("userReviews").document(userID).collection("reviewList").whereEqualTo("restaurantName",model.getRestaurantName()).
                    get().addOnSuccessListener(queryDocumentSnapshots -> {
                        WriteBatch batch = fStore.batch();
                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                        for(DocumentSnapshot snapshot: snapshotList){
                            batch.delete(snapshot.getReference());
                        }
                        batch.commit().addOnSuccessListener(unused -> Toast.makeText(ReviewList.this,"Review Deleted",Toast.LENGTH_SHORT).show());
                    }));
                    AlertDialog alert = builder.create();
                    alert.show();
                });
            }

            @NonNull
            @NotNull
            @Override
            public ReviewViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_review_list,parent,false);
                return new ReviewViewHolder(view);
            }
        };
        userReviewList.setHasFixedSize(true);
        userReviewList.setLayoutManager(new LinearLayoutManager(this));
        userReviewList.setAdapter(adapter);
    }
    private static class ReviewViewHolder extends RecyclerView.ViewHolder{
        private final TextView tvName, tvReviewComment;
        private final ImageView tvImageUrl;
        private final RatingBar rBar;
        private final FloatingActionButton deleteReview;
        public ReviewViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.restaurantNameReview);
            tvReviewComment = itemView.findViewById(R.id.tvReviewComment);
            tvImageUrl = itemView.findViewById(R.id.imageViewReview);
            rBar = itemView.findViewById(R.id.ratingBarReview);
            deleteReview = itemView.findViewById(R.id.floatingActionButtonDelete);
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