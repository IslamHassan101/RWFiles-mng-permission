package com.islam.rwfiles_mng_permission

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.islam.rwfiles_mng_permission.ui.theme.RWFilesmngpermissionTheme
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RWFilesmngpermissionTheme {
                RwFiles()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.R)
fun requestPermission(context: Context, activity: Activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        if (Environment.isExternalStorageManager()) {
            Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "permission not granted", Toast.LENGTH_SHORT).show()
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                data = Uri.parse(String.format("package:%s", activity.packageName))
            }
            context.startActivity(intent)
        }
    } else {
        Toast.makeText(context, "Permission granted on < version conde R", Toast.LENGTH_SHORT)
            .show()
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                android.Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            23
        )
    }
}

private fun writeData(data: String) {
    val folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val file = File(folder, "islam.txt")

    var fileOutputStream: FileOutputStream? = null
    try {
        fileOutputStream = FileOutputStream(file)
        fileOutputStream.write(data.toByteArray())
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    } finally {
        if (fileOutputStream != null) {
            try {
                fileOutputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}

private fun getData(): String {
    val folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val myFile = File(folder, "islam.txt")
    var fileInputStream: FileInputStream? = null
    try {
        fileInputStream = FileInputStream(myFile)
        var i = -1
        val buffer = StringBuffer()
        while (fileInputStream.read().also { i = it } != -1) {
            buffer.append(i.toChar())
        }
        return buffer.toString()
    } finally {
        if (fileInputStream != null) {
            try {
                fileInputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    return ""
}

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun RwFiles() {
    val context = LocalContext.current
    val activity = context as Activity
    var message by remember { mutableStateOf("") }
    var viewResolute by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {

        OutlinedTextField(modifier = Modifier.fillMaxWidth(),
            value = message,
            onValueChange = { message = it },
            label = { Text(text = "Type Here..") })
        Text(text = viewResolute)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                colors = ButtonDefaults.buttonColors(MaterialTheme.colors.primary),
                onClick = {
                    writeData(data = message)
                    requestPermission(context, activity)
                }) {
                Text(text = "Save")
            }
            Button(
                colors = ButtonDefaults.buttonColors(MaterialTheme.colors.secondary),
                onClick = {
                    viewResolute = getData()
                    requestPermission(context, activity)
                }) {
                Text(text = "Load")
            }
        }
    }
}
