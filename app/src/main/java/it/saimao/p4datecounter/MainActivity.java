package it.saimao.p4datecounter;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;

import it.saimao.p4datecounter.databinding.ActivityHomeBinding;
import it.saimao.p4datecounter.databinding.DgChangeNameBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private LocalDate startDate;

    private DatePickerDialog datePickerDialog;
    private AlertDialog changeNameDialog;

    private enum GENDER {
        MALE, FEMALE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initData();
        initListener();
    }

    private void initData() {
        String male = Database.readName(this, GENDER.MALE.toString()).isEmpty() ? "Male" : Database.readName(this, GENDER.MALE.toString());
        binding.tvMale.setText(male);

        String female = Database.readName(this, GENDER.FEMALE.toString()).isEmpty() ? "Female" : Database.readName(this, GENDER.FEMALE.toString());
        binding.tvFemale.setText(female);

        long dateLong = Database.readDate(this, Database.DATE);
        startDate = dateLong == -1 ? LocalDate.now() : LocalDate.ofEpochDay(dateLong);

        updateUi();

    }

    private void initListener() {
        binding.btStartDate.setOnClickListener(v -> {
            showDatePicker();
        });

        binding.tvMale.setOnClickListener(v -> {
            showChangeNameDialog(GENDER.MALE);
        });

        binding.tvFemale.setOnClickListener(v -> {
            showChangeNameDialog(GENDER.FEMALE);
        });


    }

    private DgChangeNameBinding changeNameBinding;

    private void showChangeNameDialog(GENDER gender) {

        if (changeNameDialog == null) {
            changeNameBinding = DgChangeNameBinding.inflate(getLayoutInflater());
            changeNameDialog = new AlertDialog.Builder(this)
                    .setView(changeNameBinding.getRoot())
                    .create();

            changeNameBinding.btCancel.setOnClickListener(v -> {
                changeNameDialog.cancel();
            });

        }

        if (gender == GENDER.MALE) {
            changeNameBinding.etlChangeName.setHint("Enter Mr Name");
            changeNameBinding.etChangeName.setText(binding.tvMale.getText().toString());
        } else {
            changeNameBinding.etlChangeName.setHint("Enter Mrs Name");
            changeNameBinding.etChangeName.setText(binding.tvFemale.getText().toString());
        }

        changeNameBinding.btConfirm.setOnClickListener(v -> {
            String newName = changeNameBinding.etChangeName.getText().toString();
            if (gender == GENDER.MALE) {
                binding.tvMale.setText(newName);
            } else {
                binding.tvFemale.setText(newName);
            }
            Database.saveName(this, gender.toString(), newName);
            changeNameDialog.cancel();
        });

        changeNameDialog.show();
    }


//    private void showChangeNameDialog(GENDER gender) {
//        if (changeNameDialog == null) {
//            changeNameDialog = new AlertDialog.Builder(this)
//                    .setTitle("Sample Dialog")
//                    .setMessage("I am alert dialog! IGNORE ME!")
//                    .setPositiveButton("OK", (dialog, which) -> {
//                        String message = gender == GENDER.MALE ? "MALE" : "FEMALE";
//                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
//                        dialog.cancel();
//                    })
//                    .setNegativeButton("Cancel", (dialog, which) -> {
//                        dialog.cancel();
//                    })
//                    .create();
//        }
//        changeNameDialog.show();
//    }

    private void showDatePicker() {
        if (datePickerDialog == null) {
            datePickerDialog = new DatePickerDialog(this);
            datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
                // month is index based : 6 is July!
                startDate = LocalDate.of(year, month + 1, dayOfMonth);
                updateUi();
                Database.saveDate(this, Database.DATE, startDate.toEpochDay());
            });
        }
        datePickerDialog.show();
    }

    private void updateUi() {
        binding.btStartDate.setText(DateTimeFormatter.ofPattern("MMM dd, yyyy").format(startDate));
        long days = LocalDate.now().toEpochDay() - startDate.toEpochDay(); // Epoch Day (1970, 1, 1)
        binding.tvDays.setText(days + " Days");
    }


}