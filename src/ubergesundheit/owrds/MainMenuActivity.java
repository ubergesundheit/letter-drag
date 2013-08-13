package ubergesundheit.owrds;

import ubergesundheit.owrds.elems.MainMenuView;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class MainMenuActivity extends Activity implements OnTouchListener {

	private MainMenuView mmView;
	private SharedPreferences pref;

	private boolean noGame = true;

	@TargetApi(11)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.layout_main_menu);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().hide();
		}
		mmView = (MainMenuView) this.findViewById(R.id.mainmenuview);
		mmView.setOnTouchListener(this);
		mmView.setContinueActive(true);

	}

	@Override
	protected void onResume() {
		super.onResume();
		manageGameState();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_CANCEL:
			mmView.touchUp();
			break;
		case MotionEvent.ACTION_DOWN:
			mmView.delegateTouchDown(event.getX(), event.getY());
			break;
		case MotionEvent.ACTION_UP:
			switch (mmView.touchUp()) {
			case MainMenuView.CONTINUE_GAME:
				Intent resumeGameIntent = new Intent(MainMenuActivity.this,
						MainGameActivity.class);
				resumeGameIntent.putExtra(MainGameActivity.GAME_MODE,
						MainGameActivity.RESUME_GAME);
				startActivity(resumeGameIntent);
				break;
			case MainMenuView.NEW_GAME:

				if (noGame) {
					startNewGame();
				} else {
					makeNewGameAlert();
				}
				break;
			case MainMenuView.ENDLESS_GAME:
				Intent endlessGameIntent = new Intent(MainMenuActivity.this,
						MainGameActivity.class);
				endlessGameIntent.putExtra(MainGameActivity.GAME_MODE,
						MainGameActivity.ENDLESS_GAME);
				startActivity(endlessGameIntent);
				break;
			case MainMenuView.SETTINGS:
				startActivity(new Intent(MainMenuActivity.this,
						SettingsActivity.class));
				break;
			case MainMenuView.HELP:
				startActivity(new Intent(MainMenuActivity.this,
						HowToActivity.class));
				break;
			}
			break;
		}

		return true;
	}

	private void startNewGame() {
		Intent newGameIntent = new Intent(MainMenuActivity.this,
				MainGameActivity.class);
		newGameIntent.putExtra(MainGameActivity.GAME_MODE,
				MainGameActivity.LEVEL_GAME);

		startActivity(newGameIntent);
	}

	private void makeNewGameAlert() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.str_dialog_new_game_title)
				.setMessage(R.string.str_dialog_new_game_body)
				.setPositiveButton(android.R.string.ok, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						startNewGame();

					}
				}).setNegativeButton(android.R.string.cancel, null)
				.setIcon(android.R.drawable.ic_dialog_alert).show();
	}

	private void manageGameState() {
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		Resources r = getResources();
		if (pref.getInt(r.getString(R.string.const_current_level), 1) == 1
				&& pref.getInt(
						r.getString(R.string.const_current_level_progress), 0) == 0
				&& pref.getString(r.getString(R.string.const_solved_words), "[]")
						.equals("[]")) {
			mmView.setContinueActive(false);
			noGame = true;
		} else {
			mmView.setContinueActive(true);
			noGame = false;
		}
	}
}
