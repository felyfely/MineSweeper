package leonproject.com.minesweeper;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import static leonproject.com.minesweeper.R.id.up_button;

public class GameActivity extends ActionBarActivity implements View.OnClickListener, View.OnTouchListener {

    private TableLayout mTableLayout;
    private ImageButton gameStateImageButton;
    private MineField mineField;
    private TextView timer, mineNum;
    private CountDownTimer countDownTimer = null, progressTimer = null;
    private ProgressBar longClickProgressBar;
    private long gameTime;
    private int mineFieldSize, currentFocusX, currentFocusY;
    private int mineCount;
    private int dimension;
    private RelativeLayout button_layout;
    private boolean flagState, isOutsideUp, isOutsideDown, isOutsideLeft, isOutsideRight;
    private boolean dpadThreadRunning = false;
    private boolean cancelDpadThread = false;
    private Rect rect, rect_up, rect_down, rect_left, rect_right;
    private Handler handler;
    private Runnable mAction;


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.game_board);

        button_layout = (RelativeLayout) findViewById(R.id.button_layout);
        mineFieldSize = getIntent().getIntExtra("size", 8);
        mineCount = getIntent().getIntExtra("mineNum", 10);
        gameTime = (long) getIntent().getIntExtra("time", 60) * 1000 + 100;
        longClickProgressBar = (ProgressBar) findViewById(R.id.longClickProgressBar);
        longClickProgressBar.setMax(100);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        dimension = Math.min(size.x, size.y - findViewById(R.id.score_board).getMeasuredHeight()
                - findViewById(R.id.longClickProgressBar).getMeasuredHeight()) / mineFieldSize;

        Typeface myTypeface = Typeface.createFromAsset(this.getAssets(),
                "digital.ttf");


        timer = (TextView) findViewById(R.id.timer);
        timer.setTypeface(myTypeface);

        mineNum = (TextView) findViewById(R.id.mineCount);
        mineNum.setText("" + mineCount);
        mineNum.setTypeface(myTypeface);

        gameStateImageButton = (ImageButton) findViewById(R.id.smile_button);
        gameStateImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame();
            }
        });
        gameStateImageButton.setBackgroundResource(R.drawable.normal_smiley);

        mTableLayout = (TableLayout) findViewById(R.id.game_view);
        mTableLayout.setShrinkAllColumns(true);
        mineField = new MineField(this, mineFieldSize, mineCount);


        findViewById(R.id.flag_button_2).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    flagState = true;
                    findViewById(R.id.mask).setVisibility(View.VISIBLE);
                } else if (event.getActionMasked() == MotionEvent.ACTION_UP || event.getActionMasked() == MotionEvent.ACTION_CANCEL) {
                    flagState = false;
                    findViewById(R.id.mask).setVisibility(View.GONE);
                }


                return false;
            }
        });

        createBoard();


        setDpadButtons();


        currentFocusX = mineFieldSize / 2;
        currentFocusY = mineFieldSize / 2;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_options_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            case R.id.action_cursor:

                if (button_layout.getVisibility() == View.VISIBLE) {

                    button_layout.setVisibility(View.GONE);
                    loseFocus();
                } else {

                    button_layout.setVisibility(View.VISIBLE);
                    setFocus(mineFieldSize / 2, mineFieldSize / 2);


                }


                return true;

//            case R.id.action_cheat:
//                mineField.revealAllMines();
//                return true;
            case R.id.action_help:
                new AlertDialog.Builder(this).setTitle("Help Information").setMessage(getString(R.string.help_message)).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void createBoard() {
        for (int row = 0; row < mineFieldSize; row++) {
            //create a new table row
            TableRow tableRow = new TableRow(this);

            for (int col = 0; col < mineFieldSize; col++) {

                final int currentRow = row;
                final int currentCol = col;

                final MineCell mineCell = mineField.getMineBlocks(row, col);
                mineCell.setDimension(dimension);


                mineCell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (button_layout.getVisibility() == View.VISIBLE) {
                            setFocus(currentRow, currentCol);
                        } else {
                            if (!flagState) {
                                clickOnMine(currentRow, currentCol);
                            }
                        }

                    }


                });


                mineCell.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (button_layout.getVisibility() != View.VISIBLE) {
                            longClickOnMine(mineCell);
                        }

                        return true;
                    }
                });

                mineCell.setOnGameOverListener(new MineCell.OnGameOverListener() {
                    @Override
                    public void onGameOver() {

                        loseGame();
                    }
                });

                mineCell.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (!mineCell.isCovered()) {
                            return false;
                        }



                        final boolean flaggedStatus = mineCell.isFlagged();

                        if (flaggedStatus) {
                            longClickProgressBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                        }

                        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                            if(flagState){
                                longClickOnMine(mineCell);

                            }

                            rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());


                            progressTimer = new CountDownTimer(ViewConfiguration.getLongPressTimeout(), 17) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    longClickProgressBar.setProgress((int) ((ViewConfiguration.getLongPressTimeout() - millisUntilFinished) * 100 / ViewConfiguration.getLongPressTimeout()));
                                }

                                @Override
                                public void onFinish() {

                                    longClickProgressBar.setProgress(100);
                                    if (!flaggedStatus) {
                                        longClickProgressBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                                    } else {
                                        longClickProgressBar.getProgressDrawable().clearColorFilter();
                                    }
                                }
                            };

                            progressTimer.start();


                        } else if (event.getActionMasked() == MotionEvent.ACTION_UP
                                || ((event.getActionMasked() == MotionEvent.ACTION_MOVE)
                                && !rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY()))) {
                            progressTimer.cancel();
                            longClickProgressBar.getProgressDrawable().clearColorFilter();
                            longClickProgressBar.setProgress(0);

                        }


                        return false;
                    }
                });
                tableRow.addView(mineCell);

            }
            //add the row to the minefield layout
            mTableLayout.addView(tableRow);
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }


            countDownTimer = new CountDownTimer(gameTime, 1000) {

                public void onTick(long millisUntilFinished) {
                    timer.setText("" + millisUntilFinished / 1000);
                }

                public void onFinish() {

                    timer.setText("TIME'S UP");
                    loseGame();
                }
            };
            countDownTimer.start();


        }
    }

    private void winGame() {
        countDownTimer.cancel();
        Toast.makeText(GameActivity.this, "YOU WIN!!!", Toast.LENGTH_SHORT).show();
        gameStateImageButton.setBackgroundResource(R.drawable.win_smiley);
        disableMineCells();
        findViewById(R.id.click_button).setEnabled(false);
        findViewById(R.id.flag_button).setEnabled(false);

    }

    private void loseGame() {
        countDownTimer.cancel();
//        Toast.makeText(GameActivity.this, "BOOM!!!", Toast.LENGTH_SHORT).show();
        gameStateImageButton.setBackgroundResource(R.drawable.lose_smiley);
        disableMineCells();
        findViewById(R.id.click_button).setEnabled(false);
        findViewById(R.id.flag_button).setEnabled(false);
        mineField.revealAllMines();


    }

    //disable all mines is case of ending game
    private void disableMineCells() {

        for (int row = 0; row < mineFieldSize; row++) {

            for (int col = 0; col < mineFieldSize; col++) {

                mineField.getMineBlocks(row, col).setEnabled(false);
            }
        }

    }


    private void resetGame() {
        Toast.makeText(GameActivity.this, "GAME RESTARTED", Toast.LENGTH_SHORT).show();
        gameStateImageButton.setBackgroundResource(R.drawable.normal_smiley);
        mTableLayout.removeAllViews();
        mineField = new MineField(this, mineFieldSize, mineCount);
        createBoard();
        if (button_layout.getVisibility() == View.VISIBLE) {

            setFocus(mineFieldSize / 2, mineFieldSize / 2);
        }
        findViewById(R.id.click_button).setEnabled(true);
        findViewById(R.id.flag_button).setEnabled(true);
        mineCount = getIntent().getIntExtra("mineNum", 10);
        mineNum.setText(mineCount + "");


    }


    private void setFocusRepeat(final int i, final int j) {

        if (handler != null) return;
        setFocus(currentFocusX + i, currentFocusY + j);
        mAction = new Runnable() {
            @Override
            public void run() {
                setFocus(currentFocusX + i, currentFocusY + j);
                handler.postDelayed(this, 200);
            }
        };

        handler = new Handler();

        handler.postDelayed(mAction, 500);

    }


    private void handleDpadUp() {
        if (handler == null) return;
        handler.removeCallbacks(mAction);
        handler = null;
    }

    private void setFocus(int x, int y) {


        mineField.getMineBlocks(currentFocusX, currentFocusY).setImageResource(0);
        currentFocusX = (x + mineFieldSize) % mineFieldSize;
        currentFocusY = (y + mineFieldSize) % mineFieldSize;
        mineField.getMineBlocks(currentFocusX, currentFocusY).setImageResource(R.drawable.tile_selector);

        if (android.os.Build.VERSION.SDK_INT >= 16) {
            mineField.getMineBlocks(currentFocusX, currentFocusY).setImageAlpha(70);
        } else {
            mineField.getMineBlocks(currentFocusX, currentFocusY).setAlpha(70);
        }


    }

    private void loseFocus() {

        mineField.getMineBlocks(currentFocusX, currentFocusY).setImageResource(0);

    }

    private void clickOnMine(int currentRow, int currentCol) {

        if (currentRow >= 0 && currentRow < mineFieldSize && currentCol >= 0 && currentCol < mineFieldSize) {


            MineCell mineCell = mineField.getMineBlocks(currentRow, currentCol);

            if (!mineCell.isCovered()) {
                if (mineCell.getSurroundingMineNum() == mineField.checkSurroundingFlagNum(currentRow, currentCol) &&
                        mineCell.getSurroundingMineNum() != 0) {
                    mineField.surroundCheckCube(currentRow, currentCol);
                    if (mineField.getSafeMineCellCount() == mineFieldSize * mineFieldSize - mineCount) {
                        mineField.setSafeMineCellCount(0);
                        winGame();

                    }
                    return;
                }
            }


            if (!mineCell.isFlagged()) {
//                if (mineCell.isMined()) {
//                    loseGame();
////                    Toast.makeText(GameActivity.this, "BOOM!!!", Toast.LENGTH_SHORT).show();
//                    return;
//
//                }
                mineField.surroundCheck(currentRow, currentCol);

                if (mineField.getSafeMineCellCount() == mineFieldSize * mineFieldSize - mineCount) {
                    mineField.setSafeMineCellCount(0);
                    winGame();
                    return;
                }
            } else {
                if (!mineCell.isQuestioned()) {
                    mineCell.setQuestioned(true);

                } else {
                    mineCell.setQuestioned(false);
                }


            }
        }
    }


    private void longClickOnMine(MineCell mineCell) {
        if (mineCell.isCovered()) {
            mineCell.setQuestioned(false);
            if (mineCell.isFlagged()) {

                mineField.setFlagNum(mineField.getFlagNum() - 1);
            } else {
                mineField.setFlagNum(mineField.getFlagNum() + 1);
            }
            mineCell.setFlagged();
            mineNum.setText(mineCount - mineField.getFlagNum() + "");
        }
    }

    private void setDpadButtons() {
        Typeface buttonTypeface = Typeface.createFromAsset(this.getAssets(),
                "fontawesome.ttf");
        Button up = (Button) findViewById(up_button);
        Button down = (Button) findViewById(R.id.down_button);
        Button left = (Button) findViewById(R.id.left_button);
        Button right = (Button) findViewById(R.id.right_button);
        Button click = (Button) findViewById(R.id.click_button);
        Button flag = (Button) findViewById(R.id.flag_button);
        Button flag_touch = (Button) findViewById(R.id.flag_button_2);


        up.setTypeface(buttonTypeface);
        down.setTypeface(buttonTypeface);
        left.setTypeface(buttonTypeface);
        right.setTypeface(buttonTypeface);
        click.setTypeface(buttonTypeface);
        flag.setTypeface(buttonTypeface);
        flag_touch.setTypeface(buttonTypeface);


        findViewById(R.id.dpad_layout).setOnTouchListener(this);
        click.setOnClickListener(this);

        flag.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case up_button:
//                setFocus(currentFocusX - 1, currentFocusY);
//
//
//                break;
//            case R.id.down_button:
//                setFocus(currentFocusX + 1, currentFocusY);
//
//
//                break;
//            case R.id.left_button:
//                setFocus(currentFocusX, currentFocusY - 1);
//
//
//                break;
//            case R.id.right_button:
//                setFocus(currentFocusX, currentFocusY + 1);
//
//
//                break;

            case R.id.click_button:
                clickOnMine(currentFocusX, currentFocusY);
                break;

            case R.id.flag_button:
                MineCell mineCell = mineField.getMineBlocks(currentFocusX, currentFocusY);
                longClickOnMine(mineCell);
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {

            if (rect_up == null || rect_up.isEmpty()) {
                rect_up = new Rect(findViewById(R.id.up_button).getLeft(), findViewById(R.id.up_button).getTop(), findViewById(R.id.up_button).getRight(), findViewById(R.id.up_button).getBottom());
                rect_down = new Rect(findViewById(R.id.down_button).getLeft(), findViewById(R.id.down_button).getTop(), findViewById(R.id.down_button).getRight(), findViewById(R.id.down_button).getBottom());
                rect_left = new Rect(findViewById(R.id.left_button).getLeft(), findViewById(R.id.left_button).getTop(), findViewById(R.id.left_button).getRight(), findViewById(R.id.left_button).getBottom());
                rect_right = new Rect(findViewById(R.id.right_button).getLeft(), findViewById(R.id.right_button).getTop(), findViewById(R.id.right_button).getRight(), findViewById(R.id.right_button).getBottom());
            }

            switch (v.getId()) {
                case R.id.up_button:
                    isOutsideUp = false;

                    setFocusRepeat(-1, 0);
                    break;
                case R.id.down_button:
                    isOutsideDown = false;
                    setFocusRepeat(1, 0);
                    break;
                case R.id.left_button:
                    isOutsideLeft = false;
                    setFocusRepeat(0, -1);
                    break;
                case R.id.right_button:
                    isOutsideRight = false;
                    setFocusRepeat(0, 1);
                    break;
                default:
                    break;
            }
            return false;

        }


        if (rect_up.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())) {
            if (isOutsideUp) {
                isOutsideUp = false;
                isOutsideDown = true;
                isOutsideLeft = true;
                isOutsideRight = true;
                handleDpadUp();
                setFocusRepeat(-1, 0);


            }
        } else if (rect_down.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())) {
            if (isOutsideDown) {
                isOutsideUp = true;
                isOutsideDown = false;
                isOutsideLeft = true;
                isOutsideRight = true;
                handleDpadUp();
                setFocusRepeat(1, 0);

            }
        } else if (rect_left.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())) {
            if (isOutsideLeft) {
                isOutsideUp = true;
                isOutsideDown = true;
                isOutsideLeft = false;
                isOutsideRight = true;
                handleDpadUp();
                setFocusRepeat(0, -1);

            }

        } else if (rect_right.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())) {
            if (isOutsideRight) {
                isOutsideUp = true;
                isOutsideDown = true;
                isOutsideLeft = true;
                isOutsideRight = false;
                handleDpadUp();
                setFocusRepeat(0, 1);

            }
        } else {
            isOutsideUp = true;
            isOutsideDown = true;
            isOutsideLeft = true;
            isOutsideRight = true;
            handleDpadUp();
        }


        if (event.getActionMasked() == MotionEvent.ACTION_UP || event.getActionMasked() == MotionEvent.ACTION_CANCEL) {
            isOutsideUp = true;
            isOutsideDown = true;
            isOutsideLeft = true;
            isOutsideRight = true;
            handleDpadUp();
            return false;
        }


        return true;
    }
}