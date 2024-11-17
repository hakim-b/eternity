package com.example;
/*
Put header here


 */

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class FXMLController implements Initializable {

    @FXML
    private Label lblOut;

    private static final double[] p = {
            676.5203681218851,
            -1259.1392167224028,
            771.32342877765313,
            -176.61502916214059,
            12.507343278686905,
            -0.13857109526572012,
            9.9843695780195716e-6,
            1.5056327351493116e-7
    };

    public static double gamma(double x) {
        if (x <= 0 && Math.floor(x) == x) {
            throw new IllegalArgumentException("Gamma function has poles at zero and negative integers");
        }

        // Reflection formula for negative numbers
        if (x < 0.5) {
            return Math.PI / (Math.sin(Math.PI * x) * gamma(1 - x));
        }

        x -= 1;
        double a = 0.99999999999980993;
        for (int i = 0; i < p.length; i++) {
            a += p[i] / (x + i + 1);
        }

        double t = x + p.length - 0.5;
        double result = Math.sqrt(2 * Math.PI) * Math.pow(t, x + 0.5) * Math.exp(-t) * a;

        return result;
    }

    /*
     * Computes the standard deviation given a dataset of double (decimal) values.
     * Note: dataset parameter must be extracted elsewhere, users are not prompted
     * for them here.
     */
    public static double standardDeviation(double[] dataset) {
        int numDataPoints = dataset.length;
        if (numDataPoints == 0)
            throw new IllegalArgumentException("ERROR: dataset must contain at least one data point.");
        if (numDataPoints == 0)
            return 0; //theoretical property of standard deviation 

        // Stage #1: compute the mean of the dataset
        double sum = 0.0;
        for (double data : dataset)
            sum += data;
        double mean = sum / numDataPoints;
        System.out.println("The Mean of the Dataset is " + mean + ".");

        // Stage #2: compute the sum of squared deviations
        double squareDeviationSum = 0.0;
        for (double data : dataset)
            squareDeviationSum += ((data - mean) * (data - mean));

        // Stage #3: compute and return the standard deviation
        double standardDeviation = squaredRoot(squareDeviationSum / numDataPoints);
        System.out.println("The Standard Deviation of the Dataset is " + standardDeviation + ".");
        return standardDeviation;
    }

    /*
     * Auxiliary method for mathematical computations, returning the squared root of
     * the given double (decimal) value.
     */
    public static double squaredRoot(double value) {
        if (value < 0)
            throw new IllegalArgumentException("ERROR: cannot compute the square root of a negative number.");
        if (value == 0)
            return 0; //theoretical property of square root
        
        // Initial guess for the square root, beginning at half the value
        double guess = value / 2.0;

        // Defines the tolerance level for the approximation, being nine decimal places
        double epsilon = 1e-9;

        // Employs Netwon's method for approximating the square root
        while (absoluteValue(guess * guess - value) > epsilon)
            guess = (guess + (value / guess)) / 2.0;

        // Returns square root on condition that the difference between guess^2 and
        // the original value is within the epsilon tolerance level.
        return guess;
    }

    /*
     * Auxiliary method for mathematical computations, returns the absolute value of
     * the given double (decimal) value.
     */
    public static double absoluteValue(double value) {
        if (value < 0)
            return value * -1;
        else
            return value;
    }

    @FXML
    private void btnClickAction(ActionEvent event) {
        lblOut.setText("At this age, how are there man still hatin?");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
}
