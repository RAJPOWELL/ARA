package com.example.facerecognition;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraProvider;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import com.google.common.util.concurrent.ListenableFuture;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private TextView resultText;
    private FaceRecognizer faceRecognizer;
    private ExecutorService cameraExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultText = findViewById(R.id.resultText);
        cameraExecutor = Executors.newSingleThreadExecutor();

        if (!OpenCVLoader.initDebug()) {
            Log.e("OpenCV", "OpenCV Initialization failed.");
        } else {
            Log.d("OpenCV", "OpenCV Loaded Successfully.");
        }

        faceRecognizer = new FaceRecognizer(this);
        startCamera();
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Camera camera = cameraProvider.bindToLifecycle(this, CameraProvider.DEFAULT_BACK_CAMERA);
            } catch (Exception e) {
                Log.e("Camera", "Camera initialization failed.", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void detectFace(Mat frame) {
        Rect[] faces = faceRecognizer.detectFaces(frame);
        for (Rect face : faces) {
            Mat faceMat = new Mat(frame, face);
            String person = faceRecognizer.recognizeFace(faceMat);
            Imgproc.rectangle(frame, face.tl(), face.br(), new Scalar(0, 255, 0), 2);
            runOnUiThread(() -> resultText.setText("Detected: " + person));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}
