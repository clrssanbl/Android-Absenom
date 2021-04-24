package com.example.myabsenom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myabsenom.model.Matakuliah;

import java.util.ArrayList;

//Class Adapter ini Digunakan Untuk Mengatur Bagaimana Data akan Ditampilkan
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    //Deklarasi Variable
    private ArrayList<Matakuliah> listMatakuliah;
    private Context context;

    //Membuat Konstruktor, untuk menerima input dari Database
    public RecyclerViewAdapter(ArrayList<Matakuliah> listMatakuliah, Context context) {
        this.listMatakuliah = listMatakuliah;
        this.context = context;
    }

    //ViewHolder Digunakan Untuk Menyimpan Referensi Dari View-View
    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvMatkul, tvDosen;
        private LinearLayout ListMatkul;

        ViewHolder(View itemView) {
            super(itemView);
            //Menginisialisasi View-View yang terpasang pada layout RecyclerView kita
            tvMatkul = itemView.findViewById(R.id.matkul);
            tvDosen = itemView.findViewById(R.id.dosen);
            ListMatkul= itemView.findViewById(R.id.list_matkul);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Membuat View untuk Menyiapkan dan Memasang Layout yang Akan digunakan pada RecyclerView
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_matkul, parent, false);
        return new ViewHolder(V);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //Mengambil Nilai/Value yenag terdapat pada RecyclerView berdasarkan Posisi Tertentu
        final String Matakuliah = listMatakuliah.get(position).getMatkul();
        final String Dosen = listMatakuliah.get(position).getNamaDosen();
        //untuk mengirimkan data matakuliah yang dipilih ke activity selanjutnya yaitu AbsensiActivity
        holder.tvMatkul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("data",listMatakuliah.get(position).getMatkul());
                Intent intent = new Intent(view.getContext(),AbsensiActivity.class);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
        //Memasukan Nilai/Value kedalam View
        holder.tvMatkul.setText("Matakuliah: "+Matakuliah);
        holder.tvDosen.setText("Nama: "+Dosen);
    }

    @Override
    public int getItemCount() {
        //Menghitung Ukuran/Jumlah Data Yang Akan Ditampilkan Pada RecyclerView
        return listMatakuliah.size();
    }

}
