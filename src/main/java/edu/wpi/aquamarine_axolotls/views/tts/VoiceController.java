package edu.wpi.aquamarine_axolotls.views.tts;

import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.Voice;

public class VoiceController {
    private String name;
    private com.sun.speech.freetts.Voice voice;
    private com.sun.speech.freetts.Gender gender;
    static Thread newThread = new Thread();

    public VoiceController(String name){
        this.name = name;
        this.voice = VoiceManager.getInstance().getVoice(this.name);
        this.voice.setRate(117.0f);
        this.voice.setStyle("casual");
        //this.voice.setPitch(16000);
        this.voice.setDetailedMetrics(true);
        this.voice.allocate();
    }

    public String getTextOptimization(String textToOptimize){
        int dotIndex = textToOptimize.indexOf(".");
        String subString = "";

        String[] parts = textToOptimize.split("\\.");

        if(textToOptimize.contains("You have arrived at your destination")){
            return parts[1];
        }
        else{
            return "Please" + parts[1];
        }
    }

    public void say(String thingsToSay,Thread aThread) throws InterruptedException {
        this.voice = VoiceManager.getInstance().getVoice(this.name);
        this.voice.setRate(117.0f);
        this.voice.setStyle("casual");
        //this.voice.setPitch(16000);
        this.voice.setDetailedMetrics(true);
        this.voice.allocate();
        aThread = new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.voice.speak(thingsToSay);
        });
        aThread.start();
    }

    public void stop(){
        this.voice.deallocate();
    }
}
