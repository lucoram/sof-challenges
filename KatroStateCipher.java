import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class KatroStateCipher {

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final int BOARD_HEIGHT = 4;
    private static final int BOARD_WIDTH = 4;
    private static final Random RANDOM = new Random();

    private static Map<Character, Set<int[]>> charEncodersMap;

    static {
        charEncodersMap = new HashMap<>();

        for (int power = 0; power <= 5; power++) {
            for (int coeff = 0; coeff <= 16; coeff++) {
                int charIndex = calcIndex(power, coeff);

                if (charIndex >= 0 && charIndex < ALPHABET.length()) {
                    char letter = ALPHABET.charAt(charIndex);

                    if (!charEncodersMap.containsKey(letter)) {
                        charEncodersMap.put(letter, new HashSet<>());
                    }

                    Set<int[]> charEncoders = charEncodersMap.get(letter);
                    charEncoders.add(new int[] { power, coeff });
                }
            }
        }
    }

    public static void main(String[] args) {

        /**
         * Decode test case
         * 
         * [2][3][3][2]
         * [4][3][4][8]
         * ------------ =====> "helloo"
         * [4][4][0][0]
         * [1][1][0][0]
         */

        int[][] gameStateToDecode = {
                { 2, 3, 3, 2 },
                { 4, 3, 4, 8 },
                { 4, 4, 0, 0 },
                { 1, 1, 0, 0 }
        };

        String decoded = decode(gameStateToDecode);
        System.out.println(decoded);

        /**
         * Encode test case
         * 
         * ..................[05][04][03][02]
         * ..................[13][11][11][14]
         * "secreted" =====> ----------------
         * ..................[01][03][02][01]
         * ..................[07][12][09][02]
         */

        int[][] encoded = encode("secreted");

        for (int[] row : encoded) {
            System.out.println(Arrays.toString(row));
        }
    }

    private static String decode(int[][] state) {
        StringBuilder builder = new StringBuilder();

        outer: for (int row = 0; row < BOARD_HEIGHT; row += 2) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                int topValue = state[row][col];
                int bottomValue = state[row + 1][col];

                if (topValue <= 0 && bottomValue <= 0) {
                    break outer;
                }

                builder.append(decodeChar(topValue, bottomValue));
            }
        }

        return builder.toString();
    }

    private static char decodeChar(int power, int coeff) {
        int charIndex = calcIndex(power, coeff);

        if (charIndex < ALPHABET.length()) {
            return ALPHABET.charAt(charIndex);
        }

        return '\0';
    }

    private static int calcIndex(int power, int coeff) {
        if (coeff % 2 != 0) {
            coeff *= -1;
        }

        return Math.abs((int) Math.pow(2, power) + coeff) - 1;
    }

    private static int[][] encode(String word) {
        int[][] gameState = new int[BOARD_HEIGHT][BOARD_WIDTH];
        int rowCursor = 0;
        int colCursor = 0;

        for (char letter : word.toCharArray()) {
            if (rowCursor >= BOARD_HEIGHT) {
                break;
            }

            if (colCursor >= BOARD_WIDTH) {
                rowCursor += 2;
                colCursor = 0;
            }

            encodeChar(gameState, rowCursor, colCursor, letter);

            colCursor++;
        }

        return gameState;
    }

    private static void encodeChar(int[][] gameState, int rowCursor, int colCursor, char letter) {
        List<int[]> letterEncoders = new ArrayList<>(charEncodersMap.get(letter));

        int randomIndex = RANDOM.nextInt(letterEncoders.size());
        int[] randomEncoderValues = letterEncoders.get(randomIndex);

        gameState[rowCursor][colCursor] = randomEncoderValues[0];
        gameState[rowCursor + 1][colCursor] = randomEncoderValues[1];
    }
}
