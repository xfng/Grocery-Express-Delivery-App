package group35.droneDeliverySystem.Classes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class AdminSetting {

    @Id
    @Column(name = "system_id")
    private String systemID;

    private int limitCount;
    private int limitPeriod;
    private int returnPeriod;
    private String password;

    protected AdminSetting() {
    }

    public int getLimitCount() {
        return this.limitCount;
    }

    public int getLimitPeriod() {
        return this.limitPeriod;
    }

    public int getReturnPeriod() {
        return this.returnPeriod;
    }

    public void setLimitCount(int limitCount) {
        this.limitCount = limitCount;
    }

    public void setLimitPeriod(int limitPeriod) {
        this.limitPeriod = limitPeriod;
    }

    public void setReturnPeriod(int returnPeriod) {
        this.returnPeriod = returnPeriod;
    }
}
