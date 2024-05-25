package com.newenergy.arfors.myapplication;

import android.animation.IntArrayEvaluator;

import java.util.ArrayList;
import java.util.HashMap;



public class MorseCodeTranslator {

    private int dotDelay = 100;

    public static final HashMap<Character, Integer> morseCodeDuration = new HashMap<>();
        static {
            morseCodeDuration.put('.', 1);
            morseCodeDuration.put('-', 3);
            morseCodeDuration.put('_', 4);
        }
    public static final HashMap<Character, String> morseCodeMap = new HashMap<>();
       static {
           morseCodeMap.put('A', ".-");
           morseCodeMap.put('B', "-...");
           morseCodeMap.put('C', "-.-.");
           morseCodeMap.put('D', "-..");
           morseCodeMap.put('E', ".");
           morseCodeMap.put('F', "..-.");
           morseCodeMap.put('G', "--.");
           morseCodeMap.put('H', "....");
           morseCodeMap.put('I', "..");
           morseCodeMap.put('J', ".---");
           morseCodeMap.put('K', "-.-");
           morseCodeMap.put('L', ".-..");
           morseCodeMap.put('M', "--");
           morseCodeMap.put('N', "-.");
           morseCodeMap.put('O', "---");
           morseCodeMap.put('P', ".--.");
           morseCodeMap.put('Q', "--.-");
           morseCodeMap.put('R', ".-.");
           morseCodeMap.put('S', "...");
           morseCodeMap.put('T', "-");
           morseCodeMap.put('U', "..-");
           morseCodeMap.put('V', "...-");
           morseCodeMap.put('W', ".--");
           morseCodeMap.put('X', "-..-");
           morseCodeMap.put('Y', "-.--");
           morseCodeMap.put('Z', "--..");

           // Додаємо цифри та їх транслітерації
           morseCodeMap.put('0', "-----");
           morseCodeMap.put('1', ".----");
           morseCodeMap.put('2', "..---");
           morseCodeMap.put('3', "...--");
           morseCodeMap.put('4', "....-");
           morseCodeMap.put('5', ".....");
           morseCodeMap.put('6', "-....");
           morseCodeMap.put('7', "--...");
           morseCodeMap.put('8', "---..");
           morseCodeMap.put('9', "----.");
           // Додаємо таймінги
           morseCodeMap.put('.', ".-.-.-");
           morseCodeMap.put(',', "--..--");
           morseCodeMap.put('?', "..--..");
           morseCodeMap.put('!', "-.-.--");
           morseCodeMap.put(' ', "_");
       }
       public static String CodeMorse(Character letter) {
           return morseCodeMap.getOrDefault(letter, "?");
       }

       public static ArrayList<Integer> morseCodeDurations(Character letter) {
           ArrayList<Integer> duration = new ArrayList<>();
           String morseCoded = CodeMorse(letter);
           for (char i: morseCoded.toCharArray()) {
               duration.add(morseCodeDuration.get(i));
               duration.add(-1);
           }
           duration.add(-2);
        return duration;
       }

}
