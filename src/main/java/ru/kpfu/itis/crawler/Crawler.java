package ru.kpfu.itis.crawler;

import ru.kpfu.itis.util.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Crawler {
    private static final int MAX_PAGES_TO_SEARCH = 10;
    private Set<String> pagesVisited = new HashSet<>();
    private List<String> pagesToVisit = new LinkedList<>();

    public static final String BASE_URL = "https://habr.com";
    public static final String START_PATH = BASE_URL + "/en/";

    public Crawler() {
        FileUtils fileUtils = new FileUtils();
        fileUtils.clearDirectory();
        fileUtils.createDirectories(FileUtils.BASE_PATH);
    }

    public void search(String url) {
        while (this.pagesVisited.size() < MAX_PAGES_TO_SEARCH) {
            String currentUrl;
            Parser parser = new Parser();
            if (this.pagesToVisit.isEmpty()) {
                currentUrl = url;
                this.pagesVisited.add(url);
            } else {
                currentUrl = this.nextUrl();
            }
            parser.crawl(currentUrl);
            this.pagesToVisit.addAll(parser.getLinks());
        }
        System.out.println("\n**Done** Visited " + this.pagesVisited.size() + " web page(s)");

    }

    private String nextUrl() {
        String nextUrl;
        do {
            nextUrl = this.pagesToVisit.remove(0);
        } while (this.pagesVisited.contains(nextUrl));
        this.pagesVisited.add(nextUrl);
        return nextUrl;
    }

    private void writeLinks() {
        Path path = Paths.get("src/main/resources/links.txt");
        try {
            Files.write(path, pagesVisited);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Crawler crawler = new Crawler();
        crawler.search(START_PATH);
        crawler.writeLinks();
    }
}
