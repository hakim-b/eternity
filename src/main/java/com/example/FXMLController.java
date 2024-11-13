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

    @FXML
    private void btnClickAction(ActionEvent event) {
        lblOut.setText("At this age, how are there man still hatin?");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
}
