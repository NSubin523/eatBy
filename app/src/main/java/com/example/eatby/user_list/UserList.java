package com.example.eatby.user_list;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.eatby.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import org.jetbrains.annotations.NotNull;

public class UserList extends AppCompatActivity {

    private RecyclerView fireStoreList;
    private FirebaseFirestore fStore;
    private FirestoreRecyclerAdapter adapter;
    private EditText searchBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        fireStoreList = findViewById(R.id.fireStoreList);
        fStore = FirebaseFirestore.getInstance();
        Query query = fStore.collection("users");
        FirestoreRecyclerOptions<UsersFireStore> options = new FirestoreRecyclerOptions.Builder<UsersFireStore>()
                .setQuery(query,UsersFireStore.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<UsersFireStore, UsersViewHolder>(options) {
            @NonNull
            @NotNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_user_display,parent,false);
                return new UsersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull @NotNull UsersViewHolder holder, int position, @NonNull @NotNull UsersFireStore model ) {
                holder.fName.setText(model.getFirstName());
                holder.lName.setText(model.getLastName());
                holder.userCity.setText(model.getUserCity());
                holder.userBio.setText(model.getAboutUser());


                holder.getDirection.setOnClickListener(v -> {
                    String address = model.getUserCity();
                    Uri u = Uri.parse("http://maps.google.co.in/maps?q=" + address);
                    Intent startMaps = new Intent(Intent.ACTION_VIEW,u);
                    startActivity(startMaps);
                });

                holder.chatUser.setOnClickListener(v -> {
                    String phoneNum = model.getUserPhone();
                    Intent intent = new Intent("android.intent.action.VIEW");
                    Uri data = Uri.parse("sms:"+phoneNum);
                    intent.setData(data);
                    startActivity(intent);
                });
            }
        };

        fireStoreList.setHasFixedSize(true);
        fireStoreList.setLayoutManager(new LinearLayoutManager(this));
        fireStoreList.setAdapter(adapter);

        searchBar = findViewById(R.id.searchUsers);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                fStore = FirebaseFirestore.getInstance();
                Query query;
                if(s.toString().isEmpty()) query = fStore.collection("users");
                else query = fStore.collection("users").whereEqualTo("firstName", s.toString());
                FirestoreRecyclerOptions<UsersFireStore> options = new FirestoreRecyclerOptions.Builder<UsersFireStore>()
                        .setQuery(query,UsersFireStore.class)
                        .build();
                adapter.updateOptions(options);
            }
        });
        RecyclerView.ItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        fireStoreList.addItemDecoration(divider);
    }

    private static class UsersViewHolder extends RecyclerView.ViewHolder {

        private final TextView fName, lName, userBio, userCity;
        private final ImageView  chatUser, getDirection;

        public UsersViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            fName = itemView.findViewById(R.id.loadedUserFirstName);
            lName = itemView.findViewById(R.id.loadedUserSecondName);
            userBio = itemView.findViewById(R.id.loadedUserBio);
            userCity = itemView.findViewById(R.id.loadedUserCity);
            chatUser = itemView.findViewById(R.id.chatWithUser);
            getDirection = itemView.findViewById(R.id.locationIcon);
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