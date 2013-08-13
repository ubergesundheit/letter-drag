package ubergesundheit.owrds.elems;

import ubergesundheit.owrds.R;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class MainMenuView extends View {

	private static final int FONT_SIZE_BASE = 100;
	private static final int BORDER_DIVIDER_BOTH_SIDES = 20;

	private static final float DIST = 0.06f;
	
	public static final int NEW_GAME = 1;
	public static final int CONTINUE_GAME = 2;
	public static final int ENDLESS_GAME = 3;
	public static final int SETTINGS = 4;
	public static final int HELP = 5;

	private Paint fontPaint;
	private Rect characterBounds;
	private int viewWidth;
	private int viewHeight;
	private int currentWordWidth;
	private int computedFontSize;

	private Rect newGameRect;
	private Rect continueGameRect;
	private Rect settingsRect;
	private Rect endlessGameRect;
	private Rect helpRect;

	private int touchRectPosition;

	private int fontColor = Color.WHITE;
	private int bgColor = Color.BLACK;

	private boolean continueGameActive = false;

	private String newGame;
	private String continueGame;
	private String settings;
	private String endlessGame;
	private String help;

	public MainMenuView(Context context) {
		super(context);
		init();
	}

	public MainMenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MainMenuView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void delegateTouchDown(float x, float y) {
		// if (touchCharPosition == -1) {
		if (continueGameActive && continueGameRect.contains((int) x, (int) y)) {
			touchRectPosition = CONTINUE_GAME;
		} else if (newGameRect.contains((int) x, (int) y)) {
			touchRectPosition = NEW_GAME;
		} else if (endlessGameRect.contains((int) x, (int) y)) {
			touchRectPosition = ENDLESS_GAME;
		} else if (settingsRect.contains((int) x, (int) y)) {
			touchRectPosition = SETTINGS;
		}else if (helpRect.contains((int) x, (int) y)){
			touchRectPosition = HELP;
		} else {
			touchRectPosition = -1;
		}
		// }
		this.invalidate();
	}

	public int touchUp() {
		int rtrn = touchRectPosition;
		touchRectPosition = -1;
		this.invalidate();
		return rtrn;
	}

	public void setFontColor(int color) {
		fontColor = color;
		fontPaint.setColor(fontColor);
		this.invalidate();
	}

	public void setBgColor(int color) {
		bgColor = color;
		this.invalidate();
	}

	public void setTypeface(String path) {
		if (path.startsWith("/")) {
			fontPaint.setTypeface(Typeface.createFromFile(path));
		} else {
			fontPaint.setTypeface(Typeface.createFromAsset(getContext()
					.getAssets(), path));
		}
		compute();
		this.invalidate();

	}

	public Typeface getTypeface() {
		return fontPaint.getTypeface();
	}

	private void init() {

		// font-paint
		fontPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
		fontPaint.setColor(fontColor);
		fontPaint.setTextAlign(Paint.Align.CENTER);
		fontPaint.setTextSize(FONT_SIZE_BASE);

		Resources r = getContext().getResources();

		newGame = r.getString(R.string.str_main_menu_start_game);
		continueGame = r.getString(R.string.str_main_menu_continue_game);
		settings = r.getString(R.string.str_main_menu_settings);
		endlessGame = r.getString(R.string.str_main_menu_endless_game);
		help = r.getString(R.string.str_main_menu_help);
		
		
		setTypeface("Junction.otf");
		// anotherPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		// anotherPaint.setColor(Color.rgb(66, 133, 244));
		compute();

	}

	private void compute() {
		DisplayMetrics metrics = new DisplayMetrics();
		((Activity) this.getContext()).getWindowManager().getDefaultDisplay()
				.getMetrics(metrics);

		fontPaint.setTextSize(FONT_SIZE_BASE);
		characterBounds = new Rect();
		fontPaint.getTextBounds("W", 0, 1, characterBounds); // Annahme dass W
																// die breiteste
																// breite hat.

		viewWidth = metrics.widthPixels;
		viewHeight = metrics.heightPixels;

		// font size computation

		int wordlen = Math.max(
				Math.max(newGame.length(), endlessGame.length()),
				Math.max(continueGame.length(), settings.length()));

		int realWidth = viewWidth - viewWidth / BORDER_DIVIDER_BOTH_SIDES;
		currentWordWidth = characterBounds.width() * wordlen;
		computedFontSize = FONT_SIZE_BASE;

		while (currentWordWidth > realWidth) {
			fontPaint.setTextSize(--computedFontSize);
			fontPaint.getTextBounds("W", 0, 1, characterBounds);
			currentWordWidth = characterBounds.width() * wordlen;
		}

		// rectangle computation
		int currentCharPos;
		if (computedFontSize == FONT_SIZE_BASE) {
			currentCharPos = (viewWidth >> 1) - (currentWordWidth >> 1)
					+ (characterBounds.width() >> 1);
		} else {
			currentCharPos = (viewWidth / (BORDER_DIVIDER_BOTH_SIDES << 1))
					+ (characterBounds.width() >> 1);
		}
		// first line
		int verticalPos = (int) (((viewHeight >> 1) - characterBounds.height() * 3) - (viewHeight * DIST));
		int top = (int) (verticalPos - (characterBounds.height()) - viewHeight * 0.02);
		int bottom = (int) (verticalPos + viewHeight * 0.02);
		int left = currentCharPos - (characterBounds.width() >> 1);
		for (int i = 0; i < wordlen; i++) {
			currentCharPos = currentCharPos + characterBounds.width();
		}
		int right = (characterBounds.width() >> 1) + currentCharPos;
		continueGameRect = new Rect(left, top, right, bottom);
		// next line
		verticalPos += characterBounds.height() + (viewHeight * DIST);
		top = (int) (verticalPos - (characterBounds.height()) - viewHeight * 0.02);
		bottom = (int) (verticalPos + viewHeight * 0.02);
		newGameRect = new Rect(left, top, right, bottom);
		// next line
		verticalPos += characterBounds.height() + (viewHeight * DIST);
		top = (int) (verticalPos - (characterBounds.height()) - viewHeight * 0.02);
		bottom = (int) (verticalPos + viewHeight * 0.02);
		endlessGameRect = new Rect(left, top, right, bottom);
		// next line
		verticalPos += characterBounds.height() + (viewHeight * DIST);
		top = (int) (verticalPos - (characterBounds.height()) - viewHeight * 0.02);
		bottom = (int) (verticalPos + viewHeight * 0.02);
		settingsRect = new Rect(left, top, right, bottom);
		// next line
		verticalPos += characterBounds.height() + (viewHeight * DIST);
		top = (int) (verticalPos - (characterBounds.height()) - viewHeight * 0.02);
		bottom = (int) (verticalPos + viewHeight * 0.02);
		helpRect = new Rect(left, top, right, bottom);

	}

	public void setContinueActive(boolean active){
		continueGameActive = active;
		this.invalidate();
	}
	
	private void drawWord(Canvas canvas) {

		// draw the rects of the chars
		// canvas.drawRect(continueGameRect,fontPaint);
		// canvas.drawRect(newGameRect,fontPaint);
		// canvas.drawRect(endlessGameRect,fontPaint);
		// canvas.drawRect(settingsRect,fontPaint);

		int currentCharPos;
		int verticalPos = (int) (((viewHeight >> 1) - characterBounds.height() * 3) - (viewHeight * DIST));
		if (continueGameActive) {
			if (computedFontSize == FONT_SIZE_BASE) {
				currentCharPos = (viewWidth >> 1) - (currentWordWidth >> 1)
						+ (characterBounds.width() >> 1);
			} else {
				currentCharPos = (viewWidth / (BORDER_DIVIDER_BOTH_SIDES << 1))
						+ (characterBounds.width() >> 1);
			}

			for (int i = 0; i < continueGame.length(); i++) {
				// canvas.restore();

				canvas.drawText(continueGame.toCharArray()[i] + "",
						currentCharPos, verticalPos
						// + (characterBounds.height() >> 1)
						, fontPaint);

				currentCharPos = currentCharPos + characterBounds.width();
			}
		}

		if (computedFontSize == FONT_SIZE_BASE) {
			currentCharPos = (viewWidth >> 1) - (currentWordWidth >> 1)
					+ (characterBounds.width() >> 1);
		} else {
			currentCharPos = (viewWidth / (BORDER_DIVIDER_BOTH_SIDES << 1))
					+ (characterBounds.width() >> 1);
		}
		verticalPos += characterBounds.height() + (viewHeight * DIST);

		for (int i = 0; i < newGame.length(); i++) {

			canvas.drawText(newGame.toCharArray()[i] + "", currentCharPos,
					verticalPos
					// + (characterBounds.height() >> 1)
					, fontPaint);

			currentCharPos = currentCharPos + characterBounds.width();
		}

		if (computedFontSize == FONT_SIZE_BASE) {
			currentCharPos = (viewWidth >> 1) - (currentWordWidth >> 1)
					+ (characterBounds.width() >> 1);
		} else {
			currentCharPos = (viewWidth / (BORDER_DIVIDER_BOTH_SIDES << 1))
					+ (characterBounds.width() >> 1);
		}
		verticalPos += characterBounds.height() + (viewHeight * DIST);

		for (int i = 0; i < endlessGame.length(); i++) {

			canvas.drawText(endlessGame.toCharArray()[i] + "", currentCharPos,
					verticalPos
					// + (characterBounds.height() >> 1)
					, fontPaint);

			currentCharPos = currentCharPos + characterBounds.width();
		}

		if (computedFontSize == FONT_SIZE_BASE) {
			currentCharPos = (viewWidth >> 1) - (currentWordWidth >> 1)
					+ (characterBounds.width() >> 1);
		} else {
			currentCharPos = (viewWidth / (BORDER_DIVIDER_BOTH_SIDES << 1))
					+ (characterBounds.width() >> 1);
		}
		verticalPos += characterBounds.height() + (viewHeight * DIST);

		for (int i = 0; i < settings.length(); i++) {
			// canvas.restore();

			canvas.drawText(settings.toCharArray()[i] + "", currentCharPos,
					verticalPos
					// + (characterBounds.height() >> 1)
					, fontPaint);

			currentCharPos = currentCharPos + characterBounds.width();
		}
		
		if (computedFontSize == FONT_SIZE_BASE) {
			currentCharPos = (viewWidth >> 1) - (currentWordWidth >> 1)
					+ (characterBounds.width() >> 1);
		} else {
			currentCharPos = (viewWidth / (BORDER_DIVIDER_BOTH_SIDES << 1))
					+ (characterBounds.width() >> 1);
		}
		verticalPos += characterBounds.height() + (viewHeight * DIST);

		for (int i = 0; i < help.length(); i++) {
			// canvas.restore();

			canvas.drawText(help.toCharArray()[i] + "", currentCharPos,
					verticalPos
					// + (characterBounds.height() >> 1)
					, fontPaint);

			currentCharPos = currentCharPos + characterBounds.width();
		}		

	}

	private void drawTouchIndicator(Canvas canvas) {
		if (touchRectPosition != -1) {

			switch (touchRectPosition) {
			case CONTINUE_GAME:
				canvas.drawLine(continueGameRect.left, continueGameRect.bottom
						+ (characterBounds.height() >> 3),
						continueGameRect.right, continueGameRect.bottom
								+ (characterBounds.height() >> 3), fontPaint);
				break;
			case NEW_GAME:
				canvas.drawLine(newGameRect.left, newGameRect.bottom
						+ (characterBounds.height() >> 3), newGameRect.right,
						newGameRect.bottom + (characterBounds.height() >> 3),
						fontPaint);
				break;
			case ENDLESS_GAME:
				canvas.drawLine(endlessGameRect.left, endlessGameRect.bottom
						+ (characterBounds.height() >> 3),
						endlessGameRect.right, endlessGameRect.bottom
								+ (characterBounds.height() >> 3), fontPaint);
				break;
			case SETTINGS:
				canvas.drawLine(settingsRect.left, settingsRect.bottom
						+ (characterBounds.height() >> 3), settingsRect.right,
						settingsRect.bottom + (characterBounds.height() >> 3),
						fontPaint);
				break;
			case HELP:
				canvas.drawLine(helpRect.left, helpRect.bottom
						+ (characterBounds.height() >> 3), helpRect.right,
						helpRect.bottom + (characterBounds.height() >> 3),
						fontPaint);				
				break;
			}

		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// remember x >> 1 is equivalent to x / 2, but works much much faster
		canvas.drawColor(bgColor);

		drawWord(canvas);
		drawTouchIndicator(canvas);
		// }

	}
}
