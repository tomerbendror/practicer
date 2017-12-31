package com.practice.model;

import com.practice.tts.TtsSource;
import com.practice.type.FileMimeType;
import com.practice.type.Language;

import javax.persistence.*;
import java.util.Objects;

/**
 * Created by Tomer
 */
@Entity
@Table(name = "tts_record")
public class TTSRecord extends BaseEntity {

    @Column(name = "TEXT")
    private String text;

    @Column(name = "FILE_MIME_TYPE")
    @Enumerated(EnumType.STRING)
    private FileMimeType fileMimeType;

    @Column(name = "LANGUAGE")
    @Enumerated(EnumType.STRING)
    private Language language;

    @Column(name = "TTS_SOURCE")
    @Enumerated(EnumType.STRING)
    private TtsSource ttsSource;

    @Lob
    @Basic(fetch=FetchType.LAZY)
    @Column(name = "CONTENT")
    private byte[] content;

    public TTSRecord() {
    }

    public TTSRecord(String text, FileMimeType fileMimeType, Language language, TtsSource ttsSource, byte[] content) {
        this.text = text;
        this.fileMimeType = fileMimeType;
        this.language = language;
        this.ttsSource = ttsSource;
        this.content = content;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public FileMimeType getFileMimeType() {
        return fileMimeType;
    }

    public void setFileMimeType(FileMimeType fileMimeType) {
        this.fileMimeType = fileMimeType;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public TtsSource getTtsSource() {
        return ttsSource;
    }

    public void setTtsSource(TtsSource ttsSource) {
        this.ttsSource = ttsSource;
    }

    public static class TTSRecordKey {
        private String text;
        private Language language;

        public TTSRecordKey(String text, Language language) {
            this.text = text.toLowerCase();
            this.language = language;
        }

        public TTSRecordKey(TTSRecord ttsRecord) {
            this.text = ttsRecord.text.toLowerCase();
            this.language = ttsRecord.getLanguage();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TTSRecordKey that = (TTSRecordKey) o;
            return Objects.equals(text, that.text) &&
                    Objects.equals(language, that.language);
        }

        @Override
        public int hashCode() {
            return Objects.hash(text, language);
        }
    }
}
