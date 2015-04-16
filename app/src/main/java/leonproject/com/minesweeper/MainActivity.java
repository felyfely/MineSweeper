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
    Button newGameButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

//add LED style fonts here
        Typeface myTypeface = Typeface.createFromAsset(this.getAssets(),
                "digital.ttf");
        newGameButton = (Button) findViewById(R.id.start_player);
        newGameButton.setTypeface(myTypeface);
        newGameButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        startGame();
                    }
                });

        welcomeTextView = (TextView) findViewById(R.id.text_title);
        welcomeTextView.setTypeface(myTypeface);

    }

    private void startGame() {
        Intent i = new Intent(this, GameActivity.class);
        startActivity(i);
    }

}
