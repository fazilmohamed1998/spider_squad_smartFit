package com.project.smartfit.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.smartfit.EditActivity;
import com.project.smartfit.R;
import com.project.smartfit.databinding.FragmentDashboardBinding;
import com.project.smartfit.ui.model.User;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private User user;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);

        SharedPreferences preferences = getActivity().getSharedPreferences("userIdPrefs", Context.MODE_PRIVATE);
        String userId = preferences.getString("userId", "default");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User u = dataSnapshot.getValue(User.class);

                    if (u != null && u.getId().equals(userId)) {
                        user = u;
                        binding.nameTextView.setText(u.getName());

                        String bmi = String.format("%.2f", u.getBmiValue());
                        binding.bmiValueTextView.setText(bmi);

                        String bmiText, bmiDescText;

                        if (u.getBmiValue() < 18.5) {
                            bmiText = "Underweight";
                            bmiDescText = getResources().getString(R.string.des_underweight);
                            binding.card1.setBackground(getResources().getDrawable(R.drawable.card_underweight_background));
                            binding.bmiRangeTextView.setText("Underweight Range\n< 18.5");
                            binding.bmiRangeTextView.setTextColor(getResources().getColor(R.color.blue_500));

                        }else if (u.getBmiValue() >= 18.5 && u.getBmiValue() < 25.0) {
                            bmiText = "Healthy";
                            bmiDescText = getResources().getString(R.string.des_healthy);
                            binding.card1.setBackground(getResources().getDrawable(R.drawable.card_healthy_background));
                            binding.bmiRangeTextView.setText("Healthy Range\n18.5 - 24.9");
                            binding.bmiRangeTextView.setTextColor(getResources().getColor(R.color.green_500));

                        }else if (u.getBmiValue() >= 25.0 && u.getBmiValue() < 30.0) {
                            bmiText = "Pre-Obesity";
                            bmiDescText = getResources().getString(R.string.des_pre);
                            binding.card1.setBackground(getResources().getDrawable(R.drawable.card_pre_background));
                            binding.bmiRangeTextView.setText("Pre-Obesity Range\n25.0 - 29.9");
                            binding.bmiRangeTextView.setTextColor(getResources().getColor(R.color.yellow_500));

                        }else if (u.getBmiValue() >= 30.0) {
                            bmiText = "Obesity";
                            bmiDescText = getResources().getString(R.string.des_obesity);
                            binding.card1.setBackground(getResources().getDrawable(R.drawable.card_obesity_background));
                            binding.bmiRangeTextView.setText("Obesity Range\n> 30.0");
                            binding.bmiRangeTextView.setTextColor(getResources().getColor(R.color.red_500));

                        }else {
                            bmiText = "Undefined";
                            bmiDescText = "Undefined";
                        }

                        binding.bmiTextTextView.setText(bmiText);
                        binding.bmiDescTextView.setText(bmiDescText);
                        binding.weightRangeTextView.setText(calculateWeightRange(u.getHeight()));

                        String age = u.getAge() + " Years";
                        binding.ageValueTextView.setText(age);

                        String weight = u.getWeight() + " kg";
                        binding.weightValueTextView.setText(weight);

                        String height = u.getHeight() + " cm";
                        binding.heightValueTextView.setText(height);

                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.card2.setOnClickListener(view -> {
            Intent i = new Intent(getActivity(), EditActivity.class);
            i.putExtra("user", user);
            startActivity(i);
        });

        return binding.getRoot();
    }

    private String calculateWeightRange(double height) {
        String weightRangeText = "Your healthy weight range: ";

        double minWeight, maxWeight;

        minWeight = (18.5 * height * height) / (100 * 100);
        maxWeight = (24.9 * height * height) / (100 * 100);

        weightRangeText += String.format("%.2f - %.2f kg", minWeight, maxWeight);

        return weightRangeText;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}