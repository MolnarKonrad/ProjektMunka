package konrad.hu;

public class Perk extends BaseEntity{

    private String description;


    public Perk(String name, String description) {
        super(name);
        this.description = description;
    }
}
