package com.example.nlcs_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.widget.ViewPager2;

import com.example.nlcs_app.databinding.FragmentInfoBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class Fragment_Info extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters
    private Activity_Home   mainScreen;
    private Account         user            =   new Account();
    private final FirebaseStorage storage         =   FirebaseStorage.getInstance();
    private Button          updateAvatar;
    private Button          cancelUpdate;
    private Button          updateInfo;
    private Button          saveInfo;
    private Button          cancelSave;
    private ViewPager2      viewPager2;
    private CircleImageView avatarImage;
    private TextView        name;
    private TextView        email;
    private TextView        date;
    private TextView        emailTv;
    private EditText        nameEt;
    private EditText        dayEt;
    private EditText        monthEt;
    private EditText        yearEt;
    private boolean         setIcon = true;

    private DatabaseReference databaseReference;

    private ActivityResultLauncher<String> takephoto;
    private FragmentInfoBinding binding;

    public Fragment_Info() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainScreen  = (Activity_Home) getActivity();
        user = new Account(mainScreen != null ? mainScreen.getUserLogin() : null);
        viewPager2 = mainScreen.getViewPager();
        binding = FragmentInfoBinding.inflate(inflater, container, false);

        if(user.getAvataUrl().compareTo("") != 0){
            new fetchImage(user.getAvataUrl()).start();
        }

//        set takephoto
        takephoto = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                result -> binding.avataImage.setImageURI(result)
        );

//        set storageReference to up image
        StorageReference storageRef = storage.getReference();

//        set info page
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if(position == 2) { // 2 is number of info page

                    mapping();
                    updateAvatarEnable(false);
                    updateInfoEnable(false);
                    if( setIcon) {
                        setToolbarMenu();
                        setIcon = false;
                    }

                    setInfo();

                    mainScreen.checkNotificationsPermission();
                    avatarImage.setOnClickListener(v -> {
                        if (mainScreen.getImage()) {
                            takephoto.launch("image/*");
                            updateAvatarEnable(true);
                        }
                    });

                    updateInfo.setOnClickListener(v -> updateInfoEnable(true));
                    saveInfo.setOnClickListener(v -> {
                        String nameStr;
                        int day, month, year;
                        nameStr     = nameEt.getText().toString();
                        day         = Integer.parseInt(dayEt.getText().toString());
                        month       = Integer.parseInt(monthEt.getText().toString());
                        year        = Integer.parseInt(yearEt.getText().toString());
                            if( nameStr.compareTo("") != 0 && day != 0 && month != 0 && year != 0) {
                                Date d =  new Date( day, month, year);
                                user.setName(nameStr);
                                user.setDate(d);
                                setInfo();

                                databaseReference.child("account").child("user" + user.getId()).child("name").setValue(nameStr);
                                databaseReference.child("account").child("user" + user.getId()).child("date").setValue(d);
                                for( int i = 0; i < user.getListContact().size(); i++) {
                                    if( user.getListContact().get(i).getIdUser() != -1) {
                                        databaseReference.child("account")
                                                .child("user" + user.getListContact().get(i).getIdUser())
                                                .child("listContact")
                                                .child(Integer.toString(user.getListContact().get(i).getpositon()))
                                                .child("name")
                                                .setValue(nameStr);
                                        databaseReference.child("account")
                                                .child("user" + user.getListContact().get(i).getIdUser())
                                                .child("listContact")
                                                .child(Integer.toString(user.getListContact().get(i).getpositon()))
                                                .child("date")
                                                .setValue(d);
                                    }
                                }
                                updateInfoEnable(false);
                            }

                    });
                    cancelSave.setOnClickListener(v -> updateInfoEnable(false));
                    updateAvatar.setOnClickListener(v -> {
                        updateAvata(storageRef,user);
                        updateAvatarEnable(false);
                    });
                    cancelUpdate.setOnClickListener(v -> {
                        if( user.getAvataUrl().compareTo("") != 0){
                            new fetchImage(user.getAvataUrl()).start();
                        }
                        updateAvatarEnable(false);
                    });
                }
            }
        });

        return binding.getRoot();
    }

    public void updateAvatarEnable( boolean b) {
        if (b) {
            binding.layoutButtonUpdate.setVisibility(View.VISIBLE);
            binding.layoutButtonCancel.setVisibility(View.VISIBLE);
            binding.layoutTextViewInfo.setVisibility(View.INVISIBLE);
            binding.layoutButtonInfo.setVisibility(View.INVISIBLE);
            binding.layoutUpdateInfo.setVisibility(View.INVISIBLE);
            binding.lableInfo.setVisibility(View.INVISIBLE);
            binding.layoutSavaInfo.setVisibility(View.INVISIBLE);
            binding.layoutCanelSave.setVisibility(View.INVISIBLE);

        } else {
            binding.layoutButtonUpdate.setVisibility(View.INVISIBLE);
            binding.layoutButtonCancel.setVisibility(View.INVISIBLE);
            binding.lableInfo.setVisibility(View.VISIBLE);
            binding.layoutTextViewInfo.setVisibility(View.VISIBLE);
            binding.layoutButtonInfo.setVisibility(View.VISIBLE);
        }
    }

    public void updateInfoEnable( boolean b) {
        if (b) {
            binding.layoutTextViewInfo.setVisibility(View.INVISIBLE);
            binding.layoutButtonInfo.setVisibility(View.INVISIBLE);
            binding.layoutUpdateInfo.setVisibility(View.VISIBLE);
            binding.layoutSavaInfo.setVisibility(View.VISIBLE);
            binding.layoutCanelSave.setVisibility(View.VISIBLE);
            binding.layoutUpdateInfo.setEnabled(true);

        } else {
            binding.layoutTextViewInfo.setVisibility(View.VISIBLE);
            binding.layoutButtonInfo.setVisibility(View.VISIBLE);
            binding.layoutUpdateInfo.setVisibility(View.INVISIBLE);
            binding.layoutSavaInfo.setVisibility(View.INVISIBLE);
            binding.layoutCanelSave.setVisibility(View.INVISIBLE);
            binding.layoutUpdateInfo.setEnabled(false);
        }
    }

    public void mapping() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        updateInfo          = viewPager2.findViewById(R.id.updateInfo);
        saveInfo            = viewPager2.findViewById(R.id.saveInfo);
        cancelSave          = viewPager2.findViewById(R.id.cancelSave);
        updateAvatar        = viewPager2.findViewById(R.id.updateImage);
        cancelUpdate        = viewPager2.findViewById(R.id.cancelUpdate);
        avatarImage         = viewPager2.findViewById(R.id.avataImage);
        name                = viewPager2.findViewById(R.id.nameInfo);
        email               = viewPager2.findViewById(R.id.emailInfo);
        date                = viewPager2.findViewById(R.id.dateInfo);
        emailTv             = viewPager2.findViewById(R.id.emailTv);
        nameEt              = viewPager2.findViewById(R.id.updateName);
        dayEt               = viewPager2.findViewById(R.id.updateDayDate);
        monthEt             = viewPager2.findViewById(R.id.updateMonthDate);
        yearEt              = viewPager2.findViewById(R.id.updateYearDate);
    }

    @SuppressLint("SetTextI18n")
    public void setInfo(){
        name    .setText(user.getName());
        email   .setText(user.getEmail());
        date    .setText(user.getDate().toString());
        nameEt  .setText(user.getName());
        emailTv .setText(user.getEmail());
        dayEt   .setText(Integer.toString(user.getDate().getday()));
        monthEt .setText(Integer.toString(user.getDate().getmonth()));
        yearEt  .setText(Integer.toString(user.getDate().getyear()));
    }
    private void setToolbarMenu() {
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.main_menu, menu);
                MenuItem search = menu.findItem(R.id.actionSearch);
                search.setVisible(false);
            }
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent( getActivity(), Activity_Login.class);
                startActivity(intent);
                requireActivity().finish();
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }
    //    update image to storage
    public void updateAvata(StorageReference storageRef, Account userLogin){

        try {
            Calendar calendar = Calendar.getInstance();
            StorageReference mountainsRef = storageRef.child(userLogin.getEmail() + "Time" + calendar.getTimeInMillis() + ".png");

            // Get the data from an ImageView as bytes
            avatarImage.setDrawingCacheEnabled(true);
            avatarImage.buildDrawingCache();
            Bitmap b = ((BitmapDrawable) avatarImage.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();

            //upload Image
            UploadTask uploadTask = mountainsRef.putBytes(data);
            uploadTask.addOnFailureListener(exception -> {
                // Handle unsuccessful uploads
                Toast.makeText(mainScreen, "Cập nhật ảnh đại diện không thành công", Toast.LENGTH_SHORT).show();
            }
            ).addOnSuccessListener(taskSnapshot -> {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                //set Image
                try {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                        String Url = uri.toString();
                        userLogin.setAvataUrl(uri.toString());
                        new fetchImage(Url).start();
                        databaseReference.child("account").child("user" + userLogin.getId()).child("avataUrl").setValue(userLogin.getAvataUrl());

                        for( int i = 0; i < userLogin.getListContact().size(); i++){
                            if( userLogin.getListContact().get(i).getIdUser() != -1) {
                                databaseReference.child("account")
                                        .child("user" + userLogin.getListContact().get(i).getIdUser())
                                        .child("listContact")
                                        .child(Integer.toString(userLogin.getListContact().get(i).getpositon()))
                                        .child("url")
                                        .setValue(userLogin.getAvataUrl());
                            }
                        }
                        Toast.makeText(mainScreen, "Đã cập nhật ảnh đại diện", Toast.LENGTH_SHORT).show();
                    });
                }catch ( Exception e){
                    Toast.makeText( mainScreen, "faild", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(mainScreen, "Vui lòng chọn ảnh đại diện", Toast.LENGTH_SHORT).show();
        }
    }

    //    class to set image from URL
    class fetchImage extends Thread{
        Handler handler = new Handler();
        String URL;
        Bitmap bitmap;

        public fetchImage(String url){
            this.URL = url;
        }

        @Override
        public  void run(){

            InputStream inputStream;
            try {
                inputStream =  new URL(URL).openStream();
                bitmap  = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

            handler.post(() -> {
                try {
                    requireActivity().runOnUiThread(() -> binding.avataImage.setImageBitmap(bitmap));
                } catch ( Exception e){
                    Log.d("FAIL", "FAIL");
                }
            });
        }
    }
}