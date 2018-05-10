// IMyAidlInterface.aidl
package com.andr0day.memoryfiletest;
import android.os.ParcelFileDescriptor;

// Declare any non-default types here with import statements

interface IMyAidlInterface {

    ParcelFileDescriptor getFileDescriptor();
}
