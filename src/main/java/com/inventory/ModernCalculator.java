package com.inventory;

import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.net.URL;
import java.text.DecimalFormat;

public class ModernCalculator extends Application {

    private Label resultLabel;
    private Label historyLabel1;
    private Label historyLabel2;

    private String currentExpression = "";
    private final DecimalFormat formatter = new DecimalFormat("#,###.########");
    private boolean startNewCalculation = false;

    @Override
    public void start(Stage primaryStage) {
        historyLabel1 = new Label("");
        historyLabel1.getStyleClass().add("history-label-dim");

        historyLabel2 = new Label("");
        historyLabel2.getStyleClass().add("history-label");

        resultLabel = new Label("0");
        resultLabel.getStyleClass().add("result-label");

        VBox displayArea = new VBox(2, historyLabel1, historyLabel2, resultLabel);
        displayArea.setAlignment(Pos.BOTTOM_RIGHT);

        // --- UPDATED PADDING: Reduced from 25 to 10 for more space ---
        displayArea.setPadding(new javafx.geometry.Insets(20, 10, 20, 10));
        // -------------------------------------------------------------

        VBox.setVgrow(displayArea, Priority.ALWAYS);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new javafx.geometry.Insets(0, 20, 30, 20));

        String[] buttons = {
                "C", "DEL", "%", "÷",
                "7", "8", "9", "×",
                "4", "5", "6", "-",
                "1", "2", "3", "+",
                "K", "0", ".", "="
        };

        int col = 0;
        int row = 0;

        for (String text : buttons) {
            Button btn = createButton(text);
            grid.add(btn, col, row);

            col++;
            if (col == 4) { col = 0; row++; }
        }

        VBox root = new VBox(displayArea, grid);
        root.getStyleClass().add("root-pane");

        Scene scene = new Scene(root, 360, 650);
        URL cssResource = getClass().getResource("/style.css");
        if (cssResource != null) scene.getStylesheets().add(cssResource.toExternalForm());

        primaryStage.setTitle("Calculator By Keshara");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private Button createButton(String text) {
        Button btn = new Button();

        if (text.equals("DEL")) {
            SVGPath icon = new SVGPath();
            icon.setContent("M 20 10 L 8 10 L 2 16 L 8 22 L 20 22 L 20 10 M 22 8 L 22 24 L 7 24 L 0 17 L 7 8 Z M 11 13 L 13 13 L 14 15 L 15 13 L 17 13 L 15 16 L 17 19 L 15 19 L 14 17 L 13 19 L 11 19 L 13 16 Z");
            icon.setFill(Color.web("#2979ff"));
            btn.setGraphic(icon);
            btn.getStyleClass().add("operator-btn");
            btn.setOnAction(e -> handlePress(text));
        }
        else if (text.equals("K")) {
            btn.setText("K");
            btn.getStyleClass().add("secondary-btn");
            btn.setMouseTransparent(true);
        }
        else {
            btn.setText(text);

            if (text.equals("=")) btn.getStyleClass().add("equal-btn");
            else if ("C%×-+÷".contains(text)) btn.getStyleClass().add("operator-btn");
            else btn.getStyleClass().add("number-btn");

            btn.setOnAction(e -> handlePress(text));
        }

        if (!text.equals("K")) {
            btn.setOnMousePressed(event -> {
                ScaleTransition st = new ScaleTransition(Duration.millis(80), btn);
                st.setToX(0.92); st.setToY(0.92); st.play();
            });
            btn.setOnMouseReleased(event -> {
                ScaleTransition st = new ScaleTransition(Duration.millis(80), btn);
                st.setToX(1.0); st.setToY(1.0); st.play();
            });
        }

        return btn;
    }

    private void handlePress(String text) {

        if (startNewCalculation) {
            if ("0123456789.".contains(text)) {
                currentExpression = "";
                resultLabel.setText("0");
            }
            startNewCalculation = false;
        }

        if (text.equals("C")) {
            currentExpression = "";
            resultLabel.setText("0");
            historyLabel1.setText("");
            historyLabel2.setText("");
        }
        else if (text.equals("DEL")) {
            if (!currentExpression.isEmpty()) {
                currentExpression = currentExpression.substring(0, currentExpression.length() - 1);
                resultLabel.setText(currentExpression.isEmpty() ? "0" : currentExpression);
            }
        }
        else if (text.equals("=")) {
            try {
                double result = Evaluator.evaluate(currentExpression);
                String resultString;

                if (result % 1 == 0) {
                    resultString = String.format("%.0f", result);
                } else {
                    resultString = formatter.format(result);
                }

                String fullHistoryString = currentExpression + "=" + resultString;
                historyLabel1.setText(historyLabel2.getText());
                historyLabel2.setText(fullHistoryString);

                resultLabel.setText(resultString);
                currentExpression = String.valueOf(resultString).replaceAll(",", "");

                startNewCalculation = true;

            } catch (Exception e) {
                resultLabel.setText("Error");
                startNewCalculation = true;
            }
        }
        else {
            if (isOperator(text)) {
                if (currentExpression.isEmpty()) return;
                char lastChar = currentExpression.charAt(currentExpression.length() - 1);

                if (isOperator(String.valueOf(lastChar))) {
                    currentExpression = currentExpression.substring(0, currentExpression.length() - 1) + text;
                    resultLabel.setText(currentExpression);
                    return;
                }
            }

            if (resultLabel.getText().equals("0") || resultLabel.getText().equals("Error")) {
                currentExpression = "";
            }
            currentExpression += text;
            resultLabel.setText(currentExpression);
        }
    }

    private boolean isOperator(String s) {
        return "+-×%÷".contains(s);
    }

    public static void main(String[] args) {
        launch(args);
    }
}