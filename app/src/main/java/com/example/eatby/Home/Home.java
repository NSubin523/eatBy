package com.example.eatby.Home;
import com.example.eatby.R;
import com.example.eatby.restaurants.MainActivity;
import com.example.eatby.review_class.ReviewList;
import com.example.eatby.ui.login.LoginActivity;
import com.example.eatby.userFav.UserFav;
import com.example.eatby.user_list.UserList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Home extends AppCompatActivity implements PhotoPickHelper.PhotoPickCallback {
    FirebaseAuth fAuth;
    String userID;
    ImageView yelpImage, androidImage;
    TextView clickHere,userCity,userFName,userLName,userAcc,savedBio;
    ImageView createIcon, preview;
    ImageButton clearIcon,saveBio, editBio;
    PhotoPickHelper photoPickHelper = new PhotoPickHelper(this);
    Button searchBtn;
    FirebaseFirestore db;
    EditText userBio;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        fAuth = FirebaseAuth.getInstance();
        userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        yelpImage = findViewById(R.id.yelpAPI);
        androidImage = findViewById(R.id.androidImage);
        clickHere = findViewById(R.id.clickHere);
        createIcon = findViewById(R.id.createIcon);
        preview = findViewById(R.id.preview);
        clearIcon = findViewById(R.id.clearIcon);
        saveBio = findViewById(R.id.saveUserBio);
        editBio = findViewById(R.id.editBio);
        searchBtn = findViewById(R.id.searchBtn);

        DocumentReference docIdRef = fStore.collection("usersBio").document(userID);
        docIdRef.get().addOnCompleteListener(task -> {
            DocumentSnapshot document = task.getResult();
            assert document != null;
            if(document.exists()) {
                savedBio = findViewById(R.id.savedBio);
                editBio = findViewById(R.id.editBio);
                userBio = findViewById(R.id.aboutYourself);
                saveBio = findViewById(R.id.saveUserBio);
                savedBio.setVisibility(View.VISIBLE);
                editBio.setVisibility(View.VISIBLE);
                userBio.setVisibility(View.INVISIBLE);
                saveBio.setVisibility(View.INVISIBLE);
                DocumentReference dRef = fStore.collection("users").document(userID);
                dRef.addSnapshotListener((value, error) -> {
                    assert value != null;
                    savedBio.setText(value.getString("aboutUser"));
                });
            }
            else userBioListener();
        });
        editBio.setOnClickListener(v -> userBioEdit());
        saveBio.setOnClickListener(v -> userBioListener());

        createIcon.setOnClickListener(v -> showImageSelection());
        clearIcon.setOnClickListener(v -> removeImage());
        photoPickHelper.addPhotoPickCallback(this);
        photoPickHelper.setActivity(this);

        showUser();

        userAccountClicker();

        yelpImage.setOnClickListener(v -> {
            Uri u = Uri.parse("https://www.yelp.com/fusion");
            Intent intent = new Intent(Intent.ACTION_VIEW, u);
            startActivity(intent);
        });

        androidImage.setOnClickListener(v -> {
            Uri u = Uri.parse("https://developer.android.com/");
            Intent intent = new Intent(Intent.ACTION_VIEW, u);
            startActivity(intent);
        });

        clickHere.setOnClickListener(v -> {
            Uri u = Uri.parse("https://github.com/NSubin523");
            Intent intent = new Intent(Intent.ACTION_VIEW, u);
            startActivity(intent);
        });

       searchBtn.setOnClickListener(v -> {
           Intent intent = new Intent(getApplicationContext(), MainActivity.class);
           startActivity(intent);
       } );
    }

    private void userAccountClicker(){
        userAcc = findViewById(R.id.userAcc);
        userAcc.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this,userAcc);
            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.verifyAccount){
                    Uri u = Uri.parse("https://accounts.google.com/signin/v2/identifier?continue=https%3A%2F%2Fmail.google.com%2Fmail%2F&service=mail&sacu=1&rip=1&flowName=GlifWebSignIn&flowEntry=ServiceLogin");
                    Intent intent = new Intent(Intent.ACTION_VIEW,u);
                    startActivity(intent);
                    return true;
                }
                else if (itemId == R.id.signOut){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                    builder.setTitle("LogOut").setMessage("Do you want to Sign out?").
                            setNegativeButton("No",null).
                            setPositiveButton("Yes", (dialog, which) -> {
                                FirebaseAuth.getInstance().signOut();
                                Intent logOut = new Intent(getApplicationContext(), LoginActivity.class);
                                logOut.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                logOut.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(logOut);
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                    return true;
                }
                else if (itemId == R.id.usersFriends){
                    Intent intent = new Intent(getApplicationContext(), UserList.class);
                    startActivity(intent);
                    return true;
                }
                else if (itemId == R.id.userFavorites){
                    Intent intent = new Intent(getApplicationContext(), UserFav.class);
                    startActivity(intent);
                    return true;
                }
                else if(itemId == R.id.myReviews){
                    Intent intent = new Intent(getApplicationContext(), ReviewList.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            });
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.useraccount, popupMenu.getMenu());
            popupMenu.show();
        });
    }

    private void showImageSelection(){
        PopupMenu popup = new PopupMenu(this, createIcon);
        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.select_images_from_local) {
                photoPickHelper.requestPickPhoto();
                return true;
            } else if (itemId == R.id.take_photo_using_camera) {
                photoPickHelper.requestTakePhoto();
                return true;
            }
            return false;
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.camera_button_menu, popup.getMenu());
        popup.show();
    }
    private void removeImage(){
        createIcon.setVisibility(View.VISIBLE);
        clearIcon.setVisibility(View.GONE);
        preview.setVisibility(View.GONE);
    }

    private void showUser(){
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID =  Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        userFName = findViewById(R.id.userFname);
        userLName = findViewById(R.id.userLname);
        userCity = findViewById(R.id.userCity);

        DocumentReference dRef = db.collection("users").document(userID);
        dRef.addSnapshotListener(this, (value, error) -> {
            assert value != null;
            userFName.setText(value.getString("firstName"));
            userLName.setText(value.getString("lastName"));
            userCity.setText(value.getString("userCity"));
        });

    }

    private void userBioListener(){
        userBio = findViewById(R.id.aboutYourself);
        editBio = findViewById(R.id.editBio);
        savedBio = findViewById(R.id.savedBio);
        fAuth = FirebaseAuth.getInstance();
        userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        DocumentReference dRef = fStore.collection("users").document(userID);
        dRef.addSnapshotListener(this, (value, error) -> {
            assert value != null;
            savedBio.setText(value.getString("aboutUser"));
        });

        savedBio.setVisibility(View.VISIBLE);
        userBio.setVisibility(View.INVISIBLE);
        editBio.setVisibility(View.VISIBLE);
    }

    private void userBioEdit(){
        editBio = findViewById(R.id.editBio);
        userBio = findViewById(R.id.aboutYourself);
        savedBio = findViewById(R.id.savedBio);
        saveBio = findViewById(R.id.saveUserBio);
        userBio.setVisibility(View.VISIBLE);
        saveBio.setVisibility(View.VISIBLE);
        editBio.setVisibility(View.INVISIBLE);
        savedBio.setVisibility(View.INVISIBLE);
        saveBio.setOnClickListener(v -> {
           DocumentReference dRef = fStore.collection("users").document(userID);
           Map<String,Object> map = new HashMap<>();
           map.put("aboutUser",userBio.getText().toString());
           dRef.update(map);
           savedBio.setVisibility(View.VISIBLE);

        });
    }

    @Override
    public void showError(boolean b, Throwable e) {

    }

    @Override
    public void setUpImage(String currentPhotoPath) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}