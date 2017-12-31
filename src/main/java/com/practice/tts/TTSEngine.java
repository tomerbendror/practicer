package com.practice.tts;

import com.practice.model.TTSRecord;
import com.practice.type.Language;

/**
 * Created by Tomer
 */
public interface TTSEngine {
    TTSRecord getAudioContent(String source, Language language);
}
