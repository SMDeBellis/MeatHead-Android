package com.sdrockstarstudios.meatheadandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainMenuActivity extends AppCompatActivity {

    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem userProfile = menu.findItem(R.id.user);
        userProfile.setTitle(currentUser.getDisplayName());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.signout:
                signOutUser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void signOutUser() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent loginActivityIntent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(loginActivityIntent);
                    }
                });
    }

    public void pressWorkoutsButton(View view){
        Intent workoutLogIntent = new Intent(this, CurrentWorkoutLogMenuActivity.class);
        startActivity(workoutLogIntent);
    }

    public void pressPlanningButton(View view){
        Intent preplannedWorkoutIntent = new Intent(this, PreplannedWorkoutLogMenuActivity.class);
        startActivity(preplannedWorkoutIntent);
    }
}