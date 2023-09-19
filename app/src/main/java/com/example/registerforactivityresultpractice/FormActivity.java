package com.example.registerforactivityresultpractice;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.registerforactivityresultpractice.databinding.ActivityFormBinding;

public class FormActivity extends AppCompatActivity {

    private ActivityFormBinding binding;
    public static final String NAME = "NAME";
    public static final String AGE = "AGE";
    public static final String STUDENT = "STUDENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getIntent().putExtra(NAME,binding.ediTtextName.getText().toString());
                getIntent().putExtra(AGE,binding.editTextAge.getText().toString());
                getIntent().putExtra(STUDENT,binding.checkBoxStudent.isChecked());

                setResult(MainActivity.RESULT_CODE,getIntent());
                finish();
            }
        });

        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(MainActivity.CANCEL_CODE);
                finish();
            }
        });

    }

}