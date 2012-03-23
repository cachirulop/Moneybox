package com.cachirulop.moneybox.manager;

import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import com.cachirulop.moneybox.R;
import com.cachirulop.moneybox.activity.MoneyboxActivity;

public class SoundsManager {
	
	private static final int SOUND_DROP_COIN = 0;
	private static final int SOUND_BREAK_MONEYBOX = 1;

	private static SoundPool _sounds;
	private static int _soundsMap [];
	
	static {
       _sounds = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        _soundsMap = new int [2];
        
        _soundsMap [SOUND_DROP_COIN] = _sounds.load(MoneyboxActivity.getContext(), R.raw.coin_dropping, 1);
        _soundsMap [SOUND_BREAK_MONEYBOX] =_sounds.load(MoneyboxActivity.getContext(), R.raw.breaking_glass, 1); 
	}

	public static void playCoinsSound () {
		SoundsManager.playSound(SOUND_DROP_COIN);
	}
	
	public static void playBreakingMoneyboxSound () {
		SoundsManager.playSound(SOUND_BREAK_MONEYBOX);
	}
	
	private static void playSound (int soundIndex) {
		if (soundIndex < _soundsMap.length) {
			_sounds.play(_soundsMap [soundIndex], 1.0f, 1.0f, 1, 0, 1.0f);
		}
		else {
			Log.w(SoundsManager.class.getName(), "Sound not found (" + soundIndex + ")");
		}
	}
}
