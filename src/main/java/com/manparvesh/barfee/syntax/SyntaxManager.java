package com.manparvesh.barfee.syntax;

import lombok.extern.slf4j.Slf4j;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;

@Component("syntaxManager")
@Slf4j
public class SyntaxManager {

    private Languages language;

    public void setLanguage(String fileName) {
        this.language = getLanguageFromFileName(fileName);
    }

    private Languages getLanguageFromFileName(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return Languages.PLAIN_TEXT;
        }

        if (fileName.endsWith(".java")) {
            return Languages.JAVA;
        }
        if (fileName.endsWith(".c") || fileName.endsWith(".h")) {
            return Languages.C;
        }
        return Languages.PLAIN_TEXT;
    }

    public StyleSpans<Collection<String>> computeHighlighting(String text) {
        if (this.language == Languages.PLAIN_TEXT) {
            return null;
        }
        Matcher matcher = new SyntaxPattern(this.language).getPattern().matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                            matcher.group("PAREN") != null ? "paren" :
                                    matcher.group("BRACE") != null ? "brace" :
                                            matcher.group("BRACKET") != null ? "bracket" :
                                                    matcher.group("SEMICOLON") != null ? "semicolon" :
                                                            matcher.group("STRING") != null ? "string" :
                                                                    matcher.group("COMMENT") != null ? "comment" :
                                                                            null; /* never happens */
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    public enum Languages {
        C,
        JAVA,
        PLAIN_TEXT
    }
}
