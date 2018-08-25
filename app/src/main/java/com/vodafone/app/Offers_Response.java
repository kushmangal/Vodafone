package com.vodafone.app;

class Offers_Response {
    private String title = "";
    private String description = "";
    private String offer_category = "";
    private String image_url = "";

    private int offer_id = 0;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOffer_category() {
        return offer_category;
    }

    public void setOffer_category(String offer_category) {
        this.offer_category = offer_category;
    }

    public int getOffer_id() {
        return offer_id;
    }

    public void setOffer_id(int offer_id) {
        this.offer_id = offer_id;
    }


    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }


}
