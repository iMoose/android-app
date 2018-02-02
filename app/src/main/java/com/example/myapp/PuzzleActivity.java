package com.example.myapp;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class PuzzleActivity extends AppCompatActivity {
    ArrayList<Button> buttons = new ArrayList<>();
    Random r = new Random();
    int round = 0;
    int currentRandom = 0;
    int randomColor = 0;
    int correctAnswer = 0;

    // int RGB values
    int indigo = -4915330;
    int maroon = -8388608;
    int amber = -607700;
    int saphire = -15573820;

    int[] answers = {indigo,maroon,amber,saphire,4,6,2,7};
    int numRounds = answers.length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

        // Initialise buttons arraylist
        buttons.add((Button) findViewById(R.id.button1));
        buttons.add((Button) findViewById(R.id.button2));
        buttons.add((Button) findViewById(R.id.button3));
        buttons.add((Button) findViewById(R.id.button4));
        buttons.add((Button) findViewById(R.id.button5));
        buttons.add((Button) findViewById(R.id.button6));
        buttons.add((Button) findViewById(R.id.button7));
        buttons.add((Button) findViewById(R.id.button8));
        buttons.add((Button) findViewById(R.id.button9));

        // Syntax: r.nextInt((max-min)+1)+min;
        nextRound();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_secret, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                Toast.makeText(this, "Reset", Toast.LENGTH_SHORT).show();
                round = 0;
                nextRound();
                break;
            default:
                break;
        }
        return true;
    }

    public void onClick( View v ) {
        switch (v.getId()) {
            case (R.id.button1):
                clickNumber(0);
                break;
            case (R.id.button2):
                clickNumber(1);
                break;
            case (R.id.button3):
                clickNumber(2);
                break;
            case (R.id.button4):
                clickNumber(3);
                break;
            case (R.id.button5):
                clickNumber(4);
                break;
            case (R.id.button6):
                clickNumber(5);
                break;
            case (R.id.button7):
                clickNumber(6);
                break;
            case (R.id.button8):
                clickNumber(7);
                break;
            case (R.id.button9):
                clickNumber(8);
                break;
        }}

    public void clickNumber(int position) {
        if(correctAnswer == position) {
            round++;
            if (round == numRounds) {
                onCorrect();
            }
        }
        else {
            round = 1;
        }
        nextRound();
    }

    public void nextRound() {
        // Set each buttons background color to a random color
        for (Button b:buttons) {
            randomColor = getRandomColor();
            // b.setText(Integer.toString(randomColor));
            b.setBackgroundColor(randomColor);
        }

        // Get the current answer for the round
        int currentAnswer = answers[round];

        // Check if the current answer is a colour choice or a position choice
        if (currentAnswer > 0) {
            // Set the answer button as the correct answer
            correctAnswer = currentAnswer-1;
        }
        else {
            currentRandom = r.nextInt(9);
            // Set a random buttons background colour to the answer
            // buttons.get(currentRandom).setText(Integer.toString(currentAnswer));
            buttons.get(currentRandom).setBackgroundColor(currentAnswer);
            correctAnswer = currentRandom;
        }
    }

    public void onCorrect() {
        Toast.makeText(getApplicationContext(),"Congrats!", Toast.LENGTH_LONG).show();
        round = 0;
        nextRound();
    }

    public int getRandomColor() {
        return r.nextInt(16777216) * -1;
    }
}
