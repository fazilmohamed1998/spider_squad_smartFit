package com.project.smartfit.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.smartfit.AddNewActivity;
import com.project.smartfit.R;
import com.project.smartfit.databinding.ActivityRecyclerviewItemBinding;
import com.project.smartfit.ui.model.UserActivity;

import java.util.List;

public class ActivityRecyclerViewAdapter extends RecyclerView.Adapter<ActivityRecyclerViewAdapter.ViewHolder> {

    private ActivityRecyclerviewItemBinding binding;
    private final Activity context;
    private final List<UserActivity> userActivityList;

    public ActivityRecyclerViewAdapter(Activity context, List<UserActivity> userActivityList) {
        this.context = context;
        this.userActivityList = userActivityList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull ActivityRecyclerviewItemBinding binding) {
            super(binding.getRoot());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ActivityRecyclerviewItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String type = userActivityList.get(position).getType();
        String amount, cal;

        if (type.equals("Burn"))
            amount = userActivityList.get(position).getActivity() + " | " + userActivityList.get(position).getAmount() + " km";
        else
            amount = userActivityList.get(position).getActivity() + " | " + userActivityList.get(position).getAmount();

        if (type.equals("Intake")) {
            cal = "+" + userActivityList.get(position).getCalories() + " Calories";

            binding.itemCardView.setCardBackgroundColor(context.getResources().getColor(R.color.white));
            binding.activityTitleTextView.setTextColor(context.getResources().getColor(R.color.orange_700));
            binding.typeTextView.setTextColor(context.getResources().getColor(R.color.orange_700));
            binding.caloriesTextView.setTextColor(context.getResources().getColor(R.color.orange_700));

        } else {
            cal = "-" + userActivityList.get(position).getCalories() + " Calories";

            binding.itemCardView.setCardBackgroundColor(context.getResources().getColor(R.color.orange_500));
            binding.activityTitleTextView.setTextColor(context.getResources().getColor(R.color.white));
            binding.typeTextView.setTextColor(context.getResources().getColor(R.color.white));
            binding.caloriesTextView.setTextColor(context.getResources().getColor(R.color.white));
        }

        binding.activityTitleTextView.setText(amount);
        binding.typeTextView.setText(type);
        binding.caloriesTextView.setText(cal);

        binding.itemCardView.setOnClickListener(view -> {
            Intent i = new Intent(context, AddNewActivity.class);
            i.putExtra("selectedActivity", userActivityList.get(position));
            context.startActivity(i);
        });
    }

    public String getTotalIntakeCalories() {
        int total = 0;

        for (UserActivity ac : userActivityList) {
            if (ac.getType().equals("Intake"))
                total += ac.getCalories();
        }

        return total + " Calories";
    }

    public String getTotalBurnCalories() {
        int total = 0;

        for (UserActivity ac : userActivityList) {
            if (ac.getType().equals("Burn"))
                total += ac.getCalories();
        }

        return total + " Calories";
    }

    @Override
    public int getItemCount() {
        return userActivityList.size();
    }
}
