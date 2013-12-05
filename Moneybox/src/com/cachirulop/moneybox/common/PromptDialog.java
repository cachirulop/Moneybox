package com.cachirulop.moneybox.common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cachirulop.moneybox.R;

/**
 * helper for Prompt-Dialog creation
 */
public abstract class PromptDialog extends DialogFragment implements
		View.OnClickListener {
	private EditText _input;
	private int _titleId;
	private int _messageId;

	public PromptDialog(int titleId, int messageId) {
		super();

		_titleId = titleId;
		_messageId = messageId;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder;

		_input = new EditText(getActivity());

		builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(_titleId);
		builder.setMessage(_messageId);
		builder.setView(_input);

		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                //Do nothing here because we override this button later to change the close behaviour. 
                //However, we still need this because on older versions of Android unless we 
                //pass a handler the button doesn't get instantiated
            }
        });

		builder.setNegativeButton(android.R.string.cancel, null);
        
		return builder.create();
	}

	@Override
	public void onStart() {
		super.onStart();
		
		AlertDialog dialog;

		dialog = (AlertDialog) getDialog();
		if (dialog != null) {
			Button positive;

			positive = (Button) dialog.getButton(Dialog.BUTTON_POSITIVE);

			positive.setOnClickListener(this);
		}
	}

	public void onClick(View v) {
		String txt;

		txt = _input.getText().toString();
		if (txt.trim().equals("")) {
			Toast t;

			t = Toast.makeText(getActivity(), R.string.empty_text_not_allowed,
					Toast.LENGTH_LONG);
			t.show();
		} else {
			if (onOkClicked(txt)) {
				((AlertDialog) getDialog()).dismiss();
			}
		}
	}

	/**
	 * called when "ok" pressed.
	 * 
	 * @param input
	 * @return true, if the dialog should be closed. false, if not.
	 */
	abstract public boolean onOkClicked(String input);
}