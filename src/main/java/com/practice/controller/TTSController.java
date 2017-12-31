package com.practice.controller;

import com.practice.model.TTSRecord;
import com.practice.repository.TTSRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

/**
 * Created by Tomer
 */
@Controller
@RequestMapping("/tts")
public class TTSController {

    @Autowired
    private TTSRepository ttsRepository;

    @RequestMapping(value="/{id}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_OCTET_STREAM_VALUE })
    @ResponseBody
    @Transactional(readOnly = true)
    public HttpEntity<byte[]> getAudio(@PathVariable("id") Long recordId) throws ServletException, IOException, UnsupportedAudioFileException {
        TTSRecord ttsRecord = ttsRepository.findAudio(recordId);
        if (ttsRecord == null || ttsRecord.getContent() == null || ttsRecord.getContent().length <= 0) {
            throw new ServletException("No clip found/clip has not data, id=" + recordId);
        }

        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("audio", "mp3"));
        return new HttpEntity<>(ttsRecord.getContent(), header);
    }
}
