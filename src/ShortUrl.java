import java.util.Date;

public class ShortUrl {

    private final String shortUrl;
    private final String originalUrl;
    private int clickLimit;
    private int clickCount = 0;
    private Date dateCreation;
    private int dateValueLimit;

    public ShortUrl(String hashValue, String originalUrl, Date dateCreation, int clickLimit, int dateValueLimit) {
        this.shortUrl = "http://shorturl.ru/" + hashValue;
        this.originalUrl = originalUrl;
        this.clickLimit = clickLimit;
        this.dateCreation = dateCreation;
        this.dateValueLimit = dateValueLimit;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setClickLimit(int clickLimit) {
        clickCount = 0;
        dateCreation = new Date();
        this.clickLimit = clickLimit;
    }

    public Boolean isClickAvailableCheck() {
        return clickLimit - clickCount <= 0;
    }

    public void setActualNumberClicks() {
        clickCount += 1;
        System.out.println("По ссылке совершено переходов: " + clickCount);
        System.out.println("Осталось переходов: " + (clickLimit - clickCount));
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public int getDateValueLimit() {
        return dateValueLimit;
    }

    public void setDateValueLimit(int dateValueLimit) {
        this.dateValueLimit = dateValueLimit;
    }

    public Boolean isDateAvailableCheck() {
        return new Date().getTime() - getDateCreation().getTime() > getDateValueLimit() * 60000;
    }
}