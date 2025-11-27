package com.exam.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Component;

@Component
public class OmrScanner {

    static {
        nu.pattern.OpenCV.loadLocally(); // Load OpenCV native libs
    }

    public Map<Integer, String> scan(File omrFile) {
        Map<Integer, String> answers = new HashMap<>();

        // Load in grayscale
        Mat gray = Imgcodecs.imread(omrFile.getAbsolutePath(), Imgcodecs.IMREAD_GRAYSCALE);

        if (gray.empty()) {
            System.out.println("Error: Could not load image!");
            return answers;
        }

        // Gaussian blur + adaptive threshold (better for OMR sheets)
        Imgproc.GaussianBlur(gray, gray, new Size(5, 5), 0);
        Imgproc.adaptiveThreshold(gray, gray, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C,
                Imgproc.THRESH_BINARY_INV, 15, 8);

        // Morphology → remove small noise
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3, 3));
        Imgproc.morphologyEx(gray, gray, Imgproc.MORPH_OPEN, kernel);

        // Find contours
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(gray, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        List<Rect> bubbleRects = new ArrayList<>();

        for (MatOfPoint contour : contours) {
            Rect rect = Imgproc.boundingRect(contour);

            // Filter by reasonable bubble size
            if (rect.width > 12 && rect.width < 80 && rect.height > 12 && rect.height < 80) {
                double aspectRatio = (double) rect.width / rect.height;
                if (aspectRatio > 0.7 && aspectRatio < 1.3) { // roughly square
                    bubbleRects.add(rect);
                }
            }
        }

        if (bubbleRects.isEmpty()) {
            System.out.println("⚠ No bubbles detected.");
            return answers;
        }

        // Sort bubbles top-to-bottom, left-to-right
        bubbleRects.sort(Comparator.comparingInt(r -> r.y * 10000 + r.x));

        int questionNo = 1;
        int optionsPerQ = 4;
        String[] options = {"A", "B", "C", "D"};

        for (int i = 0; i < bubbleRects.size(); i += optionsPerQ) {
            List<Rect> optionRects = bubbleRects.subList(i, Math.min(i + optionsPerQ, bubbleRects.size()));

            String selectedOption = null;
            double maxFill = 0.0;

            for (int j = 0; j < optionRects.size(); j++) {
                Rect r = optionRects.get(j);
                Mat roi = gray.submat(r);

                double nonZero = Core.countNonZero(roi);
                double total = r.width * r.height;
                double fillRatio = nonZero / total;

                // Threshold: bubble considered marked if >30% filled
                if (fillRatio > 0.3 && fillRatio > maxFill) {
                    maxFill = fillRatio;
                    selectedOption = options[j];
                }
            }

            if (selectedOption != null) {
                answers.put(questionNo, selectedOption);
            }

            questionNo++;
        }

        return answers;
    }
}


