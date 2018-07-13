package com.example.asus.prueba_android.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.asus.prueba_android.Classes.Routes;
import com.example.asus.prueba_android.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdapterRutas extends RecyclerView.Adapter<AdapterRutas.ViewHolder>{
    private Context context;
    private List<Routes> my_data,mydata_array;
    private OnFragmentInteractionListener mListener;


    //, Padron mPadron, int idorigen//Pensar se usaran**************************
    public AdapterRutas(Context context, List<Routes> my_data) {
        this.context = context;
        this.my_data = my_data;
        mListener = (OnFragmentInteractionListener) context;
        mydata_array=new ArrayList<>();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view,parent,false);
        return new ViewHolder(itemView);

    }

    @SuppressLint("WrongConstant")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.lblOrigen.setText(boldText(""+my_data.get(position).getLocation()));

        holder.lblDestino.setText(boldText(""+my_data.get(position).getDestination()));



    }




    private SpannableString underLineText(String text){
        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        return content;
    }
    private SpannableString boldText(String text){
        SpannableString content = new SpannableString(text);
        content.setSpan(new ForegroundColorSpan(Color.BLACK), 0, content.length(), 0);// set color
        return content;
    }
    @Override
    public int getItemCount() {
        return my_data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView lblOrigen, lblDestino;
        public ImageButton imgBtnInside,imgBtnVoto,imgBtnVotoYes;

        public ViewHolder(final View itemView) {
            super(itemView);
            bindUI(itemView);



        }


        private void bindUI(View itemView){
            //TextViews nombre y telefono/celular
            lblOrigen =(TextView)itemView.findViewById(R.id.lblOrigen);
            lblDestino =(TextView)itemView.findViewById(R.id.lblDestino);
            //ImageButton siguiente hijo
            imgBtnInside =(ImageButton)itemView.findViewById(R.id.imgBtnInside);
        }
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

    }
}


