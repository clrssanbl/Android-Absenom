package com.example.myabsenom;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myabsenom.model.Kehadiran;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import static android.text.TextUtils.isEmpty;

public class AbsensiActivity extends AppCompatActivity implements View.OnClickListener {

    private Button tdk_hdr, hdr, btDatePicker, Upload;
    private TextView txtMatkul, txtMhs, txtNim, tvDateResult;
    private DatabaseReference database;
    private FirebaseAuth auth;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;
    private static final String USERS = "User";
    private String email;
    private FirebaseUser user;
    private EditText txt_alasan;
    private ImageView ImageContainer;
    private Uri filePath;

    //Deklarasi Variable StorageReference
    private FirebaseStorage storage;
    private StorageReference storageReference;

    //Kode permintaan untuk memilih metode pengambilan gamabr
    private static final int REQUEST_CODE_GALLERY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //menampilkan layout untuk melakukan presensi atau absensi
        setContentView(R.layout.content_matkul);
        setActionBarTitle("Presensi");
        //inisialisasi widget
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        txtMatkul = (TextView) findViewById(R.id.text_matkul);
        txtMhs = (TextView) findViewById(R.id.text_mhs);
        txtNim = (TextView) findViewById(R.id.text_Nim);
        txt_alasan = (EditText) findViewById(R.id.alasan);
        tdk_hdr = findViewById(R.id.btn_tidakhadir);
        tdk_hdr.setOnClickListener(this);
        hdr = findViewById(R.id.btn_hadir);
        hdr.setOnClickListener(this);
        Upload = findViewById(R.id.btn_upload);
        Upload.setOnClickListener(this);
        ImageContainer = findViewById(R.id.imageContainer);
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        tvDateResult = (TextView) findViewById(R.id.tv_dateresult);
        btDatePicker = (Button) findViewById(R.id.bt_datepicker);
        btDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });
        //Instance / Membuat Objek Firebase Authentication
        auth = FirebaseAuth.getInstance();
        //Mendapatkan Instance dari Database
        database = FirebaseDatabase.getInstance().getReference();
        //untuk mendapatkan informasi user yang sedang login
        user = auth.getCurrentUser();
        String UID = user.getUid();
        email = user.getEmail();

        DatabaseReference userRef = database.child(USERS);
        Log.v("USERID", userRef.getKey());
        //untuk mendapatkan data matakuliah yang telah dipilih pada activity sebelumnya yaitu HomeActivity
        getData();
        //untuk menampilkan informasi user ke dalam layout berupa nama dan nim
        userRef.addValueEventListener(new ValueEventListener() {
            String e_nama, e_nim;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot keyId : dataSnapshot.getChildren()) {
                    if (keyId.child("email").getValue().equals(email)) {
                        e_nama = keyId.child("nama").getValue(String.class);
                        e_nim = keyId.child("nim").getValue(String.class);
                        break;
                    }
                }
                txtMhs.setText(e_nama);
                txtNim.setText(e_nim);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
            }
        });

    }

    //memberikan title pada toolbar
    private void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    private void getImage() {
        //Method ini digunakan untuk mengambil gambar dari galeri
        CharSequence[] menu = {"Galeri"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle("Upload Image")
                .setItems(menu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                //Mengambil gambar dari galeri
                                Intent imageIntentGallery = new Intent(Intent.ACTION_PICK,
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(imageIntentGallery, REQUEST_CODE_GALLERY);
                                break;
                        }
                    }
                });
        dialog.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Menghandle hasil data yang diambil dari galeri untuk ditampilkan pada ImageView yang ada pada layout
        switch (requestCode) {
            case REQUEST_CODE_GALLERY:
                if (resultCode == RESULT_OK) {
                    ImageContainer.setVisibility(View.VISIBLE);
                    filePath = data.getData();
                    ImageContainer.setImageURI(filePath);
                }
                break;
        }
    }

    private void uploadImage() {
        //Method ini digunakan untuk mengupload gambar pada Firebase Storage
        //Mendapatkan data dari ImageView sebagai Bytes
        ImageContainer.setDrawingCacheEnabled(true);
        ImageContainer.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) ImageContainer.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        //Mengkompress bitmap menjadi JPG dengan kualitas gambar 100%
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bytes = stream.toByteArray();
        if (filePath != null) {
            //jika filepath tidak kosong maka gambar akan disimpan dalam direktori "gambar/" yang ada pada firebase storage
            StorageReference ref = storageReference.child("gambar/" + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(AbsensiActivity.this, "Uploading Berhasil", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AbsensiActivity.this, "Uploading Gagal", Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    //menampilkan kalendar
    private void showDateDialog() {
        /**
         * Calendar untuk mendapatkan tanggal sekarang
         */
        Calendar newCalendar = Calendar.getInstance();
        /**
         * Initiate DatePicker dialog
         */
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                /**
                 * Method ini dipanggil saat kita selesai memilih tanggal di DatePicker
                 */
                /**
                 * Set Calendar untuk menampung tanggal yang dipilih
                 */
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                /**
                 * Update TextView dengan tanggal yang kita pilih
                 */
                tvDateResult.setText(dateFormatter.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        /**
         * Tampilkan DatePicker dialog
         */
        datePickerDialog.show();
    }

    //untuk mendapatkan data matakuliah yang telah dipilih pada activity sebelumnya yaitu HomeActivity
    private void getData() {
        final String getMatkul = getIntent().getExtras().getString("data");
        txtMatkul.setText(getMatkul);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                Intent i = new Intent(this, HomeActivity.class);
                this.startActivity(i);
                return true;
            case R.id.action_logout:
                auth = FirebaseAuth.getInstance();
                auth.signOut();
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            case R.id.action_profile:
                Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(profileIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_hadir:
                //Mendapatkan Instance dari Database
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference getReference;

                //Menyimpan Data yang diinputkan User kedalam Variable
                String nim = txtNim.getText().toString();
                String nama = txtMhs.getText().toString();
                String matkul = txtMatkul.getText().toString();
                String kehadiran = hdr.getText().toString();
                String tanggal = tvDateResult.getText().toString();

                getReference = database.getReference(); // Mendapatkan Referensi dari Database

                // Mengecek apakah ada data yang kosong
                if (isEmpty(nim) || isEmpty(nama) || isEmpty(matkul) || isEmpty(kehadiran) || isEmpty(tanggal)) {
                    Toast.makeText(AbsensiActivity.this, "Data tidak boleh ada yang kosong", Toast.LENGTH_SHORT).show();
                } else {
                    /*
                    Jika Tidak, maka data dapat diproses dan meyimpannya pada Database
                    */
                    getReference.child("kehadiran").push()
                            .setValue(new Kehadiran(nim, nama, matkul, kehadiran, tanggal))
                            .addOnSuccessListener(this, new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    //Peristiwa ini terjadi saat user berhasil menyimpan datanya kedalam Database
                                    Toast.makeText(AbsensiActivity.this, "Presensi Success", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                break;
            case R.id.btn_tidakhadir:
                //Mendapatkan Instance dari Database
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference Reference;

                //Menyimpan Data yang diinputkan User kedalam Variable
                nim = txtNim.getText().toString();
                nama = txtMhs.getText().toString();
                matkul = txtMatkul.getText().toString();
                kehadiran = tdk_hdr.getText().toString();
                tanggal = tvDateResult.getText().toString();
                String alasan = txt_alasan.getText().toString();

                Reference = db.getReference(); // Mendapatkan Referensi dari Database

                // Mengecek apakah ada data yang kosong
                if (isEmpty(nim) || isEmpty(nama) || isEmpty(matkul) || isEmpty(kehadiran) || isEmpty(tanggal) || isEmpty(alasan)) {
                    //Jika Ada, maka akan menampilkan pesan singkan seperti berikut ini.
                    Toast.makeText(AbsensiActivity.this, "Data tidak boleh ada yang kosong", Toast.LENGTH_SHORT).show();
                } else {
                    /*
                    Jika Tidak, maka data dapat diproses dan meyimpannya pada Database
                    */
                    //mengupload file gambar yang sudah dipilih ke dalam firebase storage
                    uploadImage();
                    Reference.child("kehadiran").push()
                            .setValue(new Kehadiran(nim, nama, matkul, kehadiran, tanggal, alasan))
                            .addOnSuccessListener(this, new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    //Peristiwa ini terjadi saat user berhasil menyimpan datanya kedalam Database
                                    Toast.makeText(AbsensiActivity.this, "Absensi Success", Toast.LENGTH_SHORT).show();
                                }
                            });
                }


                break;
            case R.id.btn_upload:
                //untuk memilih file gambar yang ingin diupload
                getImage();
                break;
        }
    }
}
