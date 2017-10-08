package personslist.com.assignmeesho;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    RecyclerView recyclerView = null;
    PullRequestAdaptor adaptor = null;

    ProgressDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        TextView done = (TextView) findViewById(R.id.done);
        if(done!=null){
            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText userName = (EditText) findViewById(R.id.user_name);
                    EditText repoName = (EditText) findViewById(R.id.repo_name);

                    if(userName!=null && repoName!=null){
                        hideKeyboard(context,userName);
                        hideKeyboard(context,repoName);
                        String userString = userName.getText().toString();
                        String repoString = repoName.getText().toString();
                        if(userString!=null && repoString!=null){
                            try {
                                if (dialog != null && dialog.isShowing()) {
                                    dialog.dismiss();
                                    dialog = null;
                                }
                            }catch(Exception e){
                            }
                            dialog = new ProgressDialog(context);
                            dialog.setTitle("Fetching pull requests");
                            dialog.setMessage("Please wait ...");
                            dialog.setCancelable(false);
                            dialog.show();
                            new APIAsyncTask(context, new FinishAPI() {
                                @Override
                                public void onFinish(String result) {
                                    if(result!=null) {
                                        JSONArray array = null;
                                        try {
                                            array = new JSONArray(result);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        parseJsonArray(array);
                                    }
                                    try {
                                        if (dialog != null && dialog.isShowing()) {
                                            dialog.dismiss();
                                            dialog = null;
                                        }
                                    }catch(Exception e){
                                    }
                                }
                            },userString,repoString).execute();
                        }
                    }
                }
            });
        }


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adaptor = new PullRequestAdaptor(items,context);
        if(recyclerView!=null){
            recyclerView.setAdapter(adaptor);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(linearLayoutManager);
        }
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

        if(adaptor!=null){
            adaptor.notifyDataSetChanged();
        }
    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null/* && inputMethodManager.isAcceptingText()*/) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public class APIAsyncTask extends AsyncTask<Boolean, Void, Boolean> {
        private String TAG = APIAsyncTask.class.getName();
        Context mContext;
        String result = "";
        FinishAPI callback = null;
        String owner = "Maxcodingworld";
        String repo = "AssignMeesho";

        public APIAsyncTask(Context context,FinishAPI callback,String owner,String repo) {
            mContext = context;
            this.callback = callback;
            this.owner = owner;
            this.repo = repo;
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
            if(aBoolean) {
                Log.e(TAG, "Inside onPostExecute ::  " + result);
                if (callback != null) {
                    callback.onFinish(result);
                }
            }else{
                Toast.makeText(context,"File Not found",Toast.LENGTH_LONG).show();
                if (callback != null) {
                    callback.onFinish(null);
                }
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

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
                dialog = null;
            }
        }catch(Exception e){
        }
    }
}
