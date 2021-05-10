package com.example.eatby.restaurants;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eatby.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Reviews extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        TextView restaurantTitle = findViewById(R.id.restaurantName);
        String imagePathOfRestaurant = getIntent().getStringExtra("imageUrl");
        TextView userReview = findViewById(R.id.writeReview);
        RatingBar rBar = findViewById(R.id.ratingBar);
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        String userId = fAuth.getCurrentUser().getUid();
        Button submit = findViewById(R.id.submitReview);

        restaurantTitle.setText(getIntent().getStringExtra("title"));

        submit.setOnClickListener(v -> {
            DocumentReference dRef = fStore.collection("userReviews").document(userId).collection("reviewList").document();
            Map<String,Object> userReviews = new HashMap<>();
            userReviews.put("restaurantName",restaurantTitle.getText().toString());
            userReviews.put("image",imagePathOfRestaurant);
            userReviews.put("review",userReview.getText().toString());
            userReviews.put("userRating",rBar.getRating());
            dRef.set(userReviews).addOnSuccessListener(unused -> Log.d("TAG","onSuccess: user review stored")).addOnFailureListener(e -> Log.d("TAG","onFailure"+e.toString()));
            Toast.makeText(getApplicationContext(),"Thank you for reviewing",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Reviews.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

    }
}
