package opossum.family.musicbot.telegram.service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

public class KeyboardFactory {

    public static ReplyKeyboard getVideoOrAudioKeyboard(){
        KeyboardRow row = new KeyboardRow();
        row.add("Video");
        row.add("Audio");
        return new ReplyKeyboardMarkup(List.of(row));
    }

    public static ReplyKeyboard getNewOrByeKeyboard(){
        KeyboardRow row = new KeyboardRow();
        row.add("Convert another");
        row.add("Finish");
        return new ReplyKeyboardMarkup(List.of(row));
    }

}