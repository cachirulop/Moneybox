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

package com.cachirulop.moneybox.manager;

import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import com.cachirulop.moneybox.R;
import com.cachirulop.moneybox.entity.CurrencyValueDef;

public class SoundsManager
{

    private static final int SOUND_DROP_COIN      = 0;
    private static final int SOUND_DROP_BILL      = 1;
    private static final int SOUND_BREAK_MONEYBOX = 2;

    private static SoundPool _sounds;
    private static int       _soundsMap[];

    /**
     * Load the sound to be used by the object.
     */
    static {
        loadSounds ();
    }

    /**
     * Load the sound in the _soundMap object and create the sound pool.
     */
    private synchronized static void loadSounds ()
    {
        if (_sounds == null) {
            _sounds = new SoundPool (20,
                                     AudioManager.STREAM_MUSIC,
                                     0);
            _soundsMap = new int[3];

            _soundsMap [SOUND_DROP_COIN] = _sounds.load (ContextManager.getContext (),
                                                         R.raw.coin_dropping,
                                                         1);
            _soundsMap [SOUND_DROP_BILL] = _sounds.load (ContextManager.getContext (),
                                                         R.raw.paper_dropping,
                                                         1);
            _soundsMap [SOUND_BREAK_MONEYBOX] = _sounds.load (ContextManager.getContext (),
                                                              R.raw.breaking_glass,
                                                              1);
        }
    }

    public static void playMoneySound (CurrencyValueDef.MoneyType type)
    {
        if (type == CurrencyValueDef.MoneyType.COIN) {
            playCoinsSound ();
        }
        else {
            playBillSound ();
        }
    }

    public static void playCoinsSound ()
    {
        SoundsManager.playSound (SOUND_DROP_COIN);
    }

    public static void playBillSound ()
    {
        SoundsManager.playSound (SOUND_DROP_BILL);
    }

    public static void playBreakingMoneyboxSound ()
    {
        SoundsManager.playSound (SOUND_BREAK_MONEYBOX);
    }

    private static void playSound (int soundIndex)
    {
        if (soundIndex < _soundsMap.length) {
            _sounds.play (_soundsMap [soundIndex],
                          1.0f,
                          1.0f,
                          1,
                          0,
                          1.0f);
        }
        else {
            Log.w (SoundsManager.class.getName (),
                   "Sound not found (" + soundIndex + ")");
        }
    }

    /**
     * Load the sounds to avoid delays.
     */
    public static void initSounds ()
    {
        // Do nothing, the initialization is done in the static constructor
        // loadSounds();
    }
}
