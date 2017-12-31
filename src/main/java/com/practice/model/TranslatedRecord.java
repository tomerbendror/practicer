package com.practice.model;

import com.practice.type.Language;

import java.util.List;

/**
 * Created by Tomer
 */
public class TranslatedRecord {
    private String text;
    private List<String> translations;
    private Language sourceLanguage;
    private Language targetLanguage;

    public TranslatedRecord() {
    }

    public TranslatedRecord(String text, List<String> translations, Language sourceLanguage, Language targetLanguage) {
        this.text = text;
        this.translations = translations;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getTranslations() {
        return translations;
    }

    public void setTranslations(List<String> translations) {
        this.translations = translations;
    }

    public Language getSourceLanguage() {
        return sourceLanguage;
    }

    public void setSourceLanguage(Language sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }

    public Language getTargetLanguage() {
        return targetLanguage;
    }

    public void setTargetLanguage(Language targetLanguage) {
        this.targetLanguage = targetLanguage;
    }
}
