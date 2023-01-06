package com.example.remoteimage;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.remoteimage.databinding.FragmentSecondBinding;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context c = getContext();
        Drawable broken = null;
        if(c != null)
            broken = getDrawable(getContext(),R.drawable.broken);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String url = bundle.getString("url", "");
            binding.textviewSecond.setText(url);
            RemoteImage r = new RemoteImage(binding.progress,binding.imageView,broken);
            r.execute(url);
        }
        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        ActionBar supportActionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
//        if (supportActionBar != null)
//            supportActionBar.hide();
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        ActionBar supportActionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
//        if (supportActionBar != null)
//            supportActionBar.show();
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}