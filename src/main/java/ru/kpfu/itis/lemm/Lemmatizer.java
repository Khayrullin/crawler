package ru.kpfu.itis.lemm;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class Lemmatizer {

    private static final Properties PROPERTIES = new Properties();

    public StanfordCoreNLP stanfordCoreNLP;

    static {
        PROPERTIES.put("annotators", "tokenize, ssplit, pos, lemma");
    }

    public Lemmatizer() {
        stanfordCoreNLP = new StanfordCoreNLP(PROPERTIES);
    }

    public String lemmatize(String documentText) {
        List<String> lemmas = new LinkedList<String>();
        Annotation document = new Annotation(documentText);
        this.stanfordCoreNLP.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                lemmas.add(token.get(CoreAnnotations.LemmaAnnotation.class));
            }
        }

        return String.join(" ", lemmas);
    }
}
