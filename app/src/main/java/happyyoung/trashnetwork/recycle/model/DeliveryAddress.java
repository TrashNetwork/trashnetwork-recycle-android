package happyyoung.trashnetwork.recycle.model;

import java.io.Serializable;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-07-05
 */
public class DeliveryAddress implements Serializable {
    private String name;
    private String phoneNumber;
    private String address;
    private Boolean isDefault;

    public DeliveryAddress(String name, String phoneNumber, String address, Boolean isDefault) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.isDefault = isDefault;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isDefault() {
        if(isDefault == null)
            return false;
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }
}
