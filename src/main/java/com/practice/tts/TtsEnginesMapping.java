package com.practice.tts;

import com.practice.type.Language;

import java.util.Map;

/**
 * Created by Tomer
 */
public class TtsEnginesMapping {

    private Map<Language, AbsTTSEngine> ttsEnginesMap;

    public Map<Language, AbsTTSEngine> getTtsEnginesMap() {
        return ttsEnginesMap;
    }

    public void setTtsEnginesMap(Map<Language, AbsTTSEngine> ttsEnginesMap) {
        this.ttsEnginesMap = ttsEnginesMap;
    }

    public AbsTTSEngine get(Language language) {
        return ttsEnginesMap.get(language);
    }
}
