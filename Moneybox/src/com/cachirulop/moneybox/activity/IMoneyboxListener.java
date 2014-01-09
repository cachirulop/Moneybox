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

package com.cachirulop.moneybox.activity;

import com.cachirulop.moneybox.entity.Movement;

public interface IMoneyboxListener
{
    void onSetTotal (double value);

    void onUpdateTotal ();

    void onUpdateMoneybox ();

    void onUpdateMovements ();

    void onUpdateMoneyboxesList ();
    
    void onGetMovement (Movement m);
    
    void onDeleteMovement (Movement m);
    
    void onDropAgainMovement (Movement m);
}
