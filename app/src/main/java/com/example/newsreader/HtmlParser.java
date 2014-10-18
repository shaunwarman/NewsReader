package com.example.newsreader;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by shaun on 10/17/14.
 */
public class HtmlParser {
    private String title = null;
    private String summary = null;
    private String source = null;
    private String current = null;

    public HtmlParser() {

    }


    public List<NewsArticle> parseHTML(String html) {
        List<NewsArticle> articles = new LinkedList<NewsArticle>();
        NewsArticle pa;

        for(int a = 0; a <= 10; a++) {
            pa = new NewsArticle();
            if(html.indexOf("<title type=\"html\"") == -1) {
                break;
            }

            html = html.substring(html.indexOf("<title type=\"html\""));
            current = html.substring(html.indexOf("title type=\"html\"")+18, html.indexOf("</title>"));
            title = current;
            pa.setTitle(title);
            for(int b = 0; b <1; b++) {
                html = html.substring(html.indexOf("<summary type=\"html\""));
                current = html.substring(html.indexOf("summary type=\"html\"")+20, html.indexOf("&lt;"));
                summary = current;
                pa.setSummary(summary);
                for(int c = 0; c<1;c++) {
                    html = html.substring(html.indexOf("<link rel=\"alternate\""));
                    current = html.substring(html.indexOf("<link rel=\"alternate\" href=\"")+28, html.indexOf("\" />"));
                    //	Log.i("SOURCE", current);
                    source = current;
                    pa.setSource(source);
                    articles.add(pa);
                }
            }
        }
        return articles;
    }

}
