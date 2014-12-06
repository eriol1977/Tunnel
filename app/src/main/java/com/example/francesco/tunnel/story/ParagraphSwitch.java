package com.example.francesco.tunnel.story;

/**
 * Created by Francesco on 21/11/2014.
 */
public class ParagraphSwitch {

    private final String sectionId;

    private final int paragraphPosition;

    private final String newParagraph;

    ParagraphSwitch(String sectionId, int paragraphPosition, String newParagraph) {
        this.sectionId = sectionId;
        this.paragraphPosition = paragraphPosition;
        this.newParagraph = newParagraph;
    }

    String getSectionId() {
        return sectionId;
    }

    int getParagraphPosition() {
        return paragraphPosition;
    }

    String getNewParagraph() {
        return newParagraph;
    }
}
