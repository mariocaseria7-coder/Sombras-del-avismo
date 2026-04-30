package com.sombrasdelavismo.util;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.net.URL;

public class SoundManager {
    private static Clip backgroundClip;
    private static float currentVolumeDb = 0.0f; // Por defecto

    public static void playBackgroundMusic(String resourcePath) {
        if (backgroundClip != null && backgroundClip.isRunning()) {
            return; // Ya está sonando
        }
        try {
            URL url = SoundManager.class.getResource(resourcePath);
            if (url == null) {
                System.err.println("No se encontró el audio: " + resourcePath);
                return;
            }
            
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(url);
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(audioStream);
            
            applyVolume(); // Aplicamos el volumen actual
            
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY); // Bucle infinito
            backgroundClip.start();
        } catch (Exception e) {
            System.err.println("Error al reproducir audio: " + e.getMessage());
        }
    }
    
    public static void stopBackgroundMusic() {
        if (backgroundClip != null && backgroundClip.isRunning()) {
            backgroundClip.stop();
        }
    }
    
    public static void setVolume(int volumePercent) {
        // Convierte 0-100 a decibelios
        if (volumePercent <= 0) {
            currentVolumeDb = -80.0f; // Mute efectivo
        } else {
            float linearVol = volumePercent / 100f;
            currentVolumeDb = (float) (Math.log10(linearVol) * 20.0f);
        }
        applyVolume();
    }
    
    private static void applyVolume() {
        if (backgroundClip != null && backgroundClip.isOpen()) {
            try {
                FloatControl gainControl = (FloatControl) backgroundClip.getControl(FloatControl.Type.MASTER_GAIN);
                currentVolumeDb = Math.max(gainControl.getMinimum(), Math.min(gainControl.getMaximum(), currentVolumeDb));
                gainControl.setValue(currentVolumeDb);
            } catch (Exception e) {
                // El control de volumen puede no estar soportado en todos los sistemas
            }
        }
    }
}
