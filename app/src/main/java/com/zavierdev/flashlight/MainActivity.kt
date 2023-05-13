package com.zavierdev.flashlight

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zavierdev.flashlight.ui.font.roboto

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

enum class CameraPosition {
    BACK,
    FRONT
}

@Composable
@Preview(showBackground = false)
fun MainScreen() {
    var isPowerActive by rememberSaveable {
        mutableStateOf(false)
    }
    var activeButton by rememberSaveable {
        mutableStateOf(CameraPosition.BACK.ordinal)
    }
    var activeFlashCameraId by rememberSaveable {
        mutableStateOf("")
    }
    val context = LocalContext.current

    fun getCameraManager(context: Context): CameraManager {
        return context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    fun getCameraId(cameraPosition: CameraPosition): String {
        val cameraManager = getCameraManager(context)
        val cameraIds = cameraManager.cameraIdList

        for (cameraId in cameraIds) {
            val cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId)
            val lensFacing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)

            val isLensFacingBack = lensFacing == CameraCharacteristics.LENS_FACING_BACK
            val isPositionBack = cameraPosition == CameraPosition.BACK
            val isLensFacingFront = lensFacing == CameraCharacteristics.LENS_FACING_FRONT
            val isPositionFront = cameraPosition == CameraPosition.FRONT

            if (isLensFacingBack && isPositionBack) {
                return cameraId
            } else if (isLensFacingFront && isPositionFront) {
                return cameraId
            }
        }

        return ""
    }

    fun turnOnFlashLight(cameraId: String) {
        try {
            getCameraManager(context).setTorchMode(cameraId, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun turnOffFlashLight(cameraId: String) {
        try {
            getCameraManager(context).setTorchMode(cameraId, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun toggleFlashLight() {
        if (activeFlashCameraId != "") {
            turnOffFlashLight(activeFlashCameraId)
        }

        when (activeButton) {
            CameraPosition.BACK.ordinal -> {
                activeFlashCameraId = getCameraId(CameraPosition.BACK)
            }

            CameraPosition.FRONT.ordinal -> {
                activeFlashCameraId = getCameraId(CameraPosition.FRONT)
            }
        }

        if (isPowerActive) {
            turnOnFlashLight(activeFlashCameraId)
        } else {
            turnOffFlashLight(activeFlashCameraId)
        }
    }

    fun changePowerStatus() {
        isPowerActive = !isPowerActive
        toggleFlashLight()
    }

    fun setActiveButton(cameraPosition: CameraPosition) {
        if (cameraPosition.ordinal != activeButton) {
            activeButton = cameraPosition.ordinal
            toggleFlashLight()
        }
    }

    DisposableEffect(key1 = "TURN_OFF_FLASHLIGHT") {
        onDispose {
            turnOffFlashLight(activeFlashCameraId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF242323),
                        Color(0xFF000000)
                    ),
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "FLASHLIGHT",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontFamily = roboto
        )
        Spacer(Modifier.height(90.dp))
        PowerButton(size = 220.dp, isActive = isPowerActive) {
            changePowerStatus()
        }
        Spacer(Modifier.height(40.dp))
        ButtonStatus(
            "BACK",
            isActive = activeButton == CameraPosition.BACK.ordinal,
            modifier = Modifier.width(220.dp)
        ) {
            setActiveButton(CameraPosition.BACK)
        }
        Spacer(Modifier.height(10.dp))
        ButtonStatus(
            "FRONT",
            isActive = activeButton == CameraPosition.FRONT.ordinal,
            modifier = Modifier.width(220.dp)
        ) {
            setActiveButton(CameraPosition.FRONT)
        }
    }
}

@Composable
fun PowerButton(size: Dp, isActive: Boolean = false, onClick: () -> Unit) {
    val defaultModifier = Modifier
        .size(size)
        .border(
            width = 5.dp,
            color = Color(0xFF008CDB),
            shape = CircleShape,
        )
        .clip(CircleShape)
        .clickable {
            onClick()
        }
    val activeModifier = defaultModifier
        .background(Color.White)

    Box(
        modifier = if (isActive) activeModifier else defaultModifier
    ) {
        Icon(
            painter = painterResource(id = R.drawable.baseline_power_settings_new_24),
            contentDescription = "Power Icon",
            tint = Color(0xFF008CDB),
            modifier = Modifier
                .scale(3f)
                .align(Center)
        )
    }
}

@Composable
fun ButtonStatus(
    text: String,
    isActive: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var buttonColors = ButtonDefaults.buttonColors(Color(0xFF4C4E50))
    var textColor = Color.White

    if (isActive) {
        buttonColors = ButtonDefaults.buttonColors(Color.White)
        textColor = Color(0xFF008CDB)
    }

    Button(
        onClick = onClick, modifier, colors = buttonColors
    ) {
        Text(
            text,
            color = textColor,
            fontFamily = roboto,
            modifier = Modifier.padding(5.dp),
            fontWeight = FontWeight.Medium
        )
    }
}