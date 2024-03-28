package com.viettelpost.core.services.domains;

import java.io.Serializable;

public class PhoneInfo implements Serializable {
    String oldPhone;
    String newPhone;

    public PhoneInfo(String oldPhone, String newPhone) {
        this.oldPhone = oldPhone;
        this.newPhone = newPhone;
    }

    public String getOldPhone() {
        return oldPhone;
    }

    public void setOldPhone(String oldPhone) {
        this.oldPhone = oldPhone;
    }

    public String getNewPhone() {
        return newPhone;
    }

    public void setNewPhone(String newPhone) {
        this.newPhone = newPhone;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof String) {
            return oldPhone.equals(obj) || newPhone.equals(obj);
        }
        if (obj instanceof PhoneInfo) {
            PhoneInfo phone = (PhoneInfo) obj;
            return (phone.getOldPhone() != null && oldPhone.equals(phone.getOldPhone())) || (phone.getNewPhone() != null && newPhone.equals(phone.getNewPhone()));
        }
        return super.equals(obj);
    }
}
