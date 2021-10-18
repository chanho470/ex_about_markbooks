package com.example.markbooks.common;

import android.content.Intent;
import android.graphics.Color;
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
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.markbooks.MainActivity;
import com.example.markbooks.tts.PlayState;
import com.example.markbooks.R;
import com.example.markbooks.highlight.SelectedWord;
import com.example.markbooks.tts.TextPlayer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BookInfoActivity extends AppCompatActivity implements TextPlayer, View.OnClickListener {
    TextView a, b, c, d, e, f, s_a;       // TextView 선언
    ImageView cover;                 // ImageView 선언 (책 표지)
    List<String> str = new ArrayList<>();
    List<String> str_t = new ArrayList<>();
    String Bookname;


    int k = 0;                       // 횡스크롤뷰 이미지 number
    int[] Recommend = new int[10];
    final ImageButton[] img = new ImageButton[10];

    FirebaseStorage storage = FirebaseStorage.getInstance("gs://markbooks-8df8a.appspot.com");
    StorageReference storageRef = storage.getReference();
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    String userID;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    List<Map<String, Object>> userList = new ArrayList<Map<String, Object>>();
    LinkedHashSet<String> ID = new LinkedHashSet<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //좋아요 갯수
    private int like_count = 0; //좋아요누른지안누른지
    int all_like_count; //전체 좋아요수
    int user_cnt;

    ScaleAnimation scaleAnimation;
    BounceInterpolator bounceInterpolator;//애니메이션이 일어나는 동안의 회수, 속도를 조절하거나 시작과 종료시의 효과를 추가 할 수 있다
    CompoundButton button_favorite;

    //TextView Textvw;
    TextView HighLighttv;
    ArrayList<SelectedWord> selectedWordsCounter = new ArrayList<SelectedWord>();
    Button btnGetHighlightedWords;


    //음성 인식
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

        scaleAnimation = new ScaleAnimation(0.6f, 1.0f, 0.6f, 1.0f, Animation.RELATIVE_TO_SELF, 0.6f, Animation.RELATIVE_TO_SELF, 0.6f);
        scaleAnimation.setDuration(500);
        bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator);


        initTTS(); //음성 인식
        initView(); // 음성 인식


        //Text_View 선언
        a = findViewById(R.id.title);
        b = findViewById(R.id.author);
        c = findViewById(R.id.publisher);
        d = findViewById(R.id.genre);
        e = findViewById(R.id.year);
        f = findViewById(R.id.summary);
        cover = findViewById(R.id.pic);
        s_a = findViewById(R.id.smallTitle);

        //Recommend_View 선언
        for (int i = 1; i <= 6; i++) {
            Recommend[i] = getResources().getIdentifier("Recommend" + i, "id", getPackageName());
            img[i - 1] = (ImageButton) findViewById(Recommend[i]);
        }

        Intent dataIntent = getIntent();
        Bookname = dataIntent.getStringExtra("검색어");

        //Storage에서 책표지 가져오기
        storageRef.child("Book img").child(Bookname + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(cover);
            }
        });


        //Cloud Firestore에서 책 데이터 가져오기
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
                String pic = (ds.getString("pic"));

                a.setText(title);
                b.setText(author);
                c.setText("출판사: " + publisher);
                d.setText("장르:  " + genre);
                e.setText("출간일: " + year);
                f.setText("<줄거리>\n" + summary);


                s_a.setText(title);

                mAuth = FirebaseAuth.getInstance();
                userID = mAuth.getCurrentUser().getUid();
                Map<String, Object> userid = new HashMap<>();
                userid.put("like_user", userID);

                //좋아요 기능
                button_favorite = findViewById(R.id.button_favorite);
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
                    private Map<String, Object> favorite( int user_cnt) {
                        Map<String, Object> favorite = new HashMap<>();
                        favorite.put("like_count", user_cnt);
                        favorite.put("title", title);
                        favorite.put("author", author);
                        favorite.put("genre", genre);
                        favorite.put("pic",pic);
                        return favorite;
                    }
                });

            }
        });


        // 횡 스크롤 뷰
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
                                    String title = ds.get("title").toString();
                                    str.add(pic.substring(42));
                                    str_t.add(title);
                                }
                                for (int i = 0; i < str.size(); i++) {
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

        //하이라이트 기능
        f = (TextView) findViewById(R.id.summary);
        f.setCustomSelectionActionModeCallback(new BookInfoActivity.StyleCallBack());
        f.setVerticalScrollBarEnabled(true);
        f.requestFocus();

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
            //하이라이트 색상
            final ForegroundColorSpan foreGroundWhite = new ForegroundColorSpan(Color.BLACK);
            final BackgroundColorSpan backgroundRed = new BackgroundColorSpan(Color.YELLOW);
            //   final CharacterStyle csBold = new StyleSpan(Typeface.BOLD);

            final ForegroundColorSpan foreGroundBlack = new ForegroundColorSpan(Color.BLACK);
            final BackgroundColorSpan backgroundWhite = new BackgroundColorSpan(Color.WHITE);
            //   final CharacterStyle csNormal = new StyleSpan(Typeface.NORMAL);

            switch (item.getItemId()) {
                case R.id.highlight:

                    final CharSequence selectedText = f.getText().subSequence(start, end);
                    boolean b = checkWordStorage(selectedText.toString(), start, end);
                    if (b) {
                        // DESELECT
                        ssb.setSpan(backgroundWhite, start, end, 1);
                        //       ssb.setSpan(csNormal, start, end, 1);
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

                        // Highlight 문장 저장
                        StringBuffer sb = new StringBuffer();
                        for (int i = 0; i < selectedWordsCounter.size(); i++) {
                            sb.append(selectedWordsCounter.get(i).getWord() + "\n");
                        }

                        userID = mAuth.getCurrentUser().getUid();
                        LocalDateTime dateTime = LocalDateTime.now();
                        Map<String, Object> hl = new HashMap<>();
                        hl.put("sentence", sb.toString());
                        hl.put("time", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(dateTime).toString());
                        fStore.collection("user").document(userID).collection("highlight").document(Bookname).set(hl);
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

    // 책 표지 이미지 버튼 클릭 이벤트
    public void handleSelection(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.Recommend1:
                this.finish();
                intent = new Intent(BookInfoActivity.this, BookInfoActivity.class);
                intent.putExtra("검색어", str_t.get(0));
                startActivity(intent);
                break;
            case R.id.Recommend2:
                this.finish();
                intent = new Intent(BookInfoActivity.this, BookInfoActivity.class);
                intent.putExtra("검색어", str_t.get(1));
                startActivity(intent);
                break;
            case R.id.Recommend3:
                this.finish();
                intent = new Intent(BookInfoActivity.this, BookInfoActivity.class);
                intent.putExtra("검색어", str_t.get(2));
                startActivity(intent);
                break;
            case R.id.Recommend4:
                this.finish();
                intent = new Intent(BookInfoActivity.this, BookInfoActivity.class);
                intent.putExtra("검색어", str_t.get(3));
                startActivity(intent);
                break;
            case R.id.Recommend5:
                this.finish();
                intent = new Intent(BookInfoActivity.this, BookInfoActivity.class);
                intent.putExtra("검색어", str_t.get(4));
                startActivity(intent);
                break;
            case R.id.Recommend6:
                this.finish();
                intent = new Intent(BookInfoActivity.this, BookInfoActivity.class);
                intent.putExtra("검색어", str_t.get(5));
                startActivity(intent);
                break;
        }
    }

    public void Hyperlink(View view) {
        Intent dataIntent = getIntent();
        String Bookname = dataIntent.getStringExtra("검색어");

        DocumentReference docRef = db.collection("book").document(Bookname);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot ds) {    //ds: documentSnapshot
                String url = (ds.getString("url"));
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });
    }

    private void initView() {
        playBtn = findViewById(R.id.btn_play); //버튼
        pauseBtn = findViewById(R.id.btn_pause);
        stopBtn = findViewById(R.id.btn_stop);
        playBtn.setOnClickListener(this);
        pauseBtn.setOnClickListener(this);
        stopBtn.setOnClickListener(this);


        contentTextView = findViewById(R.id.summary); // 텍스트 나와서 보여주는 부분

    }

    private void initTTS() { //tts 초기화 함
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, null);
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int state) {
                if (state == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.KOREAN);
                } else {
                    showState("TTS 객체 초기화 중 문제가 발생했습니다.");
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
                showState("재생 중 에러가 발생했습니다.");
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

    //  start pause stop 구현 하는 부분

    @Override
    public void startPlay() { // 읽기 시작하는 부분
        String content = contentTextView.getText().toString(); //바꾼 부분
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


    // 형광펜 칠하는 부분임
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
        spannable = (SpannableString) contentTextView.getText();  //아마 형광펜 하는거 일듯
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