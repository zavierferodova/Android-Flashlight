package com.zavierdev.flashlight

import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.annotation.RequiresApi
import com.zavierdev.flashlight.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    companion object {
        const val CAMERA_IDS_STATE = "CAMERA_IDS_STATE"
        const val CURRENT_FLASH_CAMERA_STATE = "CURRENT_FLASH_CAMERA_STATE"
    }

    private lateinit var cameraManager: CameraManager
    private lateinit var binder: ActivityMainBinding
    private lateinit var cameraIds: Array<String>
    private lateinit var currentFlashCameraId: String

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binder.root)
        initState()
        initView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArray(CAMERA_IDS_STATE, cameraIds)
        outState.putString(CURRENT_FLASH_CAMERA_STATE, currentFlashCameraId)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        cameraIds = savedInstanceState.getStringArray(CAMERA_IDS_STATE)!!
        currentFlashCameraId = savedInstanceState.getString(CURRENT_FLASH_CAMERA_STATE)!!
        toggleCurrentFlashText(currentFlashCameraId)
    }

    private fun initState () {
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraIds = cameraManager.cameraIdList
        currentFlashCameraId = cameraIds[0]
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initView() {
        binder.btnToggleFlashlight.setOnClickListener {
            onClickListener(it)
        }
        binder.btnSwitchFlashlight.setOnClickListener {
            onClickListener(it)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun onClickListener (view: View) {
        when (view.id) {
            binder.btnToggleFlashlight.id -> {
                if (binder.btnToggleFlashlight.isChecked) {
                    turnOnFlashLight(currentFlashCameraId)
                } else {
                    turnOffFlashLight(currentFlashCameraId)
                }
            }
            binder.btnSwitchFlashlight.id -> {
                // Turn off flashlight when active
                if (binder.btnToggleFlashlight.isChecked) {
                    turnOffFlashLight(currentFlashCameraId)
                }

                if (currentFlashCameraId == cameraIds[0]) {
                    currentFlashCameraId = cameraIds[1]
                    toggleCurrentFlashText(currentFlashCameraId)
                } else {
                    currentFlashCameraId = cameraIds[0]
                    toggleCurrentFlashText(currentFlashCameraId)
                }

                // Turn on back flashlight with changed current flash camera id
                if (binder.btnToggleFlashlight.isChecked) {
                    turnOnFlashLight(currentFlashCameraId)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun turnOnFlashLight (cameraId: String) {
        try {
            cameraManager.setTorchMode(cameraId, true)
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun turnOffFlashLight (cameraId: String) {
        try {
            cameraManager.setTorchMode(cameraId, false)
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }

    private fun toggleCurrentFlashText (cameraId: String) {
        if (cameraId == cameraIds[0]) {
            binder.tvCurrentFlashlight.text = getText(R.string.back_flashlight)
        } else {
            binder.tvCurrentFlashlight.text = getText(R.string.front_flashlight)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun finish() {
        super.finish()
        turnOffFlashLight(currentFlashCameraId)
    }
}