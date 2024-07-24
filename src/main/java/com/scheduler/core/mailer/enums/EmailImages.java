package com.scheduler.core.mailer.enums;

public enum EmailImages {

    LOGO(
            "/email/images/logo.png",
            "logo@scheduler",
            "image/png",
            "Logo do Scheduler"
    );

    private final String imageAddress;

    private final String cid;

    private final String type;

    private final String name;

    EmailImages(String imageAddress, String cid, String type, String name) {
        this.imageAddress = imageAddress;
        this.cid = cid;
        this.type = type;
        this.name = name;
    }

    public String getImageAddress() {
        return imageAddress;
    }

    public String getCid() {
        return cid;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
