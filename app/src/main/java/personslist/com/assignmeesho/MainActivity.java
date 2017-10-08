package personslist.com.assignmeesho;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity{

    Context context = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        new APIAsyncTask(context, new FinishAPI() {
            @Override
            public void onFinish(String result) {

            }
        }).execute();

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
