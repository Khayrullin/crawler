package ru.kpfu.itis.invertedindex;

import java.util.TreeMap;

public class Idf {
    private TreeMap<String, Double> idf = new TreeMap<>();

    public TreeMap<String, Double> getIdf() {
        return idf;
    }

    public void setIdf(TreeMap<String, Double> idf) {
        this.idf = idf;
    }
}
