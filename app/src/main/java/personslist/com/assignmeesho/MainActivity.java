package personslist.com.assignmeesho;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import personslist.com.assignmeesho.pojo.PullRequestitem;

public class MainActivity extends AppCompatActivity{

    Context context = null;

    ArrayList<PullRequestitem> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        new APIAsyncTask(context, new FinishAPI() {
            @Override
            public void onFinish(String result) {
                JSONArray array = null;
                try {
                    array = new JSONArray(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                parseJsonArray(array);
            }
        }).execute();

    }

    private void parseJsonArray(JSONArray array){
        if(items!=null){
            items.clear();
        }
        if(array == null || array.length()==0){
            return;
        }

        for(int i=0;i<array.length();i++){
            JSONObject object = null;
            try {
                object = (JSONObject) array.get(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(object!=null){
                PullRequestitem item = new PullRequestitem();
                try {
                    item.setId(object.getLong("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    item.setId(null);
                }

                try {
                    item.setNumber(object.getInt("number"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    item.setState(object.getString("state"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    item.setTitle(object.getString("title"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    item.setUrl(object.getString("url"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {
                    item.setUserName(object.getJSONObject("user").getString("login"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    item.setAvatorUrl(object.getJSONObject("user").getString("avatar_url"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    item.setSiteAdmin(object.getJSONObject("user").getBoolean("site_admin"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    item.setUpdatedAt(object.getString("updated_at"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    item.setRepoName(object.getJSONObject("head").getJSONObject("repo").getString("name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(items!=null){
                    items.add(item);
                }
            }
        }
    }

    public class APIAsyncTask extends AsyncTask<Boolean, Void, Boolean> {
        private String TAG = APIAsyncTask.class.getName();
        Context mContext;
        String result = "";
        FinishAPI callback = null;
        String owner = "Maxcodingworld";
        String repo = "AssignMeesho";

        public APIAsyncTask(Context context,FinishAPI callback) {
            mContext = context;
            this.callback = callback;
        }

        @Override
        protected Boolean doInBackground(Boolean... params) {
            Log.e(TAG,"Inside InBackground");
            boolean success = false;
            try {
                URL url = new URL("https://api.github.com/repos/"+ owner +"/"+repo+"/pulls?state=open");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(15000);
                connection.connect();
                success = connection.getResponseCode() == 200;
                InputStream inputStream = connection.getInputStream();
                if(inputStream!=null) {
                    result = convertToString(inputStream);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return success;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            Log.e(TAG,"Inside onPostExecute ::  " + result);
            if(callback!=null){
                callback.onFinish(result);
            }
        }

        private String convertToString(InputStream inputStream){
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            try {
                while((line = reader.readLine())!=null){
                    result+=line;
                }
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

    }

    public interface FinishAPI{
        void onFinish(String result);
    }
}
