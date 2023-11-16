package com.example.chittopot;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;

import com.example.chittopot.R;
import com.example.chittopot.ml.MyModel;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.vdurmont.emoji.EmojiParser;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;



public class HomeActivity extends AppCompatActivity {
    Button button;
    TextView textView;
    EditText editText;
    MyModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        button = findViewById(R.id.btn);
        textView = findViewById(R.id.res);
        editText = findViewById(R.id.msg);

        try {
            model = MyModel.newInstance(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = editText.getText().toString();

                if (model != null) {
                    TensorBuffer inputFeature0 = prepareInputData(inputText);

                    if (inputFeature0 != null) {
                        MyModel.Outputs outputs = model.process(inputFeature0);
                        TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                        if (outputFeature0 != null) {
                            String result = interpretOutput(outputFeature0);
                            textView.setText(result);
                        } else {
                            textView.setText("Model output is null.");
                        }
                    } else {
                        textView.setText("input feature is null.");
                    }
                } else {
                    textView.setText("Model not initialized.");
                }
            }

        });
    }
    private String preprocessEmojis(String inputText) {
        // Get the list of emojis in the input text
        List<Emoji> emojis = new ArrayList<>(EmojiManager.getAll()); // Convert to List

        // Replace emojis with their textual meanings
        for (Emoji emoji : emojis) {
            String emojiUnicode = emoji.getUnicode();
            String emojiText = emoji.getDescription();
            inputText = inputText.replace(emojiUnicode, emojiText);
        }

        return inputText;
    }


    private TensorBuffer prepareInputData(String inputText) {

        inputText = preprocessEmojis(inputText);
        // Log the input data for debugging
        Log.d("Debug", "Inside prepareInputData");
        Log.d("Input Data", "Input Text: " + inputText);

        // Convert the preprocessed inputText to a byte array, assuming it's encoded as UTF-8
        // Assuming you have the dictionary text in a file (e.g., "dictionary.txt")
        HashMap<String, Integer> wordToIntegerMap = new HashMap<>();

        AssetManager assetManager = getAssets();
        try {
            InputStream is = assetManager.open("dictionary.txt");
            Log.d("Debug", "Reading dictionary file");

            BufferedReader reader = new BufferedReader(new InputStreamReader(is)); // Change this line
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t"); // Split using tab character

                if (parts.length == 2) {
                    String word = parts[0].trim(); // Remove leading/trailing whitespace
                    int integer = Integer.parseInt(parts[1].trim());
                    wordToIntegerMap.put(word, integer);
                }
            }

            Log.d("Debug", "Finished reading dictionary file");
            reader.close();
        } catch (IOException e) {
            // Handle exceptions
            Log.e("Debug", "Error reading dictionary file: " + e.getMessage());
        }

        inputText = inputText.toLowerCase();
        inputText = inputText.replaceAll("[^a-zA-Z0-9\\s]", "");
        Log.d("Debug", "Processed Input Text: " + inputText);
        String[] tokens = inputText.split("\\s+");

        List<Integer> integerTokens = new ArrayList<>();
        for (String token : tokens) {
            if (wordToIntegerMap.containsKey(token)) {
                integerTokens.add(wordToIntegerMap.get(token));
                Log.d("Token", "Token: " + token);
                Log.d("Token", "Token: " + wordToIntegerMap.get(token));
            } else {
                Log.w("Token", "Unknown Token: " + token);
            }
        }

        // Ensure the number of tokens does not exceed 87
        if (integerTokens.size() > 87) {
            // Truncate or handle as needed
            integerTokens = integerTokens.subList(integerTokens.size() - 87, integerTokens.size());
        } else if (integerTokens.size() < 87) {
            // Pad the list with zeros at the front to reach the required length
            while (integerTokens.size() < 87) {
                integerTokens.add(0, 0);
            }
        }

// Log the final integer tokens
        Log.d("Debug", "Integer Tokens: " + integerTokens);

// Now, 'integerTokens' contains 87 integers, and you can create the 'TensorBuffer'
        int[] inputArray = new int[87];
        for (int i = 0; i < 87; i++) {
            inputArray[i] = integerTokens.get(i);
        }
        Log.d("Debug", "Input Array: " + Arrays.toString(inputArray)); // Add this line

        TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 87}, DataType.FLOAT32);
        inputFeature0.loadArray(inputArray);

        return inputFeature0;


    }



    private String interpretOutput(TensorBuffer outputFeature0) {
        // Get the probabilities for each class from the output tensor
        float[] probabilities = outputFeature0.getFloatArray();

        // Find the class with the highest probability
        int predictedClass = findMaxIndex(probabilities);

        // Map the predicted class index to the corresponding emotion label
        String[] emotions = {"Sad", "Happy", "Angry", "Love"};
        String result = "Emotion: " + emotions[predictedClass];

        return result;
    }

    private int findMaxIndex(float[] arr) {
        int maxIndex = 0;
        float maxVal = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > maxVal) {
                maxVal = arr[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    @Override
    protected void onDestroy() {
        model.close();
        super.onDestroy();
    }
}