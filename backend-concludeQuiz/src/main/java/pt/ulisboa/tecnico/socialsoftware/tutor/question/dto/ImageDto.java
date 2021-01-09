package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import java.io.Serializable;

public class ImageDto implements Serializable {
    private Integer id;
    private String url;
    private Integer width;

    public ImageDto() {
    }

    public Integer getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    @Override
    public String toString() {
        return "ImageDto{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", width=" + width +
                '}';
    }
}