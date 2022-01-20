package program;

import java.util.Date;

public class News {
    private int id;
    private String name;
    private String content;
    private Date date;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    public News() {};

    public News(int id, String name, String content, Date date) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.date = date;
    }
}
