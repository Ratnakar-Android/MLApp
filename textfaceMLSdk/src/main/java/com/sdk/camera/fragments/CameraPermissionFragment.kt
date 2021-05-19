package com.sdk.camera.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sdk.camera.utils.Constants


class CameraPermissionFragment : Fragment() {

    private var targetName: String? = null

    companion object {
        val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.CAMERA)
        const val PERMISSIONS_REQUEST_CODE = 10
        fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (arguments != null){
            targetName = arguments?.getString(Constants.TARGET_FRAGMENT)
        }
        if (!hasPermissions(requireContext())) {
            requestPermissions(PERMISSIONS_REQUIRED, PERMISSIONS_REQUEST_CODE)
        } else {
            navigateToTargetFragment()
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (PackageManager.PERMISSION_GRANTED == grantResults.firstOrNull()) {
                Toast.makeText(context, "Permission request granted", Toast.LENGTH_LONG).show()
                navigateToTargetFragment()
            } else {
                Toast.makeText(context, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun navigateToTargetFragment() {
        when (targetName) {
            Constants.TEXT_FRAGMENT -> {
                val action = CameraPermissionFragmentDirections.actionPermissionsToText()
                findNavController().navigate(action)
            }
            Constants.FACE_FRAGMENT -> {
                val action = CameraPermissionFragmentDirections.actionPermissionsToFace()
                findNavController().navigate(action)
            }
        }
    }
}
