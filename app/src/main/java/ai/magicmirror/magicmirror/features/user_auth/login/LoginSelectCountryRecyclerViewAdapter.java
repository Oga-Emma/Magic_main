package ai.magicmirror.magicmirror.features.user_auth.login;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ai.magicmirror.magicmirror.R;
import ai.magicmirror.magicmirror.features.user_auth.login.LoginSelectCountryFragment.OnCountrySelected;
import ai.magicmirror.magicmirror.models.CountryDTO;

import java.util.List;

public class LoginSelectCountryRecyclerViewAdapter extends RecyclerView.Adapter<LoginSelectCountryRecyclerViewAdapter.ViewHolder> {

    private final List<CountryDTO> mValues;
    private final OnCountrySelected mListener;

    public LoginSelectCountryRecyclerViewAdapter(List<CountryDTO> items, OnCountrySelected listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.login_select_country_fragment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.bindView(mValues.get(position));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView countryNameTextView, countryCodeTextView;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            countryNameTextView = view.findViewById(R.id.country_name_text_view);
            countryCodeTextView = view.findViewById(R.id.country_code_text_view);
        }

        @Override
        public void onClick(View view) {
            if(null != mListener) {
                mListener.onCountrySelected(mValues.get(getAdapterPosition()), getAdapterPosition());
            }
        }

        public void bindView(CountryDTO country) {
            countryNameTextView.setText(country.getName());
            countryCodeTextView.setText(country.getCallCode());

        }
    }
}
