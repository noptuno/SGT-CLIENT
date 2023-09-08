package com.example.dispensadorfirebase.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.dispensadorfirebase.R;
import com.example.dispensadorfirebase.clase.SectorLocal;
import com.example.dispensadorfirebase.clase.Sectores;
import com.google.android.gms.dynamic.IFragmentWrapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdapterDispensador extends RecyclerView.Adapter<AdapterDispensador.NoteViewHolder> {

    private List<SectorLocal> notes;
    private OnNoteSelectedListener onNoteSelectedListener;
    private OnNoteDetailListener onDetailListener;
    private int CantidadSectores;
    private Context context;
private boolean ejecutado = false;
private int numerofuncion = 0;
    private boolean esperar;


    public AdapterDispensador(int cantidad) {
        this.notes = new ArrayList<>();
        this.CantidadSectores = cantidad;
    }

    public AdapterDispensador(List<SectorLocal> notes) {
        this.notes = notes;
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View elementoTitular;

        if (CantidadSectores ==1){
            elementoTitular = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_note_sectores_imprimir_uno, parent, false);

        }else if (CantidadSectores ==2){
            elementoTitular = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_note_sectores_imprimir_dos, parent, false);
        }else{

            elementoTitular = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_note_sectors_imprimir, parent, false);

           // LinearLayout a = elementoTitular.findViewById(R.id.layoutsec);

           // a.setBackground(ContextCompat.getDrawable(context, R.drawable.fondos_rotiseria_vertical));
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
        private boolean esperar = false;

        public NoteViewHolder(View item) {
            super(item);


            nombre = (TextView) item.findViewById(R.id.txtnombresec);
            numero = (TextView) item.findViewById(R.id.txtnumerosec);
            layout = (LinearLayout) item.findViewById(R.id.layoutsec);


        }


        public void bind(final SectorLocal sector) {

            numero.setText("" +sector.getNumeroDispensador());

            if (!ejecutado){

                nombre.setText(sector.getNombreSector());

                layout.setBackgroundColor(Color.parseColor(sector.getColorSector()));

                Uri fondo = null;

                if (CantidadSectores>1){

                    String a = sector.getFondoSectorH();
                    if (!a.equals("sin imagen")){
                        fondo = Uri.parse(a);
                        CargarImagen(fondo,layout);
                    }

                }else{

                    String a = sector.getFondoSectorV();
                    if (!a.equals("sin imagen")){
                        fondo = Uri.parse(a);
                        CargarImagen(fondo,layout);
                    }

                }

                numerofuncion++;
                if (numerofuncion == CantidadSectores){
                    ejecutado=true;
                }

            }



           // File f = new File(getRealPathFromURI(Uri.parse(sector.getFondoh())));
          //  Drawable d = Drawable.createFromPath(f.getAbsolutePath());
           //  Glide.with(context).load(uri).into(logolocal);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onNoteSelectedListener != null) {
                        itemView.setEnabled(false);
                        onNoteSelectedListener.onClick(sector);
                        delay();

                    }
                }
            });

        }

        private void delay() {
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {

                    itemView.setEnabled(true);
                }


            }, 2000);

        }
    }




    private void CargarImagen(Uri fondo,LinearLayout layout){

        Glide.with(context).load(fondo).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {

                layout.setBackground(resource);

            }
        });

    }

    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }
}
