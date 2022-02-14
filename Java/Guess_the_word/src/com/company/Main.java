package com.company;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Main
{

    public static void main(String[] args) throws IOException
    {
        //Krzysztof_Śliwka     //test article

        GUI gui;
        int answ = 3, r_sentence, r_heading;
        Document doc = Jsoup.connect("https://pl.wikipedia.org/wiki/Specjalna:Losowa_strona").get();

        String title = doc.title();
        Element el_heading = doc.getElementById("firstHeading");
        String heading = el_heading.text();

        Element el_cat = doc.getElementById("mw-normal-catlinks");
        String S_el_cat = el_cat.text();
        String category = S_el_cat.substring(11, S_el_cat.length());

        Elements el_content = doc.getElementsByTag("p");
        String S_el_content = el_content.text();
        S_el_content = S_el_content.replaceAll("\\[([0-9])\\]", "");


        ArrayList<String> sentences = new ArrayList<>();

        BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.forLanguageTag("pl"));
        String source = S_el_content;
        iterator.setText(source);
        int start = iterator.first();

        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next())
        {
                sentences.add(source.substring(start,end));
        }

        Elements links_cat_heading = doc.select(".mw-normal-catlinks li > a[href]");
        ArrayList<String> links_list = new ArrayList<>();

        for (Element link_cat_heading : links_cat_heading)
        {
            links_list.add(link_cat_heading.absUrl("href"));
        }

        Document doc0, doc_h0, doc_h1;
        Elements els;
        String link_h0, link_h1 = null;
        String h0, h1;

        doc0 = Jsoup.connect(links_list.get(0)).get();       //change 0 to 1 for other category in wiki article

        els = doc0.select("#mw-pages li > a[href]");

        //System.out.println(els.get(0).attr("href"));  //change 0 to something for other article in category from "links_list.get(0)"

        link_h0 = "https://pl.wikipedia.org" + els.get(0).attr("href");
        link_h1 = "https://pl.wikipedia.org" + els.get(1).attr("href");

        if(els.get(1).attr("href") == null) link_h1 = "https://pl.wikipedia.org/wiki/Specjalna:Losowa_strona";

        doc_h0 = Jsoup.connect(link_h0).get();
        doc_h1 = Jsoup.connect(link_h1).get();
        h0 = doc_h0.getElementById("firstHeading").text();
        h1 = doc_h1.getElementById("firstHeading").text();

        String headings[] = new String[3];
        headings[0] = h0;
        headings[1] = h1;
        headings[2] = heading;

        r_sentence = ThreadLocalRandom.current().nextInt(2,sentences.size());
        List<String> h_temp = Arrays.asList(headings);
        Collections.shuffle(h_temp);
        headings = h_temp.toArray(new String[h_temp.size()]);

        gui = new GUI(sentences.get(r_sentence), category, headings[0], headings[1], headings[2], heading);

        /**plany: dodac liczbe prob?, czas trwania, punkty?, sprawdzic dla jakich przypadkow rozmiar listy sentences jest zerowy (i czy wgl jeszcze sa takie przypadkiczy juz nie)**/
    }



}
