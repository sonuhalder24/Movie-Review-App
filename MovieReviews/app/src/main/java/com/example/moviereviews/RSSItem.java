package com.example.moviereviews;

public class RSSItem {
    String title;
    String description;
    String link;
    String pubdate;
    String guid;

    public RSSItem(String title, String description, String link, String pubdate, String guid) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.pubdate = pubdate;
        this.guid = guid;
    }
}
