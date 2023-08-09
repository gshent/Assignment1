package my.edu.utar.assignment1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.text.InputType;
import java.util.Arrays;


public class MainActivity extends Activity {

    private EditText totalAmountEditText, numPeopleEditText;
    private RadioGroup breakdownOptionsRadioGroup;
    private Button calculateButton, shareButton;
    private TextView resultTextView;
    private EditText percentagesEditText,personAmountEditTexts;
    private LinearLayout combinationOptionsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        totalAmountEditText = findViewById(R.id.totalAmountEditText);
        numPeopleEditText = findViewById(R.id.numPeopleEditText);
        breakdownOptionsRadioGroup = findViewById(R.id.breakdownOptionsRadioGroup);
        calculateButton = findViewById(R.id.calculateButton);
        shareButton = findViewById(R.id.shareButton);
        resultTextView = findViewById(R.id.resultTextView);

        final EditText percentagesEditText = findViewById(R.id.percentagesEditText);
        final EditText personAmountEditTexts = findViewById(R.id.personAmountEditTexts);
        final LinearLayout combinationOptionsLayout = findViewById(R.id.combinationOptionsLayout);



        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateBreakdown();
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareResults();
            }
        });

        breakdownOptionsRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.customRadioButton) {
                    percentagesEditText.setVisibility(View.VISIBLE);
                    personAmountEditTexts.setVisibility(View.VISIBLE);
                    combinationOptionsLayout.setVisibility(View.INVISIBLE); // Hide combinationOptionsLayout

                } else if (checkedId == R.id.amountRadioButton) {
                    percentagesEditText.setVisibility(View.INVISIBLE);
                    personAmountEditTexts.setVisibility(View.VISIBLE);
                    combinationOptionsLayout.setVisibility(View.VISIBLE); // Show combinationOptionsLayout

                } else {
                    percentagesEditText.setVisibility(View.INVISIBLE);
                    personAmountEditTexts.setVisibility(View.VISIBLE);
                    combinationOptionsLayout.setVisibility(View.INVISIBLE); // Hide combinationOptionsLayout
                }
            }
        });


    }

    private void calculateBreakdown() {

        EditText totalAmountEditText = findViewById(R.id.totalAmountEditText);
        EditText numPeopleEditText = findViewById(R.id.numPeopleEditText);

        if (totalAmountEditText.getText().toString().isEmpty() || numPeopleEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter total amount and number of people.", Toast.LENGTH_SHORT).show();
            return;
        }


        double totalAmount = Double.parseDouble(totalAmountEditText.getText().toString());
        int numPeople = Integer.parseInt(numPeopleEditText.getText().toString());
        double[] amounts = new double[numPeople];


        int selectedOptionId = breakdownOptionsRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedOption = findViewById(selectedOptionId);

        if (selectedOption.getText().equals(getString(R.string.equal_breakdown))) {
            double equalAmount = totalAmount / numPeople;
            Arrays.fill(amounts, equalAmount);

        } else if (selectedOption.getText().equals(getString(R.string.custom_breakdown))) {
            EditText percentagesEditText = findViewById(R.id.percentagesEditText);
            String percentagesInput = percentagesEditText.getText().toString();

            String[] percentages = percentagesInput.split(",");
            if (percentages.length == numPeople) {
                double totalPercentage = 0.0;
                for (int i = 0; i < numPeople; i++) {
                    double percentage = Double.parseDouble(percentages[i].trim());
                    totalPercentage += percentage;
                    amounts[i] = totalAmount * (percentage / 100);
                }

                if (totalPercentage != 100.0) {
                    Toast.makeText(this, "Total percentage must be 100%", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                Toast.makeText(this, "Enter exactly " + numPeople + " percentages", Toast.LENGTH_SHORT).show();
                return;
            }

        } else if (selectedOption.getText().equals(getString(R.string.amount_breakdown))) {
            EditText[] personAmountEditTexts = new EditText[numPeople];

            for (int i = 0; i < numPeople; i++) {
                EditText editText = new EditText(this);
                editText.setId(View.generateViewId()); // Generate unique IDs for EditTexts
                editText.setHint("Amount for Person " + (i + 1));
                editText.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));
                combinationOptionsLayout.addView(editText);
                personAmountEditTexts[i] = editText;
            }

            // Calculate breakdown
            double totalEnteredAmounts = 0.0;
            for (int i = 0; i < numPeople; i++) {
                EditText editText = personAmountEditTexts[i];
                String enteredText = editText.getText().toString().trim();

                if (enteredText.isEmpty()) {
                    Toast.makeText(this, "Please enter valid amounts for all persons.", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    double enteredAmount = Double.parseDouble(enteredText);
                    totalEnteredAmounts += enteredAmount;
                    amounts[i] = enteredAmount;
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid amount entered for Person " + (i + 1), Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (totalEnteredAmounts != totalAmount) {
                Toast.makeText(this, "Total entered amounts must match the total amount.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        displayResults(amounts);
    }



    private void displayResults(double[] amounts) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < amounts.length; i++) {
            result.append("Person ").append(i + 1).append(": RM").append(String.format("%.2f", amounts[i])).append("\n");
        }
        resultTextView.setText(result.toString());
    }

    private void shareResults() {
        String results = resultTextView.getText().toString();
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Bill Breakdown Results");
        shareIntent.putExtra(Intent.EXTRA_TEXT, results);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }
}





