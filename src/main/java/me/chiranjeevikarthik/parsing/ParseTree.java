package parsing;

public class ParseTree<T> {

    private String type = null;
    private T value = null;
    private Long length = null;

    public T getValue() {
        return this.value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public Long getLength() {
        return this.length;
    }

    public void setLength(Long length) {
        this.length = length;
    }
}


