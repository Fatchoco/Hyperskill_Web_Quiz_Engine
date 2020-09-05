package engine;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Entity
public class Quiz {
    private static int latestId = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected int id;
    @NotBlank(message = "Title is mandatoryy")
    protected String title;
    @NotBlank(message = "Text is mandatoryy")
    protected String text;
    @NotEmpty(message = "Options is mandatorry")
    @Size(message = "Options should at least two items", min = 2)
    protected String[] options;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    protected int[] answer;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    protected String author;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Quiz(){
        //generateId();
        this.answer = new int[]{};
    }

    public Quiz(String title, String text, String[] options, int[] answer) {
        //generateId();
        this.title = title;
        this.text = text;
        this.options = options;
        this.answer = answer;
    }

    private void generateId() {
        this.id = ++latestId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public int[] getAnswer() {
        return answer;
    }

    public void setAnswer(int[] answer) {
        this.answer = answer;
    }

}
