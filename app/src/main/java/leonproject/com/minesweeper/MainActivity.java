package leonproject.com.minesweeper;

/**
 * Created by fudan on 4/15/15.
 */

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


//could add difficulty settings and resume old game in this activity
public class MainActivity extends ActionBarActivity {

    TextView welcomeTextView;
    Button newGameButtonEasy,newGameButtonNormal,newGameButtonHard;
    int size,mineNum;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

//add LED style fonts here
        Typeface myTypeface = Typeface.createFromAsset(this.getAssets(),
                "digital.ttf");
        newGameButtonEasy = (Button) findViewById(R.id.start_player);
        newGameButtonEasy.setTypeface(myTypeface);
        newGameButtonEasy.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        startGame(8, 10, 60);
                    }
                });

        newGameButtonNormal = (Button) findViewById(R.id.start_player_intermediate);
        newGameButtonNormal.setTypeface(myTypeface);
        newGameButtonNormal.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        startGame(16, 40, 240);
                    }
                });

        newGameButtonHard = (Button) findViewById(R.id.start_player_expert);
        newGameButtonHard.setTypeface(myTypeface);
        newGameButtonHard.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        startGame(24, 99, 1000);
                    }
                });

        welcomeTextView = (TextView) findViewById(R.id.text_title);
        welcomeTextView.setTypeface(myTypeface);

    }

    private void startGame(int size, int mineNum, int time) {
        Intent i = new Intent(this, GameActivity.class);
        i.putExtra("size",size);
        i.putExtra("mineNum",mineNum);
        i.putExtra("time",time);


        startActivity(i);
    }

}
