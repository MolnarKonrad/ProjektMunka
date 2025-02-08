package konrad.hu;

public interface Database {
    boolean isDatabaseEmpty();
    Member findMemberByName(String memberName);
    void addMemberToDatabase(Member member);
    boolean removeMemberFromDatabase(String memberNameToDelete);
    boolean isLeader(String memberName);
    boolean hasActiveLeader();
    void addPerkToMember(String member, String perkName);
    void replaceMemberPerk(String memberName, String oldPerkName, String newPerkName);
    void listMembersAndPerks();
    boolean promoteMemberToLeader(String memberName);
}
