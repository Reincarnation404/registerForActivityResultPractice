package com.example.registerforactivityresultpractice;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import static android.Manifest.permission.*;

import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Toast;

import com.example.registerforactivityresultpractice.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    public ChatService mService;
    public static final int REQUEST_CODE = 1;
    public static final int RESULT_CODE = 2;
    public static final int CANCEL_CODE = 3;

    public static final int REQUEST_EXTERNAL_STORAGE=0;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    System.out.println("取得外部存取權限");
                }else{
                    //使用者拒絕
                    new AlertDialog.Builder(this)
                            .setMessage("確定拒絕?")
                            .setPositiveButton("Yes",null)
                            .show();
                    System.out.println("使用者拒絕");
                    return;

                }

        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int permission = ActivityCompat.checkSelfPermission(this,WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED){
            //未取得權限,向使用者請求權限的類別方法
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE);
        }else{
            //已取得
            System.out.println("已有權限");
        }

        //BroadcastReceiver Step3: 建立IntentFilter物件，並指定接收的識別標籤為螢幕開啟
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        MyReceiver2 myReceiver = new MyReceiver2();
        //BroadcastReceiver Step4:註冊Receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,intentFilter);

        //BroadcastReceiver Step5: 建立帶有識別標籤的Intent
        Intent i = new Intent(Intent.ACTION_SCREEN_ON);
        //BroadcastReceiver Step6: 發送懬播
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);


        binding.btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = binding.txtServiceMsg.getText().toString();
                if (mService != null){
                    mService.sendMessage(msg);
                }
            }
        });

        binding.btnService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService(new Intent(MainActivity.this, MyService.class));
            }
        });




        binding.buttonFillOutForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,FormActivity.class);

                arl.launch(intent);
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(MainActivity.this, ChatService.class),connection, Context.BIND_AUTO_CREATE);
        System.out.println("bindService");
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(connection);
        System.out.println("unbindService");
        stopService(new Intent(MainActivity.this, MyService.class));
    }

    ActivityResultLauncher<Intent> arl = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode()==RESULT_CANCELED || result.getResultCode()==CANCEL_CODE){
                    Toast.makeText(this, "取消填寫", Toast.LENGTH_SHORT).show();
                }else if(result.getResultCode()==RESULT_CODE){
                    String name = result.getData().getStringExtra(FormActivity.NAME);
                    String age = result.getData().getStringExtra(FormActivity.AGE);
                    boolean student = result.getData().getBooleanExtra(FormActivity.STUDENT,false);
                    binding.textViewResult.setText("名字:"+name+"\n年齡:"+age+"\n是否為學生:"+student);
                }
            }
    );

    //BroadcastReceiver Step1: 建立BroadcastReceiver物件
    public static class MyReceiver2 extends BroadcastReceiver {

        //BroadcastReceiver Step2: 在onReceive()中加入接收廣播後要執行的動作
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("onReceive: Screen on");
        }
    }

    //綁定型service class
    public static class ChatService extends Service {
        ChatBinder mBinder =new ChatBinder();
        public class ChatBinder extends Binder{
            public ChatService getService(){
                return ChatService.this;
            }
        }
        public ChatService() {
        }

        @Override
        public IBinder onBind(Intent intent) {
            return mBinder;
        }

        public void sendMessage(String msg){
            System.out.println("ChatService sendMessage:"+msg);
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ChatService.ChatBinder binder = (ChatService.ChatBinder) iBinder;
            mService = binder.getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
        }
    };

}