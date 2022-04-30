package com.project.smartfit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.smartfit.databinding.ActivityEditBinding;
import com.project.smartfit.ui.model.User;
import com.project.smartfit.ui.model.UserActivity;

import java.util.ArrayList;
import java.util.List;

public class EditActivity extends AppCompatActivity {

    private ActivityEditBinding binding;
    private User user;
    private List<UserActivity> activities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        user = (User) getIntent().getSerializableExtra("user");

        binding.nameEditText.setText(user.getName());
        binding.ageEditText.setText(String.valueOf(user.getAge()));
        binding.weightEditText.setText(String.valueOf(user.getWeight()));
        binding.heightEditText.setText(String.valueOf(user.getHeight()));

        loadActivityData();

        binding.saveBtn.setOnClickListener(this::validateData);
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
            double bmiValue = (100 * 100 * validatedWeight) / (validatedHeight * validatedHeight);
            saveData(new User(user.getId(), name, validatedAge, validatedWeight, validatedHeight, bmiValue));

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

    private void saveData(User user) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users/" + user.getId());

        reference.setValue(user);

        for (UserActivity activity : activities) {
            reference.child("activities").child(activity.getId()).setValue(activity);
        }

        finish();
    }

    private void loadActivityData() {
        activities = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users/" + user.getId() + "/activities");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                activities.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserActivity activity = dataSnapshot.getValue(UserActivity.class);
                    activities.add(activity);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}