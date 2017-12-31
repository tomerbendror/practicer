package com.practice.util;

import com.practice.type.Language;

import java.text.Collator;
import java.util.Locale;

/**
 * Created by Tomer
 */
public class PracticerUtils {

    public static String secondsToString(int seconds, Language language) {
        if (seconds <= 120) {
            return seconds + (language == Language.HE ? " שניות" : " sec");
        } else {
            int minutes = seconds / 60;
            seconds = seconds % 60;
            if (language == Language.HE) {
                return minutes + " דק'" + (seconds > 0 ? " "  + seconds + " שנ'" : "");
            } else {
                return minutes + " min" + (seconds > 0 ? " "  + seconds + " sec" : "");
            }
        }
    }

    public static String secondsToString(double seconds, Language language) {
        return secondsToString((int) seconds, language);
    }

    public static boolean equalsIgnoreVowels(String str1, String str2) {
        Collator collator = Collator.getInstance( new Locale( "he" ) );
        collator.setStrength( Collator.PRIMARY );
        return collator.equals(str1, str2);
    }

    /**
     * remove Hebrew NIKKUD
     */
    public static String stripHebrewVowels(String str) {
        StringBuilder stringBuilder = new StringBuilder();
        for(int index=0; index<str.length(); index++) {
            char currentChar = str.charAt(index);
            if(currentChar < 1425 || currentChar > 1479) {
                stringBuilder.append(currentChar);
            }
        }
        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        System.out.println(stripHebrewVowels("אֲבַטִּיחַ"));
    }
}
