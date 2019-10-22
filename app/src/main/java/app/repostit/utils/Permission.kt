package app.repostit.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity


class Permission {
    companion object {

        fun askForPermission(activity: Activity) {
            if (ContextCompat.checkSelfPermission(activity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                } else {

                    ActivityCompat.requestPermissions(activity,
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            1000)

                }
            } else {

            }
        }
    }
}