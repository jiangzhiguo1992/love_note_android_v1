package com.jiangzg.lovenote.model.entity;

/**
 * Created by JZG on 2018/10/13.
 * MensesInfo
 */
public class MensesInfo {

    private boolean canMe;
    private boolean canTa;
    private MensesLength mensesLengthMe;
    private MensesLength mensesLengthTa;

    public boolean isCanMe() {
        return canMe;
    }

    public void setCanMe(boolean canMe) {
        this.canMe = canMe;
    }

    public boolean isCanTa() {
        return canTa;
    }

    public void setCanTa(boolean canTa) {
        this.canTa = canTa;
    }

    public MensesLength getMensesLengthMe() {
        return mensesLengthMe;
    }

    public void setMensesLengthMe(MensesLength mensesLengthMe) {
        this.mensesLengthMe = mensesLengthMe;
    }

    public MensesLength getMensesLengthTa() {
        return mensesLengthTa;
    }

    public void setMensesLengthTa(MensesLength mensesLengthTa) {
        this.mensesLengthTa = mensesLengthTa;
    }
}
