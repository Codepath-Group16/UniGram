package com.codepath_group16.unigram.ui.post;

import android.Manifest;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.codepath_group16.unigram.R;
import com.codepath_group16.unigram.databinding.FragmentCaptureImageBinding;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

//import kotlinx.android.synthetic.main.activity_main.*;
//        typealias LumaListener = (luma: Double) -> Unit;


public class CaptureImageFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();
    private final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    private final int REQUEST_CODE_PERMISSIONS = 10;
    private final String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA};
    private ImageCapture imageCapture = null;
    private File outputDirectory;
    private ExecutorService cameraExecutor;
    private FragmentCaptureImageBinding mBinding;

    public CaptureImageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentCaptureImageBinding.inflate(inflater, container, false);

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            requestPermissions(
                    REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            );
        }

        // Set up the listener for take photo button
        mBinding.cameraCaptureButton.setOnClickListener(v -> takePhoto());

        outputDirectory = getOutputDirectory();

        cameraExecutor = Executors.newSingleThreadExecutor();

        return mBinding.getRoot();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(requireContext(),
                        R.string.permission_denied,
                        Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            }
        }
    }

    private boolean allPermissionsGranted() {
        boolean all_granted = true;
        for (String permission : REQUIRED_PERMISSIONS) {
            if (PermissionChecker.checkSelfPermission(
                    requireContext(),
                    permission
            ) != PERMISSION_GRANTED) {
                all_granted = false;
                break;
            }
        }
        return all_granted;
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            ProcessCameraProvider cameraProvider = null;
            try {
                cameraProvider = cameraProviderFuture.get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

            // Preview
            Preview preview = new Preview.Builder().build();
            preview.setSurfaceProvider(mBinding.viewFinder.getSurfaceProvider());

            imageCapture = new ImageCapture.Builder()
                    .build();

            // Select back camera as a default
            CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;


            try {
                // Unbind use cases before rebinding
                Objects.requireNonNull(cameraProvider).unbindAll();

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                        requireActivity(), cameraSelector, preview, imageCapture);

            } catch (Exception exc) {
                Log.e(TAG, "Use case binding failed", exc);
            }

        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        if (imageCapture == null) {
            return;
        }


        // Create time-stamped output file to hold the image
        File photoFile = new File(
                outputDirectory,
                new SimpleDateFormat(FILENAME_FORMAT, Resources.getSystem().getConfiguration().locale
                ).format(System.currentTimeMillis()) + ".jpg"
        );

        // Create output options object which contains file + metadata
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();


        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(requireContext()), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                Uri savedUri = Uri.fromFile(photoFile);
                String msg = "Photo capture succeeded: " + savedUri;
                Navigation.findNavController(mBinding.getRoot()).navigate(
                        CaptureImageFragmentDirections.actionNavigationCaptureImageToNavigationImagePreview(savedUri)
                );
                Log.d(TAG, msg);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exc) {
                Log.e(TAG, "Photo capture failed: ${exc.message}", exc);
            }
        });

    }

    private File getOutputDirectory() {

        File[] mediaDir = requireContext().getExternalMediaDirs();

        File file;
        if (mediaDir.length > 0) {
            file = mediaDir[0];
        } else {
            file = null;
        }

        if (file != null) {
            File outputFile = new File(file, requireContext().getResources().getString(R.string.app_name));
            if (outputFile.mkdirs()) {
                file = outputFile;
            }
        }

        if (file != null && file.exists()) {
            return file;
        } else {
            return requireContext().getFilesDir();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }

}

