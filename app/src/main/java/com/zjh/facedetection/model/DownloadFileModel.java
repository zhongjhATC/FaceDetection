package com.zjh.facedetection.model;

/**
 * 下载
 * @author zhongjh
 * @date 2021/6/1
 */
public class DownloadFileModel {

    private String url;
    private String destDir;
    private String fileName;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDestDir() {
        return destDir;
    }

    public void setDestDir(String destDir) {
        this.destDir = destDir;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
