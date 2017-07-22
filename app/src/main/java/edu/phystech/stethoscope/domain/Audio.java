package edu.phystech.stethoscope.domain;

public class Audio {
    private long id;
    private long personId;
    private boolean isHeart;
    private int point;
    private int number;
    private String filePath;

    public Audio(long id, long personId,
                 boolean isHeart, int point, int number, String filePath) {
        this.id = id;
        this.isHeart = isHeart;
        this.point = point;
        this.number = number;
        this.filePath = filePath;
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

    public long getPersonId() {
        return personId;
    }
}
