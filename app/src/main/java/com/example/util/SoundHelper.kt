package com.example.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

object SoundHelper {
    private const val SAMPLE_RATE = 44100
    private val lock = Any()

    private var malaTrack: AudioTrack? = null
    private var confirmTrack: AudioTrack? = null

    private val malaChimePcm: ShortArray by lazy {
        generateHarmonicChime(
            baseFreq = 528.0, // Solfeggio 528Hz peaceful resonance
            durationMs = 650,
            decayRate = 4.5
        )
    }

    private val confirmClickPcm: ShortArray by lazy {
        generateHarmonicChime(
            baseFreq = 660.0,
            durationMs = 120,
            decayRate = 18.0
        )
    }

    private fun generateHarmonicChime(
        baseFreq: Double,
        durationMs: Int,
        decayRate: Double
    ): ShortArray {
        val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val progress = i.toDouble() / numSamples
            val envelope = exp(-decayRate * progress)

            // Pure harmonic blend: fundamental + overtone 1 + overtone 2
            val fundamental = sin(2.0 * PI * baseFreq * t) * 0.65
            val harmonic1 = sin(2.0 * PI * (baseFreq * 2.0) * t) * 0.25
            val harmonic2 = sin(2.0 * PI * (baseFreq * 3.0) * t) * 0.10

            val sample = (fundamental + harmonic1 + harmonic2) * envelope * 32767.0 * 0.85
            buffer[i] = sample.coerceIn(-32768.0, 32767.0).toInt().toShort()
        }
        return buffer
    }

    private fun createStaticTrack(pcmData: ShortArray): AudioTrack? {
        return try {
            val bufferSize = pcmData.size * 2
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val audioFormat = AudioFormat.Builder()
                .setSampleRate(SAMPLE_RATE)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build()

            val track = AudioTrack.Builder()
                .setAudioAttributes(audioAttributes)
                .setAudioFormat(audioFormat)
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(pcmData, 0, pcmData.size)
            track
        } catch (e: Exception) {
            Log.w("SoundHelper", "Failed to create static AudioTrack: ${e.message}")
            null
        }
    }

    fun playMalaCompleteTone() {
        synchronized(lock) {
            try {
                if (malaTrack == null || malaTrack?.state != AudioTrack.STATE_INITIALIZED) {
                    malaTrack?.release()
                    malaTrack = createStaticTrack(malaChimePcm)
                }
                malaTrack?.let { track ->
                    if (track.playState == AudioTrack.PLAYSTATE_PLAYING) {
                        track.stop()
                    }
                    track.reloadStaticData()
                    track.play()
                }
            } catch (e: Exception) {
                Log.w("SoundHelper", "Error playing mala completion chime: ${e.message}")
            }
        }
    }

    fun playConfirmationTone() {
        synchronized(lock) {
            try {
                if (confirmTrack == null || confirmTrack?.state != AudioTrack.STATE_INITIALIZED) {
                    confirmTrack?.release()
                    confirmTrack = createStaticTrack(confirmClickPcm)
                }
                confirmTrack?.let { track ->
                    if (track.playState == AudioTrack.PLAYSTATE_PLAYING) {
                        track.stop()
                    }
                    track.reloadStaticData()
                    track.play()
                }
            } catch (e: Exception) {
                Log.w("SoundHelper", "Error playing confirmation tone: ${e.message}")
            }
        }
    }

    fun release() {
        synchronized(lock) {
            try {
                malaTrack?.stop()
                malaTrack?.release()
                malaTrack = null
            } catch (_: Exception) {}

            try {
                confirmTrack?.stop()
                confirmTrack?.release()
                confirmTrack = null
            } catch (_: Exception) {}
        }
    }
}
