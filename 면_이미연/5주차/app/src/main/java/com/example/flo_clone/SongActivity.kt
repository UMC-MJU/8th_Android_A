package com.example.flo_clone

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo_clone.databinding.ActivitySongBinding
import com.google.gson.Gson

class SongActivity : AppCompatActivity() {

    lateinit var binding: ActivitySongBinding
    lateinit var timer: Timer
    private var mediaPlayer: MediaPlayer? = null
    private var gson: Gson = Gson()

    val songs = arrayListOf<Song>()
    lateinit var songDB: SongDatabase
    var nowPos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initPlayList()
        initSong()
        initClickListener()
        initSeekBarListener()
    }

    private fun initPlayList() {
        songDB = SongDatabase.getInstance(this)!!
        songs.addAll(songDB.songDao().getSongs())
    }

    private fun initSong() {
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", 0)
        nowPos = getPlayingSongPosition(songId)

        Log.d("now Song ID", songs[nowPos].id.toString())

        setPlayer(songs[nowPos])
        startTimer()
    }

    private fun getPlayingSongPosition(songId: Int): Int {
        for (i in songs.indices) {
            if (songs[i].id == songId) return i
        }
        return 0
    }

    private fun moveSong(direct: Int) {
        if (nowPos + direct < 0) {
            Toast.makeText(this, "첫 번째 곡입니다.", Toast.LENGTH_SHORT).show()
            return
        }
        if (nowPos + direct >= songs.size) {
            Toast.makeText(this, "마지막 곡입니다.", Toast.LENGTH_SHORT).show()
            return
        }

        nowPos += direct

        timer.interrupt()
        mediaPlayer?.release()
        mediaPlayer = null

        setPlayer(songs[nowPos])
        startTimer()
    }

    private fun replayCurrentSong() {
        timer.interrupt()
        mediaPlayer?.release()
        mediaPlayer = null

        songs[nowPos].second = 0
        setPlayer(songs[nowPos])
        startTimer()
    }

    private fun setPlayer(song: Song) {
        binding.songMusicTitleTv.text = song.title
        binding.songSingerNameTv.text = song.singer
        binding.songStartTimeTv.text = String.format("%02d:%02d", song.second / 60, song.second % 60)
        binding.songEndTimeTv.text = String.format("%02d:%02d", song.playTime / 60, song.playTime % 60)
        binding.songAlbumIv.setImageResource(song.coverImg!!)
        binding.songProgressSb.max = song.playTime * 1000
        binding.songProgressSb.progress = song.second * 1000

        val music = resources.getIdentifier(song.music, "raw", this.packageName)
        mediaPlayer = MediaPlayer.create(this, music)
        mediaPlayer?.seekTo(song.second * 1000)

        setPlayerStatus(song.isPlaying)
    }

    private fun setPlayerStatus(isPlaying: Boolean) {
        songs[nowPos].isPlaying = isPlaying
        if (::timer.isInitialized) {
            timer.isPlaying = isPlaying
        }

        if (isPlaying) {
            binding.songMiniplayerIv.visibility = View.GONE
            binding.songPauseIv.visibility = View.VISIBLE
            mediaPlayer?.start()
        } else {
            binding.songMiniplayerIv.visibility = View.VISIBLE
            binding.songPauseIv.visibility = View.GONE
            mediaPlayer?.pause()
        }
    }

    private fun initClickListener() {
        binding.songDownIb.setOnClickListener {
            finish()
        }

        binding.songMiniplayerIv.setOnClickListener {
            setPlayerStatus(true)
        }

        binding.songPauseIv.setOnClickListener {
            setPlayerStatus(false)
        }

        binding.songNextIv.setOnClickListener {
            moveSong(+1)
        }

        binding.songPreviousIv.setOnClickListener {
            replayCurrentSong()
        }
    }

    private fun initSeekBarListener() {
        binding.songProgressSb.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val newMillis = progress
                    val newSecond = newMillis / 1000
                    songs[nowPos].second = newSecond
                    mediaPlayer?.seekTo(newMillis)

                    binding.songStartTimeTv.text = String.format("%02d:%02d", newSecond / 60, newSecond % 60)

                    // SeekBar 직접 조작 시에도 SharedPreferences에 반영
                    val spf = getSharedPreferences("song", MODE_PRIVATE)
                    spf.edit().putInt("second", newSecond).apply()
                }
            }

            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })
    }

    private fun startTimer() {
        timer = Timer(songs[nowPos].playTime, songs[nowPos].isPlaying)
        timer.start()
    }

    inner class Timer(private val playTime: Int, var isPlaying: Boolean = true) : Thread() {
        private var mills: Float = (songs[nowPos].second * 1000).toFloat()

        override fun run() {
            super.run()
            try {
                while (songs[nowPos].second < playTime) {
                    if (isPlaying) {
                        sleep(50)
                        mills += 50

                        runOnUiThread {
                            binding.songProgressSb.progress = mills.toInt()
                        }

                        if (mills % 1000 == 0f) {
                            songs[nowPos].second++

                            // 매초마다 재생 시간 SharedPreferences에 저장
                            val spf = getSharedPreferences("song", MODE_PRIVATE)
                            spf.edit().putInt("second", songs[nowPos].second).apply()

                            runOnUiThread {
                                binding.songStartTimeTv.text = String.format(
                                    "%02d:%02d",
                                    songs[nowPos].second / 60,
                                    songs[nowPos].second % 60
                                )
                            }
                        }
                    }
                }
            } catch (e: InterruptedException) {
                Log.d("Song", "쓰레드 종료됨: ${e.message}")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        setPlayerStatus(false)

        songs[nowPos].second = binding.songProgressSb.progress / 1000

        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("songId", songs[nowPos].id)
        editor.putInt("second", songs[nowPos].second)
        editor.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.interrupt()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
