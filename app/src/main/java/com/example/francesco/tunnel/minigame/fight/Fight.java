package com.example.francesco.tunnel.minigame.fight;

import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
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

    public final static String ATTACK_FIRST = "attackFirst";

    private final static int DEFAULT_HITS_REQUIRED = 3;

    private final static int DEFAULT_PENALTIES_LIMIT = 3;

    private final static int DEFAULT_ROUNDS_LIMIT = 0;

    private final static int DEFAULT_ROUND_LENGTH = 4;

    private final static int DEFAULT_COMBO_LIMIT = 3;

    private final static boolean DEFAULT_ATTACK_FIRST = true;

    private boolean attack;

    private int hits = 0;

    private int penalties = 0;

    private int errors = 0;

    private int round = 0;

    private int moveIndex = 0;

    private List<Move> moves = new ArrayList<>();

    private final static int PERFECT_ROUND = 111;

    private final static int WON_ROUND = 222;

    private final static int LOST_ROUND = 333;

    private final static int AWFUL_ROUND = 444;

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
        this.attack = getIntent().getBooleanExtra(ATTACK_FIRST, DEFAULT_ATTACK_FIRST);
    }

    @Override
    protected void initGameSounds() {

    }

    @Override
    protected void showWinning() {
        speak(R.string.l_fi_won);
    }

    @Override
    protected void showLosing() {
        speak(R.string.l_fi_lost);
    }

    private void fight() {
        if (attack)
            speak(R.string.l_fi_start_attack);
        else
            speak(R.string.l_fi_start_defense);

        fightNextRound();
    }

    private void fightNextRound() {
        round++;
        // se il limite è 0, significa round infiniti
        if (roundsLimit != 0 && round > roundsLimit)
            lose();
        else {
            errors = 0;
            generateMoves();
            speak(moves.get(0).getResourceId(attack));
        }
    }

    /**
     * Gestisce il risultato di un attacco, per poi chiamare la prosecuzione del combattimento.
     *
     * @param roundResult
     */
    private void manageAttackResult(final int roundResult) {
        if (roundResult == WON_ROUND) {
            hits++;
            speak(R.string.l_fi_round_attack_won);
        } else if (roundResult == PERFECT_ROUND) {
            hits++;
            speak(R.string.l_fi_round_attack_perfect);
        } else {
            speak(R.string.l_fi_round_attack_lost);
        }

        if (hits == hitsRequired)
            win();
        else {
            speak(R.string.l_fi_missing, String.valueOf(hitsRequired - hits), R.string.l_fi_missing_hits);
            attack = (roundResult == PERFECT_ROUND);
            if (attack)
                speak(R.string.l_fi_now_attack);
            else
                speak(R.string.l_fi_now_defend);
            fightNextRound();
        }
    }

    /**
     * Gestisce il risultato di una difesa, per poi chiamare la prosecuzione del combattimento.
     *
     * @param roundResult
     */
    private void manageDefenseResult(final int roundResult) {
        if (roundResult == LOST_ROUND) {
            penalties++;
            speak(R.string.l_fi_round_defense_lost);
        } else if (roundResult == AWFUL_ROUND) {
            penalties++;
            speak(R.string.l_fi_round_defense_awful);
        } else if (roundResult == PERFECT_ROUND) {
            penalties--;
            speak(R.string.l_fi_round_defense_perfect);
        } else {
            speak(R.string.l_fi_round_defense_won);
        }

        if (penalties == penaltiesLimit)
            lose();
        else {
            speak(R.string.l_fi_missing, String.valueOf(penaltiesLimit - penalties), R.string.l_fi_missing_penalties);
            attack = !(roundResult == AWFUL_ROUND);
            if (attack)
                speak(R.string.l_fi_now_attack);
            else
                speak(R.string.l_fi_now_defend);
            fightNextRound();
        }
    }

    /**
     * Verifica passo passo la situazione del round, per poi chiamare il metodo che gestisce il
     * risultato in caso di attacco o di difesa.
     *
     * @param inputOk
     */
    private void checkRound(final boolean inputOk) {
        if (!inputOk)
            errors++;

        if (attack && errors > 3)
            // più di 3 errori in attacco: round perso
            manageAttackResult(LOST_ROUND);
        else if (!attack && errors > 5)
            // più di 5 errori in difesa: round perso malamente
            manageDefenseResult(AWFUL_ROUND);
        else if (!attack && errors > 3)
            // più di 3 errori in difesa (ma meno di 5, già verificato sopra): round perso
            manageDefenseResult(LOST_ROUND);

        if (moveIndex < moves.size() - 1) {
            // ci sono ancora delle mosse per decidere l'esito del round
            moveIndex++;
            speak(moves.get(moveIndex).getResourceId(attack));
        } else {
            // tutte le mosse del round sono state eseguite

            // nessun errore: round perfetto
            if (errors == 0)
                if (attack)
                    manageAttackResult(PERFECT_ROUND);
                else
                    manageDefenseResult(PERFECT_ROUND);
            else
                // meno di 3 errori: round vinto
                if (attack)
                    manageAttackResult(WON_ROUND);
                else
                    manageDefenseResult(WON_ROUND);
        }
    }

    /**
     * Verifica se la mossa attuale è stata eseguita correttamente e passa il risultato alla verifica
     * del round.
     *
     * @param input
     */
    private void checkMove(final Move input) {
        final Move requiredMove = moves.get(moveIndex);
        final boolean inputOk = input.equals(requiredMove);
        // FIXME usare suoni
        if (inputOk)
            ttsUtil.speak("OK");
        else
            ttsUtil.speak("NO");
        checkRound(inputOk);
    }

    /**
     * Crea una lista casuale di mosse da eseguire nel round attuale
     */
    private void generateMoves() {
        int value;
        int max = Move.values().length;
        moves.clear();
        moveIndex = 0;
        for (int i = 0; i < roundLength; i++) {
            value = randomInt(1, max);
            moves.add(Move.get(value)); // TODO combos
        }
    }

    private int randomInt(int min, int max) {
        final Random random = new Random();
        // add 1 to make it inclusive
        return random.nextInt((max - min) + 1) + min;
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

    /**
     * Listener usato dal detector per gestire movimenti standard.
     * <p/>
     * TODO contatore del tempo di reazione
     */
    class FightGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            if (!started) {
                started = true;
                ttsUtil.stop();
                fight();
            } else if (finished) {
                sendBackResult();
            }
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (canPerformMove()) {
                checkMove(Move.MIDDLE);
            }
            return true;
        }

        @Override
        public boolean onFling(final MotionEvent e1, final MotionEvent e2, final float velocityX, final float velocityY) {
            if (canPerformMove()) {
                if (detectLeftFling(velocityX, velocityY)) {
                    checkMove(Move.LEFT);
                } else if (detectRightFling(velocityX, velocityY)) {
                    checkMove(Move.RIGHT);
                } else if (detectUpFling(velocityX, velocityY)) {
                    checkMove(Move.UP);
                } else if (detectDownFling(velocityX, velocityY)) {
                    checkMove(Move.DOWN);
                }
            }
            return true;
        }

        private boolean canPerformMove() {
            return started && !finished;
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
