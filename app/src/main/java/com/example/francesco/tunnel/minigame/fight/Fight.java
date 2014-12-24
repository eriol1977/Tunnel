package com.example.francesco.tunnel.minigame.fight;

import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.francesco.tunnel.R;
import com.example.francesco.tunnel.minigame.MinigameActivity;
import com.example.francesco.tunnel.util.LimitedList;

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

    private long moveDuration;

    /**
     * Colpi andati a segno necessari per vincere lo scontro
     */
    public final static String HITS_REQUIRED = "hitsRequired";

    /**
     * Colpi subiti necessari per perdere lo scontro
     */
    public final static String PENALTIES_LIMIT = "penaltiesLimit";

    /**
     * Durata massima dello scontro in termini di round, prima che lo scontro risulti in una
     * sconfitta automatica (0 indica che non esiste limite)
     */
    public final static String ROUNDS_LIMIT = "roundsLimit";

    /**
     * Numero di mosse e/o combo che compongono ogni round
     */
    public final static String ROUND_LENGTH = "roundLength";

    /**
     * Numero massimo di mosse richieste per eseguire una combo (0 indica che non verrà richiesta
     * l'esecuzione di combo)
     */
    public final static String COMBO_LIMIT = "comboLimit";

    /**
     * Definisce se il giocatore attacca per primo a inizio scontro, o se si difende
     */
    public final static String ATTACK_FIRST = "attackFirst";

    /**
     * Tempo a disposizione del giocatore per eseguire una mossa, affinché sia considerata valida
     * (in millisecondi).
     * Il tempo per una combo viene calcolato moltiplicando questo valore per il numero di mosse
     * presenti nella combo.
     */
    public final static String MOVE_DURATION = "moveDuration";

    private final static int DEFAULT_HITS_REQUIRED = 2;

    private final static int DEFAULT_PENALTIES_LIMIT = 2;

    private final static int DEFAULT_ROUNDS_LIMIT = 0;

    private final static int DEFAULT_ROUND_LENGTH = 4;

    private final static int DEFAULT_COMBO_LIMIT = 3;

    private final static boolean DEFAULT_ATTACK_FIRST = true;

    private final static long DEFAULT_MOVE_DURATION = 1000;

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

    private FightTimer timer;

    private final LimitedList<Move> input = new LimitedList(1);

    private int comboMovesTotal = 1;

    private int comboMovesCount = 0;

    private FightTouchListener fightTouchListener;

    private StartingTouchListener startingTouchListener;

    private TextView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fight);

        view = (TextView) findViewById(R.id.fight);
        fightTouchListener = new FightTouchListener();
        startingTouchListener = new StartingTouchListener();
        view.setOnTouchListener(startingTouchListener);
    }

    @Override
    protected void initGameParams() {
        this.hitsRequired = getIntent().getIntExtra(HITS_REQUIRED, DEFAULT_HITS_REQUIRED);
        this.penaltiesLimit = getIntent().getIntExtra(PENALTIES_LIMIT, DEFAULT_PENALTIES_LIMIT);
        this.roundsLimit = getIntent().getIntExtra(ROUNDS_LIMIT, DEFAULT_ROUNDS_LIMIT);
        this.roundLength = getIntent().getIntExtra(ROUND_LENGTH, DEFAULT_ROUND_LENGTH);
        this.comboLimit = getIntent().getIntExtra(COMBO_LIMIT, DEFAULT_COMBO_LIMIT);
        this.attack = getIntent().getBooleanExtra(ATTACK_FIRST, DEFAULT_ATTACK_FIRST);
        this.moveDuration = getIntent().getLongExtra(MOVE_DURATION, DEFAULT_MOVE_DURATION);
    }

    @Override
    protected void initGameSounds() {

    }

    @Override
    protected void showWinning() {
        view.setOnTouchListener(startingTouchListener);
        speak(R.string.l_fi_won);
    }

    @Override
    protected void showLosing() {
        view.setOnTouchListener(startingTouchListener);
        speak(R.string.l_fi_lost);
    }

    private void fight() {
        view.setOnTouchListener(fightTouchListener);
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
            fightMove();
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
            speakMissingHits();
        } else if (roundResult == PERFECT_ROUND) {
            hits++;
            speak(R.string.l_fi_round_attack_perfect);
            speakMissingHits();
        } else {
            speak(R.string.l_fi_round_attack_lost);
        }

        if (hits == hitsRequired)
            win();
        else {
            attack = (roundResult == PERFECT_ROUND);
            if (attack)
                speak(R.string.l_fi_now_attack);
            else
                speak(R.string.l_fi_now_defend);
            fightNextRound();
        }
    }

    private void speakMissingHits() {
        int missingHits = hitsRequired - hits;
        if (missingHits > 0)
            speak(String.valueOf(missingHits), R.string.l_fi_missing_hits);
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
            speakMissingPenalties();
        } else if (roundResult == AWFUL_ROUND) {
            penalties++;
            speak(R.string.l_fi_round_defense_awful);
            speakMissingPenalties();
        } else if (roundResult == PERFECT_ROUND) {
            if (penalties > 0)
                penalties--;
            speak(R.string.l_fi_round_defense_perfect);
        } else {
            speak(R.string.l_fi_round_defense_won);
        }

        if (penalties == penaltiesLimit)
            lose();
        else {
            attack = !(roundResult == AWFUL_ROUND);
            if (attack)
                speak(R.string.l_fi_now_attack);
            else
                speak(R.string.l_fi_now_defend);
            fightNextRound();
        }
    }

    private void speakMissingPenalties() {
        int missingPenalties = penaltiesLimit - penalties;
        if (missingPenalties > 0)
            speak(String.valueOf(missingPenalties), R.string.l_fi_missing_penalties);
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
        else {
            if (moveIndex < moves.size() - 1) {
                // ci sono ancora delle mosse per decidere l'esito del round
                moveIndex++;
                fightMove();
            } else {
                // tutte le mosse del round sono state eseguite
                if (!attack && errors > 3)
                    // più di 3 errori in difesa (ma meno di 5, già verificato sopra): round perso
                    manageDefenseResult(LOST_ROUND);
                    // nessun errore: round perfetto
                else if (errors == 0)
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
    }

    /**
     * Verifica se la mossa attuale è stata eseguita correttamente e passa il risultato alla verifica
     * del round.
     * TODO verificare combos
     * FIXME usare suoni
     */
    void checkInput() {
        lockInput();

        // nessun movimento entro il tempo limite
        if (input.isEmpty()) {
            ttsUtil.speak("Lento");
            checkRound(false);
            return;
        }

        // è stato eseguito il movimento esatto?
        final Move requiredMove = moves.get(moveIndex);
        final Move inputMove = input.get(0);
        final boolean inputOk = inputMove.equals(requiredMove);
        if (inputOk)
            ttsUtil.speak("OK");
        else
            ttsUtil.speak("NO");
        checkRound(inputOk);
    }

    /**
     * Crea una lista casuale di mosse da eseguire nel round attuale.
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

    private void fightMove() {
        speak(moves.get(moveIndex).getResourceId(attack));
        while (ttsUtil.isSpeaking()) {
        }
        timer = new FightTimer(this, moveDuration);
        unlockInput();
        timer.start();
    }

    class StartingTouchListener implements View.OnTouchListener {

        private final GestureDetectorCompat detector;

        StartingTouchListener() {
            detector = new GestureDetectorCompat(Fight.this, new StartingGestureListener());
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return detector.onTouchEvent(event);
        }
    }

    class StartingGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            if (!started) {
                started = true;
                ttsUtil.stop();
                fight();
            } else if (finished) {
                sendBackResult();
            }
            return true;
        }
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
     */
    class FightGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (canPerformMove()) {
                input.add(Move.MIDDLE);
            }
            return true;
        }

        @Override
        public boolean onFling(final MotionEvent e1, final MotionEvent e2, final float velocityX, final float velocityY) {
            if (canPerformMove()) {
                if (detectLeftFling(velocityX, velocityY)) {
                    input.add(Move.LEFT);
                } else if (detectRightFling(velocityX, velocityY)) {
                    input.add(Move.RIGHT);
                } else if (detectUpFling(velocityX, velocityY)) {
                    input.add(Move.UP);
                } else if (detectDownFling(velocityX, velocityY)) {
                    input.add(Move.DOWN);
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
        if (attack)
            speak(R.string.l_fi_start_attack);
        else
            speak(R.string.l_fi_start_defense);
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

    void lockInput() {
        input.lock();
    }

    void unlockInput() {
        input.unlock();
    }

    public LimitedList<Move> getInput() {
        return input;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
