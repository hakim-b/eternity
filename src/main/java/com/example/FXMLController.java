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

    // Constants we'll need
    private static final double PI = 3.141592653589793238462643383279502884;
    private static final double E = 2.718281828459045235360287471352662497;

    /**
     * Computes the gamma of a number
     * @param x the input number
     * @return the computed value
     */
    public static double gamma(double x) {
        if (x <= 0 && floor(x) == x) {
            throw new IllegalArgumentException("Gamma function has poles at zero and negative integers");
        }

        // Reflection formula for negative numbers
        if (x < 0.5) {
            return PI / (sin(PI * x) * gamma(1 - x));
        }

        x -= 1;
        double a = 0.99999999999980993;
        for (int i = 0; i < p.length; i++) {
            a += p[i] / (x + i + 1);
        }

        double t = x + p.length - 0.5;
        return sqrt(2 * PI) * pow(t, x + 0.5) * exp(-t) * a;
    }

    /**
     * Returns the floor of a number
     * @param x the input number
     * @return the floored result
     */
    private static double floor(double x) {
        int intValue = (int) x;
        return x < intValue ? intValue - 1 : intValue;
    }

    /**
     * Computed the sin value of a real number
     * @param x the input number
     * @return the computed sin value
     */
    private static double sin(double x) {
        // Normalize x to be between -2π and 2π
        x = x % (2 * PI);

        double term = x;
        double sum = x;

        for (int i = 1; i <= 10; i++) {
            term = -term * x * x / (2 * i * (2 * i + 1));
            sum += term;
            if (abs(term) < 1e-10) break;
        }

        return sum;
    }

    /**
     * Computes e^x
     * @param x the input exponent
     * @return the computed value
     */
    private static double exp(double x) {
        double term = 1;
        double sum = 1;

        for (int i = 1; i <= 100; i++) {
            term *= x / i;
            sum += term;
            if (abs(term) < 1e-10) break;
        }

        return sum;
    }

    /**
     * Computes the value of a base raised to an exponent
     * @param base the input base
     * @param exponent the input exponent
     * @return the computed value
     */
    private static double pow(double base, double exponent) {
        // Handle special cases
        if (exponent == 0) return 1;
        if (base == 0) return 0;

        // Handle integer exponents more efficiently
        if (floor(exponent) == exponent) {
            double result = 1;
            long exp = (long) abs(exponent);
            while (exp > 0) {
                if ((exp & 1) == 1) result *= base;
                base *= base;
                exp >>= 1;
            }
            return exponent < 0 ? 1 / result : result;
        }

        // For other cases, use exp(ln(base) * exponent)
        return exp(ln(base) * exponent);
    }

    // Custom implementation of natural logarithm using Taylor series
    private static double ln(double x) {
        if (x <= 0) throw new IllegalArgumentException("ln(x) is undefined for x <= 0");

        // Scale x to [0.5, 1.5] for better convergence
        int scale = 0;
        while (x > 1.5) { x /= E; scale++; }
        while (x < 0.5) { x *= E; scale--; }

        x = x - 1; // Taylor series around 1
        double term = x;
        double sum = x;

        for (int i = 2; i <= 100; i++) {
            term *= -x * (i - 1) / i;
            sum += term;
            if (abs(term) < 1e-10) break;
        }

        return sum + scale;
    }

    // Custom implementation of sqrt using Newton's method
    private static double sqrt(double x) {
        if (x < 0) throw new IllegalArgumentException("sqrt of negative number");
        if (x == 0) return 0;

        double guess = x / 2;
        for (int i = 0; i < 10; i++) {
            guess = (guess + x / guess) / 2;
        }
        return guess;
    }

    /**
     * Returns the absolute value of a number
     * @param x the input value
     * @return the absolute value
     */
    private static double abs(double x) {
        return x < 0 ? -x : x;
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
        double standardDeviation = sqrt(squareDeviationSum / numDataPoints);
        System.out.println("The Standard Deviation of the Dataset is " + standardDeviation + ".");
        return standardDeviation;
    }



    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
}
