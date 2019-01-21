package shared.enums;

public enum GameAgentEnum {

	MIPSMAN (0), EINY (1), DOSY (2), SANY (3), FOURY (4);

    private int numVal;

    GameAgentEnum(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}
