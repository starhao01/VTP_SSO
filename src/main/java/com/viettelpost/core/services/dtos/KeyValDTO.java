package com.viettelpost.core.services.dtos;


import com.viettelpost.core.utils.Utils;

import java.io.Serializable;

public class KeyValDTO implements Serializable {
    String name;
    String description;
    Object parentValue;
    Object value;
    String otherValue;
    Object fullData;
    Object other;
    String otherName;
    String type;
    Long idOther;

    public Long getIdOther() {
        return idOther;
    }

    public void setIdOther(Long idOther) {
        this.idOther = idOther;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public KeyValDTO() {
    }

    public KeyValDTO(Object value) {
        this.value = value;
    }

    public KeyValDTO(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public KeyValDTO(String name, String description, Object value) {
        this.name = name;
        this.description = description;
        this.value = value;
    }

    public KeyValDTO(String name, String description, Object value, Object fullData) {
        this.name = name;
        this.description = description;
        this.value = value;
        this.fullData = fullData;
    }

    public KeyValDTO(String name, String description, Object parentValue, Object value, String otherValue) {
        this.name = name;
        this.description = description;
        this.parentValue = parentValue;
        this.value = value;
        this.otherValue = otherValue;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public Object getOther() {
        return other;
    }

    public void setOther(Object other) {
        this.other = other;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getParentValue() {
        return parentValue;
    }

    public void setParentValue(Object parentValue) {
        this.parentValue = parentValue;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }


    public String getOtherValue() {
        return otherValue;
    }

    public void setOtherValue(String otherValue) {
        this.otherValue = otherValue;
    }

    public Object getFullData() {
        return fullData;
    }

    public void setFullData(Object fullData) {
        this.fullData = fullData;
    }

    public String getDisplayName() {
        if (Utils.isNullOrEmpty(otherValue)) {
            return name;
        } else return "[" + otherValue + "] - " + (Utils.isNullOrEmpty(name) ? "No Name" : name);
    }

    public String getDisplayName2() {
        if (Utils.isNullOrEmpty(value)) {
            return name;
        } else return "[" + value + "] " + (Utils.isNullOrEmpty(name) ? "No Name" : name);
    }

    public String getDisplayName3() {
        if (Utils.isNullOrEmpty(description)) {
            return name;
        } else
            return "[" + description + "] - " + (Utils.isNullOrEmpty(name) ? "No Name" : name + (fullData == null ? "" : ", "+fullData.toString().toUpperCase()));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof KeyValDTO && obj != null) {
            KeyValDTO cpm = (KeyValDTO) obj;
            if (cpm.getValue() != null && this.getValue() != null) {
                return cpm.getValue().equals(this.getValue());
            }
        }
        return super.equals(obj);
    }
}
