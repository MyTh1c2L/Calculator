package com.example.calculator;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CalculatorActivity extends AppCompatActivity {

    private TextView resultField;
    private EditText numberOne;
    private EditText numberTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculator);

        DisplayMetrics display = getResources().getDisplayMetrics();
        if ((display.heightPixels>=1920 & display.widthPixels>=1080)||
                (display.heightPixels>=1080 & display.widthPixels>=1920)) {
            showAlert("Разрешение экрана устройства несовместимо с приложением!", true);
        }

        resultField = findViewById(R.id.textView);
        numberOne = findViewById(R.id.editTextNumberDecimal);
        numberTwo = findViewById(R.id.editTextNumberDecimal2);
    }

    public void calculateResult(View view) {
        Button button = (Button)view;
        String operation = button.getText().toString();

        String numberOneStr = numberOne.getText().toString();
        String numberTwoStr = numberTwo.getText().toString();

        if (numberOneStr.length() > 0 && numberOneStr != null && numberTwoStr.length() > 0 && numberTwoStr != null) {
            double a = Double.parseDouble(numberOneStr);
            double b = Double.parseDouble(numberTwoStr);
            double result = 0;

            try {
                switch (operation) {
                    case "+":
                        result = a + b;
                        break;
                    case "-":
                        result = a - b;
                        break;
                    case "*":
                        result = a * b;
                        break;
                    case "/":
                        if (b != 0)
                            result = a / b;
                        else
                            throw new Exception("делить на ноль нельзя!");
                        break;
                    default:
                        break;
                }
            } catch (Exception ex) {
                showAlert("Во время расчета произошла ошибка: " + ex.getMessage(), false);
                clearInput(view);
                return;
            }

            resultField.setText(String.valueOf(result));
        } else {
            showAlert("Необходимо ввести оба числа!", false);
            clearInput(view);
        }
    }

    public void backToMain(View view) {
        this.finish();
    }

    public void clearInput(View view) {
        numberOne.getText().clear();
        numberTwo.getText().clear();
        resultField.setText("");
    }

    public void showAlert(String errorText, boolean serious) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CalculatorActivity.this);
        builder.setTitle("Ошибка!");
        builder.setMessage(errorText);
        builder.setCancelable(false);

        if (serious)
            builder.setNegativeButton("Выход", (dialog, id) -> killProcess());
        else
            builder.setNegativeButton("Закрыть", (dialog, id) -> dialog.cancel());

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void killProcess() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}