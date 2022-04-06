package com.zrzyyzt.runtimeviewer.Widgets.BookmarkWidget.Entity;

public class BookmarkEntity {
    private String name;
    private Extent extent;

    public BookmarkEntity(String name, Extent extent) {
        this.name = name;
        this.extent = extent;
    }

    public String getName() { return name; }
    public void setName(String value) { this.name = value; }

    public Extent getExtent() { return extent; }
    public void setExtent(Extent value) { this.extent = value; }
}
