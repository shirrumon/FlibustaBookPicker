package com.fp.flibustapicker.viewModels

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fp.flibustapicker.di.MainNotificationCompatBuilder
import com.fp.flibustapicker.di.SecondNotificationCompatBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    @MainNotificationCompatBuilder
    private val notificationBuilder: NotificationCompat.Builder,
    @SecondNotificationCompatBuilder
    private val notificationBuilder2: NotificationCompat.Builder,
    private val notificationManager: NotificationManagerCompat,
    application: Application
): AndroidViewModel(application) {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun showSimpleNotification() {
        if (ActivityCompat.checkSelfPermission(
                getApplication(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(getApplication(), arrayOf(Manifest.permission.POST_NOTIFICATIONS), 102)
            return
        }
        fun updateSimpleNotification() {
            notificationManager.notify(1, notificationBuilder
                .setContentTitle("NEW TITLE")
                .build()
            )
        }

        fun cancelSimpleNotification() {
            notificationManager.cancel(1)
        }

        fun showProgress() {
            val max = 10
            var progress = 0
            viewModelScope.launch {
                while (progress != max) {
                    delay(1000)
                    progress += 1
                    notificationManager.notify(
                        3,
                        notificationBuilder2
                            .setContentTitle("Downloading")
                            .setContentText("${progress}/${max}")
                            .setProgress(max, progress, false).build()
                    )
                }
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