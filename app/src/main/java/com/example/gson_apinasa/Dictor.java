package com.example.gson_apinasa;

public class Dictor {
    String ShortName;
    String Gender;
    String Locale;

    @Override
    public String toString() {
        return "ShortName: " + ShortName + "\nGender: " + Gender + "\nLocale" + Locale + "\n";
    }
}
