import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Scanner;
import java.util.UUID;

public class AppMenu {

    public static void start(UrlAPI urlAPI) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("""
                    Выберите действие из списка:
                    1. Создать новый UUID
                    2. Войти по UUID
                    3. Завершить работу
                    
                    Введите число:""");
            String scan = scanner.next();
            if (Objects.equals(scan, "3")) System.exit(0);
            String userId = login(scan, scanner);
            action(userId, scanner, urlAPI);
        }
    }

    private static String login(String scan, Scanner scanner) {
        String userId;
        switch (scan) {
            case "2":
                System.out.print("Введите ваш UUID: ");
                userId = scanner.next();
                return userId;
            case "1":
            default:
                userId = String.valueOf(UUID.randomUUID());
                System.out.println("Ваш UUID: " + userId);
                return userId;
        }
    }

    private static void action(String userId, Scanner scanner, UrlAPI urlAPI) {
        while (true) {
            System.out.println("""
                    Выберите действие из списка:
                    1. Создать короткую ссылку
                    2. Перейти по короткой ссылке
                    3. Изменить количество переходов по короткой ссылке
                    4. Изменить время жизни короткой ссылки
                    5. Удалить короткую ссылку
                    6. Выйти из сессии
                    
                    Введите число:""");
            String scan = scanner.next();

            if (scan.equals("6")) break;

            String url;
            ShortUrl shortUrl;
            switch (scan) {
                case "1":
                    System.out.print("Введите исходную ссылку: ");
                    url = scanner.next();

                    System.out.print("Введите количество переходов: ");
                    int addClickLimit = scanner.nextInt();

                    System.out.print("Введите время жизни ссылки в минутах: ");
                    int addDateValueLimit = scanner.nextInt();

                    System.out.println("Короткая ссылка: " + urlAPI.createShortURL(url, userId, addClickLimit, addDateValueLimit));
                    System.out.println();
                    break;
                case "2":
                    System.out.print("Введите короткую ссылку: ");
                    url = scanner.next();
                    shortUrl = urlAPI.restoreOriginalURL(userId, url);
                    if (shortUrl != null) {
                        url = shortUrl.getOriginalUrl();
                        System.out.println("Исходная ссылка: " + url);
                        try {
                            Desktop.getDesktop().browse(new URI(url));
                            shortUrl.setActualNumberClicks();
                        } catch (IOException | URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    System.out.println();
                    break;
                case "3":
                    System.out.print("Введите короткую ссылку: ");
                    url = scanner.next();
                    if (!urlAPI.isUrlInMapExist(userId, url)) {
                        System.out.println("Не удалось найти исходную ссылку");
                        break;
                    }
                    System.out.print("Введите новое количество переходов: ");
                    int newClickLimit = scanner.nextInt();
                    shortUrl = urlAPI.restoreOriginalURL(userId, url);
                    if (shortUrl != null) {
                        shortUrl.setClickLimit(newClickLimit);
                        System.out.println("Лимит обновлен");
                    }
                    System.out.println();
                    break;

                case "4":
                    System.out.print("Введите короткую ссылку: ");
                    url = scanner.next();
                    if (!urlAPI.isUrlInMapExist(userId, url)) {
                        System.out.println("Не удалось найти исходную ссылку");
                        break;
                    }
                    System.out.print("Введите время жизни ссылки в минутах: ");
                    int newDateValueLimit = scanner.nextInt();
                    shortUrl = urlAPI.restoreOriginalURL(userId, url);
                    if (shortUrl != null) {
                        shortUrl.setDateValueLimit(newDateValueLimit);
                        System.out.println("Время жизни ссылки обновлено");
                    }
                    System.out.println();
                    break;
                case "5":
                    System.out.print("Введите короткую ссылку: ");
                    url = scanner.next();
                    if (!urlAPI.isUrlInMapExist(userId, url)) {
                        System.out.println("Не удалось найти исходную ссылку");
                        break;
                    }

                    urlAPI.delShortUrlInMap(userId, url);
                    System.out.println();
                    break;
            }
        }
    }
}