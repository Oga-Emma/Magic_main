package ai.magicmirror.magicmirror.FEATURES.user_auth.login;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ai.magicmirror.magicmirror.models.CountryDTO;

public class CountryListUtils{

    private List<CountryDTO> ITEMS;
    private static CountryListUtils countryListUtils;


    public static CountryListUtils getInstance(Context context){
        if(countryListUtils == null){
            countryListUtils = new CountryListUtils(context);
        }

        return countryListUtils;
    }

    private CountryListUtils(Context context) {
        this.ITEMS = new ArrayList<>();

        ITEMS.addAll(loadJSONFromAsset(context));
    }

    public List<CountryDTO> getCountries(){
        return ITEMS;
    }

    private List<CountryDTO> loadJSONFromAsset(Context context) {

        List<CountryDTO> countryList = new ArrayList<>();

        try {
            InputStream is = context.getAssets().open("country_list.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonString = new String(buffer, "UTF-8");

            JSONObject obj = new JSONObject(jsonString);
            JSONArray jsonArray = obj.getJSONArray("countries");

            for(int i = 0; i < jsonArray.length(); i++){
                obj = jsonArray.getJSONObject(i);
                countryList.add(new CountryDTO(obj.getString("name"), obj.getString("code")));
            }


        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return countryList;
    }

}
