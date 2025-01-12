import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class UrlAPI {

    private final Map<String, Map<String, ShortUrl>> mapURL = new HashMap<>();

    public String createShortURL(String originalUrl, String userId, int clickThroughLimit, int dateValueLimit) {
        Date dateCreation = new Date();

        int clickThroughLimitNew = Math.max(clickThroughLimit, getConfiguredMaxClickCount());
        int dateValueLimitNew = Math.min(dateValueLimit, getConfiguredDateValueLimit());

        String hashValue = generateMD5Hash(originalUrl + userId + dateCreation.getTime() + clickThroughLimitNew);
        ShortUrl shortUrl = new ShortUrl(hashValue, originalUrl, dateCreation, clickThroughLimit, dateValueLimitNew);
        saveShortUrlInMap(userId, shortUrl);
        return shortUrl.getShortUrl();
    }

    public ShortUrl restoreOriginalURL(String userId, String shortUrl) {
        Map<String, ShortUrl> map;
        ShortUrl originalUrl = null;
        try {
            map = mapURL.get(userId);
            originalUrl = map.get(shortUrl);
            if (originalUrl.isClickAvailableCheck()) {
                System.out.println("Достигнут лимит переходов, ссылка удалена");
                mapURL.get(userId).remove(shortUrl);
                return null;
            }

            if (originalUrl.isDateAvailableCheck()) {
                System.out.println("Время жизни ссылки истекло,ссылка удалена");
                mapURL.get(userId).remove(shortUrl);
                return null;
            }
        } catch (Exception e) {
            System.out.println("Не удалось найти исходную ссылку");
        }
        return originalUrl;
    }

    public Boolean isUrlInMapExist(String userId, String shortUrlNew) {
        Map<String, ShortUrl> map = mapURL.get(userId);
        if (map == null) {
            mapURL.put(userId, map);
            System.out.println("У вас нет ссылок");
            return false;
        }
        return mapURL.get(userId).get(shortUrlNew) != null;
    }

    private void saveShortUrlInMap(String userId, ShortUrl shortUrl) {
        Map<String, ShortUrl> map = mapURL.get(userId);
        if (map != null) {
            map.put(shortUrl.getShortUrl(), shortUrl);
            mapURL.put(userId, map);
        } else {
            Map<String, ShortUrl> mapNew = new HashMap<>();
            mapNew.put(shortUrl.getShortUrl(), shortUrl);
            mapURL.put(userId, mapNew);
        }
    }

    public void delShortUrlInMap(String userId, String shortUrl) {
        mapURL.get(userId).remove(shortUrl);
        System.out.println("Ссылка удалена");
    }

    private String generateMD5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashInBytes = md.digest(input.getBytes());

            // Преобразуем байты в шестнадцатеричную строку
            StringBuilder sb = new StringBuilder();
            for (byte b : hashInBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    sb.append('0');
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private int getConfiguredMaxClickCount() {
        Properties properties = new Properties();
        try (InputStream input = UrlAPI.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
                return Integer.parseInt(properties.getProperty("max.click.count", "1"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 1;
    }

    private int getConfiguredDateValueLimit() {
        Properties properties = new Properties();
        try (InputStream input = UrlAPI.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
                return Integer.parseInt(properties.getProperty("max.storage.time", "1"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 1;
    }
}