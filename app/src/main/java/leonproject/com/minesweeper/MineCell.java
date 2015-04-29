package leonproject.com.minesweeper;

import android.content.Context;
import android.widget.ImageButton;


/**
 * Created by fudan on 4/14/15.
 */
public class MineCell extends ImageButton {
    private int surroundingMineNum = 0,dimension;
    private boolean isCovered = true;
    private boolean isMined = false;
    private boolean isFlagged = false;
    private boolean isQuestioned = false;
    private OnGameOverListener onGameOverListener;


    public MineCell(Context context) {
        super(context);



        this.setBackgroundResource(R.drawable.tile);

    }


    public void setIsMined(boolean isMined) {
        this.isMined = isMined;
    }

    public boolean isMined() {
        return isMined;
    }


    public void setCovered(boolean isCovered) {
        if (this.isCovered) {

            this.isCovered = isCovered;
            if (!isMined) {


                //set image resource to corresponding mine numbers;
                this.setBackgroundResource(getResources().getIdentifier("tile" + surroundingMineNum,
                        "drawable", getContext().getPackageName()));
            }

            else{
                onGameOverListener.onGameOver();
            }


        }


    }

    public boolean isCovered() {
        return isCovered;
    }

    public void setQuestioned(boolean isQuestioned) {
        this.isQuestioned = isQuestioned;
        if (isQuestioned) {
            this.setBackgroundResource(R.drawable.question);
        } else {
            this.setBackgroundResource(R.drawable.flag);
        }
    }

    public boolean isQuestioned() {
        return isQuestioned;
    }

    public void setSurroundingMineNum(int surroundingMineNum) {

        this.surroundingMineNum = surroundingMineNum;

    }

    public int getSurroundingMineNum() {
        return surroundingMineNum;
    }

    //use this if decide to implement mark function
    public void setFlagged() {
        if (!isFlagged) {
            this.isFlagged = true;
            this.setBackgroundResource(R.drawable.flag);

        } else {
            this.isFlagged = false;
            this.setBackgroundResource(R.drawable.tile);

        }
    }

    public boolean isFlagged() {
        return isFlagged;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    //make sure button is square. this case applies to portrait mode
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        setMeasuredDimension(dimension,dimension);
    }

     public interface OnGameOverListener{

         void onGameOver();
    }

    public void setOnGameOverListener(OnGameOverListener onGameOverListener) {
        this.onGameOverListener = onGameOverListener;
    }
}
