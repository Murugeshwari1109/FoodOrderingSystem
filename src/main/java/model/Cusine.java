package model;

public class Cusine {
    private int cusineId;
    private String cusineName;

    public Cusine() {}

    public Cusine(int cusineId, String cusineName) {
        this.cusineId = cusineId;
        this.cusineName = cusineName;
    }

    public int getCusineId() {
        return cusineId;
    }

    public void setCusineId(int cusineId) {
        this.cusineId = cusineId;
    }

    public String getCusineName() {
        return cusineName;
    }

    public void setCusineName(String cusineName) {
        this.cusineName = cusineName;
    }
}
