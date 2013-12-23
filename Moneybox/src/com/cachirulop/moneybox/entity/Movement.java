/*******************************************************************************
 * Copyright (c) 2012 David Magro Martin.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     David Magro Martin - initial API and implementation
 ******************************************************************************/

package com.cachirulop.moneybox.entity;

import java.io.Serializable;
import java.util.Date;

import com.cachirulop.moneybox.common.Util;
import com.cachirulop.moneybox.manager.MoneyboxesManager;

public class Movement
        implements Serializable
{

    private static final long serialVersionUID = 1L;

    private long              _idMovement;
    private long              _idMoneybox;
    private double            _amount;
    private Date              _insertDate;
    private Date              _getDate;
    private String            _description;
    private boolean           _breakMoneybox;

    private Moneybox          _moneybox        = null;

    public long getIdMovement ()
    {
        return _idMovement;
    }

    public void setIdMovement (long idMovement)
    {
        this._idMovement = idMovement;
    }

    public long getIdMoneybox ()
    {
        return _idMoneybox;
    }

    public void setIdMoneybox (long idMoneybox)
    {
        this._idMoneybox = idMoneybox;
    }

    public double getAmount ()
    {
        return _amount;
    }

    public void setAmount (double amount)
    {
        this._amount = amount;
    }

    public Date getInsertDate ()
    {
        return _insertDate;
    }

    public long getInsertDateDB ()
    {
        return _insertDate.getTime ();
    }

    public String getInsertDateFormatted ()
    {
        return Util.formatDate (_insertDate);
    }

    public void setInsertDate (Date insertDate)
    {
        this._insertDate = insertDate;
    }

    public Date getGetDate ()
    {
        return _getDate;
    }

    public Long getGetDateDB ()
    {
        if (_getDate != null) {
            return _getDate.getTime ();
        }
        else {
            return null;
        }
    }

    public String getGetDateFormatted ()
    {
        return Util.formatDate (_getDate);
    }

    public void setGetDate (Date getDate)
    {
        this._getDate = getDate;
    }

    public String getDescription ()
    {
        return _description;
    }

    public void setDescription (String description)
    {
        this._description = description;
    }

    public boolean isBreakMoneybox ()
    {
        return _breakMoneybox;
    }

    public int isBreakMoneyboxAsInt ()
    {
        if (!_breakMoneybox) {
            return 0;
        }
        else {
            return 1;
        }
    }

    public void setBreakMoneybox (boolean breakMoneybox)
    {
        this._breakMoneybox = breakMoneybox;
    }

    public void setBreakMoneyboxAsInt (int breakMoneybox)
    {
        this._breakMoneybox = (breakMoneybox != 0);
    }

    public Moneybox getMoneybox ()
    {
        if (_moneybox == null) {
            _moneybox = MoneyboxesManager.getMoneybox (this.getIdMoneybox ());
        }

        return _moneybox;
    }

    public void setMoneybox (Moneybox m)
    {
        _moneybox = m;
        _idMoneybox = m.getIdMoneybox ();
    }
}
