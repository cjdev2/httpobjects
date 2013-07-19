package org.httpobjects;

public enum StandardCharset {
    US_ASCII("US-ASCII"),
    ISO_8859_1("ISO-8859-1"),
    UTF_8("UTF-8"),
    UTF_16BE("UTF-16BE"),
    UTF_16LE("UTF-16LE"),
    UTF_16("UTF-16");

    public final String name;

    private StandardCharset(String name) {
        this.name = name;
    }
}
