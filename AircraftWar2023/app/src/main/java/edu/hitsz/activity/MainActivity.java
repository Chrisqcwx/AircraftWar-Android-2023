package edu.hitsz.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.hitsz.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    public static final ExecutorService threadPool = Executors.newCachedThreadPool();
    private String name = null;

    private void setName(String name) {
        this.name = name;
        if (name == null) {
            Log.i(TAG, "clear name");
            isLoginLayout.setVisibility(View.GONE);
            notLoginLayout.setVisibility(View.VISIBLE);
        }else {
            Log.i(TAG, "set name: "+name);
            notLoginLayout.setVisibility(View.GONE);
            isLoginLayout.setVisibility(View.VISIBLE);
        }
    }
    private String getName() {
        return name;
    }

    private LinearLayout notLoginLayout;

    private LinearLayout isLoginLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        this.openFileInput()

        setContentView(R.layout.activity_main);

        Button alone_btn = findViewById(R.id.alone_mode);
        Button online_btn = findViewById(R.id.online_mode);

        notLoginLayout = findViewById(R.id.start_notLoginLayout);
        Button login_btn = findViewById(R.id.start_login);
        Button register_btn = findViewById(R.id.start_register);

        isLoginLayout = findViewById(R.id.start_isLoginLayout);
        Button exit_btn = findViewById(R.id.start_exit);


        alone_btn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SelectGameModeActivity.class);
            intent.putExtra("isOnline", false);
            intent.putExtra("usrname", name);
            startActivity(intent);
        });

        online_btn.setOnClickListener(view -> {
            if (getName() == null){
                Toast.makeText(MainActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
            }
            else {
                Intent intent = new Intent(MainActivity.this, SelectGameModeActivity.class);
                intent.putExtra("isOnline", true);
                intent.putExtra("usrname", name);
                startActivity(intent);
            }
        });

        ActivityResultLauncher<Intent> registerOrLoginLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
//                    Log.d(TAG, "onActivityResult: data = " + result.getData().getStringExtra("data_return"));
                    MainActivity.this.setName(result.getData().getStringExtra("name"));
                }
            }
        });
        register_btn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.putExtra("login_or_register", R.string.register);
            registerOrLoginLauncher.launch(intent);
        });


        login_btn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.putExtra("login_or_register", R.string.login);

            registerOrLoginLauncher.launch(intent);
        });

        exit_btn.setOnClickListener(view -> setName(null));



        // TODO: delete
//        setName(String.valueOf(new Random().nextInt(1000)));
    }


}