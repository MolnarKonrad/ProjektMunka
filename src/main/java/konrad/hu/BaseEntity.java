package konrad.hu;

import java.io.Serializable;
import java.util.Objects;

public abstract class BaseEntity implements Serializable{
    protected String name;

    public BaseEntity(){ }

    public BaseEntity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity)) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode(){
        return Objects.hash(name);
    }

    public abstract void displayInfo();
}
