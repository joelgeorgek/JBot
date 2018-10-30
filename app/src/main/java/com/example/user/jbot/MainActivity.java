package com.example.user.jbot;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    LinearLayout parent_layout;
    EditText my_text_field;
    TextToSpeech textToSpeech;
    ImageButton voiceButton;
    ImageButton toggletalkButton;

    ArrayList<String> chats_Bot = new ArrayList<>();
    ArrayList<String> chats_User = new ArrayList<>();

    boolean talkButtonToggleState=true;

    RecyclerView recyclerView;
    Random random = new Random();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parent_layout = findViewById(R.id.parent_layout);

        int colorFrom = Color.parseColor("#FFFFFF");
        int colorTo = Color.parseColor("#212121");
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(2000); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                parent_layout.setBackgroundColor((int) animator.getAnimatedValue());
            }

        });
        colorAnimation.start();

        my_text_field = findViewById(R.id.editText1);
        textToSpeech = new TextToSpeech(getApplicationContext(),this);
        voiceButton = findViewById(R.id.voice_button);
        toggletalkButton = findViewById(R.id.talk_action_button);

        recyclerView = findViewById(R.id.my_recycler_view);
        chats_Bot.add("Hello, I am JBot");
        chats_User.add("Hello from user");
        //chats_User.add("Hello");
        MyAdapter adapter = new MyAdapter(chats_Bot,chats_User);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);



    }

    public void onClickSend(View view){
        String chat_text = my_text_field.getText().toString();
        my_text_field.setText("");
        respond(chat_text);
    }

    public void respond(String chat_text){
        if(chat_text.equals("")==false){
            chats_User.add(chat_text);
            String Bot_answer = getAnswer(chat_text);
            chats_Bot.add(Bot_answer);
            MyAdapter adapter = new MyAdapter(chats_Bot,chats_User);
            recyclerView.setAdapter(adapter);
            recyclerView.scrollToPosition(chats_Bot.size()-1);
            if(talkButtonToggleState) {
                speakOut(Html.fromHtml(Bot_answer).toString().replaceAll("[^a-zA-z\\d\\s.!?:\"',;]",""));
            }
        }
    }
    public String getAnswer(String input){
        String response = "I didn't get what you meant by \""+input+"\"";
        ArrayList<String> Queries = new ArrayList<>();
        ArrayList<String> Responses = new ArrayList<>();
        if(input.toCharArray().length<2){
            return response;
        }
        try {
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(this.getResources().openRawResource(R.raw.responses, new TypedValue()),null);
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.nextTag();

            loop :
            while(parser.next()!=XmlPullParser.END_DOCUMENT){
                if(parser.getEventType()!=XmlPullParser.START_TAG){
                    continue;
                }
                String tag_1 = parser.getName();
                if(tag_1.equals("Text")){
                    Queries.removeAll(Queries);
                    Responses.removeAll(Responses);
                    while(parser.next()!=XmlPullParser.END_TAG){
                        if(parser.getEventType()!=XmlPullParser.START_TAG){
                            continue;
                        }
                        String tag_2 = parser.getName();
                        if(tag_2.equals("Q")){
                            while(parser.next()!=XmlPullParser.END_TAG){
                                if(parser.getEventType()!=XmlPullParser.TEXT){
                                    continue;
                                }
                                Queries.add(parser.getText());
                            }
                        }else if(tag_2.equals("A")){
                            while(parser.next()!=XmlPullParser.END_TAG){
                                if(parser.getEventType()!=XmlPullParser.TEXT){
                                    continue;
                                }
                                Responses.add(parser.getText());
                            }
                        }
                    }
                }
                for(String Query : Queries){
                    if(Query.toLowerCase().contains(input.toLowerCase())){
                        response = Responses.get(random.nextInt(Responses.size()));
                        Log.i("HAHA",Responses.size()+"");
                        break loop;
                    }
                }
            }
            return response;
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
    public void onClickRecord(View view){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Talk something to me");
        try {
            startActivityForResult(intent, 111);

        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! your device does not support speech input.",
                    Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 111: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    respond(result.get(0));
                    }
                break;
            }

        }
    }


    public void onClickToggleTalk(View view){
        if(talkButtonToggleState) {
            toggletalkButton.setImageResource(R.drawable.ic_keyboard_voice_black_24dp);
            talkButtonToggleState=false;
            Toast.makeText(getApplicationContext(),"Speech disabled", Toast.LENGTH_SHORT).show();
        }else{
            toggletalkButton.setImageResource(R.drawable.enable_talk);
            talkButtonToggleState=true;
            Toast.makeText(getApplicationContext(),"Speech enabled", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS){
            int result = textToSpeech.setLanguage(Locale.ENGLISH);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                //Log.e("TTS", "This Language is not supported");
            } else {
                voiceButton.setEnabled(true);
                speakOut("Hello, I am J Bot");
            }

        } else {
            //Log.e("TTS", "Initilization Failed!");

        }
    }
    public void speakOut(String text){
        //String chat_text = my_text_field.getText().toString();
        text = text.replaceAll("JBot","J Bot");
        textToSpeech.speak(text,TextToSpeech.QUEUE_FLUSH, null);
    }
    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

}





