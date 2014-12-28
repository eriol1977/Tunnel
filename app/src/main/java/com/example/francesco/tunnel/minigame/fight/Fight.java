package com.example.francesco.tunnel.minigame.fight;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.francesco.tunnel.R;
import com.example.francesco.tunnel.minigame.MinigameActivity;
import com.example.francesco.tunnel.util.LimitedList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private List<FightAction> moves = new ArrayList<>();

    private final static int PERFECT_ROUND = 111;

    private final static int WON_ROUND = 222;

    private final static int LOST_ROUND = 333;

    private final static int AWFUL_ROUND = 444;

    private FightTimer timer;

    private LimitedList<Move> input;

    private FightTouchListener fightTouchListener;

    private TextView view;

    private Map<Move, MediaPlayer> attackSounds = new HashMap<>();

    private Map<Move, MediaPlayer> defenseSounds = new HashMap<>();

    private MediaPlayer wrongSound;

    private final static String FIGHT_UTTERANCE_ID = "FIGHT";

    private boolean go = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fight);

        view = (TextView) findViewById(R.id.fight);
        fightTouchListener = new FightTouchListener();
        view.setOnTouchListener(fightTouchListener);

        input = new LimitedList<>(0);
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
        wrongSound = MediaPlayer.create(this, R.raw.beep);
        for (final Move move : Move.values()) {
            attackSounds.put(move, MediaPlayer.create(this, move.getSoundResourceId(true)));
            defenseSounds.put(move, MediaPlayer.create(this, move.getSoundResourceId(false)));
        }
    }

    @Override
    protected void showWinning() {
        speak(R.string.l_fi_won);
    }

    @Override
    protected void showLosing() {
        speak(R.string.l_fi_lost);
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
     * Verifica se la mossa/combo attuale è stata eseguita correttamente e passa il risultato alla verifica
     * del round.
     */
    void checkInput() {

        lockInput();

        final FightAction requiredMove = moves.get(moveIndex);

        // numero insufficiente di mosse entro il tempo limite
        if (input.size() < requiredMove.size()) {
            playSound(null);
            checkRound(false);
            return;
        }

        // è stato eseguito il movimento/sequenza di movimenti esatta?
        boolean inputOk = true;
        if (requiredMove.getType().equals(FightActionType.SINGLE_MOVE)) {
            inputOk = input.get(0).equals(requiredMove);
        } else {
            for (int i = 0; i < requiredMove.size(); i++) {
                if (!input.get(i).equals(((Combo) requiredMove).get(i))) {
                    inputOk = false;
                    break;
                }
            }
        }

        if (!inputOk)
            playSound(null);

        checkRound(inputOk);
    }

    private void playSound(final Move move) {
        MediaPlayer sound = null;

        if (move != null)
            sound = (attack ? attackSounds.get(move) : defenseSounds.get(move));
        else
            sound = wrongSound;

        if (sound != null)
            sound.start();
    }

    /**
     * Crea una lista casuale di mosse da eseguire nel round attuale.
     */
    private void generateMoves() {
        moves.clear();
        moveIndex = 0;
        int buildCombo = 0;
        for (int i = 0; i < roundLength; i++) {
            // crea combos nel 25% dei casi, quando consentito
            // FIXME: usare parametro esterno?
            if (comboLimit > 0)
                buildCombo = randomInt(1, 2);

            if (buildCombo == 1)
                moves.add(buildCombo());
            else {
                moves.add(buildMove());
            }
        }
    }

    private Combo buildCombo() {
        Combo combo = new Combo(comboLimit);
        for (int i = 0; i < comboLimit; i++)
            combo.addMove(buildMove());
        return combo;
    }

    private Move buildMove() {
        int max = Move.values().length;
        int value = randomInt(1, max);
        return Move.get(value);
    }

    private void fightMove() {
        view.setOnTouchListener(null);
        final FightAction action = moves.get(moveIndex);
        if (action.getType().equals(FightActionType.SINGLE_MOVE)) {
            speakMove(msg(((Move) action).getResourceId(attack)));
        } else {
            final Combo combo = (Combo) action;
            StringBuilder sb = new StringBuilder();
            sb.append(getResources().getString(R.string.l_fi_combo)).append(" ");
            for (int i = 0; i < action.size(); i++) {
                sb.append(getResources().getString(combo.get(i).getResourceId(attack))).append(" ");
            }
            speakMove(sb.toString());
        }

        while (!go) {
        }

        go = false;
        long duration = moveDuration * action.size();
        input.setMaxElements(action.size());
        unlockInput();
        view.setOnTouchListener(fightTouchListener);
        timer = new FightTimer(this, duration);
        timer.start();
    }

    @Override
    public void afterUtteranceCompleted(String utteranceId) {
        if (utteranceId.equals(FIGHT_UTTERANCE_ID)) {
            go = true;
        }
    }

    private void speakMove(final String moveText) {
        ttsUtil.speak(moveText, FIGHT_UTTERANCE_ID);
    }

    /**
     * Il listener di tocco delega tutto al detector, che aiuta a gestire movimenti standard.
     */
    class FightTouchListener implements View.OnTouchListener {

        private GestureDetectorCompat detector;

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
            if (!started) {
                started = true;
                ttsUtil.stop();
                fightNextRound();
            } else if (finished) {
                sendBackResult();
            }
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (canPerformMove()) {
                performMove(Move.MIDDLE);
            }
            return true;
        }

        @Override
        public boolean onFling(final MotionEvent e1, final MotionEvent e2, final float velocityX, final float velocityY) {
            if (canPerformMove()) {
                if (detectLeftFling(velocityX, velocityY)) {
                    performMove(Move.LEFT);
                } else if (detectRightFling(velocityX, velocityY)) {
                    performMove(Move.RIGHT);
                } else if (detectUpFling(velocityX, velocityY)) {
                    performMove(Move.UP);
                } else if (detectDownFling(velocityX, velocityY)) {
                    performMove(Move.DOWN);
                }
            }
            return true;
        }

        private void performMove(final Move move) {
            playSound(move);
            input.add(move);
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
    protected void onStop() {

        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        input = null;

        wrongSound.release();
        wrongSound = null;

        for (final MediaPlayer sound : attackSounds.values()) {
            sound.release();
        }
        attackSounds = null;

        for (final MediaPlayer sound : defenseSounds.values()) {
            sound.release();
        }
        defenseSounds = null;

        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
