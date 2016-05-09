package com.example.arash.translation01;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends AppCompatActivity {
        EditText translateedittext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onTranslate(View view) {

        EditText translateEditTextView= (EditText) findViewById(R.id.editText);
        if(!isEmpty(translateEditTextView)){

            Toast.makeText(this,"Getting translations",Toast.LENGTH_LONG).show();
            new SaveTheFeed().execute();


        }else{
            Toast.makeText(this,"Enter word to trasnlate ",Toast.LENGTH_SHORT).show();


        }


    }
    protected boolean isEmpty(EditText editText){

        return editText.getText().toString().trim().length()==0;



    }


    class SaveTheFeed extends AsyncTask <Void ,Void,Void>{


        String jsonString = "";
        String result="";



        @Override
        protected Void doInBackground(Void... params) {

            EditText translateEditTextView= (EditText) findViewById(R.id.editText);
            String wordsToTranslate=translateEditTextView.getText().toString();

            wordsToTranslate=wordsToTranslate.replace(" ","+");
            DefaultHttpClient httpClient=new DefaultHttpClient(new BasicHttpParams());
            HttpPost httpPost = new HttpPost("http://newjustin.com/translateit.php?action=translations&english_words=" + wordsToTranslate);
            httpPost.setHeader("Content-type", "application/json");
            InputStream inputStream = null;
            try{

                HttpResponse response= httpClient.execute(httpPost);
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
                    "french", "german", "italian", "portuguese", "russian",
                    "spanish"};
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
}
