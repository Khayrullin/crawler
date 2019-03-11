package ru.kpfu.itis;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Parser {
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    private List<String> links = new LinkedList<String>();
    private Document htmlDocument;

    public boolean crawl(String url) {
        try {
            Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
            Document htmlDocument = connection.get();
            this.htmlDocument = htmlDocument;
            if (connection.response().statusCode() == 200) {
                System.out.println("\n**Visiting** Received web page at " + url);
            }
            if (!connection.response().contentType().contains("text/html")) {
                System.out.println("**Failure** Retrieved something other than HTML");
                return false;
            }
            writeDocument(htmlDocument, url);
            Elements linksOnPage = htmlDocument.select("a[href^=\"https://habr.com/ru/post/\"]")
                    .stream().filter(e -> !e.attr("abs:href").contains("#"))
                    .collect(Collectors.toCollection(Elements::new));
            for (Element link : linksOnPage) {
                this.links.add(link.absUrl("href"));
            }
            return true;
        } catch (IOException ioe) {
            return false;
        }
    }

    public void writeDocument(Document document, String id) throws IOException {
        Path path = Paths.get("src/main/resources/post_" + id.replaceAll("[:/\\\\]", "") + ".txt");
        Files.write(path, document.select("div.post__text.post__text-html.js-mediator-article").text().getBytes());
    }

    public List<String> getLinks() {
        return this.links;
    }
}
