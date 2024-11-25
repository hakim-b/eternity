package com.example;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

import com.example.constants.CommonConstants;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FXMLController implements Initializable {
    public Label calcSeqLbl;
    public Label outputLabel;

    private boolean binaryOpPressed, unaryOpPressed, equalPressed;
    private boolean operand1Stored, operand2Stored;

    private double oper1, oper2;
    private String binaryOperator;

    // Identifies the special function chosen.
    public String functionType = null;

    // Storage structure holding data points for standard deviation
    // calculation.
    private final List<Double> dataset = new ArrayList<>();

    @FXML // the "σ" button belonging to the main UI
    private Button sigmaButton;

    /**
     * Handles the action when the "o" button is clicked.
     *
     * @param e
     */
    @FXML
    public void handleSigmaButtonAction(ActionEvent e) {
        functionType = "σ";
        // Constructs a new window (stage) for standard deviation dataset options.
        Stage dataStage = new Stage();
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 10; -fx-alignment: center;");

        // Option #1: Manual Input
        Button manualInputButton = new Button("Manual Input");
        manualInputButton.setOnAction(ev -> showManualInputPane(dataStage));

        // Stage #2: File Import
        Button fileImporButton = new Button("Import from Excel");
        fileImporButton.setOnAction(ev -> handleFileImport(dataStage));

        layout.getChildren().addAll(manualInputButton, fileImporButton);
        setBackground(layout, "src\\main\\resources\\calc_sigmaWizard.png");
        Scene scene = new Scene(layout, 350, 150);
        dataStage.setTitle("Standard Deviaton Wizard!");
        dataStage.setScene(scene);
        dataStage.show();
    }

    /**
     * Sets a custom background wallpaper for the given pane.
     * 
     * @param pane
     * @param imagePath
     */
    private void setBackground(Pane pane, String imagePath) {
        // Load the image
        Image backgroundImage = new Image("file:" + imagePath);

        // Set the image as the background
        BackgroundImage bgImage = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, true, true));
        pane.setBackground(new Background(bgImage));
    }

    /**
     * Displays a dedicated pane for manual data input.
     *
     * @param parentStage
     */
    private void showManualInputPane(Stage parentStage) {
        Stage manualInputStage = new Stage();
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
                for (int i = 0; i < count; i++) {
                    TextField dataField = new TextField();
                    dataField.setPromptText("Data Point " + (i + 1));
                    dataFields.add(dataField);
                    manualInputLayout.getChildren().add(dataField);
                }

                // Button to submit the data points.
                Button submitButton = new Button("Submit Data");
                submitButton.setOnAction(ev -> {
                    dataset.clear(); // clears existing data
                    try {
                        for (TextField field : dataFields)
                            dataset.add(Double.valueOf(Double.parseDouble(field.getText())));
                        manualInputStage.close();
                        showAlert("Success", "Data successfully recorded!");

                        // Proceed with standard deviation calculation using dataset.
                        double[] dataArray = new double[dataset.size()];
                        for (int i = 0; i < dataset.size(); i++)
                            dataArray[i] = dataset.get(i);
                        double result = standardDeviation(dataArray);
                        String computation = "σ of loaded dataset";
                        displayResultsWithGraph(functionType, computation, result, dataset);
                        parentStage.close();
                    } catch (NumberFormatException exception) {
                        showError("Invalid Input", "Please enter a valid number for all data points.");
                    }
                });
                manualInputLayout.getChildren().add(submitButton);
            } catch (NumberFormatException exception) {
                showError("Invalid Input", "Please enter a valid integer.");
            } catch (RuntimeException exception) {
                showError(exception.getClass().getSimpleName(), exception.getMessage());
            }
        });
        manualInputLayout.getChildren().addAll(instructionLabel, dataPointField, confirmButton);
        manualInputStage.setScene(new Scene(manualInputLayout, 400, 300));
        manualInputStage.setTitle("Manual Data Input");
        manualInputStage.show();
    }

    /**
     * Handles importing data from an Excel file option.
     * <p>
     * [Formatting Rules for Excel File]:
     * - the file must be in CSV format with one numerical data point per line
     * - no headers or non-numeric data should be included
     */
    private void handleFileImport(Stage parentStage) {
        FileChooser fileChooser = new FileChooser(); // constructs a file explorer window
        // Restricts valid file type to just Excel
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File selectedFile = fileChooser.showOpenDialog(parentStage);

        if (selectedFile != null) {
            try (Scanner scanner = new Scanner(selectedFile)) {
                dataset.clear(); // clear existing data
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    try {
                        dataset.add(Double.valueOf(Double.parseDouble(line)));
                    } catch (NumberFormatException exception) {
                        showError("Invalid Format",
                                "The file contains invalid data, only numeric values are permitted.");
                        return;
                    } catch (RuntimeException exception) {
                        showError(exception.getClass().getSimpleName(), exception.getMessage());
                    }
                }
                showAlert("Success", "Data successfully imported!");
                // Proceed with standard deviation calculation using dataset.
                double[] dataArray = new double[dataset.size()];
                for (int i = 0; i < dataset.size(); i++)
                    dataArray[i] = dataset.get(i);
                double result = standardDeviation(dataArray);
                String computation = "σ of loaded dataset";
                displayResultsWithGraph(functionType, computation, result, dataset);
                parentStage.close();
            } catch (Exception exception) {
                showError(exception.getClass().getSimpleName(), exception.getMessage());
            }
        }
    }

    /**
     * Displays a miscellaneous alert dialog with the specified title and message.
     *
     * @param title   The title of the alert dialog.
     * @param message The message content of the alert.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("ALERT");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays an error dialog with the specified title and message.
     *
     * @param title   The title of the alert dialog.
     * @param message The message content of the alert.
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays both the result of the evaluated equation and accompanying graphical
     * visualization.
     * 
     * @param functionType
     * @param computation
     * @param result
     * @param dataset
     */
    private void displayResultsWithGraph(String functionType, String computation, double result, List<Double> dataset) {
        Stage stage = new Stage();
        stage.setTitle(("Computation Results & Graph"));

        // Display the Equation evaluated.
        Label computationLabel = new Label("Equation Evaluated: " + computation + " = " + result);
        computationLabel.setStyle("-fx-font-size: 16; -fx-padding: 10;");

        // Axes for charts
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Horizontal Axis");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Vertical Axis");

        // Line Chart
        // LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        // lineChart.setTitle(functionType + " Function Visualization");

        // Dataset Series or Function Graph
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("f(x)");

        if (functionType.equals("MAD") || functionType.equals("σ")) {
            for (int i = 0; i < dataset.size(); i++)
                series.getData().add(new XYChart.Data<>(i, dataset.get(i)));
        } else if (functionType.equals("arccos(x)")) {
            for (double x = -1.0; x <= 1.0; x += 0.1) {
                double y = arccos(x);
                series.getData().add(new XYChart.Data<>(x, y));
            }
        } else if (functionType.equals("ab^x")) {
            double a = dataset.get(0);
            double b = dataset.get(1);
            for (double x = -10; x <= 10; x++) {
                double y = a * pow(b, x);
                series.getData().add(new XYChart.Data<>(x, y));
            }
        } else if (functionType.equals("x^y")) {
            double base = dataset.get(0);
            for (double x = -10; x <= 10; x++) {
                double y = pow(base, x);
                series.getData().add(new XYChart.Data<>(x, y));
            }
        } else if (functionType.equals("Γ(x)")) {
            for (double x = 0.5; x <= 10.0; x += 0.5) {
                double y = gamma(x);
                series.getData().add(new XYChart.Data<>(x, y));
            }
        } else if (functionType.equals("sinh(x)")) {
            for (double x = -10.0; x <= 10.0; x++) {
                double y = sinh(x);
                series.getData().add(new XYChart.Data<>(x, y));
            }
            yAxis.setAutoRanging(false);
            yAxis.setLowerBound(-1000);
            yAxis.setUpperBound(1000);
            yAxis.setTickUnit(5);
            yAxis.setMinorTickCount(15);

            xAxis.setAutoRanging(false);
            xAxis.setLowerBound(-10);
            xAxis.setUpperBound(10);
            xAxis.setTickUnit(2);
        } else if (functionType.equals("logb(x)")) {
            double base = dataset.get(0);
            double maxRange = 10;
            for (double x = 0.01; x <= maxRange; x += 0.05) {
                double y = Math.log(x) / Math.log(base);
                series.getData().add(new XYChart.Data<>(x, y));
            }
        }

        // Decide which type of chart to use acccording to function type
        XYChart<Number, Number> chart;
        if (functionType.equals("MAD") || functionType.equals("σ")) {
            ScatterChart<Number, Number> scatterChart = new ScatterChart<>(xAxis, yAxis);
            scatterChart.setTitle(functionType + " Function Visualization");
            scatterChart.getData().add(series);
            chart = scatterChart;
        } else {
            LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
            lineChart.setTitle(functionType + " Function Visualization");
            lineChart.getData().add(series);
            chart = lineChart;
        }

        VBox layout = new VBox(10, computationLabel, chart);
        layout.setStyle("-fx-padding: 20;");
        Scene scene = new Scene(layout, 700, 500);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Handles the action when the "Logarithmic Function" button is clicked.
     * This method prompts the user for a value (x) and a base (b),
     * computes the logarithm of x to the base b using the logb function,
     * and displays the result and visualization.
     *
     * @param event The event triggered by clicking the "Log" button.
     */
    @FXML
    private void handleLogBtnClick(ActionEvent event) {
        dataset.clear();
        // Prompt the user to input the value of x
        TextInputDialog xDialog = new TextInputDialog();
        xDialog.setTitle("Logarithmic Function");
        xDialog.setHeaderText("Compute logb(x)");
        xDialog.setContentText("Enter the value (x): ");

        // Prompt the user to input the base b
        TextInputDialog bDialog = new TextInputDialog();
        bDialog.setTitle("Logarithmic Function");
        bDialog.setHeaderText("Compute logb(x)");
        bDialog.setContentText("Enter the base (b):");

        // Show dialog for x and process input
        xDialog.showAndWait().ifPresent(xInput -> {
            try {
                // Parse the input for x
                double x = Double.parseDouble(xInput);
                if (x < 0)
                    throw new IllegalArgumentException("Invalid argument x, value cannot be negative");
                // Show dialog for b and process input
                bDialog.showAndWait().ifPresent(bInput -> {
                    try {
                        double b = Double.parseDouble(bInput);
                        // Compute logb(x) using the custom logb function
                        double result = logb(x, b);
                        String equation = "log_" + b + "(" + x + ")";
                        // Display the results with optional graph visualization
                        dataset.add(b);
                        displayResultsWithGraph("logb(x)", equation, result, dataset);
                    } catch (NumberFormatException exception) {
                        // Handle invalid numeric input for b
                        showError("NUMBER FORMAT ERROR", "Invalid base. Please enter a numeric value.");
                    } catch (IllegalArgumentException exception) {
                        // Handle invalid values for b (e.g., b <= 0 or b == 1)
                        showError("INVALID INPUT", exception.getMessage());
                    }
                });
            } catch (NumberFormatException exception) {
                // Handle invalid numeric input for x
                showError("NUMBER FORMAT ERROR", "Invalid value for x. Please enter a numeric value.");
            } catch (IllegalArgumentException exception) {
                // Handle invalid values for x (e.g., x <= 0)
                showError("INVALID INPUT", exception.getMessage());
            }
        });
    }

    /**
     * Handles the action when the "MAD" button is clicked.
     * 
     * @param event
     */
    @FXML
    private void handleMADBtnClick(ActionEvent event) {
        dataset.clear();

        // Prompt user for a list of numbers
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Mean Absolute Deviation Function");
        dialog.setHeaderText("Compute the Mean Absolute Deviation (MAD)");
        dialog.setContentText("Enter a list of numbers separated by commas (e.g., 1,2,3,4,5):");

        dialog.showAndWait().ifPresent(input -> {
            try {
                // Parse the input into an array of numbers
                String[] values = input.split(",");
                double[] numbers = new double[values.length];
                for (int i = 0; i < values.length; i++) {
                    numbers[i] = Double.parseDouble(values[i].trim());
                    dataset.add(Double.parseDouble(values[i].trim()));
                }
                double mad = MAD(numbers);
                // Prepare the equation string
                String equation = "MAD = (|x1-mean| + |x2-mean| + .. + |xn-mean|) / n";
                // Display the results with graph visualization
                dataset.add(mad);
                displayResultsWithGraph("MAD", equation, mad, dataset);
            } catch (NumberFormatException exception) {
                showError("NUMBER FORMAT ERROR", "Invalid input. Please enter a list of numeric values.");
            } catch (Exception exception) {
                showError(exception.getClass().getSimpleName(), exception.getMessage());
            }
        });
    }

    /**
     * Handles the action when the "ab^x" button is clicked.
     * 
     * @param event
     */
    @FXML
    private void handleExponentialBtnClick(ActionEvent event) {
        dataset.clear();
        // Prompt user for the parameter 'a'
        TextInputDialog aDialog = new TextInputDialog();
        aDialog.setTitle("Exponential Function");
        aDialog.setHeaderText("Compute ab^x");
        aDialog.setContentText("Enter the value of 'a':");

        // Prompt user for the parameter 'b'
        TextInputDialog bDialog = new TextInputDialog();
        bDialog.setTitle("Exponential Function");
        bDialog.setHeaderText("Compute ab^x");
        bDialog.setContentText("Enter the value of 'b':");

        // Prompt user for the parameter 'x'
        TextInputDialog xDialog = new TextInputDialog();
        xDialog.setTitle("Exponential Function");
        xDialog.setHeaderText("Compute ab^x");
        xDialog.setContentText("Enter the value of 'x':");

        aDialog.showAndWait().ifPresent(aInput -> {
            try {
                double a = Double.parseDouble(aInput);
                bDialog.showAndWait().ifPresent(bInput -> {
                    try {
                        double b = Double.parseDouble(bInput);
                        xDialog.showAndWait().ifPresent(xInput -> {
                            try {
                                double x = Double.parseDouble(xInput);
                                double result = a * pow(b, x);
                                String equation = "(" + a + ")(" + b + ")^(" + x + ")";
                                dataset.add(a);
                                dataset.add(b);
                                displayResultsWithGraph("ab^x", equation, result, dataset);
                                dataset.clear();
                            } catch (NumberFormatException exception) {
                                showError("NUMBER FORMAT ERROR", "Invalid 'x' value. Please enter a numeric value.");
                            } catch (RuntimeException exception) {
                                showError(exception.getClass().getSimpleName(), exception.getMessage());
                            }
                        });
                    } catch (NumberFormatException exception) {
                        showError("NUMBER FORMAT ERROR", "Invalid 'b' value. Please enter a numeric value.");
                    } catch (RuntimeException exception) {
                        showError(exception.getClass().getSimpleName(), exception.getMessage());
                    }
                });
            } catch (NumberFormatException exception) {
                showError("NUMBER FORMAT ERROR", "Invalid 'a' value. Please enter a numeric value.");
            } catch (RuntimeException exception) {
                showError(exception.getClass().getSimpleName(), exception.getMessage());
            }
        });
    }

    /**
     * Handles the action when the "x^y" button is clicked.
     * 
     * @param event
     */
    @FXML
    private void handleCartesianProductBtnClick(ActionEvent event) {
        dataset.clear();
        // Prompt user for base "x"
        TextInputDialog baseDialog = new TextInputDialog();
        baseDialog.setTitle("Power Function");
        baseDialog.setHeaderText("Compute x^y");
        baseDialog.setContentText("Enter the base (x):");

        // Prompt user for exponent "y"
        TextInputDialog exponentDialog = new TextInputDialog();
        exponentDialog.setTitle("Power Function");
        exponentDialog.setHeaderText("Compute x^y");
        exponentDialog.setContentText("Enter the exponent (y):");

        baseDialog.showAndWait().ifPresent(baseInput -> {
            try {
                double base = Double.parseDouble(baseInput);
                exponentDialog.showAndWait().ifPresent(expInput -> {
                    try {
                        double exponent = Double.parseDouble(expInput);
                        double result = pow(base, exponent);
                        dataset.add(base);
                        dataset.add(exponent);
                        String equation = "(" + base + ")^(" + exponent + ")";
                        displayResultsWithGraph("x^y", equation, result, dataset);
                    } catch (NumberFormatException exception) {
                        showError("NUMBER FORMAT ERROR", "Invalid exponent. Please enter a numeric value.");
                    } catch (RuntimeException exception) {
                        showError(exception.getClass().getSimpleName(), exception.getMessage());
                    }
                });
            } catch (NumberFormatException exception) {
                showError("NUMBER FORMAT ERROR", "Invalid base. Please enter a numeric value.");
            } catch (RuntimeException exception) {
                showError(exception.getClass().getSimpleName(), exception.getMessage());
            }
        });
    }

    /**
     * Handles the action when the "arccos(x)" button is clicked.
     * 
     * @param event
     */
    @FXML
    private void handleArcCosBtnClick(ActionEvent event) {
        dataset.clear();
        // Prompt user for "x"
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Arccosine Function");
        dialog.setHeaderText("Compute arccos(x)");
        dialog.setContentText("Enter the value (x):");

        dialog.showAndWait().ifPresent(input -> {
            try {
                double x = Double.parseDouble(input);
                String equation = "arccos(" + x + ")";
                double result = arccos(x);
                displayResultsWithGraph("arccos(x)", equation, result, dataset);
            } catch (NumberFormatException exception) {
                showError("NUMBER FORMAT ERROR", "Invalid value. Please enter a numeric value.");
            } catch (RuntimeException exception) {
                showError(exception.getClass().getSimpleName(), exception.getMessage());
            }
        });
    }

    @FXML
    private void handleGammaBtnClick(ActionEvent event) {
        dataset.clear();
        // Prompt user for "x"
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Gamma Function");
        dialog.setHeaderText("Compute Γ(x)");
        dialog.setContentText("Enter the value (x):");

        dialog.showAndWait().ifPresent(input -> {
            try {
                double x = Double.parseDouble(input);
                String equation = "Γ(" + x + ")";
                double result = gamma(x);
                displayResultsWithGraph("Γ(x)", equation, result, dataset);
            } catch (NumberFormatException exception) {
                showError("NUMBER FORMAT ERROR", "Invalid value. Please enter a numeric value.");
            } catch (RuntimeException exception) {
                showError(exception.getClass().getSimpleName(), exception.getMessage());
            }
        });
    }

    @FXML
    private void handleSineBtnClick(ActionEvent event) {
        dataset.clear();
        // Prompt user for "x"
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Hyperbolic Sine Function");
        dialog.setHeaderText("Compute sinh(x)");
        dialog.setContentText("Enter the value (x):");

        dialog.showAndWait().ifPresent(input -> {
            try {
                double x = Double.parseDouble(input);
                String equation = "sinh(" + x + ")";
                double result = sinh(x);
                displayResultsWithGraph("sinh(x)", equation, result, dataset);
            } catch (NumberFormatException exception) {
                showError("NUMBER FORMAT ERROR", "Invalid value. Please enter a numeric value.");
            } catch (RuntimeException exception) {
                showError(exception.getClass().getSimpleName(), exception.getMessage());
            }
        });
    }

    /**
     * Determines whether a decimal point is present within the output label.
     * Exception Handling method.
     * 
     * @return
     */
    private boolean containsDecimal() {
        String outputLabelTxt = outputLabel.getText();
        return outputLabelTxt.contains(".");
    }

    /**
     * Inserts a numerical constant representing PI into the calculator text label.
     * 
     * @param event
     */
    @FXML
    private void handlePIBtnClick(ActionEvent event) {
        String piStr = String.format("%.5f", PI);
        String outputLabelTxt = outputLabel.getText();

        if (containsDecimal() == true) {
            System.out.println("ERROR: there should be at most one decimal point in the expression.");
            showAlert("INVALID INPUT", "There should be at most one decimal point in the expression.");
        } else if (shouldReplaceZero(outputLabelTxt)) {
            outputLabel.setText(piStr);
        } else {
            outputLabel.setText(outputLabelTxt + piStr);
        }
    }

    /**
     * Inserts a numerical constant representing Euler's Number into the calculator
     * text label.
     * 
     * @param event
     */
    @FXML
    private void handleEulerBtnClick(ActionEvent event) {
        String eStr = String.format("%.5f", E);
        String outputLabelTxt = outputLabel.getText();

        if (containsDecimal() == true) {
            System.out.println("ERROR: there should be at most one decimal point in the expression.");
            showAlert("INVALID INPUT", "There should be at most one decimal point in the expression.");
        } else if (shouldReplaceZero(outputLabelTxt)) {
            outputLabel.setText(eStr);
        } else {
            outputLabel.setText(outputLabelTxt + eStr);
        }
    }

    @FXML
    private void handleNumberBtnClick(ActionEvent event) {
        Button btn = (Button) event.getSource();
        String numInput = btn.getText();
        String outputLabelTxt = outputLabel.getText();

        if (shouldReplaceZero(outputLabelTxt)) {
            outputLabel.setText(numInput);

        } else {
            outputLabel.setText(outputLabelTxt + numInput);
        }
    }

    @FXML
    private void handleUnaryBtnClick(ActionEvent event) {
        Button btn = (Button) event.getSource();
        String unaryOperator = btn.getText();

        double result = Double.parseDouble(outputLabel.getText());

        switch (unaryOperator) {
            case CommonConstants.OPERATOR_PERCENT:
                result /= 100;
                calcSeqLbl.setText(Double.toString(result));
                break;
            case CommonConstants.OPERATOR_RECIPROCAL:
                if (result == 0) {
                    outputLabel.setText("undefined");
                    reset();
                } else {
                    result = 1 / result;
                    calcSeqLbl.setText(String.format("1/%f", result));
                }

                break;
            case CommonConstants.OPERATOR_SQUARE:
                result *= result;
                calcSeqLbl.setText(String.format("sqr(%f)", result));
                break;
            case CommonConstants.OPERATOR_SQRT:
                if (result < 0) {
                    calcSeqLbl.setText("Keep it real");
                    reset();
                } else {
                    result = sqrt(result);
                    calcSeqLbl.setText(String.format("sqrt(%f)", result));
                }
                break;
            case CommonConstants.OPERATOR_NEGATE:
                result *= -1;
                break;
        }

        if (!operand1Stored) {
            oper1 = result;
            operand1Stored = true;
        } else if (shouldStoreOper2()) {
            oper2 = result;
            operand2Stored = true;
        }

        outputLabel.setText(Double.toString(result));

        unaryOpPressed = true;
        equalPressed = false;
        binaryOpPressed = false;
    }

    @FXML
    private void handleBinaryBtnClick(ActionEvent event) {
        Button btn = (Button) event.getSource();
        String binaryOperator = btn.getText();

        if (!operand1Stored) {
            oper1 = Double.parseDouble(outputLabel.getText());
            operand1Stored = true;
        }

        if (operand1Stored) {
            updateBinaryOperator(binaryOperator);
        }

        binaryOpPressed = true;
        unaryOpPressed = false;
        equalPressed = false;
    }

    private void updateBinaryOperator(String binaryOperator) {
        this.binaryOperator = binaryOperator;
        calcSeqLbl.setText(String.format("%f %s", oper1, this.binaryOperator));
    }

    @FXML
    private void handleDotBtnClick() {
        if (!outputLabel.getText().contains(".")) {
            outputLabel.setText(outputLabel.getText() + ".");
        }
    }

    @FXML
    private void handleOutputControl(ActionEvent event) {
        Button button = (Button) event.getSource();
        String btnTxt = button.getText();

        switch (btnTxt) {
            case CommonConstants.CLEAR_ENTRY:
                outputLabel.setText("0");
                break;
            case CommonConstants.CLR:
                reset();
                break;
            case CommonConstants.DEL:
                String outputTxt = outputLabel.getText();

                if (!outputTxt.isEmpty() && outputTxt.length() > 1) {
                    // Remove the last character
                    outputLabel.setText(outputTxt.substring(0, outputTxt.length() - 1));
                } else {
                    // Reset to "0" when empty or only one character remains
                    outputLabel.setText("0");
                }

                break;
        }
    }

    private void reset() {
        outputLabel.setText("0");
        calcSeqLbl.setText("");
        operand1Stored = false;
        operand2Stored = false;
        binaryOpPressed = false;
        unaryOpPressed = false;
        equalPressed = false;
    }

    @FXML
    private void handleEqualBtnClick() {
        if (shouldStoreOper2()) {
            oper2 = oper1;
            operand2Stored = true;
        }

        if (shouldCalculate()) {
            calculate();

            equalPressed = true;
            binaryOpPressed = false;
            unaryOpPressed = false;
        }
    }

    private boolean shouldCalculate() {
        return operand1Stored && operand2Stored;
    }

    private void calculate() {
        oper2 = Double.parseDouble(outputLabel.getText());
        operand2Stored = true;

        oper1 = getBinaryCalculation();
        outputLabel.setText(Double.toString(oper1));
    }

    private double getBinaryCalculation() {
        double result = 0;

        switch (binaryOperator) {
            case CommonConstants.ADDITION:
                result = oper1 + oper2;
                break;
            case CommonConstants.SUBTRACTION:
                result = oper1 - oper2;
                break;
            case CommonConstants.MULTIPLICATION:
                result = oper1 * oper2;
                break;
            case CommonConstants.DIVISION:
                if (oper2 == 0) {
                    outputLabel.setText("undefined");
                    reset();
                } else {
                    result = oper1 / oper2;
                }
                break;
        }

        calcSeqLbl.setText(String.format("%f %s %f = ", oper1, binaryOperator, oper2));

        oper1 = 0;
        operand1Stored = false;

        oper2 = 0;
        operand2Stored = false;

        return result;
    }

    private boolean shouldReplaceZero(String outputTxt) {
        boolean replaceZero = (operand1Stored && !operand2Stored && binaryOpPressed) || equalPressed || unaryOpPressed;
        if (!replaceZero) {
            try {
                replaceZero = Double.parseDouble(outputTxt) == 0;
            } catch (NumberFormatException e) {
                replaceZero = false;
            }
        }
        return replaceZero;

        // return (operand1Stored && !operand2Stored && binaryOpPressed) || equalPressed
        // || unaryOpPressed
        // Double.parseDouble(outputTxt) == 0;
    }

    private boolean shouldStoreOper2() {
        return !operand2Stored && operand1Stored && binaryOpPressed;
    }

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
     *
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
     *
     * @param x the input number
     * @return the floored result
     */
    private static double floor(double x) {
        int intValue = (int) x;
        return x < intValue ? intValue - 1 : intValue;
    }

    /**
     * Computed the sin value of a real number
     *
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
            if (abs(term) < 1e-10)
                break;
        }

        return sum;
    }

    /**
     * Computes the hyperbolic sine value of the double paramater "x".
     * 
     * @param x The input value
     * @return The hyperbolic sine of x
     */
    private static double sinh(double x) {
        return 0.5 * (exp(x) - exp(-x));
    }

    /**
     * Computes e^x
     *
     * @param x the input exponent
     * @return the computed value
     */
    private static double exp(double x) {
        double term = 1;
        double sum = 1;

        for (int i = 1; i <= 100; i++) {
            term *= x / i;
            sum += term;
            if (abs(term) < 1e-10)
                break;
        }

        return sum;
    }

    /**
     * Computes the value of a base raised to an exponent
     *
     * @param base     the input base
     * @param exponent the input exponent
     * @return the computed value
     */
    private static double pow(double base, double exponent) {
        // Handle special cases
        if (exponent == 0)
            return 1;
        if (base == 0)
            return 0;

        if (exponent < 0)
            return 1 / pow(base, -exponent); // Handle negative exponents

        // Handle integer exponents more efficiently
        if (floor(exponent) == exponent) {
            double result = 1;
            long exp = (long) abs(exponent);
            while (exp > 0) {
                if ((exp & 1) == 1)
                    result *= base;
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
        if (x <= 0)
            throw new IllegalArgumentException("ln(x) is undefined for x <= 0");

        // Scale x to [0.5, 1.5] for better convergence
        int scale = 0;
        while (x > 1.5) {
            x /= E;
            scale++;
        }
        while (x < 0.5) {
            x *= E;
            scale--;
        }

        x = x - 1; // Taylor series around 1
        double term = x;
        double sum = x;

        for (int i = 2; i <= 100; i++) {
            term *= -x * (i - 1) / i;
            sum += term;
            if (abs(term) < 1e-10)
                break;
        }

        return sum + scale;
    }

    // Custom implementation of sqrt using Newton's method
    private static double sqrt(double x) {
        if (x < 0)
            throw new IllegalArgumentException("sqrt of negative number");
        if (x == 0)
            return 0;

        double guess = x / 2;
        for (int i = 0; i < 10; i++) {
            guess = (guess + x / guess) / 2;
        }
        return guess;
    }

    /**
     * Returns the absolute value of a number
     *
     * @param x the input value
     * @return the absolute value
     */
    private static double abs(double x) {
        return x < 0 ? -x : x;
    }

    /**
     * Computes the logarithm of x to the base b using the existing ln function.
     * 
     * @param x The value for which the logarithm is calculated. Must be greater
     *          than 0.
     * @param b The base of the logarithm. Must be greater than 0 and not equal to
     *          1.
     * @return The logarithm of x to the base b.
     * @throws IllegalArgumentException if x <= 0, b <= 0, or b == 1.
     */
    private static double logb(double x, double b) {
        if (x <= 0) {
            throw new IllegalArgumentException("The argument x must be greater than 0.");
        }

        if (b <= 0 || b == 1) {
            throw new IllegalArgumentException("The base b must be greater than 0 and not equal to 1.");
        }

        return ln(x) / ln(b);
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
            return 0; // theoretical property of standard deviation

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
     * Computes the mean absolute deviation (MAD) given a dataset of double
     * (decimal) values.
     * Note: dataset paramater must be extracted elsewhere, users are not prompted
     * for them here.
     * 
     * @param dataset
     * @return
     */
    public static double MAD(double[] dataset) {
        // Stage #1: compute the mean of the dataset
        double mean = 0;
        for (double dataEntries : dataset)
            mean += dataEntries;
        mean /= dataset.length;

        // Stage #2: compute the mean absolute deviation (MAD) of the dataset.
        double mad = 0;
        for (double dataEntries : dataset)
            mad += abs(dataEntries - mean);
        mad /= dataset.length;
        return mad;
    }

    /**
     * Calculates the arccosine (inverse cosine) of a value using Taylor series
     * expansion.
     * This implementation uses the relationship arccos(x) = π/2 - arcsin(x) and
     * calculates
     * arcsin(x) using its Taylor series.
     *
     * @param x The value whose arccosine is to be calculated. Must be between -1
     *          and 1 inclusive.
     * @return The arccosine of x in radians, in the range [0, π]
     * @throws IllegalArgumentException if x is outside the domain [-1, 1]
     *                                  <p>
     *                                  Accuracy note:
     *                                  - The function uses 20 terms of the Taylor
     *                                  series for approximation
     *                                  - Most accurate near 0, may lose some
     *                                  precision near -1 and 1
     *                                  - Typical accuracy is within 10^-10 of the
     *                                  true value for |x| < 0.9
     *                                  <p>
     *                                  Examples:
     *                                  arccos(1.0) = 0.0
     *                                  arccos(0.0) = π/2
     *                                  arccos(-1.0) = π
     */
    public static double arccos(double x) {
        if (x < -1 || x > 1) {
            throw new IllegalArgumentException("Input must be between -1 and 1");
        }

        // Special cases
        if (x == 1.0)
            return 0.0;
        if (x == -1.0)
            return PI;

        // arccos(x) = π/2 - arcsin(x)
        // arcsin(x) can be calculated using the formula:
        // arcsin(x) = x + (1/2)(x³/3) + (1·3)/(2·4)(x⁵/5) + (1·3·5)/(2·4·6)(x⁷/7) + ...

        double result = PI / 2;
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
