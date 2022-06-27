package com.example.sp20_bse_024_roomapi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

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

        database = Room.databaseBuilder(this, AppDataBase.class, "User").fallbackToDestructiveMigration().build();
    }
    public void addData(View v){
        // Room API calls should be in a separate thread
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                User user = new User();
                user.name = user_name.getText().toString();
                user.email = mail.getText().toString();
                user.address = address.getText().toString();
                user.marital_status = radioButton.getCheckedRadioButtonId();
                UserDao userDao = database.userDao();
                userDao.insertOne(user);
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
                User user = userDao.findByName(user_name.getText().toString());
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
                User user = userDao.findByName(user_name.getText().toString());

                if(user == null) return;

                user.name = user_name.getText().toString();
                user.email = mail.getText().toString();
                user.address = address.getText().toString();
                user.marital_status = radioButton.getCheckedRadioButtonId();

                userDao.updateOne(user);

                v.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "User Updated!", Toast.LENGTH_SHORT).show();
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
                User user = userDao.findByName(user_name.getText().toString());

                if(user == null) return;

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