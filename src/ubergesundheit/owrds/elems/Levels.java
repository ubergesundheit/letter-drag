package ubergesundheit.owrds.elems;

public class Levels {

	public static final int LEVEL_1 = 10; //10
	public static final int LEVEL_2 = 11;
	public static final int LEVEL_3 = 13;
	public static final int LEVEL_4 = 15;
	public static final int LEVEL_5 = 17;
	public static final int LEVEL_6 = 20;
	public static final int LEVEL_7 = 23;
	public static final int LEVEL_8 = 27;
	public static final int LEVEL_9 = 31;
	public static final int LEVEL_10 = 35;
	public static final int LEVEL_11 = 40;
	public static final int LEVEL_12 = 47;
	public static final int LEVEL_13 = 54;
	public static final int LEVEL_14 = 62;
	public static final int LEVEL_15 = 71;
	public static final int LEVEL_16 = 81;
	public static final int LEVEL_17 = 94;
	public static final int LEVEL_18 = 108;
	public static final int LEVEL_19 = 124;
	public static final int LEVEL_20 = 142;

	public static final int[] levels = { LEVEL_1, LEVEL_2, LEVEL_3, LEVEL_4,
			LEVEL_5, LEVEL_6, LEVEL_7, LEVEL_8, LEVEL_9, LEVEL_10, LEVEL_11,
			LEVEL_12, LEVEL_13, LEVEL_14, LEVEL_15, LEVEL_16, LEVEL_17,
			LEVEL_18, LEVEL_19, LEVEL_20 };

	public static final int getWordCountForLevel(int level) {
		try {
			return levels[level - 1];
		} catch (ArrayIndexOutOfBoundsException e) {
			return 0;
		}
	}

}
