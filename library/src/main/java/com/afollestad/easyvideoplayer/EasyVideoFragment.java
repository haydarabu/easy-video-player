package com.afollestad.easyvideoplayer;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


public class EasyVideoFragment extends Fragment implements FullscreenCallback {

    private static final String TAG = "EasyVideoFragment";
    private EasyVideoCallback callback;
    private EasyVideoPlayer easyVideoPlayer;
    private boolean hasPlayer = false;
    private boolean fullscreen;


    public static EasyVideoFragment newInstance(boolean fullscreen, Uri source) {
        EasyVideoFragment fragment = new EasyVideoFragment();
        Bundle args = new Bundle();
        args.putBoolean("fullscreen", fullscreen);
        args.putString("source", source.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        boolean player = true;
        if (savedInstanceState != null) {
            player = savedInstanceState.getBoolean("player", true);
        }
        Bundle args = getArguments();
        fullscreen = args.getBoolean("fullscreen", false);
        FrameLayout view;
        if (easyVideoPlayer == null && player) {
            view = (FrameLayout) inflater.inflate(R.layout.fragment_player, container, false);
            easyVideoPlayer = (EasyVideoPlayer) view.findViewById(R.id.player);

            //easyVideoPlayer = new EasyVideoPlayer(getContext(), attributeSet);
            //easyVideoPlayer.setId(View.generateViewId());
            //view.addView(easyVideoPlayer);
            //easyVideoPlayer.setSource(Uri.parse(args.getString("source")));
            if (callback != null)
                callback.onCreatedView(easyVideoPlayer);
        } else {
            view = (FrameLayout) inflater.inflate(R.layout.fragment_empty, container, false);
            if (easyVideoPlayer != null) {
                ((ViewGroup) easyVideoPlayer.getParent()).removeView(easyVideoPlayer);
                view.addView(easyVideoPlayer);
            }
        }

        if (easyVideoPlayer != null) {
            easyVideoPlayer.setFullScreenCallback(this);
            easyVideoPlayer.setVideoOnly(fullscreen);
            hasPlayer = true;
        }

        return view;
    }

    public void setCallback(EasyVideoCallback callback) {
        this.callback = callback;
    }

    public EasyVideoPlayer getPlayer() {
        return easyVideoPlayer;
    }

    public void setPlayer(EasyVideoPlayer player) {
        this.easyVideoPlayer = player;
        if (easyVideoPlayer != null && getView() != null) {
            ((ViewGroup) easyVideoPlayer.getParent()).removeView(easyVideoPlayer);
            ((FrameLayout) getView()).addView(easyVideoPlayer);
            easyVideoPlayer.setFullScreenCallback(this);
            hasPlayer = true;
        } else {
            hasPlayer = false;
        }
    }

    @Override
    public void onFullScreen(EasyVideoPlayer player) {
        if (callback != null) {
            callback.onFullScreen(player);
        }
    }

    @Override
    public void onFullScreenExit(EasyVideoPlayer player) {
        if (callback != null) {
            callback.onFullScreenExit(player);
        }
        hasPlayer = false;
    }


    @Override
    public void onDetach() {
        if (!hasPlayer && easyVideoPlayer != null) {
            easyVideoPlayer.detach();
        } else if (easyVideoPlayer != null) {
            easyVideoPlayer.setVideoOnly(false);
            callback.onFullScreenExit(easyVideoPlayer);
        }
        super.onDetach();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("player", easyVideoPlayer != null);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (hasPlayer && easyVideoPlayer != null) {
            easyVideoPlayer.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (hasPlayer && easyVideoPlayer != null) {
            easyVideoPlayer.onResume();
        }
    }

    public void onAttach() {
        if (hasPlayer && easyVideoPlayer != null) {
            easyVideoPlayer.attach();
        }
    }

}
