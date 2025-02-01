package konrad.hu;

public abstract class BaseEntity {
    protected String name;

    public BaseEntity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
