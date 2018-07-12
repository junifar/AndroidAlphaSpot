package com.mobile.transpotid.transpot.Fragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.mobile.transpotid.transpot.Adapter.MissionJSONParser;
import com.mobile.transpotid.transpot.Detail_Mission;
import com.mobile.transpotid.transpot.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import static com.mobile.transpotid.transpot.R.layout;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class FragmentMission extends Fragment {

    ListView mListView;

    public FragmentMission() {
        // Required empty public constructor
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        #################String Connection URL####################
        String strUrl = getString(R.string.mission);
//        ##########################################################

        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(strUrl);
        
        mListView = (ListView) getActivity().findViewById(R.id.listViewMission);

    }

    @Override
    public void onResume() {
        super.onResume();

//        #################String Connection URL####################
        String strUrl = getString(R.string.mission);
//        ##########################################################

        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(strUrl);

        mListView = (ListView) getActivity().findViewById(R.id.listViewMission);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(layout.fragment_fragment_mission, container, false);
    }

    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        try {
            URL url = new URL(strUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();

            String line = "";
            while ( (line = br.readLine())!=null){
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e){
            Log.d("Exception while download url",e.toString());
        } finally {
            iStream.close();
        }

        return data;
    }

    private class DownloadTask extends AsyncTask<String, Integer, String>{
        String data = null;

        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);                
            } catch (IOException e){
                Log.d("Background Task",e.toString());
            }
            
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            ListViewLoaderTask listViewLoaderTask = new ListViewLoaderTask();
            listViewLoaderTask.execute(result);
        }
    }

    private class ListViewLoaderTask extends AsyncTask<String, Void, SimpleAdapter> {
        JSONArray jArray;

        @Override
        protected SimpleAdapter doInBackground(String... strJson) {

            try {
                jArray = new JSONArray(strJson[0]);
                MissionJSONParser missionJSONParser = new MissionJSONParser();
                missionJSONParser.getMissions(jArray);
            } catch (JSONException e) {
                Log.d("JSON Exception 1",e.toString());
            }

            MissionJSONParser missionJSONParser = new MissionJSONParser();

            List<HashMap<String, Object>> missions = null;

            try {
                missions = missionJSONParser.getMissions(jArray);
            } catch (Exception e){
                Log.d("Exception",e.toString());
            }

            String[] from = {"pk","name","description","photo_url","period","price","agent"};
            int[] to = {R.id.txtPK,R.id.txtCampaign,R.id.txtDesc,R.id.imgCampaign,R.id.txtPeriode,R.id.txtPrice,R.id.txtAgent};
            SimpleAdapter adapter = new SimpleAdapter(getActivity().getBaseContext(),missions, R.layout.item_mission_layout,from,to);

            return adapter;
        }

        @Override
        protected void onPostExecute(SimpleAdapter adapter) {
            mListView.setAdapter(adapter);

            for (int i=0; i<adapter.getCount();i++){
                HashMap<String, Object> hm = (HashMap<String, Object>) adapter.getItem(i);
                String imgUrl = (String) hm.get("photo_url_path");
                ImageLoaderTask imageLoaderTask = new ImageLoaderTask();

                HashMap<String, Object> hmDownload = new HashMap<String, Object>();
                hm.put("photo_url_path",imgUrl);
                hm.put("position",i);

                imageLoaderTask.execute(hm);
            }
            registerClickCallBack();
        }
    }

    private void registerClickCallBack() {

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                //Mission clickedMission = myMissions.get(position);
                LinearLayout ll = (LinearLayout) viewClicked;
                TextView tv = (TextView)ll.findViewById(R.id.txtPK);
                final String PK = tv.getText().toString();
                Intent i = new Intent(getActivity().getApplicationContext(), Detail_Mission.class);
                i.putExtra("PK",PK);
                startActivity(i);
            }
        });
    }

    private class ImageLoaderTask extends AsyncTask<HashMap<String, Object>, Void, HashMap<String, Object>> {

        @Override
        protected HashMap<String, Object> doInBackground(HashMap<String, Object>... hm) {
            InputStream iStream = null;
            String imgUrl = (String) hm[0].get("photo_url_path");
            int position = (Integer) hm[0].get("position");

            URL url;
            try {
                url = new URL(imgUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                iStream = urlConnection.getInputStream();
                File cacheDirectory = getActivity().getBaseContext().getCacheDir();
                File tempFile = new File(cacheDirectory.getPath() + ".mission_" + position + ".png");
                FileOutputStream fOutStream = new FileOutputStream(tempFile);
                Bitmap b = BitmapFactory.decodeStream(iStream);
                b.compress(Bitmap.CompressFormat.PNG, 25, fOutStream);
                fOutStream.flush();
                fOutStream.close();
                HashMap<String, Object> hmBitmap = new HashMap<String, Object>();
                hmBitmap.put("photo_url",tempFile.getPath());
                hmBitmap.put("position",position);
                return hmBitmap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(HashMap<String, Object> result) {
            String path = (String) result.get("photo_url");
            int position = (Integer) result.get("position");
            SimpleAdapter adapter = (SimpleAdapter) mListView.getAdapter();
            HashMap<String, Object> hm = (HashMap<String, Object>) adapter.getItem(position);
            hm.put("photo_url",path);
            adapter.notifyDataSetChanged();
        }
    }
}
