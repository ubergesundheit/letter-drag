package ubergesundheit.owrds.elems;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

import ubergesundheit.owrds.R;
import android.content.Context;
import android.content.res.Resources;

public class Words {

	private ArrayList<String[]> words;
	private int[] ids;
	private Resources resources;
	private Context context;

	private Random random;

	public Words(Context context) {
		words = new ArrayList<String[]>(10);
	    for (int i = 0; i < 10; i++)  
	        words.add(null); 
		this.context = context;
		init();
	}

	private void init() {
		random = new Random();
		resources = context.getResources();
		ids = getAllResourceIDs(R.array.class);
		for (int i = 0; i < ids.length; i++) {
			if (resources.getResourceEntryName(ids[i]).startsWith("words")) {
				String[] tmp = resources.getStringArray(ids[i]);
				words.set(tmp[0].length()-4, tmp);
			}
		}
	}

	public Word getWord() {
		int lvl = random.nextInt(words.size());
		return new Word(words.get(lvl)[random.nextInt(words.get(lvl).length)]);
	}

	public Word getWord(int level, final ArrayList<String> solvedWords) {
		String returnWord = "";

		switch (level) {
		case 1:
			do {
				returnWord = words.get(0)[random.nextInt(words.get(0).length)];
			} while (solvedWords.contains(returnWord));

			break;

		case 2:
			do {
				returnWord = words.get(0)[random.nextInt(words.get(0).length)];
//				}
			} while (solvedWords.contains(returnWord));

			break;

		case 3:
			do {
				int lvl = random.nextInt(1);
				returnWord = words.get(lvl)[random
						.nextInt(words.get(lvl).length)];
//				}
			} while (solvedWords.contains(returnWord));
			break;

		case 4:
			do{
				int lvl = random.nextInt(2);
				returnWord = words.get(lvl)[random
						.nextInt(words.get(lvl).length)];
//				}
			}
			while (solvedWords.contains(returnWord));

		case 5:
			do {
				int lvl = random.nextInt(2)+1;
				returnWord = words.get(lvl)[random
						.nextInt(words.get(lvl).length)];
			} while (solvedWords.contains(returnWord));
			break;

		case 6:
			do{
								int lvl = random.nextInt(3) + 1;
				returnWord = words.get(lvl)[random
						.nextInt(words.get(lvl).length)];
			}
			while (solvedWords.contains(returnWord)) ;
			break;

		case 7:
			do {
				int lvl = random.nextInt(3) + 1;
				returnWord = words.get(lvl)[random
						.nextInt(words.get(lvl).length)];
			} while (solvedWords.contains(returnWord));
			break;

		case 8:
			do {
				int lvl = random.nextInt(3) + 1;
				returnWord = words.get(lvl)[random
						.nextInt(words.get(lvl).length)];
			} while (solvedWords.contains(returnWord));
			break;

		case 9:
			do {
				int lvl = random.nextInt(4) + 1;
				returnWord = words.get(lvl)[random
						.nextInt(words.get(lvl).length)];
			} while (solvedWords.contains(returnWord));
			break;

		case 10:
			do {
				int lvl = random.nextInt(4) + 2;
				returnWord = words.get(lvl)[random
						.nextInt(words.get(lvl).length)];
			} while (solvedWords.contains(returnWord));
			break;

		case 11:
			do {
				int lvl = random.nextInt(5) + 2;
				returnWord = words.get(lvl)[random
						.nextInt(words.get(lvl).length)];
			} while (solvedWords.contains(returnWord));
			break;

		case 12:
			do {
				int lvl = random.nextInt(6) + 2;
				returnWord = words.get(lvl)[random
						.nextInt(words.get(lvl).length)];
			} while (solvedWords.contains(returnWord));
			break;

		case 13:
			do {
				int lvl = random.nextInt(6) + 2;
				returnWord = words.get(lvl)[random
						.nextInt(words.get(lvl).length)];
			} while (solvedWords.contains(returnWord));
			break;

		case 14:
			do {
				int lvl = random.nextInt(7) + 2;
				returnWord = words.get(lvl)[random
						.nextInt(words.get(lvl).length)];
			} while (solvedWords.contains(returnWord));
			break;

		case 15:
			do {
				int lvl = random.nextInt(7) + 2;
				returnWord = words.get(lvl)[random
						.nextInt(words.get(lvl).length)];
			} while (solvedWords.contains(returnWord));
			break;

		case 16:
			do {
				int lvl = random.nextInt(7) + 2;
				returnWord = words.get(lvl)[random
						.nextInt(words.get(lvl).length)];
			} while (solvedWords.contains(returnWord));
			break;

		case 17:
			do {
				int lvl = random.nextInt(7) + 2;
				returnWord = words.get(lvl)[random
						.nextInt(words.get(lvl).length)];
			} while (solvedWords.contains(returnWord));
			break;

		case 18:
			do {
				int lvl = random.nextInt(7) + 2;
				returnWord = words.get(lvl)[random
						.nextInt(words.get(lvl).length)];
			} while (solvedWords.contains(returnWord));
			break;

		case 19:
			do {
				int lvl = random.nextInt(7) + 2;
				returnWord = words.get(lvl)[random
						.nextInt(words.get(lvl).length)];
			} while (solvedWords.contains(returnWord));
			break;

		case 20:
			do {
				int lvl = random.nextInt(9);
				returnWord = words.get(lvl)[random
						.nextInt(words.get(lvl).length)];
			} while (solvedWords.contains(returnWord));
			break;
		}

		return new Word(returnWord);
	}

	private int[] getAllResourceIDs(Class<?> aClass)
			throws IllegalArgumentException {
		/* Get all Fields from the class passed. */
		Field[] IDFields = aClass.getFields();

		/* int-Array capable of storing all ids. */
		int[] IDs = new int[IDFields.length];

		try {
			/* Loop through all Fields and store id to array. */
			for (int i = 0; i < IDFields.length; i++) {
				/*
				 * All fields within the subclasses of R are Integers, so we
				 * need no type-check here.
				 */

				// pass 'null' because class is static
				IDs[i] = IDFields[i].getInt(null);
			}
		} catch (Exception e) {
			/* Exception will only occur on bad class submitted. */
			throw new IllegalArgumentException();
		}
		return IDs;
	}

}
