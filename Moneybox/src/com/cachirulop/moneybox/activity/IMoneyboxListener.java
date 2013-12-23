
package com.cachirulop.moneybox.activity;

public interface IMoneyboxListener
{
    void onSetTotal (double value);

    void onUpdateTotal ();

    void onUpdateMoneybox ();

    void onUpdateMovements ();

    void onUpdateMoneyboxesList ();

    void onSelectDefaultTab ();
}
