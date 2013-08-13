package ubergesundheit.owrds.elems;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class OwrdView extends View {

	private static final int FONT_SIZE_BASE = 100;
	private static final int BORDER_DIVIDER_BOTH_SIDES = 20;

	private Paint fontPaint;
	private Rect characterBounds;
	private int viewWidth;
	private int viewHeight;
	private int currentWordWidth;
	private int realWidth;
	private int computedFontSize;

	private Word currentWord;
	private Rect[] wordRects;
	private Rect touchRect = null;
	private int touchCharPosition;

	private int fontColor = Color.WHITE;
	private int bgColor = Color.BLACK;
	
	private DisplayMetrics metrics;
	
	private boolean drawIndicator = true;

	
	public OwrdView(Context context) {
		super(context);
		init();
	}

	public OwrdView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public OwrdView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void delegateTouchDown(float x, float y) {
		if (touchRect == null) {
			for (int i = 0; i < wordRects.length; i++) {
				if (wordRects[i].contains((int) x, (int) y)) {
					touchRect = wordRects[i];
					touchCharPosition = i;
					break;
				}
			}
		}
		this.invalidate();
	}

	public void inTouch(float x, float y) {
		if (touchRect != null) {
			for (int i = 0; i < wordRects.length; i++) {
				if (wordRects[i].contains((int) x, (int) y)) {
					currentWord.swap(i, touchCharPosition);
					touchCharPosition = i;
					break;
				}
			}
		}
		this.invalidate();
	}

	public void touchUp() {
		touchRect = null;
		this.invalidate();
	}

	
	public Word getCurrentWord() {
		return currentWord;
	}

	public void setWord(Word w) {
		this.currentWord = w;	
		compute();
		this.invalidate();
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

	public Typeface getTypeface(){
		return fontPaint.getTypeface();
	}
	
	private void init() {

		// font-paint
		fontPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
		fontPaint.setColor(fontColor);
		fontPaint.setTextAlign(Paint.Align.CENTER);
		fontPaint.setTextSize(FONT_SIZE_BASE);

		// anotherPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		// anotherPaint.setColor(Color.rgb(66, 133, 244));

		metrics = new DisplayMetrics();
	}

	private void compute() {

		
		
		((Activity) this.getContext()).getWindowManager().getDefaultDisplay()
				.getMetrics(metrics);
		viewWidth = metrics.widthPixels;
		viewHeight = metrics.heightPixels;
//		Log.i("h+w",viewWidth+" "+viewHeight+"|"+this.getWidth()+" "+this.getHeight());
		fontPaint.setTextSize(FONT_SIZE_BASE);
		characterBounds = new Rect();
		fontPaint.getTextBounds("W", 0, 1, characterBounds); // Annahme dass W
																// die breiteste
																// breite hat.

		// font size computation

		realWidth = viewWidth - viewWidth / BORDER_DIVIDER_BOTH_SIDES;
		currentWordWidth = characterBounds.width() * currentWord.getLength();
		computedFontSize = FONT_SIZE_BASE;

		while (currentWordWidth > realWidth) {
			fontPaint.setTextSize(--computedFontSize);
			fontPaint.getTextBounds("W", 0, 1, characterBounds);
			currentWordWidth = characterBounds.width()
					* currentWord.getLength();
		}

		// rectangle computation
		wordRects = new Rect[currentWord.getLength()];
		int currentCharPos;
		if (computedFontSize == FONT_SIZE_BASE) {
			currentCharPos = (viewWidth >> 1) - (currentWordWidth >> 1)
					+ (characterBounds.width() >> 1);
		} else {
			currentCharPos = (viewWidth / (BORDER_DIVIDER_BOTH_SIDES << 1))
					+ (characterBounds.width() >> 1);
		}

		for (int i = 0; i < currentWord.getLength(); i++) {

			// wordRects[i] = new Rect(
			// currentCharPos-(characterBounds.width() >> 1),
			// (viewHeight>>1)-(characterBounds.height()>>1),
			// (viewHeight>>1)-(characterBounds.height()),
			// (characterBounds.width()>>1)+currentCharPos,
			// (viewHeight>>1)+(characterBounds.height()>>1));
			// (viewHeight>>1)+(characterBounds.height()));

			wordRects[i] = new Rect(currentCharPos
					- (characterBounds.width() >> 1), 0,
					(characterBounds.width() >> 1) + currentCharPos, viewHeight);

			currentCharPos = currentCharPos + characterBounds.width();
		}
	}

	public void setDrawIndicator(boolean drawIndicator){
		this.drawIndicator = drawIndicator;
	}
	
	
	private void drawWord(Canvas canvas) {

		// draw the rects of the chars
		// for(int i = 0; i < currentWord.getLength();i++){
		// canvas.drawRect(wordRects[i],anotherPaint);
		// }

		
		int currentCharPos;
		if (computedFontSize == FONT_SIZE_BASE) {
			currentCharPos = (viewWidth >> 1) - (currentWordWidth >> 1)
					+ (characterBounds.width() >> 1);
		} else {
			currentCharPos = (viewWidth / (BORDER_DIVIDER_BOTH_SIDES << 1))
					+ (characterBounds.width() >> 1);
		}

		for (int i = 0; i < currentWord.getLength(); i++) {
//			canvas.restore();
			
			canvas.drawText(currentWord.get(i), currentCharPos,
					(viewHeight >> 1) 
					//+ (characterBounds.height() >> 1)
					,
					fontPaint);
//			canvas.save();
//			canvas.scale(1f, -1f, viewWidth>>1, viewHeight>>1 );
//			fontPaint.setShader(new LinearGradient(0, 0, 0, getHeight(), 0xff000000,0xffffffff, Shader.TileMode.MIRROR));
//
//			canvas.drawText(currentWord.get(i), currentCharPos,
//					(viewHeight >> 1)  
//					- (characterBounds.height() >> 3)
//					,
//					fontPaint);
//			canvas.scale(1f, 1f, viewWidth>>1, viewHeight>>1 );
//			reflectionCanvas.drawColor(bgColor);
//			reflectionCanvas.drawText(currentWord.get(i), characterBounds.width()>>1, characterBounds.height(), fontPaint);
			
//			canvas.drawBitmap(reflection,currentCharPos, (viewHeight >> 1) , null);
//			canvas.drawBitmap(reflection, new Matrix(), null);
			currentCharPos = currentCharPos + characterBounds.width();
		}

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// remember x >> 1 is equivalent to x / 2, but works much much faster
		canvas.drawColor(bgColor);

		if (currentWord != null) {
			drawWord(canvas);
			if (drawIndicator && touchRect != null){
				canvas.drawLine(wordRects[touchCharPosition].left, (viewHeight >> 1)+(characterBounds.height()>>2), wordRects[touchCharPosition].right, (viewHeight >> 1)+(characterBounds.height()>>2), fontPaint);
			}
		}

		
		
	}
}
