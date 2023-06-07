package com.kyler.mland.egg;

public class SubjectData {
    String SubjectName;
    String Link;
    String Image;

    public SubjectData(String subjectName, String link, String image) {
        this.SubjectName = subjectName;
        this.Link = link;
        this.Image = image;
    }

    public String getItemName() {
        return this.SubjectName;
    }

    public String getItemDescription() {
        return Image;
    }
}

