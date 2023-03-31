package com.example.nlcs_app;

        import android.util.Log;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.fragment.app.Fragment;
        import androidx.fragment.app.FragmentActivity;
        import androidx.fragment.app.FragmentManager;
        import androidx.fragment.app.FragmentStatePagerAdapter;
        import androidx.lifecycle.Lifecycle;
        import androidx.viewpager2.adapter.FragmentStateAdapter;

        import com.google.android.gms.common.annotation.NonNullApi;

public class Adapter_Pager extends FragmentStateAdapter {

    public Adapter_Pager(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){
            case 0:
                return new Fragment_List_Chat();
            case 1:
                return new Fragment_Contact();
            case 2:
                return new Fragment_Info();
            default:
                return new Fragment_List_Chat();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }


}
