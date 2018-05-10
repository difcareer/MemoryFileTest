package com.andr0day.memoryfiletest;

import android.os.MemoryFile;

import java.io.BufferedWriter;
import java.io.FileDescriptor;
import java.io.OutputStreamWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MemoryFileUtil {
    private static final String GET_FILE_DESCRIPTOR = "getFileDescriptor";

    public static MemoryFile createMemory() {
        try {
            MemoryFile memoryFile = new MemoryFile("test", 1024);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(memoryFile.getOutputStream()));
            bw.write("this is a test\n");
            bw.flush();
            bw.close();
            return memoryFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static FileDescriptor getFileDescriptor(MemoryFile memoryFile) {
        try {
            Method m = getMethod(memoryFile, GET_FILE_DESCRIPTOR);
            m.setAccessible(true);
            FileDescriptor fd = (FileDescriptor) m.invoke(memoryFile);
            return fd;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static MemoryFile openMemoryFile(FileDescriptor fd, int length, int mode) {
        MemoryFile memoryFile = null;
        try {
            memoryFile = new MemoryFile("fake", 1024);
            memoryFile.close();
            Class<?> c = MemoryFile.class;
            Method native_mmap = getMethod(memoryFile, "native_mmap");
            native_mmap.setAccessible(true);

            Field mFD = getField(memoryFile, "mFD");
            mFD.setAccessible(true);
            mFD.set(memoryFile, fd);

            Field mLength = getField(memoryFile, "mLength");
            mLength.setAccessible(true);
            mLength.set(memoryFile, length);

            Field mAddress = getField(memoryFile, "mAddress");
            long addr = (long) native_mmap.invoke(memoryFile, fd, length, mode);
            mAddress.setAccessible(true);
            mAddress.set(memoryFile, addr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return memoryFile;
    }

    public static Method getMethod(Object obj, String name) {
        Class clazz = obj.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method m : methods) {
            if (m.getName().equals(name)) {
                return m;
            }
        }
        return null;
    }

    public static Field getField(Object obj, String name) {
        Class clazz = obj.getClass();
        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Constructor getConstructor(Class clazz, Class<?>... parameterTypes) {
        try {
            return clazz.getConstructor(parameterTypes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
