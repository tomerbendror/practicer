package com.practice.jira.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 * Created by Tomer.Ben-Dror on 2/13/2017
 */
public class FreeMarkerConfig {

    private final static Logger logger = LoggerFactory.getLogger(FreeMarkerConfig.class);

    private static FreeMarkerConfig instance = new FreeMarkerConfig();
    private Configuration cfg = new Configuration();

    private FreeMarkerConfig() {
        try {
            cfg.setClassForTemplateLoading(FreeMarkerConfig.class, "/templates");
            cfg.setDefaultEncoding("UTF-8");

            // Sets how errors will appear.
            // During web page *development* TemplateExceptionHandler.HTML_DEBUG_HANDLER is better.
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        } catch (Exception e) {
            logger.error("fail to create freeMarker instance", e);
            throw new RuntimeException(e);
        }
    }

    public static String processTemplate(String templateName, Map<String, Object> input) {
        try {
            Template template = instance.cfg.getTemplate(templateName);
            StringWriter out = new StringWriter();
            template.process(input, out);
            String output = out.toString();
            out.close();
            return output;
        } catch (IOException | TemplateException e) {
            logger.error("fail to load template with name [" + templateName + "]");
            throw new RuntimeException(e);
        }
    }
}
