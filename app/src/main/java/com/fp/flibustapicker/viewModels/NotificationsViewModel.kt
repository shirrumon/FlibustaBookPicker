package com.fp.flibustapicker.viewModels

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fp.flibustapicker.MainActivity.Companion.applicationContext
import com.fp.flibustapicker.di.MainNotificationCompatBuilder
import com.fp.flibustapicker.di.SecondNotificationCompatBuilder
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class NotificationsViewModel @Inject constructor(
    @MainNotificationCompatBuilder
     val notificationBuilder: NotificationCompat.Builder,
    @SecondNotificationCompatBuilder
     val notificationBuilder2: NotificationCompat.Builder,
     val notificationManager: NotificationManagerCompat,
) : ViewModel() {
    val countOfPercentage: MutableLiveData<Int> = MutableLiveData()
    @SuppressLint("MissingPermission")
    fun showProgress() {
        val max = 10
        var progress = 0
        viewModelScope.launch {
            if (progress != max) {
                delay(1000)
                progress += 1
                notificationManager.notify(
                    3,
                    notificationBuilder2
                        .setContentTitle("Downloading")
                        .setContentText("${progress}/${max}")
                        .setProgress(max, progress, false).build()
                )
            } else {
                notificationManager.notify(
                    3,
                    notificationBuilder
                        .setContentTitle("Completed!")
                        .setContentText("")
                        .setContentIntent(null)
                        .clearActions()
                        .setProgress(0, 0, false).build()
                )
            }
        }
    }

}