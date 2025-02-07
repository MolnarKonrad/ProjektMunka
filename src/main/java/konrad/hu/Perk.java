package konrad.hu;

import java.io.Serializable;
import java.util.Objects;

public class Perk extends BaseEntity implements Displayable, Serializable {
    private static final long serialVersionUID = 1L;
    private String description;

    public Perk() { }

    public Perk(String name, String description) {
        super(name);
        this.description = description;
    }

    @Override
    public void displayInfo() {
        System.out.println("Perk neve: " + name + ", Leirás: " + description);
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        Perk perk = (Perk) o;
        return Objects.equals(description, perk.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), description);
    }
}
