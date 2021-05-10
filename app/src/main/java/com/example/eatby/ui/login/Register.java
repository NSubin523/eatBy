package com.example.eatby.ui.login;

import com.example.eatby.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button storeInfoAndNavUser = findViewById(R.id.registerUser);
        EditText firstName = findViewById(R.id.editFirstNameLayout);
        EditText lastName = findViewById(R.id.editLastNameLayout);
        EditText userEmail = findViewById(R.id.editUserEmailLayout);
        EditText userPw = findViewById(R.id.editPassword);
        EditText userPhone = findViewById(R.id.editUserPh);
        EditText userCity = findViewById(R.id.editUserCity);
        EditText userState = findViewById(R.id.editUserState);
        EditText aboutUser = findViewById(R.id.editSetUserBio);
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();

        storeInfoAndNavUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidFirstName())
                {
                    if(isValidLastName())
                    {
                        if(isEmailValid()){
                            if(isValidPassword()){
                                if(isValidPhone()){
                                    if(isValidCity()){
                                        if(isValidState()){
                                            if(isValidZip()){
                                                fAuth.createUserWithEmailAndPassword(userEmail.getText().toString(),userPw.getText().toString()).addOnCompleteListener(task -> {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(getApplicationContext(),"Registration Complete!!",Toast.LENGTH_SHORT).show();
                                                        userID = fAuth.getCurrentUser().getUid();
                                                        Map<String,Object> user = new HashMap<>();
                                                        DocumentReference dRef = fStore.collection("users").document(userID);

                                                        user.put("firstName",firstName.getText().toString());
                                                        user.put("lastName",lastName.getText().toString());
                                                        user.put("userEmail",userEmail.getText().toString());
                                                        user.put("userPw",userPw.getText().toString());
                                                        user.put("userCity",userCity.getText().toString());
                                                        user.put("userPhone",userPhone.getText().toString());
                                                        user.put("aboutUser",aboutUser.getText().toString());

                                                        dRef.set(user).addOnSuccessListener(unused -> Log.d("TAG","onSuccess: User Profile created")).addOnFailureListener(e -> Log.d("TAG","onFailure: "+e.toString()));
                                                        Intent navigateUserToLogin = new Intent(Register.this,LoginActivity.class);
                                                        startActivity(navigateUserToLogin);
                                                    }
                                                    else{
                                                        Toast.makeText(getApplicationContext(),"Error can't connect to firebase",Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                            else{
                                                aboutUser.setError("Error cant be blank...");
                                            }
                                        }
                                        else{
                                            userState.setError("Invalid State!!");
                                        }
                                    }
                                    else{
                                        userCity.setError("City can't be blank!!");
                                    }
                                }
                                else {
                                    userPhone.setError("Phone No. invalid!!");
                                }
                            }
                            else{
                                userPw.setError("Password cant be blank or less than 5 characters!!");
                            }

                        }
                        else{
                            userEmail.setError("Email Address unidentified");
                        }
                    }
                    else
                    {
                        lastName.setError("Last Name can't be blank or less than 2 characters");
                    }
                }
                else {
                    firstName.setError("First Name can't be blank or less than 2 characters");
                }
            }
            public boolean isValidFirstName(){
                return !TextUtils.isEmpty(firstName.getText()) && firstName.getText().toString().length() >= 2;
            }
            public boolean isValidLastName(){
                return !TextUtils.isEmpty(lastName.getText()) && lastName.getText().toString().length() >= 2;
            }
            public boolean isEmailValid(){
                return !TextUtils.isEmpty(userEmail.getText());
            }
            public boolean isValidPassword(){
                return !TextUtils.isEmpty(userPw.getText()) && userPw.getText().toString().length() >= 5;
            }
            public boolean isValidPhone(){
                String phoneNum = userPhone.getText().toString();
                boolean isNumber;

                isNumber = phoneNum.matches("\\d+(\\.\\d+)?");
                return isNumber && phoneNum.length() == 10;
            }
            public boolean isValidCity(){
                return !TextUtils.isEmpty(userCity.getText());
            }
            public boolean isValidState(){
                boolean check = false;
                String[] usStates = {"AL","AK","AZ","AR","CA","CO","CT","DE","FL","GA","ID","HI","GU","IL","IN","IA","KS","KY","LA","ME","MD","MA",
                        "MI","MN","MO","MS","MT","NE","NV","NH","NJ","NM","NY","NC","ND","MP","OH","OK","OR","PA","PR","RI","SC","SD","TN",
                        "TX","UT","VT","VA","VI","WA","WV","WI","WY"};
                String userInputState = userState.getText().toString();
                for(String st: usStates) {
                    if(userInputState.equals(st)) {
                        userInputState.length();
                        check = true;
                        break;
                    }
                }
                return check;
            }
            public boolean isValidZip(){
                String userBio = aboutUser.getText().toString();
                return !userBio.equals("");
            }
        });
    }
}