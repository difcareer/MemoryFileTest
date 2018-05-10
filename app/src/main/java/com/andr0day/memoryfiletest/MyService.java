package com.andr0day.memoryfiletest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.MemoryFile;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;

import java.io.FileDescriptor;
import java.lang.reflect.Constructor;

public class MyService extends Service {
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new IMyAidlInterface.Stub() {
            @Override
            public ParcelFileDescriptor getFileDescriptor() throws RemoteException {
                MemoryFile memoryFile = MemoryFileUtil.createMemory();
                FileDescriptor fd = MemoryFileUtil.getFileDescriptor(memoryFile);
                Constructor constructor = MemoryFileUtil.getConstructor(ParcelFileDescriptor.class, FileDescriptor.class);
                constructor.setAccessible(true);
                try {
                    return (ParcelFileDescriptor) constructor.newInstance(fd);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }
}
