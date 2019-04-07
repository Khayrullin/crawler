package ru.kpfu.itis.crawler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import ru.kpfu.itis.util.FileUtils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Parser {
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    private List<String> links = new LinkedList<String>();
    private FileUtils fileUtils = new FileUtils();
    private static int k = 1;

    public boolean crawl(String url) {
        Document htmlDocument = scan(url);
        fileUtils.writeDocument(htmlDocument, k++ + "_" + url);
//        Elements linksOnPage = htmlDocument.select("a[href^=\"https://habr.com/en/post/\"]")
        Elements linksOnPage = htmlDocument.select("a")
                .stream().filter(e -> !e.attr("abs:href").contains("#"))
                .collect(Collectors.toCollection(Elements::new));
        linksOnPage.forEach(link -> this.links.add(link.absUrl("href")));
        return true;
    }

    public Document scan(String url) {
        Connection connection;
        Document htmlDocument = null;
        try {
            connection = Jsoup.connect(url).userAgent(USER_AGENT);
            htmlDocument = connection.get();
            if (connection.response().statusCode() == 200) {
                System.out.println("\n**Visiting** Received web page at " + url);
            }
            if (!connection.response().contentType().contains("text/html")) {
                System.out.println("**Failure** Retrieved something other than HTML");
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return htmlDocument;
    }

    public List<String> getLinks() {
        return this.links;
    }
}
