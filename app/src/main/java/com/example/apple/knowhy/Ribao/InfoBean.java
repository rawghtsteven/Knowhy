package com.example.apple.knowhy.Ribao;

import java.util.List;

/**
 * Created by Rawght Steven on 8/8/16, 16.
 * Email:rawghtsteven@gmail.com
 */
public class InfoBean {

    private int date;
    private List<Stories> stories;
    private List<Top_stories> top_stories;

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public List<Stories> getStories() {
        return stories;
    }

    public void setStories(List<Stories> stories) {
        this.stories = stories;
    }

    public List<Top_stories> getTop_stories() {
        return top_stories;
    }

    public void setTop_stories(List<Top_stories> top_stories) {
        this.top_stories = top_stories;
    }

    public static class Stories{
        private String title;
        private List<String> images;
        private int ga_prefix;
        private int type;
        private int id;
        private boolean multipic = false;

        public void setImages(List<String> images) {
            this.images = images;
        }

        public int getGa_prefix() {
            return ga_prefix;
        }

        public void setGa_prefix(int ga_prefix) {
            this.ga_prefix = ga_prefix;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public boolean isMultipic() {
            return multipic;
        }

        public void setMultipic(boolean multipic) {
            this.multipic = multipic;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<String> getImages() {
            return images;
        }

    }

    public static class Top_stories{

        private String title;
        private String image;
        private int ga_prefix;
        private int type;
        private int id;

        public int getGa_prefix() {
            return ga_prefix;
        }

        public void setGa_prefix(int ga_prefix) {
            this.ga_prefix = ga_prefix;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}
