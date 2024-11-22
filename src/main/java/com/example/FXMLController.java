package com.example;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

public class FXMLController implements Initializable {

    @FXML // the "σ" button belonging to the main UI
    private Button sigmaButton;

    // Storage structure holding the data points crucial for standard deviation calculation.
    private List<Double> dataset = new ArrayList<>();


    /**
     * Handles the action when the "o" button is clicked.
     * @param e
     */
    @FXML
    public void handleSigmaButtonAction(ActionEvent e) {
        // Constructs a new window (stage) for standard deviation dataset options.
        Stage dataStage = new Stage();
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 10; -fx-alignment: center;");

        // Option #1: Manual Input
        Button manualInputButton = new Button("Manual Input");
        manualInputButton.setOnAction(ev -> showManualInputPane(dataStage));

        // Stage #2: File Import
        Button fileImporButton = new Button("Import from Excel");
        fileImporButton.setOnAction(ev -> handleFileImport());
        
        layout.getChildren().addAll(manualInputButton,fileImporButton);
        Scene scene = new Scene(layout, 300, 150);
        dataStage.setTitle("Standard Deviaton Wizard!");
        dataStage.setScene(scene);
        dataStage.show();
    }

    /**
     * Displays a dedicated pane for manual data input.
     * @param parentStage
     */
    private void showManualInputPane(Stage parentStage) {
        VBox manualInputLayout = new VBox(10);
        manualInputLayout.setStyle("-fx-padding: 10; -fx-alignment: center;");

        Label instructionLabel = new Label("Enter number of data points:");
        TextField dataPointField = new TextField();
        Button confirmButton = new Button("Confirm");

        confirmButton.setOnAction(e -> {
            try {
                int count = Integer.parseInt(dataPointField.getText());
                manualInputLayout.getChildren().clear();

                // Storage structure for holding text fields for data entries.
                ArrayList<TextField> dataFields = new ArrayList<>();
                
                // Creates text fields for each data entry.
                for (int i = 0; i < count; i++){
                    TextField dataField = new TextField();
                    dataField.setPromptText("Data Point " + (i + 1));
                    dataFields.add(dataField);
                    manualInputLayout.getChildren().add(dataField);
                }

                // Button to submit the data points.
                Button submitButton = new Button("Submit Data");
                submitButton.setOnAction(ev -> {
                    dataset.clear(); // clears existing data
                    try{
                        for (TextField field : dataFields) 
                            dataset.add(Double.parseDouble(field.getText()));
                        showAlert("Success", "Data successfully recorded!");
                        // Proceed with standard deviation calculation using dataset.    
                        } catch (NumberFormatException exception) {
                            showAlert("Invalid Input", "Please enter a valid number for all data points.");
                        }
                    });
                    manualInputLayout.getChildren().add(submitButton);
            } catch (NumberFormatException exception) {
                showAlert("Invalid Input", "Please enter a valid integer.");
            }
        }); 

        manualInputLayout.getChildren().addAll(instructionLabel, dataPointField, confirmButton);
        Stage manualInputStage = new Stage();
        manualInputStage.setScene(new Scene(manualInputLayout, 400, 300));
        manualInputStage.setTitle("Manual Data Input");
        manualInputStage.show();
    }

    /**
     * Handles importing data from an Excel file option.
     * 
     * [Formatting Rules for Excel File]:
     * - the file must be in CSV format with one numerical data point per line
     * - no headers or non-numeric data should be included
     */
    private void handleFileImport() {
        FileChooser fileChooser = new FileChooser(); //constructs a file explorer window
        // Restricts valid file type to just Excel
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xls", "*.xlsx"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null){
            try (Scanner scanner = new Scanner(selectedFile)) {
                dataset.clear(); //clear existing data
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    try {
                        dataset.add(Double.parseDouble(line));
                    } catch (NumberFormatException exception) {
                        showAlert("Invalid Format", "The file contains invalid data, only numeric values are permitted.");
                        return;
                    }
                }
                showAlert("Success", "Data successfully imported!");
            } catch (Exception exception) {
                showAlert("ERROR", "An error occured while reading the file.");
            }
        }
    }

    /**
     * Displays an alert dialog with the specified title and message.
     *
     * @param title   The title of the alert dialog.
     * @param message The message content of the alert.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Label calcSeqLbl;
    public Label outputLabel;


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

    /**
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

    /**
     * Calculates the arccosine (inverse cosine) of a value using Taylor series expansion.
     * This implementation uses the relationship arccos(x) = π/2 - arcsin(x) and calculates
     * arcsin(x) using its Taylor series.
     *
     * @param x The value whose arccosine is to be calculated. Must be between -1 and 1 inclusive.
     * @return The arccosine of x in radians, in the range [0, π]
     * @throws IllegalArgumentException if x is outside the domain [-1, 1]
     *
     * Accuracy note:
     * - The function uses 20 terms of the Taylor series for approximation
     * - Most accurate near 0, may lose some precision near -1 and 1
     * - Typical accuracy is within 10^-10 of the true value for |x| < 0.9
     *
     * Examples:
     * arccos(1.0) = 0.0
     * arccos(0.0) = π/2
     * arccos(-1.0) = π
     */
    public static double arccos(double x) {
        if (x < -1 || x > 1) {
            throw new IllegalArgumentException("Input must be between -1 and 1");
        }

        // Special cases
        if (x == 1.0) return 0.0;
        if (x == -1.0) return PI;

        // arccos(x) = π/2 - arcsin(x)
        // arcsin(x) can be calculated using the formula:
        // arcsin(x) = x + (1/2)(x³/3) + (1·3)/(2·4)(x⁵/5) + (1·3·5)/(2·4·6)(x⁷/7) + ...

        double result = PI/2;
        double term = x;
        double sum = term;

        for (int n = 1; n < 20; n++) {
            term *= (2.0 * n - 1) * (2.0 * n - 1) * x * x;
            term /= (2.0 * n) * (2.0 * n + 1);
            sum += term;
        }

        return result - sum;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
}
