package com.example.antitheftalarmapp.Services

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.os.IBinder
import android.util.Base64
import android.util.Log
import androidx.core.app.ActivityCompat

class CapturePhotoForegroundService : Service() {

    private val TAG = "CapturePhotoService"
    private lateinit var cameraManager: CameraManager
    private var cameraDevice: CameraDevice? = null
    private var imageReader: ImageReader? = null
    private var cameraCaptureSession: CameraCaptureSession? = null
    private lateinit var sharedPrefs: SharedPreferences
    private val PREF_NAME = "MyPrefs"

    override fun onCreate() {
        super.onCreate()
        sharedPrefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()
        return START_NOT_STICKY
    }

    private fun startForegroundService() {
        val channelId =
            createNotificationChannel("ForegroundServiceChannel", "Foreground Service")

        val notificationBuilder = Notification.Builder(this, channelId)
            .setContentTitle("Foreground Service")
            .setContentText("Capturing photo...")
            .setSmallIcon(android.R.drawable.ic_menu_camera)

        val notification = notificationBuilder.build()
        startForeground(1, notification)

        if (checkCameraPermission()) {
            openCamera()
        } else {
            // Request permission if not granted
            requestCameraPermission()
        }
    }

    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        return channelId
    }

    private fun checkCameraPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        // Do nothing as this method is for activity context
    }

    private fun openCamera() {
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            val cameraId = getFrontCameraId(cameraManager)
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            cameraManager.openCamera(cameraId, cameraStateCallback, null)
        } catch (e: CameraAccessException) {
            Log.e(TAG, "Cannot access the camera", e)
        }
    }

    private fun getFrontCameraId(manager: CameraManager): String {
        val cameraIds = manager.cameraIdList
        for (cameraId in cameraIds) {
            val characteristics = manager.getCameraCharacteristics(cameraId)
            val cameraFacing = characteristics.get(CameraCharacteristics.LENS_FACING)
            if (cameraFacing != null && cameraFacing == CameraCharacteristics.LENS_FACING_FRONT) {
                return cameraId
            }
        }
        throw RuntimeException("Front camera not found")
    }

    private val cameraStateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            createCameraPreview()
        }

        override fun onDisconnected(camera: CameraDevice) {
            cameraDevice?.close()
            cameraDevice = null
        }

        override fun onError(camera: CameraDevice, error: Int) {
            onDisconnected(camera)
        }
    }

    private fun createCameraPreview() {
        try {
            // Initialize ImageReader and its surface
            imageReader = ImageReader.newInstance( /*width=*/640, /*height=*/480, ImageFormat.JPEG, /*maxImages=*/2)
            val texture = imageReader!!.surface
            cameraDevice?.createCaptureSession(
                listOf(texture),
                captureStateCallback,
                null
            )
        } catch (e: CameraAccessException) {
            Log.e(TAG, "Cannot create camera preview", e)
        }
    }

    private val captureStateCallback = object : CameraCaptureSession.StateCallback() {
        override fun onConfigured(session: CameraCaptureSession) {
            cameraCaptureSession = session
            captureStillImage()
        }

        override fun onConfigureFailed(session: CameraCaptureSession) {
            Log.e(TAG, "Failed to configure camera capture session")
        }
    }

    private fun captureStillImage() {
        try {
            val captureBuilder =
                cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
                    ?.apply {
                        addTarget(imageReader!!.surface)
                        set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
                    }
            cameraCaptureSession?.capture(
                captureBuilder!!.build(),
                captureCallback,
                null
            )
        } catch (e: CameraAccessException) {
            Log.e(TAG, "Cannot capture still image", e)
        }
    }

    private fun saveImageToSharedPreferences(imageData: ByteArray) {
        val encodedImageString = Base64.encodeToString(imageData, Base64.DEFAULT)
        sharedPrefs.edit().putString("lastCapturedImage", encodedImageString).apply()
    }

    private val captureCallback = object : CameraCaptureSession.CaptureCallback() {
        override fun onCaptureCompleted(
            session: CameraCaptureSession,
            request: CaptureRequest,
            result: TotalCaptureResult
        ) {
            super.onCaptureCompleted(session, request, result)

            // Acquire the captured image from the ImageReader
            val image = imageReader?.acquireLatestImage()

            // Convert the captured image to a byte array
            val imageBytes = image?.let { imageToByteArray(it) }

            // Save the image to SharedPreferences
            if (imageBytes != null) {
                saveImageToSharedPreferences(imageBytes)
            }

            // Close the captured image
            image?.close()

            // Close the camera and stop the service
            closeCamera()
            stopForeground(true)
            stopSelf()
        }
    }

    private fun imageToByteArray(image: Image): ByteArray {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return bytes
    }


    private fun closeCamera() {
        cameraCaptureSession?.close()
        cameraDevice?.close()
        imageReader?.close()
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 101

        // Add a static method to request permission
        fun requestCameraPermission(activity: Activity) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }
}