package com.andr0day.memoryfiletest;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.MemoryFile;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyService.class);
                bindService(intent, new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName name, IBinder service) {
                        IMyAidlInterface iMyAidlInterface = IMyAidlInterface.Stub.asInterface(service);
                        try {
                            ParcelFileDescriptor pfd = iMyAidlInterface.getFileDescriptor();

                            MemoryFile memoryFile = MemoryFileUtil.openMemoryFile(pfd.getFileDescriptor(), 1024, 3);
                            BufferedReader br = new BufferedReader(new InputStreamReader(memoryFile.getInputStream()));
                            String line = br.readLine();
                            Log.e("XXX", "txt:" + line);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {

                    }
                }, BIND_AUTO_CREATE);
            }
        });
    }
}
