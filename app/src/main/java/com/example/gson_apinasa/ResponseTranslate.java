package com.example.gson_apinasa;

import java.util.ArrayList;

public class ResponseTranslate {
    class TextTranslate{
        String text;
        String to;
    }

    ArrayList<TextTranslate> textTranslates = new ArrayList<>();

    @Override
    public String toString() {
        String s = "";
        for (TextTranslate tt: textTranslates) {
            s += tt.text + "\n";
        }
        return s;
    }

}
