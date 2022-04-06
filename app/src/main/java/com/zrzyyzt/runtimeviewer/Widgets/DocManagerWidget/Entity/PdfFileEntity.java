package com.zrzyyzt.runtimeviewer.Widgets.DocManagerWidget.Entity;

public class PdfFileEntity {
    private int id;
    private String name;
    private String url;
    private String filepath;
    private boolean exist;

    public PdfFileEntity() {
        this.id = -1;
        this.name = "";
        this.filepath = "";
        this.url = "";
        this.exist = false;
    }

    public PdfFileEntity(int id, String name, String filePath, String url, boolean exist) {
        this.id = id;
        this.name = name;
        this.filepath = filePath;
        this.url = url;
        this.exist = exist;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setFilePath(String filePath) {
        this.filepath = filePath;
    }

    public String getFilePath() {
        return this.filepath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isExist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }
}
