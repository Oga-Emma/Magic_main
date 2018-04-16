package ai.magicmirror.magicmirror.features.user_auth.login;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ai.magicmirror.magicmirror.R;
import ai.magicmirror.magicmirror.models.CountryDTO;

public class LoginSelectCountryFragment extends Fragment {

    private static final String ARG_SELECTED_COUNTRY_INDEX = "column-count";
    private int selectedCountryIndex = 1;
    private OnCountrySelected mListener;

    LoginSelectCountryRecyclerViewAdapter adapter;

    public LoginSelectCountryFragment() {
    }

    public static LoginSelectCountryFragment newInstance(int currentlySelectedCountry) {
        LoginSelectCountryFragment fragment = new LoginSelectCountryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SELECTED_COUNTRY_INDEX, currentlySelectedCountry);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            selectedCountryIndex = getArguments().getInt(ARG_SELECTED_COUNTRY_INDEX);
        }else{
            selectedCountryIndex = 160;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_select_country_fragment_list, container, false);

        RecyclerView countryListRecyclerView = view.findViewById(R.id.country_list_recycler_view);

        countryListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new LoginSelectCountryRecyclerViewAdapter
                (CountryListUtils.getInstance(getContext()).getCountries(), mListener);
        countryListRecyclerView.setAdapter(adapter);

        countryListRecyclerView.scrollToPosition(selectedCountryIndex);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCountrySelected) {
            mListener = (OnCountrySelected) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCountrySelected");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnCountrySelected {
        void onCountrySelected(CountryDTO item, int position);
    }
}
