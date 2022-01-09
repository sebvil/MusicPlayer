package com.sebastianvm.musicplayer

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ErrorHandler @Inject constructor() : Thread.UncaughtExceptionHandler {
    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .callTimeout(10, TimeUnit.SECONDS)
        .build()

    private val defaultUEH = Thread.getDefaultUncaughtExceptionHandler();


    @RequiresApi(Build.VERSION_CODES.O)
    private fun run(error: String, t: Thread, e: Throwable) {
        try {

            val timestamp = DateTimeFormatter
                .ofPattern("yyyy-MM-dd_HH-mm-ss-SSSSSS")
                .withZone(ZoneOffset.UTC)
                .format(Instant.now())

            val body = MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart(
                "file", "error-$timestamp.txt", error.toRequestBody(
                    TEXT_MEDIA
                )
            ).build()
            val request = Request.Builder()
                .url("https://musicplayer.sebastianvm.com")
                .post(body)
                .build()

            Log.i("ERROR", "${request.body}")

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    defaultUEH?.uncaughtException(t, e)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")

                        for ((name, value) in response.headers) {
                            Log.i("Error", "$name: $value")
                        }
                        Log.i("Error", response.body!!.string())
                        defaultUEH?.uncaughtException(t, e)
                    }

                }
            })
        } catch (e: Exception) {
            Log.e("ERROR", "Exception while handling error:")
            e.printStackTrace()
            defaultUEH?.uncaughtException(t, e)

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun uncaughtException(t: Thread, e: Throwable) {
        val error = "$e, ${e.stackTraceToString()}"
        run(error, t, e)
    }

    companion object {
        val TEXT_MEDIA: MediaType = "text/plain; charset=utf-8".toMediaType()

    }
}