package com.project.smartfit.ui.main;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.smartfit.AddNewActivity;
import com.project.smartfit.databinding.FragmentActivitiesBinding;
import com.project.smartfit.ui.adapter.ActivityRecyclerViewAdapter;
import com.project.smartfit.ui.model.UserActivity;

import java.util.ArrayList;
import java.util.List;

public class ActivitiesFragment extends Fragment {

    private FragmentActivitiesBinding binding;
    private RecyclerView recyclerView;
    private String userId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentActivitiesBinding.inflate(inflater, container, false);
        recyclerView = binding.activityRecyclerView;

        SharedPreferences preferences = getActivity().getSharedPreferences("userIdPrefs", MODE_PRIVATE);
        userId = preferences.getString("userId", "default");

        fetchData();

        binding.addNewActivityBtn.setOnClickListener(view -> {
            Intent i = new Intent(getActivity(), AddNewActivity.class);
            startActivity(i);
        });

        return binding.getRoot();
    }

    private void fetchData() {
        List<UserActivity> activityList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users/" + userId + "/activities");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                activityList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserActivity activity = dataSnapshot.getValue(UserActivity.class);
                    activityList.add(activity);
                }

                if (activityList.isEmpty()) {
                    binding.totalBurnCalories.setVisibility(View.GONE);
                    binding.totalIntakeCalories.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    binding.nothingTextView.setVisibility(View.VISIBLE);

                }else {
                    ActivityRecyclerViewAdapter adapter = new ActivityRecyclerViewAdapter(getActivity(), activityList);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    layoutManager.setReverseLayout(true);
                    layoutManager.setStackFromEnd(true);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.hasFixedSize();
                    recyclerView.setAdapter(adapter);

                    binding.totalBurnCalories.setVisibility(View.VISIBLE);
                    binding.totalIntakeCalories.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    binding.nothingTextView.setVisibility(View.GONE);

                    binding.totalIntakeCalories.setText(adapter.getTotalIntakeCalories());
                    binding.totalBurnCalories.setText(adapter.getTotalBurnCalories());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}