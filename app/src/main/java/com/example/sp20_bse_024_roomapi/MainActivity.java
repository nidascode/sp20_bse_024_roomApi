package com.example.sp20_bse_024_roomapi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private AppDataBase database;
    private EditText mail, user_name, address;
    private RadioGroup radioButton;
    private Button update, delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mail = findViewById(R.id.mail);
        user_name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        radioButton = findViewById(R.id.rd);
        update = findViewById(R.id.update);
        delete = findViewById(R.id.delete);
        update.setEnabled(false);
        delete.setEnabled(false);

        database = Room.databaseBuilder(this, AppDataBase.class, "users-db").build();
    }
    public void addData(View v){
        // Room API calls should be in a separate thread
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if(mail.getText().toString().equals("")) return;

                User user = new User();
                user.name = user_name.getText().toString();
                user.email = mail.getText().toString();
                user.address = address.getText().toString();
                user.marital_status = radioButton.getCheckedRadioButtonId();
                UserDao userDao = database.userDao();

                try {
                    userDao.insertOne(user);
                    v.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "User data added successfully", Toast.LENGTH_SHORT).show();
                            update.setEnabled(false);
                            delete.setEnabled(false);
                            reset();
                        }
                    });
                } catch (SQLiteConstraintException e) {
                    v.post(new Runnable() {
                        @Override
                        public void run() {
                            if(Objects.requireNonNull(e.getMessage()).contains("code 1555 SQLITE_CONSTRAINT_PRIMARYKEY")) {
                                Toast.makeText(MainActivity.this, "already exist, add unique user", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Some Error", Toast.LENGTH_SHORT).show();
                            }
                            update.setEnabled(false);
                            delete.setEnabled(false);
                        }
                    });
                }
            }
        });
    }

    public void searchData(View v){
        // Room API calls should be in a separate thread
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                UserDao userDao = database.userDao();
                User user = userDao.findByEmail(mail.getText().toString());
                if(user != null) {
                    v.post(new Runnable() {
                        @Override
                        public void run() {
                            mail.setText(user.email);
                            user_name.setText(user.name);
                            address.setText(user.address);
                            radioButton.check(user.marital_status);

                            update.setEnabled(true);
                            delete.setEnabled(true);
                        }
                    });
                }
                else {
                    v.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "User not found!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    public void updateData(View v){
        // Room API calls should be in a separate thread
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                UserDao userDao = database.userDao();
                User user = new User();
                user.email = mail.getText().toString();

                user.name = user_name.getText().toString();
                user.address = address.getText().toString();
                user.marital_status = radioButton.getCheckedRadioButtonId();

                userDao.updateOne(user);

                v.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "User Updated", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void resetAction(View view) {
        reset();
    }

    public void reset() {
        mail.setText("");
        user_name.setText("");
        address.setText("");
        radioButton.clearCheck();
    }

    public void deleteButtonAction(View v){
        // Room API calls should be in a separate thread
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                UserDao userDao = database.userDao();
                User user = new User();
                user.email = mail.getText().toString();

                userDao.delete(user);

                v.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "User Deleted!", Toast.LENGTH_SHORT).show();
                        reset();
                    }
                });
            }
        });
    }
}