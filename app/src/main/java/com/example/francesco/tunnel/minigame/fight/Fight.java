package com.example.francesco.tunnel.minigame.fight;

import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.francesco.tunnel.R;
import com.example.francesco.tunnel.minigame.MinigameActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Francesco on 19/12/2014.
 */
public class Fight extends MinigameActivity {

    private int hitsRequired;

    private int penaltiesLimit;

    private int roundsLimit;

    private int roundLength;

    private int comboLimit;

    public final static String HITS_REQUIRED = "hitsRequired";

    public final static String PENALTIES_LIMIT = "penaltiesLimit";

    public final static String ROUNDS_LIMIT = "roundsLimit";

    public final static String ROUND_LENGTH = "roundLength";

    public final static String COMBO_LIMIT = "comboLimit";

    private final static int DEFAULT_HITS_REQUIRED = 3;

    private final static int DEFAULT_PENALTIES_LIMIT = 3;

    private final static int DEFAULT_ROUNDS_LIMIT = 0;

    private final static int DEFAULT_ROUND_LENGTH = 10;

    private final static int DEFAULT_COMBO_LIMIT = 3;

    private int hits = 0;

    private int penalties = 0;

    private int round = 0;

    private boolean attack = true;

    private List<Move> input = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fight);

        TextView view = (TextView) findViewById(R.id.fight);
        view.setOnTouchListener(new FightTouchListener());
    }

    @Override
    protected void initGameParams() {
        this.hitsRequired = getIntent().getIntExtra(HITS_REQUIRED, DEFAULT_HITS_REQUIRED);
        this.penaltiesLimit = getIntent().getIntExtra(PENALTIES_LIMIT, DEFAULT_PENALTIES_LIMIT);
        this.roundsLimit = getIntent().getIntExtra(ROUNDS_LIMIT, DEFAULT_ROUNDS_LIMIT);
        this.roundLength = getIntent().getIntExtra(ROUND_LENGTH, DEFAULT_ROUND_LENGTH);
        this.comboLimit = getIntent().getIntExtra(COMBO_LIMIT, DEFAULT_COMBO_LIMIT);
    }

    @Override
    protected void initGameSounds() {

    }

    @Override
    protected void showWinning() {

    }

    @Override
    protected void showLosing() {

    }

    /**
     * Il listener di tocco delega tutto al detector, che aiuta a gestire movimenti standard.
     */
    class FightTouchListener implements View.OnTouchListener {

        private final GestureDetectorCompat detector;

        FightTouchListener() {
            detector = new GestureDetectorCompat(Fight.this, new FightGestureListener());
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return detector.onTouchEvent(event);
        }
    }

    // TODO
    private void startRound() {
        final List<FightAction> moves = generateMoves();
        int errors = 0;
        boolean check;
        for (final FightAction move : moves) {
            input.clear();
            speak(move.getResourceId(attack));
            check = checkInput(move);
            if (check) {

            } else {
                errors++;
            }
        }
    }

    private boolean checkInput(final FightAction move) {
        final List<Move> requiredMoves = move.getMoves();
        boolean result = true;
        if (requiredMoves.size() == input.size()) {
            for (int i = 0; i < input.size(); i++) {
                if (!input.get(i).equals(requiredMoves.get(i))) {
                    result = false;
                    break;
                }
            }
        } else
            result = false;
        return result;
    }

    private List<FightAction> generateMoves() {
        final List<FightAction> moves = new ArrayList<>(roundLength);
        int value;
        int max = Move.values().length;
        for (int i = 0; i < roundLength; i++) {
            value = randomInt(1, max);
            moves.add(Move.get(value)); // TODO combos
        }
        return moves;
    }

    private int randomInt(int min, int max) {
        final Random random = new Random();
        // add 1 to make it inclusive
        return random.nextInt((max - min) + 1) + min;
    }

    /**
     * Listener usato dal detector per gestire movimenti standard.
     */
    class FightGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            if (!started) {
                started = true;
                ttsUtil.stop();
            }
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (started && !finished) {
                if (attack)
                    speak(R.string.l_fi_attack_middle);
                else
                    speak(R.string.l_fi_defense_middle);
            }
            attack = !attack;
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (started && !finished) {
                if (attack) {
                    // attacco
                    if (detectLeftFling(velocityX, velocityY))
                        speak(R.string.l_fi_attack_left);
                    else if (detectRightFling(velocityX, velocityY))
                        speak(R.string.l_fi_attack_right);
                    else if (detectUpFling(velocityX, velocityY))
                        speak(R.string.l_fi_attack_up);
                    else if (detectDownFling(velocityX, velocityY))
                        speak(R.string.l_fi_attack_down);

                } else {
                    // difesa
                    if (detectLeftFling(velocityX, velocityY))
                        speak(R.string.l_fi_defense_left);
                    else if (detectRightFling(velocityX, velocityY))
                        speak(R.string.l_fi_defense_right);
                    else if (detectUpFling(velocityX, velocityY))
                        speak(R.string.l_fi_defense_up);
                    else if (detectDownFling(velocityX, velocityY))
                        speak(R.string.l_fi_defense_down);
                }
                attack = !attack;
            }
            return true;
        }

        private boolean detectLeftFling(final float velocityX, final float velocityY) {
            return (Math.abs(velocityX) > Math.abs(velocityY)) && velocityX < 0;
        }

        private boolean detectRightFling(final float velocityX, final float velocityY) {
            return (Math.abs(velocityX) > Math.abs(velocityY)) && velocityX > 0;
        }

        private boolean detectUpFling(final float velocityX, final float velocityY) {
            return (Math.abs(velocityX) < Math.abs(velocityY)) && velocityY < 0;
        }

        private boolean detectDownFling(final float velocityX, final float velocityY) {
            return (Math.abs(velocityX) < Math.abs(velocityY)) && velocityY > 0;
        }
    }

    @Override
    public void afterTTSInit() {
        speak(String.valueOf(hitsRequired), R.string.l_fi_hits_required);
        speak(String.valueOf(penaltiesLimit), R.string.l_fi_penalties);
        speak(R.string.l_fi_help_1);
        speak(R.string.l_fi_help_2);
        speak(R.string.l_fi_help_3);
        speak(R.string.l_fi_help_4);
        speak(R.string.l_fi_help_5);
        speak(R.string.l_fi_help_6);
        speak(R.string.l_fi_help_7);
        speak(R.string.l_fi_help_8);
        speak(R.string.l_fi_help_9);
        speak(R.string.l_fi_help_10);
        speak(R.string.l_fi_help_11);
        speak(R.string.l_fi_help_12);
        speak(R.string.l_fi_help_13);
        speak(R.string.l_fi_help_14);
        speak(R.string.l_fi_start);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
