package com.practice.type;

/**
 * Created by Tomer
 */
public enum  Language {
    UNIVERSAL("0123456789.,?-+!/'\"[]{} ", false),
    EN("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.,?-+!/\'\"[]{} ", true),
    HE("אבגדהוזחטיכלמנסעפצקרשת.,?-+!/\'\"[]{} ףךץןם0123456789.,?-+!/'\"[]{} ׳־  ְ ֱ ֲ ֳ ִ ֵ ֶ ַ ָ ֹ ֻ ּ ֽ ֿ ׁ ׁ ׂ ׄ", true),// NIKUD taken from http://www.i18nguy.com/unicode/hebrew.html
    N_A("", false);

    private String acceptedChars;
    private boolean supported;

    Language(String acceptedChars, boolean supported) {
        this.acceptedChars = acceptedChars;
        this.supported = supported;
    }

    public static Language detect(String text) {
        for (Language language : values()) {
            boolean allOk = true;
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if (language.acceptedChars.indexOf(c) == -1) {
                    allOk = false;
                    break;
                }
            }
            if (allOk) {
                return language;
            }
        }
        return N_A;
    }

    public static void main(String[] args) {
        System.out.println(Language.detect("שְׁזִיפים"));
        System.out.println(Language.detect("תומר"));
        System.out.println(Language.detect("tomer"));
        System.out.println(Language.detect("tomerתומר"));
        System.out.println(Language.detect("123"));
        System.out.println(Language.detect("12תומר3"));
        System.out.println(Language.detect("12tomer3"));
        System.out.println(Language.detect("תומר111"));
    }

    public boolean isSupported() {
        return supported;
    }

    public void setSupported(boolean supported) {
        this.supported = supported;
    }
}
