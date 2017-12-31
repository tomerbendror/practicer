package com.practice.translate;

import com.practice.type.Language;

import java.util.List;

/**
 * Created by Tomer
 */
public interface Translator {
    List<String> translate(String phrase, Language src, Language trg);
}
