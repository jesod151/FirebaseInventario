package com.example.firebaseconn;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class MenuActivity extends AppCompatActivity {

    private Button btnlogOut, btnAdd, btnDelete;
    private ImageView addImage;
    private TextView txtNombre, txtDescripcion, txtPrecio;
    private ListView listaProductos;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri = null;

    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private Producto current;
    private ArrayList<Producto> productos;
    private int selectedIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        mStorageRef = FirebaseStorage.getInstance().getReference("products");
        mStorage = FirebaseStorage.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("products");

        listaProductos = findViewById(R.id.listaProductos);
        listaProductos.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedIndex = position;
            }
        });

        addImage = findViewById(R.id.imageView);
        addImage.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        txtNombre = findViewById(R.id.txtNombreP);
        txtDescripcion = findViewById(R.id.txtDescripcionP);
        txtPrecio = findViewById(R.id.txtPrecioP);

        btnAdd = findViewById(R.id.btnAddP);
        btnAdd.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!txtNombre.getText().toString().equals("") && !txtDescripcion.getText().toString().equals("") && !txtPrecio.getText().toString().equals("") && mImageUri != null){
                    current = new Producto(txtNombre.getText().toString(), txtDescripcion.getText().toString(), "", Double.valueOf(txtPrecio.getText().toString()));
                    uploadFile();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Faltan datos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnDelete = findViewById(R.id.btnDeleteP);
        btnDelete.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedIndex == -1){
                    Toast.makeText(getApplicationContext(), "Seleccione un item primero", Toast.LENGTH_SHORT).show();
                }
                else{
                    final Producto toDelete = productos.get(selectedIndex);

                    StorageReference productReference = mStorage.getReferenceFromUrl(toDelete.getImageUrl());
                    productReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mDatabaseRef.child(toDelete.getMkey()).removeValue();
                            setupDataDownloader();
                        }
                    });
                }
            }
        });

        btnlogOut = findViewById(R.id.btnLogOut);
        btnlogOut.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(1);
                finish();
            }
        });

        setupDataDownloader();
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(addImage);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (mImageUri != null) {
            StorageTask mUploadTask;
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    current.setImageUrl(uri.toString());

                                    String uploadId = mDatabaseRef.push().getKey();
                                    mDatabaseRef.child(uploadId).setValue(current);
                                }
                            });
                        }
                    });
        }
    }


    private void setupDataDownloader() {
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                productos = new ArrayList<>();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Producto upload = postSnapshot.getValue(Producto.class);
                    upload.setMkey(postSnapshot.getKey());
                    productos.add(upload);
                }
                CustomAdapter customAdapter = new CustomAdapter();
                listaProductos.setAdapter(customAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    class CustomAdapter extends BaseAdapter{


        @Override
        public int getCount() {
            return productos.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.list_view_adaptee, null);
            ImageView image = convertView.findViewById(R.id.imageAdaptee);
            TextView nombre = convertView.findViewById(R.id.txtNombreAdaptee);
            TextView descripcion = convertView.findViewById(R.id.txtDescriptionAdaptee);
            TextView precio = convertView.findViewById(R.id.txtPrecioAdaptee);

            Picasso.get().load(productos.get(position).getImageUrl()).into(image);
            nombre.setText(productos.get(position).getNombre());
            descripcion.setText(productos.get(position).getDescripcion());
            precio.setText("Precio: " + Double.toString(productos.get(position).getPrecio()) + "₡");

            return convertView;
        }
    }
}
