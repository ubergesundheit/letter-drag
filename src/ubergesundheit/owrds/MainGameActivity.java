package ubergesundheit.owrds;

import java.util.ArrayList;

import ubergesundheit.owrds.elems.Levels;
import ubergesundheit.owrds.elems.OwrdApplication;
import ubergesundheit.owrds.elems.OwrdView;
import ubergesundheit.owrds.elems.Word;
import ubergesundheit.owrds.elems.Words;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

public class MainGameActivity extends Activity implements
		OnSharedPreferenceChangeListener, AnimationListener {

	public static final String PREF_WORD = "currentWord";
	public static final String SOLVED_WORDS = "solvedWords";
	public static final String CURRENT_LEVEL = "currentLevel";
	public static final String PROGRESS_LEVEL = "levelProgress";
	public static final String GAME_MODE = "gameMode";

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	public static final int ENDLESS_GAME = 0;
	public static final int LEVEL_GAME = 1;
	public static final int RESUME_GAME = 2;

	private int gameMode;

	private OwrdView owrdView;
	private ImageView correctView;
	private ProgressBar progressBarView;
	private TextView progressTextView;
	private TextView levelUpView;

	private Words words;

	private GestureDetector gestureDetector;

	private SharedPreferences pref;
	private Resources r;

	private Animation scaleIn;
	private Animation levelUpAnimation;

	private int currentLevel = 1;
	private int levelProgress = 0;

	private ArrayList<String> solvedWords;

	@TargetApi(11)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Log.d("MainGameActivity", "onCreate start");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_game_activity);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().hide();
			getActionBar().setDisplayShowHomeEnabled(false);
		}
		solvedWords = new ArrayList<String>();
		initViews();

		r = getResources();

		// solvedWords = new ArrayList<Integer>();

		gestureDetector = new GestureDetector(getBaseContext(),
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onDoubleTap(MotionEvent e) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							if (getActionBar().isShowing()) {
								getActionBar().hide();
							} else {
								getActionBar().show();
							}
						} else {
							openOptionsMenu();
						}
						return true;
					}

					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						try {
							// if (Math.abs(e1.getY() - e2.getY()) >
							// SWIPE_MAX_OFF_PATH)
							// return false;
							// right to left swipe
							if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE
									&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

								owrdView.setWord(getNewWord());

							}
						} catch (Exception e) {
							// nothing
						}

						return true;
					}

				});
		gestureDetector.setIsLongpressEnabled(true);

		owrdView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				gestureDetector.onTouchEvent(event);
				switch (event.getAction()) {
				case MotionEvent.ACTION_CANCEL:
					owrdView.touchUp();
					break;
				case MotionEvent.ACTION_DOWN:
					owrdView.delegateTouchDown(event.getX(), event.getY());
					break;
				case MotionEvent.ACTION_MOVE:
					owrdView.inTouch(event.getX(), event.getY());
					break;
				case MotionEvent.ACTION_UP:
					owrdView.touchUp();
					if (owrdView.getCurrentWord().isSolved()) {
						owrdView.setEnabled(false);
						manageLevel();
						playCorrectAnimation();
					}
					break;
				}

				return true;
			}
		});

		loadWords();
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		pref.registerOnSharedPreferenceChangeListener(this);

		Bundle extras = getIntent().getExtras();

		if (extras.getInt(GAME_MODE) == LEVEL_GAME
				|| extras.getInt(GAME_MODE) == RESUME_GAME) {
			if (extras.getInt(GAME_MODE) == LEVEL_GAME) {
				initLevelGame(true);
			}
			gameMode = LEVEL_GAME;
		}
		initPrefs();
		prepareAnims();
		initLevelGame(false);
		owrdView.setWord(getNewWord());

	}

	@Override
	protected void onResume() {
		super.onResume();
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		pref.registerOnSharedPreferenceChangeListener(this);

		Bundle extras = getIntent().getExtras();
		Log.i("mode",gameMode+" "+currentLevel);
		if (extras.getInt(GAME_MODE) == LEVEL_GAME
				|| extras.getInt(GAME_MODE) == RESUME_GAME) {
			if (extras.getInt(GAME_MODE) == LEVEL_GAME) {
				initLevelGame(true);
				owrdView.setWord(getNewWord());
			}
			gameMode = LEVEL_GAME;
			extras.putInt(GAME_MODE, RESUME_GAME);
			getIntent().putExtras(extras);
		}
		initPrefs();
		prepareAnims();
	}

	@Override
	protected void onPause() {
		super.onPause();
		SharedPreferences.Editor edit = pref.edit();
		edit.putInt(r.getString(R.string.const_current_level), currentLevel);
		edit.putInt(r.getString(R.string.const_current_level_progress),
				levelProgress);
		edit.putString(r.getString(R.string.const_solved_words),
				solvedWords.toString());
		edit.commit();
	}

	private void initViews() {
		AdView adView = (AdView) this.findViewById(R.id.adView);
		adView.loadAd(new AdRequest());
		correctView = (ImageView) this.findViewById(R.id.correctview);
		progressBarView = (ProgressBar) this.findViewById(R.id.progressBarView);
		progressTextView = (TextView) this.findViewById(R.id.levelIndicator);
		owrdView = (OwrdView) this.findViewById(R.id.surfaceview);
		levelUpView = (TextView) this.findViewById(R.id.levelupview);

	}

	private void initLevelGame(boolean newGame) {
		if (gameMode == LEVEL_GAME) {
			progressBarView.setVisibility(View.VISIBLE);
			progressTextView.setVisibility(View.VISIBLE);
			if (newGame) {
				currentLevel = 1;
				levelProgress = 0;
				solvedWords = new ArrayList<String>();
			}
			SharedPreferences.Editor edit = pref.edit();
			edit.putInt(r.getString(R.string.const_current_level), currentLevel);
			edit.putInt(r.getString(R.string.const_current_level_progress),
					levelProgress);
			edit.putString(r.getString(R.string.const_solved_words),
					solvedWords.toString());

			edit.commit();
		}
	}

	private void manageLevel() {
		if (gameMode == LEVEL_GAME) {
			if (owrdView.getCurrentWord().isSolved()) {
				solvedWords.add(owrdView.getCurrentWord().toString());
				levelProgress++;
			}

			if (levelProgress == Levels.getWordCountForLevel(currentLevel)
					&& currentLevel <= 20) {
				currentLevel++;
				if (currentLevel == 21) {
					youWon();
					return;
				}
				levelUpView.setVisibility(View.VISIBLE);
				levelUpView.startAnimation(levelUpAnimation);

				progressBarView.setMax(Levels
						.getWordCountForLevel(currentLevel));
				levelProgress = 0;
			}
			progressBarView.setProgress(levelProgress);
			progressTextView.setText("Level: " + currentLevel + ", "
					+ levelProgress + "/"
					+ Levels.getWordCountForLevel(currentLevel));

		}
	}

	private void youWon() {
		initLevelGame(true);
		new AlertDialog.Builder(this)
				.setTitle(R.string.str_dialog_game_won_title)
				.setMessage(R.string.str_dialog_game_won_body)
				.setPositiveButton(android.R.string.ok, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				}).setCancelable(false)
				.setIcon(android.R.drawable.ic_dialog_info).show();
	}

	private void loadWords() {
		words = ((OwrdApplication) getApplication()).getWords();

		owrdView.setWord(getNewWord());
	}

	private void prepareAnims() {
		scaleIn = AnimationUtils.loadAnimation(this, R.anim.scale_in);
		scaleIn.setAnimationListener(this);

		levelUpAnimation = AnimationUtils.loadAnimation(this, R.anim.level_up);
		levelUpAnimation.setAnimationListener(this);
		// pointUpdateAnimation = AnimationUtils.loadAnimation(this,
		// R.anim.point_update);

	}

	private void initPrefs() {

		// font
		owrdView.setTypeface(pref.getString(
				r.getString(R.string.const_setting_font), "Junction.otf"));
		progressTextView.setTypeface(owrdView.getTypeface());
		levelUpView.setTypeface(owrdView.getTypeface());
		// bgcolor
		owrdView.setBgColor(pref.getInt(
				r.getString(R.string.const_setting_bg_color), Color.BLACK));
		// fontcolor
		int foregroundColor = pref.getInt(
				r.getString(R.string.const_setting_font_color), Color.WHITE);
		owrdView.setFontColor(foregroundColor);
		correctView.setColorFilter(foregroundColor);
		progressBarView.getProgressDrawable().setColorFilter(foregroundColor,
				Mode.SRC_ATOP);
		progressTextView.setTextColor(foregroundColor);
		levelUpView.setTextColor(foregroundColor);

		currentLevel = pref
				.getInt(r.getString(R.string.const_current_level), 1);
		levelProgress = pref.getInt(
				r.getString(R.string.const_current_level_progress), 0);
		solvedWords = recreateArrayListFromString(pref.getString(
				r.getString(R.string.const_solved_words), ""));

		progressBarView.setMax(Levels.getWordCountForLevel(currentLevel));
		progressBarView.setProgress(levelProgress);

		manageLevel();
	}

	private ArrayList<String> recreateArrayListFromString(String string) {
		if (string.equals(""))
			return new ArrayList<String>();
		ArrayList<String> rtrn = new ArrayList<String>();
		string = string.replace("[", "");
		string = string.replace("]", "");
		string = string.replace(" ", "");
		String[] tmp = string.split(",");
		for (int i = 0; i < tmp.length; i++) {
			rtrn.add(tmp[i]);
		}
		return rtrn;
	}

	private void playCorrectAnimation() {
		correctView.setVisibility(View.VISIBLE);
		correctView.startAnimation(scaleIn);
	}

	private Word getNewWord() {
		if (gameMode == LEVEL_GAME) {
			return words.getWord(currentLevel, solvedWords);
		} else {
			return words.getWord();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.options_menu, menu);
		return super.onCreateOptionsMenu(menu);

	}

	@TargetApi(11)
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().hide();
		}

		switch (item.getItemId()) {
		case R.id.context_new_word:
			owrdView.setWord(getNewWord());
			break;
		case R.id.context_settings:

			startActivity(new Intent(MainGameActivity.this,
					SettingsActivity.class));
			break;
		}

		return true;
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {

		if (savedInstanceState != null) {
			super.onRestoreInstanceState(savedInstanceState);
			owrdView.setWord((Word) savedInstanceState.getParcelable(PREF_WORD));
			solvedWords = savedInstanceState.getStringArrayList(SOLVED_WORDS);
			currentLevel = savedInstanceState.getInt(CURRENT_LEVEL);
			levelProgress = savedInstanceState.getInt(PROGRESS_LEVEL);
			gameMode = savedInstanceState.getInt(GAME_MODE);
			manageLevel();
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(PREF_WORD, owrdView.getCurrentWord());
		outState.putStringArrayList(SOLVED_WORDS, solvedWords);
		outState.putInt(CURRENT_LEVEL, currentLevel); // currentLevel
		outState.putInt(PROGRESS_LEVEL, levelProgress); // progress in level
		outState.putInt(GAME_MODE, gameMode);

	}

	@TargetApi(8)
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {

		if (key.equals(r.getString(R.string.const_setting_bg_color))) {// background
																		// color
			owrdView.setBgColor(sharedPreferences.getInt(
					r.getString(R.string.const_setting_bg_color), Color.BLACK));
		} else if (key.equals(r.getString(R.string.const_setting_font_color))) {// font
																				// color
			int fontColor = sharedPreferences
					.getInt(r.getString(R.string.const_setting_font_color),
							Color.WHITE);
			owrdView.setFontColor(fontColor);
			levelUpView.setTextColor(fontColor);
			progressTextView.setTextColor(fontColor);
			// also set tint of correct image
			correctView.setColorFilter(fontColor);
		} else if (key.equals(r.getString(R.string.const_setting_font))) {// font
			owrdView.setTypeface(sharedPreferences.getString(
					r.getString(R.string.const_setting_font), "Junction.otf"));
			progressTextView.setTypeface(owrdView.getTypeface());
			levelUpView.setTypeface(owrdView.getTypeface());
		} else if (key.equals(r
				.getString(R.string.const_setting_draw_indicator))) {
			owrdView.setDrawIndicator(sharedPreferences.getBoolean(
					r.getString(R.string.const_setting_draw_indicator), true));
		}
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if (animation.equals(scaleIn)) {
			correctView.setVisibility(View.GONE);
			owrdView.setWord(getNewWord());
			owrdView.setEnabled(true);
		} else if (animation.equals(levelUpAnimation)) {
			levelUpView.setVisibility(View.GONE);
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
	}

	@Override
	public void onAnimationStart(Animation animation) {
	}

}
