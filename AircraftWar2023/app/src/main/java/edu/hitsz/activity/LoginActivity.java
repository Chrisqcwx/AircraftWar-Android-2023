package edu.hitsz.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.hitsz.R;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private static String IP_ADDR = "10.250.173.55";
    public static String getIpAddr(){
        return IP_ADDR;
    }
    public static final int PORT = 9999;

    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView label = findViewById(R.id.login_label);
        EditText usrname_text = findViewById(R.id.login_usrname);
        EditText password_text = findViewById(R.id.login_password);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) EditText ip_text = findViewById(R.id.login_ip);
        Button sendButton = findViewById(R.id.login_send);

        int type = getIntent().getIntExtra("login_or_register", -1);

        if (type == R.string.login) {
            label.setText("登录");
        } else if (type == R.string.register) {
            label.setText("注册");
        } else {
            Log.e (TAG, "invalid type on login/register type");
        }
        Handler failHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    if (type == R.string.login) {
                        Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                    } else if (type == R.string.register) {
                        Toast.makeText(LoginActivity.this, "用户名已存在", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (msg.what == 2) {
                    Toast.makeText(LoginActivity.this, "用户名或密码不得为空", Toast.LENGTH_SHORT).show();
                } else if (msg.what == 3) {
                    Toast.makeText(LoginActivity.this, "无法连接到服务器", Toast.LENGTH_SHORT).show();
                }
            }
        };

        sendButton.setOnClickListener(view -> {
            String username = usrname_text.getText().toString();
            String password = password_text.getText().toString();
            String ip = ip_text.getText().toString();

            if ("".equals(username) || "".equals(password)) {
                Message message = Message.obtain();
                message.what = 2;
                failHandler.sendMessage(message);
            }
            int op = -1;
            if (type == R.string.login) {
                op = 1;
            } else if (type == R.string.register) {
                op = 0;
            }else {
                Log.e(TAG, "invalid type");
                finish();
            }


            int finalOp = op;
            Runnable sendToServerTask = () -> {
                try {
                    if (ip.trim().equals("")) {
                        IP_ADDR = ip;
                    }
                    socket = new Socket(ip, PORT);
                    Log.i(TAG,"connect success");
                    DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                    DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

                    JSONObject json = new JSONObject();
                    json.put("name", username);
                    json.put("op", finalOp);
                    json.put("password", password);
                    String jsonString = json.toString();
                    out.writeUTF(jsonString);
                    out.flush();

                    Log.i(TAG, "send: " + jsonString);

                    JSONObject receiveJSON = new JSONObject(in.readUTF());
                    Log.i(TAG, "receive: "+receiveJSON);
                    boolean isSuccess = receiveJSON.getBoolean("isSuccess");
                    if (isSuccess) {

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("name", username);
                        LoginActivity.this.setResult(RESULT_OK, intent);
                        LoginActivity.this.finish();
                        try {
                            socket.close();
                        }catch (IOException ignored){
                            Log.e(TAG, "Socket close error");
                        }
                    } else {
//                        showFailText();
                        Message message = Message.obtain();
                        message.what = 1;
                        failHandler.sendMessage(message);
                    }

                } catch (UnknownHostException | ConnectException e) {
                    Message message = Message.obtain();
                    message.what = 3;
                    failHandler.sendMessage(message);
                } catch (JSONException | IOException e) {
                    throw new RuntimeException(e);
                }
            };

            MainActivity.threadPool.execute(sendToServerTask);
        });
    }

}