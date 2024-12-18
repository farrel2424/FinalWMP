package com.example.finalwmp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "studentEnrollment.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_STUDENT = "Student";
    private static final String TABLE_SUBJECT = "Subject";
    private static final String TABLE_ENROLLMENT = "Enrollment";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createStudentTable = "CREATE TABLE " + TABLE_STUDENT + " (" +
                "student_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "email TEXT UNIQUE, " +
                "password TEXT)";

        String createSubjectTable = "CREATE TABLE " + TABLE_SUBJECT + " (" +
                "subject_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "credits INTEGER)";

        String createEnrollmentTable = "CREATE TABLE " + TABLE_ENROLLMENT + " (" +
                "enrollment_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "student_id INTEGER, " +
                "subject_id INTEGER, " +
                "FOREIGN KEY(student_id) REFERENCES Student(student_id), " +
                "FOREIGN KEY(subject_id) REFERENCES Subject(subject_id))";

        db.execSQL(createStudentTable);
        db.execSQL(createSubjectTable);
        db.execSQL(createEnrollmentTable);

        insertDefaultSubjects(db); // Populate the Subject table
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBJECT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENROLLMENT);
        onCreate(db);
    }

    private void insertDefaultSubjects(SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        values.put("name", "Software Engineering");
        values.put("credits", 3);
        db.insert(TABLE_SUBJECT, null, values);

        values.put("name", "Wireless and Mobile Programming");
        values.put("credits", 4);
        db.insert(TABLE_SUBJECT, null, values);

        values.put("name", "Data Structure and Algorithm");
        values.put("credits", 5);
        db.insert(TABLE_SUBJECT, null, values);

        values.put("name", "Artificial Intelligence");
        values.put("credits", 2);
        db.insert(TABLE_SUBJECT, null, values);

        values.put("name", "Article Writing");
        values.put("credits", 3);
        db.insert(TABLE_SUBJECT, null, values);

        values.put("name", "3D Computer Graphics Animation");
        values.put("credits", 3);
        db.insert(TABLE_SUBJECT, null, values);

        values.put("name", "Numerical Methods");
        values.put("credits", 3);
        db.insert(TABLE_SUBJECT, null, values);

        values.put("name", "Network Security");
        values.put("credits", 3);
        db.insert(TABLE_SUBJECT, null, values);
    }


    public boolean registerStudent(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);
        values.put("password", password);

        long result = db.insert(TABLE_STUDENT, null, values);
        return result != -1;
    }

    public boolean loginStudent(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_STUDENT, null, "email=? AND password=?", new String[]{email, password}, null, null, null);
        boolean success = cursor.getCount() > 0;
        cursor.close();
        return success;
    }



    public List<String> getAllSubjects() {
        List<String> subjects = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM " + TABLE_SUBJECT, null);
        while (cursor.moveToNext()) {
            subjects.add(cursor.getString(0));
        }
        cursor.close();
        return subjects;
    }

    public int getSubjectCredits(String subject) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT credits FROM " + TABLE_SUBJECT + " WHERE name=?", new String[]{subject});
        if (cursor.moveToFirst()) {
            int credits = cursor.getInt(0);
            cursor.close();
            return credits;
        }
        cursor.close();
        return 0;
    }

    public boolean enrollSubjects(List<String> subjects) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            for (String subject : subjects) {
                ContentValues values = new ContentValues();
                int subjectId = getSubjectId(subject);
                if (subjectId == -1) return false;
                values.put("student_id", 1); // Replace with actual student_id
                values.put("subject_id", subjectId);
                db.insert(TABLE_ENROLLMENT, null, values);
            }
            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
        }
    }

    private int getSubjectId(String subject) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT subject_id FROM " + TABLE_SUBJECT + " WHERE name=?", new String[]{subject});
        if (cursor.moveToFirst()) {
            int subjectId = cursor.getInt(0);
            cursor.close();
            return subjectId;
        }
        cursor.close();
        return -1;
    }
}