package com.manparvesh.barfee.syntax;

import com.manparvesh.barfee.syntax.c.CSyntax;
import com.manparvesh.barfee.syntax.java.JavaSyntax;

import java.util.regex.Pattern;

import static com.manparvesh.barfee.syntax.SyntaxManager.Languages;

class SyntaxPattern {
    private static final String PAREN_PATTERN = "[()]";
    private static final String BRACE_PATTERN = "[{}]";
    private static final String BRACKET_PATTERN = "[\\[]]";
    private static final String SEMICOLON_PATTERN = ";";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    private Pattern PATTERN;
    private String KEYWORD_PATTERN;

    private Languages language;

    SyntaxPattern(Languages language) {
        switch (language) {
        case C:
            KEYWORD_PATTERN = "\\b(" + String.join("|", CSyntax.KEYWORDS) + ")\\b";
            PATTERN = Pattern.compile(
                    "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                            + "|(?<PAREN>" + PAREN_PATTERN + ")"
                            + "|(?<BRACE>" + BRACE_PATTERN + ")"
                            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                            + "|(?<STRING>" + STRING_PATTERN + ")"
                            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
            );
            break;
        case JAVA:
            KEYWORD_PATTERN = "\\b(" + String.join("|", JavaSyntax.KEYWORDS) + ")\\b";
            PATTERN = Pattern.compile(
                    "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                            + "|(?<PAREN>" + PAREN_PATTERN + ")"
                            + "|(?<BRACE>" + BRACE_PATTERN + ")"
                            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                            + "|(?<STRING>" + STRING_PATTERN + ")"
                            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
            );
            break;
        default:
            PATTERN = Pattern.compile("");
        }
    }

    Pattern getPattern() {
        return PATTERN;
    }
}
