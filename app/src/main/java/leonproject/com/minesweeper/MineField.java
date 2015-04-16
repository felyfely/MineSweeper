package leonproject.com.minesweeper;

import android.content.Context;

import java.util.Random;

/**
 * Created by fudan on 4/14/15.
 */
public class MineField {

    private int size = 8;
    private Context context;
    private int mineCount = 10;
    private int safeMineCellCount = 0;
    private final MineCell[][] mineField;


    public MineField( Context context,int size, int mineCount) {
        this.size = size;
        this.context = context;
        this.mineCount = mineCount;
        mineField = new MineCell[size][size];
        initMineCell();


    }

    public MineField(Context context) {

        this.context = context;
        mineField = new MineCell[size][size];
        initMineCell();

    }


    public MineCell getMineBlocks(int i, int j) {

        if (i >= 0 && i <= mineField.length && j >= 0 && j <= mineField[0].length) {
            return mineField[i][j];
        } else {
            return null;
        }

    }


    private void initMineCell() {
        for (int i = 0; i < mineField.length; i++) {
            for (int j = 0; j < mineField[0].length; j++) {
                final MineCell mineBlocks = new MineCell(context);
                mineField[i][j] = mineBlocks;
            }
        }

        initMineField();
        //update mine numbers
        for (int i = 0; i < mineField.length; i++) {
            for (int j = 0; j < mineField[0].length; j++) {

                mineField[i][j].setSurroundingMineNum(surroundingMineCounter(i, j));
//              Log.i("MineField", "i=" + i + "j=" + j + "SurroundMimeNum=" + surroundingMineCounter(i, j));

            }
        }
    }

// does a 3x3 subMatrix count of adjacent mines
    private int surroundingMineCounter(int i, int j) {
        int num;

        //prefer this way than  loops
        num = mineCounterHelper(i - 1, j - 1) +
                mineCounterHelper(i - 1, j) +
                mineCounterHelper(i - 1, j + 1) +
                mineCounterHelper(i, j - 1) +
                mineCounterHelper(i, j + 1) +
                mineCounterHelper(i + 1, j - 1) +
                mineCounterHelper(i + 1, j) +
                mineCounterHelper(i + 1, j + 1);

        return num;

    }

    private int mineCounterHelper(int i, int j) {
        int num = 0;

        if (i >= 0 && i < mineField.length && j >= 0 && j < mineField[0].length) {
            num = mineField[i][j].getIsMined() ? 1 : 0;

        }
        return num;

    }

    // randomly place mines in the mineField.
    private void initMineField() {

        int placedMines = 0;

        while (placedMines < mineCount) {
            int x = (new Random().nextInt(mineField.length));
            int y = (new Random().nextInt(mineField[0].length));

            if (!mineField[x][y].getIsMined()) {
                mineField[x][y].setIsMined(true);
                placedMines++;
            }

        }
    }

    //reveal all location of mines in case of cheating or ending game
    public void revealAllMines() {
        for (int i = 0; i < mineField.length; i++) {
            for (int j = 0; j < mineField[0].length; j++) {
                if (mineField[i][j].getIsMined()) {
                    mineField[i][j].setBackgroundResource(R.drawable.mine);

                }
            }
        }
    }


    // does recursive search in matrix
    public void surroundCheck(int i, int j) {
        if (i >= 0 && i < mineField.length && j >= 0 && j < mineField[0].length) {

            if (mineField[i][j].isCovered()) {

                mineField[i][j].setCovered(false);

                if (!mineField[i][j].getIsMined()) {
                    safeMineCellCount++;
//                    Log.i("MineField","i="+i+"j="+j+"SurroundMimeNum="+mineField[i][j].getSurroundingMineNum());
                    if (mineField[i][j].getSurroundingMineNum() == 0) {
//                        Log.i("MineField","i="+i+"j="+j);
                        surroundCheck(i - 1, j - 1);
                        surroundCheck(i - 1, j);
                        surroundCheck(i - 1, j + 1);
                        surroundCheck(i, j - 1);
                        surroundCheck(i, j + 1);
                        surroundCheck(i + 1, j - 1);
                        surroundCheck(i + 1, j);
                        surroundCheck(i + 1, j + 1);


                    }
                }
            }
        }

    }

    //reset or set detected mines no.
    public void setSafeMineCellCount(int safeMineCellCount) {
        this.safeMineCellCount = safeMineCellCount;
    }

    //helps to determine when all mines detected
    public int getSafeMineCellCount() {
        return safeMineCellCount;
    }


}
