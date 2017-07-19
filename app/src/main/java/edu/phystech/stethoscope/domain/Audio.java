package edu.phystech.stethoscope.domain;

public class Audio {
    private long id;
    private long personId;
    private boolean isHeart;
    private int point;
    private int number;
    private String filePath;
    private String comment;

    public Audio(long id, long personId,
                 boolean isHeart, int point, int number, String filePath, String comment) {
        this.id = id;
        this.isHeart = isHeart;
        this.point = point;
        this.number = number;
        this.filePath = filePath;
        this.comment = comment;
    }

    public long getId() {
        return id;
    }

    public boolean isHeart() {
        return isHeart;
    }

    public int getPoint() {
        return point;
    }

    public int getNumber() {
        return number;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getComment() {
        return comment;
    }

    public long getPersonId() {
        return personId;
    }
}
