package com.example.francesco.tunnel.story;

/**
 * Created by Francesco on 21/11/2014.
 */
public class ParagraphSwitch {

    private final String sectionId;

    private final int paragraphPosition;

    private final String newParagraph;

    public ParagraphSwitch(String sectionId, int paragraphPosition, String newParagraph) {
        this.sectionId = sectionId;
        this.paragraphPosition = paragraphPosition;
        this.newParagraph = newParagraph;
    }

    public String getSectionId() {
        return sectionId;
    }

    public int getParagraphPosition() {
        return paragraphPosition;
    }

    public String getNewParagraph() {
        return newParagraph;
    }
}
