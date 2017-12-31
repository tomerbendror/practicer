package com.practice.controller;

import com.practice.model.TranslatedRecord;
import com.practice.translate.Translator;
import com.practice.type.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Tomer
 */
@Controller
@RequestMapping("/translate")
public class TranslateController {

    @Autowired
    private Translator translator;

    @RequestMapping(method = RequestMethod.POST)    // using POST to easily enable multi lang support for the text param
    @PreAuthorize("hasRole('parent')")
    @ResponseBody
    @Transactional(readOnly = true)
    public TranslatedRecord translate(@RequestBody TranslatedRecord translatedRecord) {
        String text = translatedRecord.getText();
        Language src = translatedRecord.getSourceLanguage();
        Language trg = translatedRecord.getTargetLanguage();
        return new TranslatedRecord(text, translator.translate(text, src, trg), src, trg);
    }
}
