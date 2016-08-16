package com.example.apple.knowhy.Zhuti;

import java.util.List;

/**
 * Created by Rawght Steven on 8/12/16, 12.
 * Email:rawghtsteven@gmail.com
 */
public class ZhutiItemBean {

    private List<Stories> stories;
    private String description;
    private String background;
    private String name;

    public List<Stories> getStories() {
        return stories;
    }

    public void setStories(List<Stories> stories) {
        this.stories = stories;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static class Stories {
        private List<String> images;
        private String title;
        private int id;

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
