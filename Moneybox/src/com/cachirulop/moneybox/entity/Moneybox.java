/*******************************************************************************
 *   Copyright (c) 2012 David Magro Martin.
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the GNU Public License v3.0
 *   which accompanies this distribution, and is available at
 *   http://www.gnu.org/licenses/gpl.html
 *   
 * Contributors:
 *       David Magro Martin
 *******************************************************************************/

package com.cachirulop.moneybox.entity;

import java.io.Serializable;
import java.util.Date;

import com.cachirulop.moneybox.common.Util;

public class Moneybox
        implements Serializable
{

    private static final long serialVersionUID = 1L;

    private long              _idMoneybox;
    private Date              _creationDate;
    private String            _description;

    public long getIdMoneybox ()
    {
        return _idMoneybox;
    }

    public void setIdMoneybox (long idMoneybox)
    {
        this._idMoneybox = idMoneybox;
    }

    public Date getCreationDate ()
    {
        return _creationDate;
    }

    public long getCreationDateDB ()
    {
        return _creationDate.getTime ();
    }

    public String getCreationDateFormatted ()
    {
        return Util.formatDate (_creationDate);
    }

    public void setCreationDate (Date creationDate)
    {
        this._creationDate = creationDate;
    }

    public String getDescription ()
    {
        return _description;
    }

    public void setDescription (String description)
    {
        this._description = description;
    }
}
