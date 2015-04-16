package leonproject.com.minesweeper;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends ActionBarActivity {

    private TableLayout mTableLayout;
    private ImageButton gameStateImageButton;
    private MineField mineField;
    private TextView timer,mineNum;
    private CountDownTimer countDownTimer = null;
    private long gameTime = 60000;
    private int mineFieldSize = 8;
    private static final int mineCount = 10;


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.game_board);
        Typeface myTypeface = Typeface.createFromAsset(this.getAssets(),
                "digital.ttf");

        timer = (TextView) findViewById(R.id.timer);
        timer.setTypeface(myTypeface);

        mineNum = (TextView) findViewById(R.id.mineCount);
        mineNum.setText(""+mineCount);
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
        createBoard();

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
            case R.id.action_cheat:
                mineField.revealAllMines();
                return true;
            case R.id.action_help:
                new AlertDialog.Builder(this).setTitle("Help Information").setMessage(getString(R.string.help_message)).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
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


                mineCell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!mineCell.isFlagged()) {
                            if (mineCell.getIsMined()) {
                                mineField.revealAllMines();
                                loseGame();
                                Toast.makeText(GameActivity.this, "BOOM!!!", Toast.LENGTH_SHORT).show();

                            }
                            mineField.surroundCheck(currentRow, currentCol);
                            if (mineField.getSafeMineCellCount() == mineFieldSize * mineFieldSize - mineCount) {
                                mineField.setSafeMineCellCount(0);
                                winGame();
                            }
                        } else {
                            if (!mineCell.isQuestioned()) {
                                mineCell.setQuestioned(true);
                            } else {
                                mineCell.setQuestioned(false);
                            }
                        }
                    }


                });

                mineCell.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        mineCell.setQuestioned(false);
                        mineCell.setFlagged();
                        return true;
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
    }

    private void loseGame() {
        countDownTimer.cancel();
        Toast.makeText(GameActivity.this, "BOOM!!!", Toast.LENGTH_SHORT).show();
        gameStateImageButton.setBackgroundResource(R.drawable.lose_smiley);
        disableMineCells();

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
        mineField = new MineField(this);
        createBoard();

    }


}