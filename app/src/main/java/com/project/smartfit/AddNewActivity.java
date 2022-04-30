package com.project.smartfit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.smartfit.databinding.ActivityAddNewBinding;
import com.project.smartfit.ui.model.UserActivity;

import java.util.Arrays;

public class AddNewActivity extends AppCompatActivity {

    private ActivityAddNewBinding binding;
    private ArrayAdapter<String> activityAdapter, amountAdapter;
    private Button saveBtn;
    private String type = "", activity = "", amount = "";
    private String userId;
    private int calories;
    private UserActivity selectedActivity;
    private boolean isTypeSet = false, isActivitySet = false, isAmountSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences preferences = getSharedPreferences("userIdPrefs", MODE_PRIVATE);
        userId = preferences.getString("userId", "default");

        Spinner typeSpinner = binding.typeSpinner;
        Spinner activitySpinner = binding.activitySpinner;
        Spinner amountSpinner = binding.amountSpinner;
        saveBtn = binding.saveBtn;

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, R.layout.spinner_dropdown_item, R.id.itemText, getResources().getStringArray(R.array.type_spinner));
        typeSpinner.setAdapter(typeAdapter);

        selectedActivity = (UserActivity) getIntent().getSerializableExtra("selectedActivity");

        if (selectedActivity != null) {
            binding.deleteBtn.setVisibility(View.VISIBLE);

            binding.deleteBtn.setOnClickListener(view -> {
                deleteData();
            });

        }else {
            binding.deleteBtn.setVisibility(View.GONE);
        }

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (selectedActivity != null && !isTypeSet) {
                    int pos = 0;
                    String[] typeArr = getResources().getStringArray(R.array.type_spinner);

                    for (String s : typeArr) {
                        if (s.equals(selectedActivity.getType())) {
                            pos = Arrays.asList(typeArr).indexOf(s);
                            break;
                        }
                    }

                    adapterView.setSelection(pos);

                    isTypeSet = true;
                }

                if (adapterView.getSelectedItem().equals("Intake")) {
                    activityAdapter = new ArrayAdapter<>(AddNewActivity.this, R.layout.spinner_dropdown_item, R.id.itemText, getResources().getStringArray(R.array.intake_spinner));
                    amountAdapter = new ArrayAdapter<>(AddNewActivity.this, R.layout.spinner_dropdown_item, R.id.itemText, getResources().getStringArray(R.array.amount_spinner));
                    type = "Intake";
                } else {
                    activityAdapter = new ArrayAdapter<>(AddNewActivity.this, R.layout.spinner_dropdown_item, R.id.itemText, getResources().getStringArray(R.array.burn_spinner));
                    amountAdapter = new ArrayAdapter<>(AddNewActivity.this, R.layout.spinner_dropdown_item, R.id.itemText, getResources().getStringArray(R.array.distance_spinner));
                    type = "Burn";
                }

                activitySpinner.setAdapter(activityAdapter);
                amountSpinner.setAdapter(amountAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        activitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (selectedActivity != null && !isActivitySet) {
                    int pos = 0;
                    String[] intakeArr = getResources().getStringArray(R.array.intake_spinner);
                    String[] burnArr = getResources().getStringArray(R.array.burn_spinner);

                    for (String s : intakeArr) {
                        if (s.equals(selectedActivity.getActivity())) {
                            pos = Arrays.asList(intakeArr).indexOf(s);
                            break;
                        }
                    }

                    for (String s : burnArr) {
                        if (s.equals(selectedActivity.getActivity())) {
                            pos = Arrays.asList(burnArr).indexOf(s);
                            break;
                        }
                    }

                    adapterView.setSelection(pos);

                    isActivitySet = true;
                }

                if (adapterView.getSelectedItemPosition() != 0) {
                    activity = adapterView.getSelectedItem().toString();
                    calories = 100 * (adapterView.getSelectedItemPosition() + 1);
                    enableSaveBtn();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        amountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (selectedActivity != null && !isAmountSet) {
                    int pos = 0;
                    String[] amountArr = getResources().getStringArray(R.array.amount_spinner);
                    String[] distanceArr = getResources().getStringArray(R.array.distance_spinner);

                    for (String s : amountArr) {
                        if (s.equals(selectedActivity.getAmount())) {
                            pos = Arrays.asList(amountArr).indexOf(s);
                            break;
                        }
                    }

                    if (pos == 0) {
                        for (String s : distanceArr) {
                            if (s.equals(selectedActivity.getAmount())) {
                                pos = Arrays.asList(distanceArr).indexOf(s);
                                break;
                            }
                        }
                    }

                    adapterView.setSelection(pos);

                    isAmountSet = true;
                }

                if (adapterView.getSelectedItemPosition() != 0) {
                    amount = adapterView.getSelectedItem().toString();
                    calories *= Double.parseDouble(amount);
                    enableSaveBtn();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void enableSaveBtn() {
        saveBtn.setEnabled(!type.equals("") && !activity.equals("") && !amount.equals(""));

        saveBtn.setOnClickListener(view -> {
            saveData();
        });
    }

    private void saveData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users/" + userId + "/activities");

        if (selectedActivity == null) {
            String key = reference.push().getKey();

            UserActivity activity = new UserActivity(key, type, this.activity, amount, calories);

            if (key != null) {
                reference.child(key).setValue(activity).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        finish();
                    }
                });
            }

        }else {
            selectedActivity.setType(type);
            selectedActivity.setActivity(activity);
            selectedActivity.setAmount(amount);
            selectedActivity.setCalories(calories);

            reference.child(selectedActivity.getId()).setValue(selectedActivity).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    finish();
                }
            });
        }
    }

    private void deleteData() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Are you sure you want to delete activity ?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users/" + userId + "/activities");

                    reference.child(selectedActivity.getId()).removeValue((error, ref) -> {
                        finish();
                    });
                })
                .setNegativeButton("No", (dialogInterface, i) -> {
                    dialogInterface.cancel();
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("Delete activity");
        alertDialog.show();
    }
}