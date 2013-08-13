//Sub test2()
//    Dim last As Long, i As Long
//    'make sure it works with any sheet size
//    last = Cells(Rows.Count, 1).End(xlUp).Row
//    'loop bottom to top !
//
//    For j = 4 To 19
//        For i = last To 1 Step -1
//            If Len(Cells(i, 1)) = j Then
//                Cells(i, j) = Cells(i, 1)
//                Cells(i, 1) = ""
//            End If
//        Next i
//    Next j
//End Sub


package ubergesundheit.owrds.elems;

import java.util.Arrays;
import java.util.Random;

import android.os.Parcel;
import android.os.Parcelable;

public class Word implements Parcelable {

	private char[] original;
	private char[] tokens;
	private Random random;


	public Word(String word) {
		random = new Random();
		tokens = word.toCharArray();
		original = word.toCharArray();
		shuffle();
		// computePoints();
		// swaps = numberOfSwaps(computeNumerics(tokens));
//		Log.i("lol",new DamerauLevenshteinAlgorithm(1, 1, 1, 1).execute(getScramble(), getOriginal())+"");
	}

	public Word(Parcel in) {
//		Log.d("Word", "Word(Parcel in) start");
		int size = in.readInt();
		original = new char[size];
		tokens = new char[size];
		in.readCharArray(original);
		in.readCharArray(tokens);
//		Log.d("Word", "Word(Parcel in) end");
	}

	
	public Word(String original, String scramble){
		this.tokens = scramble.toCharArray();
		this.original = original.toCharArray();
	}
	/**
	 * shuffle the word.
	 */
	private void shuffle() {
		// idea for easy mode: dont shuffle first and last character
		int stop = random.nextInt(tokens.length * 2);

		for (int i = 0; i < stop; i++) {
			swap(random.nextInt(tokens.length), random.nextInt(tokens.length));
		}

		if (Arrays.equals(tokens, original)) {
			shuffle();
		}
	}

	public int getLength() {
		return tokens.length;
	}

	public void swap(int i, int j) {
		char tmp = tokens[i];
		tokens[i] = tokens[j];
		tokens[j] = tmp;
	}

	public boolean isSolved() {
		return Arrays.equals(tokens, original);
	}

	public String get(int i) {
		if (i > tokens.length)
			throw new ArrayIndexOutOfBoundsException();
		return tokens[i] + "";
	}
	
	
	public String getOriginal(){
		return toString();
	}
	
	public String getScramble(){
		StringBuffer b = new StringBuffer(tokens.length);
		for(char c : tokens){
			b.append(c);
		}
		return b.toString();
	}
	

	@Override
	public String toString() {
		StringBuffer b = new StringBuffer(original.length);
		for(char c : original){
			b.append(c);
		}
		return b.toString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
//		Log.d("Word", "writeToParcel start");
		dest.writeInt(getLength());
		dest.writeCharArray(original);
		dest.writeCharArray(tokens);
//		Log.d("Word", "writeToParcel end");
	}

	public static final Parcelable.Creator<Word> CREATOR = new Parcelable.Creator<Word>() {
		public Word createFromParcel(Parcel in) {
//			Log.d("Word", "createFromParcel call");
			return new Word(in);

		}

		public Word[] newArray(int size) {
			throw new UnsupportedOperationException();
		}
	};
	


	// private int numberOfSwaps(int[] permutation) {
	// int nswaps = 0;
	// ArrayList<Integer> seen = new ArrayList<Integer>();
	// for (int i = 0; i < permutation.length; i++) {
	// if (!seen.contains(i)) {
	// int j = i;
	// while (permutation[j] != i) {
	// j = permutation[j];
	// seen.add(j);
	// nswaps++;
	// }
	// }
	// }
	// return nswaps;
	// }
	//
	// /**
	// * Computes the corresponding Array with Numbers of the given array in
	// * relation to the original
	// */
	// private int[] computeNumerics(char[] tmpTokens) {
	// // Integer statt int für Arrays.asList, damit contains funktioniert.
	// Integer[] out = new Integer[original.length];
	// for (int i = 0; i < original.length; i++) {
	// out[i] = -1;// überall -1 reinschreiben, weil sonst 0 komplett
	// // übersprungen wird.
	// }
	// for (int i = 0; i < tmpTokens.length; i++) {// tmp durchgehen
	// inner: for (int j = 0; j < original.length; j++) {// für jedes
	// // zeichen des
	// // tmp das
	// // original
	// // durchgehen
	// if (tmpTokens[i] == original[j]) { // wenn das aktuelle tmp
	// // gleich dem original ist,
	// if (!Arrays.asList(out).contains((Integer) j)) {
	// out[i] = j; // schreibe ins out-array die position des
	// // originals
	// break inner;
	// } else {
	// continue inner;
	// }
	// }
	// }
	// }
	// int[] outout = new int[out.length];
	// for (int i = 0; i < out.length; i++) {
	// outout[i] = out[i];
	// }
	// return outout;
	// }

}
