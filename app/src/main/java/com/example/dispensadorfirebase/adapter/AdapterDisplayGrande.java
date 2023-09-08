package com.example.dispensadorfirebase.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.dispensadorfirebase.R;
import com.example.dispensadorfirebase.clase.SectorLocal;

import java.util.ArrayList;
import java.util.List;

public class AdapterDisplayGrande extends RecyclerView.Adapter<AdapterDisplayGrande.NoteViewHolder> {

    private List<SectorLocal> notes;
    private OnNoteSelectedListener onNoteSelectedListener;
    private OnNoteDetailListener onDetailListener;
    private int CantidadSectores;
    private Context context;
    private boolean ejecutado = false;
    private int numerofuncion = 0;

    public AdapterDisplayGrande(int cantidad) {
        this.notes = new ArrayList<>();
        this.CantidadSectores = cantidad;
    }

    public AdapterDisplayGrande(List<SectorLocal> notes) {
        this.notes = notes;
    }


    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View elementoTitular;

        if (CantidadSectores ==1){
            elementoTitular = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_note_sectores_21_uno, parent, false);

        }else if (CantidadSectores ==2){
            elementoTitular = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_note_sectores_21_dos, parent, false);
        }else{

            elementoTitular = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_note_sectores_21_tres, parent, false);
        }

        context = elementoTitular.getContext();

        return new NoteViewHolder(elementoTitular);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder view, int pos) {
        view.bind(notes.get(pos));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public List<SectorLocal> getNotes() {
        return notes;
    }

    public void setNotes(List<SectorLocal> notes) {
        this.notes = notes;
    }

    public void setOnNoteSelectedListener(OnNoteSelectedListener onNoteSelectedListener) {
        this.onNoteSelectedListener = onNoteSelectedListener;
    }

    public void setOnDetailListener(OnNoteDetailListener onDetailListener) {
        this.onDetailListener = onDetailListener;
    }



    public interface OnNoteSelectedListener {
        void onClick(SectorLocal note);
    }

    public interface OnNoteDetailListener {
        void onDetail(SectorLocal note);
    }



    public SectorLocal getposicionactual(int position) {
        return notes.get(position);
    }



    public class NoteViewHolder extends RecyclerView.ViewHolder {


        private TextView nombre;
        private TextView numero;
        private LinearLayout layout;
        private boolean ejecutar = false;
        public NoteViewHolder(View item) {
            super(item);

            nombre = (TextView) item.findViewById(R.id.txtnombresec);
            numero = (TextView) item.findViewById(R.id.txtnumerosec);
            layout = (LinearLayout) item.findViewById(R.id.layoutsec);
        //falta color

        }

        public void bind(final SectorLocal sector) {

            numero.setText("" +sector.getNumeroatendiendo());

            if (!ejecutado){

                nombre.setText(sector.getNombreSector());
                layout.setBackgroundColor(Color.parseColor(sector.getColorSector()));
                Uri fondo = null;
                if (CantidadSectores>1){

                    if (!sector.getFondoSectorH().equals("sin imagen"))   {
                        fondo = Uri.parse(sector.getFondoSectorH());
                        CargarImagen(fondo,layout);
                    }



                }else{

                    if (!sector.getFondoSectorV().equals("sin imagen"))   {
                        fondo = Uri.parse(sector.getFondoSectorV());
                        CargarImagen(fondo,layout);
                    }

                }

                numerofuncion++;
                if (numerofuncion == CantidadSectores){
                    ejecutado=true;
                }

            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onNoteSelectedListener != null) {

                        onNoteSelectedListener.onClick(sector);

                    }
                }
            });
        }

        private Boolean CargarImagen(Uri fondo,LinearLayout layout){

            final Boolean[] a = {false};

            Glide.with(context).load(fondo).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {

                    layout.setBackground(resource);

                    a[0] = true;

                }
            });

            return a[0];
        }

    }
}
