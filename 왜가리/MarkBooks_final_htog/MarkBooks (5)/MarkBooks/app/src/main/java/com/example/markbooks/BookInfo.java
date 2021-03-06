package com.example.markbooks;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class BookInfo extends AppCompatActivity implements TextPlayer, View.OnClickListener {
    TextView a, b, c, d, e, f, s_a;       // TextView ??????
    ImageView cover;                 // ImageView ?????? (??? ??????)
    List<String> str = new ArrayList<>();
    int k = 0;                       // ??????????????? ????????? number
    int[] Recommend = new int[10];
    final ImageButton[] img = new ImageButton[10];

    FirebaseStorage storage = FirebaseStorage.getInstance("gs://markbooks-8df8a.appspot.com");
    StorageReference storageRef = storage.getReference();
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;
    String userID;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    List<Map<String, Object>> userList = new ArrayList<Map<String, Object>>();
    LinkedHashSet<String> ID = new LinkedHashSet<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    private ConstraintLayout rootLayout;


    private Palette.Swatch vibrantSwatch;
    private Palette.Swatch lightVibrantSwatch;
    private Palette.Swatch darkVibrantSwatch;
    private Palette.Swatch mutedSwatch;
    private Palette.Swatch lightMutedSwatch;
    private Palette.Swatch darkMutedSwatch;

    //????????? ??????
    private int like_count = 0; //??????????????????????????????
    int user_cnt;


    ScaleAnimation scaleAnimation;
    BounceInterpolator bounceInterpolator;//?????????????????? ???????????? ????????? ??????, ????????? ??????????????? ????????? ???????????? ????????? ?????? ??? ??? ??????
    CompoundButton button_favorite;


    //TextView Textvw;
    TextView HighLighttv;
    ArrayList<SelectedWord> selectedWordsCounter = new ArrayList<SelectedWord>();
    Button btnGetHighlightedWords;


    //?????? ??????
    private final Bundle params = new Bundle();
    private final BackgroundColorSpan colorSpan = new BackgroundColorSpan(Color.YELLOW);
    private TextToSpeech tts;
    private ImageButton playBtn;
    private ImageButton pauseBtn;
    private ImageButton stopBtn;
    private TextView contentTextView;
    private PlayState playState = PlayState.STOP;
    private Spannable spannable;
    private int standbyIndex = 0;
    private int lastPlayIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookinfo);

        rootLayout = findViewById(R.id.root_layout);


        scaleAnimation = new ScaleAnimation(0.6f, 1.0f, 0.6f, 1.0f, Animation.RELATIVE_TO_SELF, 0.6f, Animation.RELATIVE_TO_SELF, 0.6f);
        scaleAnimation.setDuration(500);
        bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator);

        initTTS(); //?????? ??????
        initView(); // ?????? ??????


        //Text_View ??????
        a = findViewById(R.id.title);
        b = findViewById(R.id.author);
        c = findViewById(R.id.publisher);
        d = findViewById(R.id.genre);
        e = findViewById(R.id.year);
        f = findViewById(R.id.summary);
        cover = findViewById(R.id.pic);
        s_a = findViewById(R.id.smallTitle);

        //Recommend_View ??????
        for (int i = 1; i <= 6; i++) {
            Recommend[i] = getResources().getIdentifier("Recommend" + i, "id", getPackageName());
            img[i - 1] = (ImageButton) findViewById(Recommend[i]);
        }

        Intent dataIntent = getIntent();
        String Bookname;
        Bookname = dataIntent.getStringExtra("?????????");

        //Storage?????? ????????? ????????????
        storageRef.child("Book img").child(Bookname + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(cover);
            }
        });



        //Cloud Firestore?????? ??? ????????? ????????????
        DocumentReference docRef = db.collection("book").document(Bookname);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot ds) {    //ds: documentSnapshot
                String title = (ds.getString("title"));
                String author = (ds.getString("author"));
                String publisher = (ds.getString("publisher"));
                String year = (ds.getString("year"));
                String genre = (ds.getString("genre"));
                String summary = (ds.getString("summary"));

                a.setText(title);
                b.setText(author);
                c.setText("?????????: " + publisher);
                d.setText("??????:  " + genre);
                e.setText("?????????: " + year);
                f.setText("<?????????>\n" + summary);


                s_a.setText(title);

                mAuth = FirebaseAuth.getInstance();
                userID = mAuth.getCurrentUser().getUid();
                Map<String, Object> userid = new HashMap<>();
                userid.put("like_user", userID);

                button_favorite = findViewById(R.id.button_favorite1);
                fStore.collection("user").document(userID).collection("favorite").document(title).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                button_favorite.setChecked(true);
                                fStore.collection("book").document(title).collection("likeuser").document(userID).set(userid);
                                like_count++;

                            }
                        }
                    }
                });


                button_favorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
                    {
                        compoundButton.startAnimation(scaleAnimation);
                        like_count++;
                        Map<String, Object> userid = new HashMap<>();
                        userid.put("like_user", userID);
                        if (like_count % 2 == 1)
                        {
                            button_favorite.setChecked(true);
                            //favoriteimg.setImageResource(R.drawable.ic_favorite);

                            fStore.collection("book").document(title).collection("likeuser").document(userID).set(userid);
                            user_cnt = like_count % 2;
                            Map<String, Object> fav = favorite( user_cnt);
                            fStore.collection("user").document(userID).collection("favorite").document(title).set(fav);

                            fStore.collection("book").document(title).set(fav, SetOptions.merge());

                        }
                        else if (like_count % 2 == 0)
                        {
                            fStore.collection("book").document(title).collection("likeuser").document(userID).delete();
                            user_cnt = like_count % 2;
                            button_favorite.setChecked(false);
                            //favoriteimg.setImageResource(R.drawable.ic_favorite_border);
                            Map<String, Object> fav = favorite(user_cnt);
                            fStore.collection("user").document(userID).collection("favorite").document(title).delete();
                            fStore.collection("book").document(title).set(fav, SetOptions.merge());
                            like_count -= 2;

                        }
                    }
                    private Map<String, Object> favorite( int user_cnt)
                    {
                        Map<String, Object> favorite = new HashMap<>();

                        favorite.put("like_count", user_cnt);
                        return favorite;
                    }
                });

            }
        });
        if  (cover != null && cover.getDrawable() == null) {
            Bitmap bitmap = ((BitmapDrawable) cover.getDrawable()).getBitmap();
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    darkMutedSwatch = palette.getDarkMutedSwatch();
                    Palette.Swatch currnetSwatch = darkMutedSwatch;
                    rootLayout.setBackgroundColor(currnetSwatch.getRgb());
                }
            });
        }

        // ??? ????????? ???
        DocumentReference colRef = db.collection("book").document(Bookname);
        colRef.get().

                addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String re_genre = (documentSnapshot.getString("genre"));
                        db.collection("book").whereEqualTo("genre", re_genre).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                                    String pic = ds.get("pic").toString();
                                    str.add(pic.substring(42));
                                }
                                for (int i = 0; i < str.size(); i++) {
                                    System.out.println(str.get(i));
                                    storageRef.child("Book img").child(str.get(i)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Glide.with(getApplicationContext())
                                                    .load(uri)
                                                    .into(img[k]);
                                            k++;
                                        }
                                    });
                                }
                            }
                        });
                    }
                });



        //??????????????? ??????
        f = (TextView) findViewById(R.id.summary);
        f.setCustomSelectionActionModeCallback(new com.example.markbooks.BookInfo.StyleCallBack());
        f.setVerticalScrollBarEnabled(true);
        f.requestFocus();

        HighLighttv = (TextView) findViewById(R.id.highlightword);

        btnGetHighlightedWords = (Button) findViewById(R.id.btnGetHighlightedWords);

        btnGetHighlightedWords.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < selectedWordsCounter.size(); i++) {
                    sb.append(selectedWordsCounter.get(i).word + ",\n");
                }
                //HighLighttv.setText("Highlighted Words:\n\n" + sb.toString());

                //Highlight ??????????????? ??????
                Intent intent = new Intent(getApplicationContext(), Highlight.class);
                intent.putExtra("Highlightwords", sb.toString());
                startActivity(intent);
            }
        });

    }


    class StyleCallBack implements ActionMode.Callback {

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.style, menu);
            menu.removeItem(android.R.id.selectAll);
            menu.removeItem(android.R.id.copy);
            menu.removeItem(android.R.id.cut);
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {


            int start = f.getSelectionStart();
            int end = f.getSelectionEnd();

            SpannableStringBuilder ssb = new SpannableStringBuilder(f.getText());
            //??????????????? ??????
            final ForegroundColorSpan foreGroundWhite = new ForegroundColorSpan(Color.BLACK);
            final BackgroundColorSpan backgroundRed = new BackgroundColorSpan(Color.YELLOW);
            //	final CharacterStyle csBold = new StyleSpan(Typeface.BOLD);

            final ForegroundColorSpan foreGroundBlack = new ForegroundColorSpan(Color.BLACK);
            final BackgroundColorSpan backgroundWhite = new BackgroundColorSpan(Color.WHITE);
            //	final CharacterStyle csNormal = new StyleSpan(Typeface.NORMAL);

            switch (item.getItemId()) {
                case R.id.highlight:

                    final CharSequence selectedText = f.getText().subSequence(start, end);
                    boolean b = checkWordStorage(selectedText.toString(), start, end);
                    if (b) {
                        // DESELECT
                        ssb.setSpan(backgroundWhite, start, end, 1);
                        //	    ssb.setSpan(csNormal, start, end, 1);
                        ssb.setSpan(foreGroundBlack, start, end, 1);
                        Log.v("Start:" + start, "End:" + end);
                        f.setText(ssb);

                    } else {
                        //SELECT
                        ssb.setSpan(backgroundRed, start, end, 1);
                        //  ssb.setSpan(csBold, start, end, 1);
                        ssb.setSpan(foreGroundWhite, start, end, 1);
                        Log.v("Start:" + start, "End:" + end);
                        f.setText(ssb);
                    }
                    return true;
            }
            return false;
        }

        public void onDestroyActionMode(ActionMode mode) {
        }
    }




    private boolean checkWordStorage(String word, int st, int end) {
        boolean result = false;
        for (int i = 0; i < selectedWordsCounter.size(); i++) {
            int eachStart = selectedWordsCounter.get(i).getStart();
            int eachEnd = selectedWordsCounter.get(i).getEnd();
            String eachWord = selectedWordsCounter.get(i).getWord();

            if (word.equals(eachWord) && st == eachStart && end == eachEnd) {
                // word is already exist and need to DESELECT
                selectedWordsCounter.remove(i);
                result = true;
                break;
            }
        }

        if (!result) {
            // word is not found in array, We need to add it in array
            selectedWordsCounter.add(new SelectedWord(word, st, end));
        }

        return result;
    }


    public void clickButton(View view){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    // ??? ?????? ????????? ?????? ?????? ?????????
    public void handleSelection(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.Recommend1:
                this.finish();
                intent = new Intent(BookInfo.this, BookInfo.class);
                intent.putExtra("?????????", str.get(0));
                startActivity(intent);
                break;
            case R.id.Recommend2:
                this.finish();
                intent = new Intent(BookInfo.this, BookInfo.class);
                intent.putExtra("?????????", str.get(1));
                startActivity(intent);
                break;
            case R.id.Recommend3:
                this.finish();
                intent = new Intent(BookInfo.this, BookInfo.class);
                intent.putExtra("?????????", str.get(2));
                startActivity(intent);
                break;
            case R.id.Recommend4:
                this.finish();
                intent = new Intent(BookInfo.this, BookInfo.class);
                intent.putExtra("?????????", str.get(3));
                startActivity(intent);
                break;
            case R.id.Recommend5:
                this.finish();
                intent = new Intent(BookInfo.this, BookInfo.class);
                intent.putExtra("?????????", str.get(4));
                startActivity(intent);
                break;
            case R.id.Recommend6:
                this.finish();
                intent = new Intent(BookInfo.this, BookInfo.class);
                intent.putExtra("?????????", str.get(5));
                startActivity(intent);
                break;
        }
    }

    private void initView() {
        playBtn = findViewById(R.id.btn_play); //??????
        pauseBtn = findViewById(R.id.btn_pause);
        stopBtn = findViewById(R.id.btn_stop);
        playBtn.setOnClickListener(this);
        pauseBtn.setOnClickListener(this);
        stopBtn.setOnClickListener(this);


        contentTextView = findViewById(R.id.summary); // ????????? ????????? ???????????? ??????

    }

    private void initTTS() { //tts ????????? ???
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, null);
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int state) {
                if (state == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.KOREAN);
                } else {
                    showState("TTS ?????? ????????? ??? ????????? ??????????????????.");
                }
            }
        });

        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {
            }
            @Override
            public void onDone(String s) {
                clearAll();
            }
            @Override
            public void onError(String s) {
                showState("?????? ??? ????????? ??????????????????.");
            }

            @Override
            public void onRangeStart(String utteranceId, int start, int end, int frame) {
                changeHighlight(standbyIndex + start, standbyIndex + end);
                lastPlayIndex = start;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_play:
                startPlay();
                break;

            case R.id.btn_pause:
                pausePlay();
                break;

            case R.id.btn_stop:
                stopPlay();
                break;
        }
        showState(playState.getState());
    }

    //  start pause stop ?????? ?????? ??????

    @Override
    public void startPlay() { // ?????? ???????????? ??????
        String content = contentTextView.getText().toString(); //?????? ??????
        if (playState.isStopping() && !tts.isSpeaking()) {
            setContentFromEditText(content);
            startSpeak(content);
        } else if (playState.isWaiting()) {
            standbyIndex += lastPlayIndex;
            startSpeak(content.substring(standbyIndex));
        }
        playState = PlayState.PLAY;
    }

    @Override
    public void pausePlay() {
        if (playState.isPlaying()) {
            playState = PlayState.WAIT;
            tts.stop();
        }
    }

    @Override
    public void stopPlay() {
        tts.stop();
        clearAll();
    }


    // ????????? ????????? ?????????
    private void changeHighlight(final int start, final int end) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spannable.setSpan(colorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        });
    }

    private void setContentFromEditText(String content) {
        contentTextView.setText(content, TextView.BufferType.SPANNABLE);
        spannable = (SpannableString) contentTextView.getText();  //?????? ????????? ????????? ??????
    }

    private void startSpeak(final String text) {
        tts.speak(text, TextToSpeech.QUEUE_ADD, params, text);
    }


    private void clearAll() {
        playState = PlayState.STOP;
        standbyIndex = 0;
        lastPlayIndex = 0;

        if (spannable != null) {
            changeHighlight(0, 0); // remove highlight
        }
    }


    private void showState(final String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        if (playState.isPlaying()) {
            pausePlay();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (playState.isWaiting()) {
            startPlay();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        tts.stop();
        tts.shutdown();
        super.onDestroy();
    }
}