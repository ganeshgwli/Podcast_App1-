package com.projectemplate.musicpro.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.projectemplate.musicpro.BaseFragment;
import com.projectemplate.musicpro.activity.SongListActivity;
import com.projectemplate.musicpro.config.GlobalValue;

import java.io.File;

import e.aakriti.work.imageloader.ImageLoader;
import e.aakriti.work.podcast_app.R;

public class PlayerFragment extends BaseFragment implements OnClickListener {
	public static final int LIST_PLAYING = 0;
	public static final int THUMB_PLAYING = 1;

	private ImageView btnBackward, btnPlay, btnForward;
	private ToggleButton btnShuffle, btnRepeat;
	private ViewPager viewPager;
	private SeekBar seekBarLength;
	private TextView lblTimeCurrent, lblTimeLength;
	private View viewIndicatorList, viewIndicatorThumb;

	private String rootFolder;

	public PlayerListPlayingFragment playerListPlayingFragment;
	public PlayerThumbFragment playerThumbFragment;
	private ImageView player_thumb;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_player, container, false);
		try {
			initUIBase(view);
			initControl(view);
		} catch (Exception e) {
			e.printStackTrace();
			SongListActivity.cancelNotification(getActivity());
		}
		return view;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
//			SongListActivity.menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
			switch (SongListActivity.toMusicPlayer) {
				case SongListActivity.FROM_LIST_SONG:
				case SongListActivity.FROM_SEARCH:
					if ( GlobalValue.listSongPlay != null) {
						SongListActivity.mService.setListSongs(GlobalValue.listSongPlay);}
					try {

						setCurrentSong(GlobalValue.currentSongPlay);
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					try {

						playerListPlayingFragment.refreshListPlaying();
						playerThumbFragment.refreshData();
					} catch (Exception e) {
						e.printStackTrace();
					}
					setSelectTab(THUMB_PLAYING);
					viewPager.setCurrentItem(THUMB_PLAYING);
					SongListActivity.setButtonPlay();
					break;

				case SongListActivity.FROM_NOTICATION:
					try {

						playerListPlayingFragment.refreshListPlaying();
						playerThumbFragment.refreshData();
					} catch (Exception e) {
						SongListActivity.cancelNotification(getActivity());e.printStackTrace();
					}
					break;

				case SongListActivity.FROM_OTHER:
					break;
			}
		}
	}

	@Override
	protected void initUIBase(View view) {
		btnBackward = (ImageView) view.findViewById(R.id.btnBackward);
		btnPlay = (ImageView) view.findViewById(R.id.btnPlay);
		btnForward = (ImageView) view.findViewById(R.id.btnForward);
		btnShuffle = (ToggleButton) view.findViewById(R.id.btnShuffle);
		btnRepeat = (ToggleButton) view.findViewById(R.id.btnRepeat);
		seekBarLength = (SeekBar) view.findViewById(R.id.seekBarLength);
		lblTimeCurrent = (TextView) view.findViewById(R.id.lblTimeCurrent);
		lblTimeLength = (TextView) view.findViewById(R.id.lblTimeLength);
		viewPager = (ViewPager) view.findViewById(R.id.viewPager);
		viewIndicatorList = view.findViewById(R.id.viewIndicatorList);
		viewIndicatorThumb = view.findViewById(R.id.viewIndicatorThumb);
		player_thumb = (ImageView)view.findViewById(R.id.player_thumb);



	}

	private void initControl(View view) {
		rootFolder = Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name) + "/";
		File folder = new File(rootFolder);
		if (!folder.exists()) {
			folder.mkdirs();
		}

		btnShuffle.setOnClickListener(this);
		btnBackward.setOnClickListener(this);
		btnPlay.setOnClickListener(this);
		btnForward.setOnClickListener(this);
		btnRepeat.setOnClickListener(this);

		// songAdapter = new SongPlayingAdapter(getActivity(),
		// GlobalValue.listSongPlay);
		// lsvSong.setAdapter(songAdapter);
		// lsvSong.setOnItemClickListener(new OnItemClickListener() {
		// @Override
		// public void onItemClick(AdapterView<?> av, View v, int position, long
		// l) {
		// if (position != GlobalValue.currentSongPlay) {
		// setCurrentSong(position);
		// }
		// }
		// });

		seekBarLength.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				SongListActivity.mService.seekTo(seekBar.getProgress());
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			}
		});

		playerListPlayingFragment = new PlayerListPlayingFragment();
		playerThumbFragment = new PlayerThumbFragment();

		viewPager.setAdapter(new MyFragmentPagerAdapter(getFragmentManager()));
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				setSelectTab(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		playerListPlayingFragment.refreshListPlaying();
		playerThumbFragment.refreshData();

		setSelectTab(THUMB_PLAYING);
		viewPager.setCurrentItem(THUMB_PLAYING);
		SongListActivity.setButtonPlay();
	}

	private void setSelectTab(int tab) {
		if (tab == LIST_PLAYING) {
			viewIndicatorList.setBackgroundResource(R.drawable.indicator_cyan);
			viewIndicatorThumb.setBackgroundResource(R.drawable.indicator_white);
		} else {
			viewIndicatorList.setBackgroundResource(R.drawable.indicator_white);
			viewIndicatorThumb.setBackgroundResource(R.drawable.indicator_cyan);
		}
	}

	private void setCurrentSong(int position) {
		playerListPlayingFragment.refreshListPlaying();
		playerThumbFragment.refreshData();
		SongListActivity.mService.startMusic(position);
	}

	public void seekChanged(String lengthTime, String currentTime, int progress) {
		lblTimeLength.setText(lengthTime);
		lblTimeCurrent.setText(currentTime);
		seekBarLength.setProgress(progress);
	}

	public void changeSong(int indexSong) {
		lblTimeCurrent.setText(getString(R.string.timeStart));
		lblTimeLength.setText(SongListActivity.mService.getLengSong());
		playerListPlayingFragment.refreshListPlaying();
		playerThumbFragment.refreshData();
		new ImageLoader(getActivity()).DisplayImage(GlobalValue.getCurrentSong().image, player_thumb);
	}

	public void setButtonPlay() {
		if (SongListActivity.mService.isPause()) {
			btnPlay.setImageResource(R.drawable.play);
		} else {
			btnPlay.setImageResource(R.drawable.pause);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btnShuffle:
				onClickShuffle();
				break;

			case R.id.btnBackward:
				onClickBackward();
				break;

			case R.id.btnPlay:
				onClickPlay();
				break;

			case R.id.btnForward:
				onClickForward();
				break;

			case R.id.btnRepeat:
				onClickRepeat();
				break;
		}
	}

	private void onClickShuffle() {
		SongListActivity.mService.setShuffle(btnShuffle.isChecked());
	}

	private void onClickBackward() {
		SongListActivity.mService.backSongByOnClick();
	}

	private void onClickPlay() {
		SongListActivity.mService.playOrPauseMusic();
		SongListActivity.setButtonPlay();
	}

	private void onClickForward() {
		SongListActivity.mService.nextSongByOnClick();
	}

	private void onClickRepeat() {
		SongListActivity.mService.setRepeat(btnRepeat.isChecked());
		if (SongListActivity.mService.isRepeat()) {
			showToast(R.string.enableRepeat);
		} else {
			showToast(R.string.offRepeat);
		}
	}

	private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
		public MyFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 0) {
				return playerListPlayingFragment;
			}
			return playerThumbFragment;
		}

		@Override
		public int getCount() {
			return 2;
		}
	}
}
