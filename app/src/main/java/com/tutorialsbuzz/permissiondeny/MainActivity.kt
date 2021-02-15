package com.tutorialsbuzz.permissiondeny

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val REQUEST_WRITE_PERMISSION = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    private fun initViews() {
        requestbtn.setOnClickListener(this)
        allow_permission.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        if (checkPermissionForReadExtertalStorage()) {
               label!!.setText(R.string.permission_granted)
              allow_permission.visibility=View.GONE
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.requestbtn -> if (checkPermissionForReadExtertalStorage()) {
                label!!.setText(R.string.permission_granted)
            } else {
                //Make Request
                requestPermission()
            }
            R.id.allow_permission -> {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size > 0) {
            if (requestCode == REQUEST_WRITE_PERMISSION) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                    label!!.setText(R.string.permission_granted)
                    allow_permission!!.visibility = View.GONE
                } else {
                    // permission denied
                    label!!.setText(R.string.permission_denied)
                    // Check wheather checked dont ask again
                    checkUserRequestedDontAskAgain()
                }
            }
        }
    }

    fun checkPermissionForReadExtertalStorage(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED)
        } else false
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_WRITE_PERMISSION
            )
        }
    }

    private fun checkUserRequestedDontAskAgain() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val rationalFalgREAD =
                shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val rationalFalgWRITE =
                shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
            if (!rationalFalgREAD && !rationalFalgWRITE) {
                label!!.setText(R.string.permission_denied_forcefully)
                allow_permission!!.visibility = View.VISIBLE
            }
        }
    }

}