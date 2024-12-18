package com.example.finalwmp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentActivity extends AppCompatActivity {
    private ListView subjectList;
    private TextView txtTotalCredits;
    private Button btnEnroll;
    private DatabaseHelper dbHelper;
    private List<String> selectedSubjects = new ArrayList<>();
    private int totalCredits = 0;
    private final int MAX_CREDITS = 24;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollment);

        subjectList = findViewById(R.id.subjectList);
        txtTotalCredits = findViewById(R.id.txtTotalCredits);
        btnEnroll = findViewById(R.id.btnEnroll);
        dbHelper = new DatabaseHelper(this);

        // Load subjects from the database
        List<String> subjects = dbHelper.getAllSubjects();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, subjects);
        subjectList.setAdapter(adapter);
        subjectList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        subjectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String subject = subjects.get(position);
                int credits = dbHelper.getSubjectCredits(subject);

                if (selectedSubjects.contains(subject)) {
                    selectedSubjects.remove(subject);
                    totalCredits -= credits;
                } else {
                    if (totalCredits + credits > MAX_CREDITS) {
                        Toast.makeText(EnrollmentActivity.this, "Credit limit exceeded!", Toast.LENGTH_SHORT).show();
                        subjectList.setItemChecked(position, false);
                        return;
                    }
                    selectedSubjects.add(subject);
                    totalCredits += credits;
                }

                txtTotalCredits.setText("Total Credits: " + totalCredits);
            }
        });

        btnEnroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedSubjects.isEmpty()) {
                    Toast.makeText(EnrollmentActivity.this, "No subjects selected!", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean success = dbHelper.enrollSubjects(selectedSubjects);
                if (success) {
                    Toast.makeText(EnrollmentActivity.this, "Enrollment successful!", Toast.LENGTH_SHORT).show();
                    showEnrollmentSummary();// Optionally clear selection
                    selectedSubjects.clear();
                    totalCredits = 0;
                    txtTotalCredits.setText("Total Credits: " + totalCredits);
                    for (int i = 0; i < subjects.size(); i++) {
                        subjectList.setItemChecked(i, false);
                    }
                } else {
                    Toast.makeText(EnrollmentActivity.this, "Enrollment failed. Try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void showEnrollmentSummary() {
        StringBuilder summary = new StringBuilder();
        for (String subject : selectedSubjects) {
            summary.append(subject).append("\n");
        }

        String message = "Enrolled Subjects:\n" + summary.toString() +
                "\nTotal Credits: " + totalCredits;

        new android.app.AlertDialog.Builder(EnrollmentActivity.this)
                .setTitle("Enrollment Summary")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    // Optionally go back to login or exit activity
                    finish();
                })
                .show();
    }
}


