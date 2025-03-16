package com.example.facerecognition;

import android.content.Context;
import android.util.Log;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.face.FaceRecognizer;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.imgproc.Imgproc;
import java.util.HashMap;
import java.util.Map;

public class FaceRecognizer {
    private final FaceRecognizer recognizer;
    private final Map<Integer, String> labels;

    public FaceRecognizer(Context context) {
        recognizer = LBPHFaceRecognizer.create();
        labels = new HashMap<>();
        loadTrainingData();
    }

    private void loadTrainingData() {
        Mat images = new Mat(5, 1, CvType.CV_32SC1);
        Mat labelsMat = new Mat(5, 1, CvType.CV_32SC1);

        // Load five known images (assumed in raw folder)
        int[] labelArray = {0, 1, 2, 3, 4};
        String[] names = {"Alice - Engineer", "Bob - Manager", "Charlie - CEO", "David - Developer", "Emma - Designer"};

        for (int i = 0; i < 5; i++) {
            Mat img = new Mat();
            Utils.loadResource(context, context.getResources().getIdentifier("face" + i, "raw", context.getPackageName()), img, 1);
            Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2GRAY);
            Imgproc.resize(img, img, new Size(200, 200));

            labels.put(i, names[i]);
            labelsMat.put(i, 0, labelArray[i]);
        }

        recognizer.train(images, labelsMat);
        Log.d("FaceRecognizer", "Training completed.");
    }

    public Rect[] detectFaces(Mat frame) {
        Mat gray = new Mat();
        Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);
        return new Rect[]{new Rect(100, 100, 200, 200)}; // Placeholder, replace with real detection
    }

    public String recognizeFace(Mat faceMat) {
        int[] label = new int[1];
        double[] confidence = new double[1];
        recognizer.predict(faceMat, label, confidence);
        return labels.getOrDefault(label[0], "Unknown");
    }
}
