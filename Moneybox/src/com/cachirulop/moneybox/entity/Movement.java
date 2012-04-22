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

public class Movement 
	implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int _idMovement;
	private double _amount;
	private Date _insertDate;
	private Date _getDate;
	private String _description;
	private boolean _breakMoneybox;
	
	public int getIdMovement() {
		return _idMovement;
	}
	public void setIdMovement(int idMovement) {
		this._idMovement = idMovement;
	}
	public double getAmount() {
		return _amount;
	}
	public void setAmount(double amount) {
		this._amount = amount;
	}
	
	public Date getInsertDate() {
		return _insertDate;
	}
	public long getInsertDateDB() {
		return _insertDate.getTime();
	}
	public void setInsertDate(Date insertDate) {
		this._insertDate = insertDate;
	}
	public Date getGetDate() {
		return _getDate;
	}
	public Long getGetDateDB() {
		if (_getDate != null) {
			return _getDate.getTime();
		}
		else {
			return null;
		}
	}
	public void setGetDate(Date getDate) {
		this._getDate = getDate;
	}

	public String getDescription() {
		return _description;
	}
	public void setDescription(String description) {
		this._description = description;
	}
	public boolean isBreakMoneybox() {
		return _breakMoneybox;
	}
	public int isBreakMoneyboxAsInt() {
		if (!_breakMoneybox) {
			return 0;
		}
		else {
			return 1;
		}
	}
	public void setBreakMoneybox(boolean breakMoneybox) {
		this._breakMoneybox = breakMoneybox;
	}
	public void setBreakMoneyboxAsInt(int breakMoneybox) {
		this._breakMoneybox = (breakMoneybox != 0);
	}
}
