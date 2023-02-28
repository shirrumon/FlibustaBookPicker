package com.fp.flibustapicker.utils

import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class FileUtils {
    @RequiresApi(Build.VERSION_CODES.Q)
    fun writeFile(
        fileName: String,
        extension: String,
        body: ResponseBody
    ) {
        var dir: File? = null
        dir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS + "/Books")
                    .toString()
            )
        } else {
            File(Environment.getExternalStorageDirectory().toString() + "/Books")
        }

        if (!dir.exists()) {
            val success = dir.mkdirs()
            if (!success) {
                dir = null
            }
        } else {
            val filename = "$fileName.$extension"
            val downloadedFile = File(dir, filename)
            downloadedFile.createNewFile()

            val inputStream = body.byteStream()
            val fileReader = ByteArray(4096)
            var sizeOfDownloaded = 0
            val fos: OutputStream = FileOutputStream(downloadedFile)

            do {
                val read = inputStream.read(fileReader)
                if (read != -1) {
                    fos.write(fileReader, 0, read)
                    sizeOfDownloaded += read
                }
            } while (read != -1)

            fos.flush()
            fos.close()
        }
    }
}