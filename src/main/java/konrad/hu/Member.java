package konrad.hu;

import java.util.Objects;

/**
 * A Member osztály a BaseEntity osztályból származik, és reprezentálja a játék tagjait.
 * A tagoknak lehet vezetői státuszuk, amelyet a isLeader mező határoz meg.
 */
public class Member extends BaseEntity implements Displayable{

    private boolean isLeader;

    /**
     * Alapértelmezett konstruktor, amely inicializálja a Member példányt.
     */
    public Member() { }

    /**
     * Konstruktor, amely inicializálja a Member példányt a megadott névvel és vezetői státusszal.
     *
     * @param name a tag neve
     * @param isLeader a tag vezetői státusza (true, ha vezető; false, ha nem)
     */
    public Member(String name, boolean isLeader) {
        super(name);
        this.isLeader = isLeader;
    }

    /**
     * Visszaadja, hogy a tag vezető-e.
     *
     * @return true, ha a tag vezető; false, ha nem
     */
    public boolean isLeader() {
        return isLeader;
    }

    /**
     * Megjeleníti a tag információit, beleértve a nevét és a vezetői státuszát.
     */
    @Override
    public void displayInfo() {
        System.out.println("Tag neve: " + name + ", Leader: " + (isLeader ? "Igen" : "Nem"));
    }

    /**
     * Összehasonlítja az aktuális Member objektumot egy másik objektummal.
     *
     * @param o a másik objektum, amellyel összehasonlítjuk
     * @return true, ha az objektumok egyenlőek; false, ha nem
     */
    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        Member member = (Member) o;
        return isLeader == member.isLeader;
    }

    /**
     * Visszaadja a Member objektum hash kódját.
     *
     * @return a Member objektum hash kódja
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), isLeader);
    }
}
