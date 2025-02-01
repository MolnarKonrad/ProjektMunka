package konrad.hu;

public class Member extends BaseEntity{

    private boolean isLeader;


    public Member(String name, boolean isLeader) {
        super(name);
        this.isLeader = isLeader;
    }

    public boolean isLeader() {
        return isLeader;
    }
}
