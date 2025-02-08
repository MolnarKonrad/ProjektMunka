package konrad.hu;

import java.io.Serializable;
import java.util.Objects;

/**
 * Az absztrakt BaseEntity osztály, amely a névvel rendelkező entitások alapvető
 * tulajdonságait és viselkedését definiálja. Az osztály implementálja a Serializable
 * interfészt, lehetővé téve az objektumok sorosítását.
 */
public abstract class BaseEntity implements Serializable{
    private static final long serialVersionUID = 1L;
    protected String name;

    /**
     * Alapértelmezett konstruktor, amely inicializálja a BaseEntity példányt.
     */
    public BaseEntity(){ }

    /**
     * Konstruktor, amely inicializálja a BaseEntity példányt a megadott névvel.
     *
     * @param name a név, amelyet az entitásnak adunk
     */
    public BaseEntity(String name) {
        this.name = name;
    }

    /**
     * Visszaadja az entitás nevét.
     *
     * @return a név, amelyet az entitás tárol
     */
    public String getName() {
        return name;
    }

    /**
     * Összehasonlítja az aktuális objektumot egy másik objektummal.
     *
     * @param o a másik objektum, amellyel összehasonlítjuk
     * @return true, ha az objektumok egyenlőek; false, ha nem
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity)) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(name, that.name);
    }

    /**
     * Visszaadja az entitás hash kódját.
     *
     * @return az entitás hash kódja
     */
    @Override
    public int hashCode(){
        return Objects.hash(name);
    }

    /**
     * Megjeleníti az entitás információit.
     * Ezt a metódust a leszármazott osztályoknak kell implementálniuk.
     */
    public abstract void displayInfo();
}
