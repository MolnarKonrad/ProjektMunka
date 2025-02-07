package konrad.hu;

import java.util.Objects;

public class Member extends BaseEntity implements Displayable{

    private boolean isLeader;

    public Member() { }

    public Member(String name, boolean isLeader) {
        super(name);
        this.isLeader = isLeader;
    }

    public boolean isLeader() {
        return isLeader;
    }

    @Override
    public void displayInfo() {
        System.out.println("Tag neve: " + name + ", Leader: " + (isLeader ? "Igen" : "Nem"));
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        Member member = (Member) o;
        return isLeader == member.isLeader;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), isLeader);
    }
}
