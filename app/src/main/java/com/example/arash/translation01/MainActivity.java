package com.example.arash.translation01;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends Activity implements TextToSpeech.OnInitListener {
    EditText trans_EditText;

        private Locale currentSpokenLang = Locale.US;


        private Locale locSpanish = new Locale("es", "MX");
        private Locale locRussian = new Locale("ru", "RU");
        private Locale locPortuguese = new Locale("pt", "BR");
        private Locale locDutch = new Locale("nl", "NL");


        private Locale[] languages = {locDutch, Locale.FRENCH, Locale.GERMAN, Locale.ITALIAN,
                locPortuguese, locRussian, locSpanish};


        private TextToSpeech textToSpeech;


        private Spinner languageSpinner;


        private int spinnerIndex = 0;

        private String[] arrayOfTranslations;

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

            languageSpinner = (Spinner) findViewById(R.id.lang_spinner);

            // When the Spinner is changed update the currently selected language
            // to speak in
            languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {
                    currentSpokenLang = languages[index];

                    // Store the selected Spinner index for use elsewhere
                    spinnerIndex = index;

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            textToSpeech = new TextToSpeech(this, this);

    }

    public void onTranslate_old(View view) {

        trans_EditText= (EditText) findViewById(R.id.editText);
        if(!isEmpty(trans_EditText)){

            Toast.makeText(this,"Getting translations",Toast.LENGTH_LONG).show();
        new SaveTheFeed().execute();
          //  new getxmldata().execute();

        }else{
            Toast.makeText(this,"Enter word/sentence to trasnlate ",Toast.LENGTH_SHORT).show();


        }


    }



    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
    public void onTranslate(View view) {

        EditText translateEditText = (EditText) findViewById(R.id.editText);

        // If the user entered words to translate then get the JSON data
        if(!isEmpty(translateEditText)){

            Toast.makeText(this, "Getting Translations",
                    Toast.LENGTH_LONG).show();

            // Calls for the method doInBackground to execute
            new getxmldata().execute();

        } else {

            // Post an error message if they didn't enter words
            Toast.makeText(this, "Enter Words to Translate",
                    Toast.LENGTH_SHORT).show();

        }

    }

    // Check if the user entered words to translate
    // Returns false if not empty
    protected boolean isEmpty(EditText editText){

        // Get the text in the EditText convert it into a string, delete whitespace
        // and check length
        return editText.getText().toString().trim().length() == 0;

    }

    @Override
    public void onInit(int status) {
        // Check if TextToSpeech is available
        if (status == TextToSpeech.SUCCESS) {

            int result = textToSpeech.setLanguage(currentSpokenLang);

            // If language data or a specific language isn't available error
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "Language Not Supported", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Text To Speech Failed", Toast.LENGTH_SHORT).show();
        }

    }

    public void readTheText(View view) {


        textToSpeech.setLanguage(currentSpokenLang);


        if (arrayOfTranslations.length >= 9){


            textToSpeech.speak(arrayOfTranslations[spinnerIndex+4], TextToSpeech.QUEUE_FLUSH, null);

        } else {

            Toast.makeText(this, "Translate Text First", Toast.LENGTH_SHORT).show();

        }

    }
    class getxmldata extends AsyncTask<Void,Void,Void>{
        String stringToPrint = "";



        @Override
        protected Void doInBackground(Void... params) {

            String xmlString = "";

            String words_Translate = "";

            EditText translateEditText = (EditText) findViewById(R.id.editText);

            words_Translate = translateEditText.getText().toString();

            words_Translate = words_Translate.replace(" ", "+");


            DefaultHttpClient http_Client = new DefaultHttpClient(new BasicHttpParams());


            // HttpPost ht_Post = new HttpPost("http://tolksenct.bugs3.com/translatapp.php?action=translations&english_words==" + words_Translate+"english");
            HttpPost ht_Post = new HttpPost("http://newjustin.com/translateit.php?action=xmltranslations&english_words=" + words_Translate);




            ht_Post.setHeader("Content-type", "text/xml");


            InputStream in_Stream = null;

            try {

                HttpResponse response = http_Client.execute(ht_Post);

                HttpEntity entity = response.getEntity();

                in_Stream = entity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(in_Stream, "UTF-8"), 8);

                StringBuilder sb = new StringBuilder();

                String line = null;

                while((line = reader.readLine()) != null){

                    sb.append(line);

                }

                xmlString = sb.toString();

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

                factory.setNamespaceAware(true);

                XmlPullParser pullParser = factory.newPullParser();


                pullParser.setInput(new StringReader(xmlString));

                int eventType = pullParser.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {

                    if ((eventType == XmlPullParser.START_TAG) && (!pullParser.getName().equals("translations"))) {

                        stringToPrint = stringToPrint + pullParser.getName() + " : ";

                    } else if (eventType == XmlPullParser.TEXT) {
                        stringToPrint = stringToPrint + pullParser.getText() + "\n";
                    }

                    eventType = pullParser.next();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {

            TextView translateTextView = (TextView) findViewById(R.id.editText);
            translateTextView.setMovementMethod(new ScrollingMovementMethod());

            String stringOfTranslations = stringToPrint.replaceAll("\\w+\\s:","#");


            arrayOfTranslations = stringOfTranslations.split("#");

            translateTextView.setText(stringToPrint);

        }
    }






    class SaveTheFeed extends AsyncTask <Void ,Void,Void>{


        String jsonString = "";
        String result="";



        @Override
        protected Void doInBackground(Void... params) {

            EditText translateEditTextView= (EditText)findViewById(R.id.editText);
            String words_Translate=translateEditTextView.getText().toString();

            words_Translate=words_Translate.replace(" ","+");
            DefaultHttpClient httpClient=new DefaultHttpClient(new BasicHttpParams());
                //    HttpPost ht_Post = new HttpPost("http://tolksenct.bugs3.com/transapp.php?action=translations&english_words=" + words_Translate);
         HttpPost ht_Post = new HttpPost("http://tolksenct.bugs3.com/translatapp.php?action=translations&english_words=" + words_Translate);
           //HttpPost ht_Post = new HttpPost("http://newjustin.com/translateit.php?action=translations&english_words=" + words_Translate);



            ht_Post.setHeader("Content-type", "application/json");
            InputStream inputStream = null;
            try{

                HttpResponse response= httpClient.execute(ht_Post);
                HttpEntity entity= response.getEntity();
                inputStream=entity.getContent();
                BufferedReader reader= new BufferedReader(new InputStreamReader(inputStream,"UTF-8"),8);
                StringBuilder sb= new StringBuilder();
                String line = null;
                while ((line= reader.readLine())!= null){
                    sb.append(line +"\n");


                }
                jsonString= sb.toString();
                JSONObject JObject = new JSONObject(jsonString);
                JSONArray jArray= JObject.getJSONArray("translations");

                outputTranslations(jArray);

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            TextView translationtxtview= (TextView)findViewById(R.id.translationtxtview);
            translationtxtview.setText(result);

        }

        protected void outputTranslations(JSONArray jsonArray){
            String[]Languages= {"arabic", "chinese", "danish", "dutch",
                    "french", "german", "italian", "persian", "russian",
                    "spanish","swedish"};
            try{
                for (int i = 0 ;i<jsonArray.length();i++){
                    JSONObject translationObject =
                            jsonArray.getJSONObject(i);

                    result= result+Languages[i]+" : " + translationObject.getString(Languages[i])+"\n";

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    public void ExceptSpeechInput(View view) {

        // Starts an Activity that will convert speech to text
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        // Use a language model based on free-form speech recognition
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        // Recognize speech based on the default speech of device
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        // Prompt the user to speak
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_input_phrase));

        try{

            startActivityForResult(intent, 100);

        } catch (ActivityNotFoundException e){

            Toast.makeText(this, getString(R.string.stt_not_supported_message), Toast.LENGTH_LONG).show();

        }

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        // 100 is the request code sent by startActivityForResult
        if((requestCode == 100) && (data != null) && (resultCode == RESULT_OK)){

            // Store the data sent back in an ArrayList
            ArrayList<String> spokenText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            EditText wordsEntered = (EditText) findViewById(R.id.editText);

            // Put the spoken text in the EditText
            wordsEntered.setText(spokenText.get(0));

        }

    }
}
