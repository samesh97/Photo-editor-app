package com.sba.sinhalaphotoeditor.keyboard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.sba.sinhalaphotoeditor.R;

import java.util.ArrayList;
import java.util.logging.Logger;

public class KeyBoardAc extends AppCompatActivity {

    private static StringBuilder finalString = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_board);

        String str = "a aa A";
        String[] splited = str.split("\\s+");

        for(String strs : splited)
        {
            getKey(strs,0);
        }
    }
    public void getKey(String mainString,int index)
    {
        String key = "";
        Character startingKey = mainString.charAt(index);
        boolean isA = false;
        boolean isM = false;
        int currentIndex = index;


        ArrayList<Character> wordList = new ArrayList<>();
        for(int i = index; i < mainString.length(); i++)
        {
            wordList.add(mainString.charAt(i));
        }

        for(Character word : wordList)
        {
            if(word.equals("a"))
            {
                if(key.equals(""))
                {
                    key = "අ";
                    currentIndex++;

                }
                else if(key.equals("අ"))
                {
                    key = "ආ";
                    currentIndex++;

                }


            }
            else if(word.equals("A"))
            {
                key = "ඇ";
                currentIndex++;
            }


        }

        finalString.append(" ");
        finalString.append(key);

        Log.d("finalString",finalString.toString());
//        if(currentIndex <= mainString.length())
//        {
//            getKey(mainString,currentIndex);
//        }

    }
}
