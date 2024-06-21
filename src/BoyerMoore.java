import java.util.Scanner;

public class BoyerMoore {
    private final int[] badCharacter; // Bad-character skip array
    private int[] goodSuffix; // Good-suffix skip array
    private final String pattern; // Pattern string
    private int moveCount; // Number of shifts

    // Constructor to initialize the pattern and preprocess bad character and good suffix tables
    public BoyerMoore(String pattern) {
        // Radix of input characters
        int radix = 256; // Assuming ASCII character set
        this.pattern = pattern;
        this.moveCount = 0;

        // Initialize bad-character table
        badCharacter = new int[radix];//Create an array of bad characters of size 256
        for (int c = 0; c < radix; c++)
            badCharacter[c] = -1; // Initialize all entries to -1
        for (int j = 0; j < pattern.length(); j++)
            badCharacter[pattern.charAt(j)] = j; // Rightmost position of each character in pattern

        // Initialize good-suffix table
        buildGoodSuffixTable();//Call the method to construct the suffix table
    }

    //The method of constructing a good suffix table
    private void buildGoodSuffixTable() {
        int patternLength = pattern.length();//The length of the pattern string
        goodSuffix = new int[patternLength];// Create a good suffix array
        int[] borderPos = new int[patternLength + 1];// Auxiliary array for recording boundary positions
        int i = patternLength, j = patternLength + 1;
        borderPos[i] = j;

        // Process the pattern strings from right to left, filling the goodSuffix array
        while (i > 0) {
            while (j <= patternLength && pattern.charAt(i - 1) != pattern.charAt(j - 1)) {
                if (goodSuffix[j - 1] == 0) {
                    goodSuffix[j - 1] = j - i;
                }
                j = borderPos[j];
            }
            i--;
            j--;
            borderPos[i] = j;
        }

        j = borderPos[0];
        for (i = 0; i < patternLength; i++) {
            if (goodSuffix[i] == 0) {
                goodSuffix[i] = j;
            }
            if (i == j) {
                j = borderPos[j];
            }
        }
    }

    // Search method to find the pattern in the text
    public int search(String txt) {
        int patternLength = pattern.length();// The length of the mode string
        int txtLength = txt.length();// Length of the text string
        int skip;
        for (int i = 0; i <= txtLength - patternLength; i += skip) {// Traverse the text from left to right
            skip = 0;// The initial jump distance is 0
            for (int j = patternLength - 1; j >= 0; j--) {// Traverse the pattern string from right to left
                if (pattern.charAt(j) != txt.charAt(i + j)) {// If the characters do not match
                    skip = Math.max(1, j - badCharacter[txt.charAt(i + j)]);// Calculate the jump distance
                    break;
                }
            }
            if (skip == 0) return i; // // If the jump distance is 0, the match is successful
            moveCount += skip; // Increment move count
        }
        return txtLength; // If no match is found, the text length is returned
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Get pattern and text from user input
        System.out.print("Enter the pattern: ");
        String pattern = scanner.nextLine();
        System.out.print("Enter the text: ");
        String txt = scanner.nextLine();

        BoyerMoore bm = new BoyerMoore(pattern);// Create the BoyerMoore object
        int offset = bm.search(txt);//Call the search method

        // Print the result
        System.out.println("Text:    " + txt);
        System.out.print("Pattern: ");
        for (int i = 0; i < offset; i++)
            System.out.print(" ");
        System.out.println(pattern);

        // Print the bad character table
        System.out.println("\nBad Character Table:");
        for (int i = 0; i < bm.badCharacter.length; i++) {
            if (bm.badCharacter[i] != -1) {// Print only valid bad character entries
                System.out.println((char)i + ": " + bm.badCharacter[i]);
            }
        }

        // Print the good suffix table
        System.out.println("\nGood Suffix Table:");
        for (int i = 0; i < bm.goodSuffix.length; i++) {
            System.out.println("Position " + i + ": " + bm.goodSuffix[i]);
        }

        // Print the number of shifts
        System.out.println("\nNumber of shifts: " + bm.moveCount);
    }
}
