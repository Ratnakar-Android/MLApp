package com.sdk.camera.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.camera2.CameraMetadata.LENS_FACING_FRONT
import android.hardware.display.DisplayManager
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.Metadata
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sdk.camera.utils.Constants.FACE_FRAGMENT
import com.sdk.camera.utils.Constants.TEXT_FRAGMENT

import com.sdk.camera.utils.SDKUtil
import com.sdk.camera.utils.cameraUtil.aspectRatio
import com.sdk.textfacemlsdk.R
import kotlinx.android.synthetic.main.camera_ui_container.*
import kotlinx.android.synthetic.main.camera_ui_container.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Main fragment for this app. Implements all camera operations including:
 * - Viewfinder
 * - Photo taking
 * - Image analysis
 */
class CameraFragment : Fragment(), View.OnClickListener {

    private lateinit var container: ConstraintLayout
    private lateinit var viewFinder: PreviewView
    private lateinit var outputDirectory: File

    private var lensFacing: Int = LENS_FACING_FRONT
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null
    private var imageUri: Uri? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var fragmentName: String

    /** AndroidX navigation arguments */
    private val args: CameraFragmentArgs by navArgs()

    private val displayManager by lazy {
        requireContext().getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    }
    companion object {
        private const val TAG = "faceAndTextSdk"
        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PHOTO_EXTENSION = ".jpg"
    }

    /** Blocking camera operations are performed using this executor */
    private lateinit var cameraExecutor: ExecutorService


    override fun onResume() {
        super.onResume()
        ensureGivenPermission()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_camera, container, false)

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        container = view as ConstraintLayout
        viewFinder = container.findViewById(R.id.view_finder)

        // Get root directory of media from navigation arguments
        fragmentName = args.rootDirectory
        setCameraLensPosition()

        // Initialize our background executor
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Determine the output directory
        outputDirectory = SDKUtil().getOutputDirectory(requireContext())

        setUpCamera()
        updateCameraUi()


    }

    private fun setCameraLensPosition() {
        // Select lensFacing depending on the available cameras
        lensFacing = when (fragmentName) {
            TEXT_FRAGMENT -> CameraSelector.LENS_FACING_BACK
            FACE_FRAGMENT -> CameraSelector.LENS_FACING_FRONT
            else -> throw IllegalStateException("Back and front camera are unavailable")
        }
    }


    /** Method used to re-draw the camera UI controls, called every time configuration changes. */
    private fun updateCameraUi() {
        container.camera_ui_container?.let { container.removeView(it) }
        View.inflate(requireContext(), R.layout.camera_ui_container, container)
        camera_capture_button.setOnClickListener(this)

    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.camera_capture_button -> {
                takeCameraPicture()
            }
        }
    }

    /** Initialize CameraX, and prepare to bind the camera use cases  */
    private fun setUpCamera() {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
            cameraProviderFuture.addListener({
                cameraProvider = cameraProviderFuture.get()
                bindCameraUseCases()
            }, ContextCompat.getMainExecutor(requireContext()))
    }


    /** Declare and bind preview, capture and analysis use cases */
    private fun bindCameraUseCases() {

        val metrics = DisplayMetrics().also { viewFinder.display.getRealMetrics(it) }
        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
        val rotation = viewFinder.display.rotation
        val cameraProvider =
            cameraProvider ?: throw IllegalStateException("Camera initialization failed.")
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

        preview = Preview.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(rotation)
            .build()

        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(rotation)
            .build()

        cameraProvider.unbindAll()

        try {
            camera = cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                preview,
                imageCapture
            )
            preview?.setSurfaceProvider(viewFinder.surfaceProvider)
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    /** Helper function used to create a timestamped file */
    private fun createFile(baseFolder: File, format: String, extension: String) =
        File(
            baseFolder,
            SimpleDateFormat(format, Locale.US).format(System.currentTimeMillis()) + extension
        )

    private fun takeCameraPicture() {
        imageCapture?.let { imageCapture ->
            val photoFile = createFile(outputDirectory, FILENAME, PHOTO_EXTENSION)
            val metadata = Metadata().apply {
                isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT
            }
            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
                .setMetadata(metadata)
                .build()

            imageCapture.takePicture(
                outputOptions,
                cameraExecutor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onError(exc: ImageCaptureException) {
                        Log.v(TAG, "Exception is ${exc.imageCaptureError}")
                    }

                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        imageUri = output.savedUri ?: Uri.fromFile(photoFile)
                        when (fragmentName) {
                            TEXT_FRAGMENT -> {
                                CoroutineScope(Dispatchers.Main).launch {
                                    val action = CameraFragmentDirections.actionCameraToText(imageUri.toString())
                                    findNavController().navigate(action)
                                }
                            }
                            FACE_FRAGMENT -> {
                                CoroutineScope(Dispatchers.Main).launch {
                                    val action = CameraFragmentDirections.actionCameraToFace(imageUri.toString())
                                    findNavController().navigate(action)
                                }
                            }
                        }
                    }
                })
        }
    }

    private fun ensureGivenPermission() {
        if (!CameraPermissionFragment.hasPermissions(requireContext())) {
            CoroutineScope(Dispatchers.Main).launch {
                val action = CameraFragmentDirections.actionCameraToPermissions(fragmentName)
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
    }

}
