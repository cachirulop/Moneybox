package com.cachirulop.moneybox.entity;

import java.io.Serializable;
import java.util.Date;

import common.Util;

public class Moneybox implements Serializable {

    private static final long serialVersionUID = 1L;

    private int _idMoneybox;
    private Date _creationDate;
    private String _description;

    public int getIdMoneybox() {
        return _idMoneybox;
    }

    public void setIdMoneybox(int idMoneybox) {
        this._idMoneybox = idMoneybox;
    }

    public Date getCreationDate() {
        return _creationDate;
    }

    public long getCreationDateDB() {
        return _creationDate.getTime();
    }

    public String getCreationDateFormatted() {
        return Util.formatDate(_creationDate);
    }

    public void setCreationDate(Date creationDate) {
        this._creationDate = creationDate;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        this._description = description;
    }
}
