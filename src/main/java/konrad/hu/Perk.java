package konrad.hu;

import java.io.Serializable;
import java.util.Objects;

/**
 * A Perk osztály a BaseEntity osztályból származik, és reprezentálja a játék perkjeit.
 * A perkeknek van egy neve és egy leírása, amely információt nyújt a perk funkciójáról.
 */
public class Perk extends BaseEntity implements Displayable, Serializable {
    private static final long serialVersionUID = 1L;
    private String description;

    /**
     * Alapértelmezett konstruktor, amely inicializálja a Perk példányt.
     */
    public Perk() { }

    /**
     * Konstruktor, amely inicializálja a Perk példányt a megadott névvel és leírással.
     *
     * @param name a perk neve
     * @param description a perk leírása
     */
    public Perk(String name, String description) {
        super(name);
        this.description = description;
    }

    /**
     * Megjeleníti a perk információit, beleértve a nevét és a leírását.
     */
    @Override
    public void displayInfo() {
        System.out.println("Perk neve: " + name + ", Leirás: " + description);
    }

    /**
     * Összehasonlítja az aktuális Perk objektumot egy másik objektummal.
     *
     * @param o a másik objektum, amellyel összehasonlítjuk
     * @return true, ha az objektumok egyenlőek; false, ha nem
     */
    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        Perk perk = (Perk) o;
        return Objects.equals(description, perk.description);
    }

    /**
     * Visszaadja a Perk objektum hash kódját.
     *
     * @return a Perk objektum hash kódja
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), description);
    }
}
