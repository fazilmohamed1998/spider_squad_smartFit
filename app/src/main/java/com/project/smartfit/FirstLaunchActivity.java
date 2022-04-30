package com.project.smartfit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.smartfit.databinding.ActivityFirstLaunchBinding;
import com.project.smartfit.ui.model.User;
import com.project.smartfit.ui.model.UserActivity;

import java.util.ArrayList;
import java.util.List;

public class FirstLaunchActivity extends AppCompatActivity {

    private ActivityFirstLaunchBinding binding;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFirstLaunchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences("userIdPrefs", MODE_PRIVATE);

        binding.continueBtn.setOnClickListener(this::validateData);
    }

    private void validateData(View view) {
        List<String> err = new ArrayList<>();

        String name = binding.nameEditText.getText().toString();
        String age = binding.ageEditText.getText().toString();
        String weight = binding.weightEditText.getText().toString();
        String height = binding.heightEditText.getText().toString();

        int validatedAge = 0;
        double validatedWeight = 0.0, validatedHeight = 0.0;

        if (name.equals("")) {
            err.add("Name field cannot be empty.");
        }

        if (age.equals("")) {
            err.add("Age field cannot be empty.");
        }else {
            validatedAge = Integer.parseInt(age);

            if (validatedAge < 1) {
                err.add("Enter a valid age.");
            }
        }

        if (weight.equals("")) {
            err.add("Weight field cannot be empty.");
        }else {
            validatedWeight = Double.parseDouble(weight);

            if (validatedWeight < 1) {
                err.add("Enter a valid weight.");
            }
        }

        if (height.equals("")) {
            err.add("Height field cannot be empty.");
        }else {
            validatedHeight = Double.parseDouble(height);

            if (validatedHeight < 1) {
                err.add("Enter a valid height.");
            }
        }

        if (err.isEmpty()) {
            saveData(name, validatedAge, validatedWeight, validatedHeight);

        }else {
            StringBuilder errMsg = new StringBuilder();

            for(String e : err) {
                errMsg.append(e).append("\n");
            }

            Snackbar snackbar = Snackbar.make(view, errMsg.toString(), Snackbar.LENGTH_LONG);
            View snackView = snackbar.getView();
            TextView snackTextView = snackView.findViewById(com.google.android.material.R.id.snackbar_text);
            snackTextView.setMaxLines(4);
            snackbar.show();
        }
    }

    private void saveData(String name, int age, double weight, double height) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        String key = reference.push().getKey();

        double bmiValue = (100 * 100 * weight) / (height * height);

        User user = new User(key, name, age, weight, height, bmiValue);

        if (key != null) {
            reference.child(key).setValue(user).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userId", key);
                    editor.apply();

                    Intent i = new Intent(FirstLaunchActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            });
        }
    }
}