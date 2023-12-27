package opossum.family.musicbot.telegram.service;


import opossum.family.musicbot.Constants;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

import java.util.Map;

import static opossum.family.musicbot.telegram.service.UserState.*;


public class ResponseHandler {
    private final SilentSender sender;
    private final Map<Long, UserState> chatStates;

    //TODO: add logging here!!!!
    //TODO: add correlationID so that you can track one session from the /start to the /end
    //TODO: add handling of the inputs unknown to the states - like when in type stuff before the /start
    public ResponseHandler(SilentSender sender, DBContext db) {
        this.sender = sender;
        chatStates = db.getMap(Constants.CHAT_STATES);
    }

    /**
     * When the user enters the chat and starts the process we guide him how to use the bot and save his state.
     */
    public void replyToStart(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(Constants.START_TEXT);
        sender.execute(message);
        chatStates.put(chatId, AWAITING_URL);
    }

    public void replyToButtons(long chatId, Message message) {
        if (message.getText().equalsIgnoreCase("/stop")) {
            stopChat(chatId);
        }
        switch (chatStates.get(chatId)) {
            case AWAITING_URL -> replyToURL(chatId, message);
            case FORMAT_SELECTION -> replyToFormatSelection(chatId, message);
            case NEW_OR_BYE_SELECTION -> replyToNewOrByeSelection(chatId, message);
            default -> unexpectedMessage(chatId);
        }
    }

    private void unexpectedMessage(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Unknown command. This will be ignored.");
        sender.execute(sendMessage);
    }

    private void stopChat(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Thank you for a try!\nPress /start to make another conversion");
        chatStates.remove(chatId);
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
        sender.execute(sendMessage);
    }

    private void replyToNewOrByeSelection(long chatId, Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        if ("Convert another".equalsIgnoreCase(message.getText())) {
            sendMessage.setText(Constants.START_TEXT);
            sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
            sender.execute(sendMessage);
            chatStates.put(chatId, AWAITING_URL);
        } else if ("Finish".equalsIgnoreCase(message.getText())) {
            sendMessage.setText("Thank you! Bye!");
            sender.execute(sendMessage);
            stopChat(chatId);
        } else {
            sendMessage.setText("Please select from provided options");
            sendMessage.setReplyMarkup(KeyboardFactory.getNewOrByeKeyboard());
            sender.execute(sendMessage);
        }
    }


    private void replyToFormatSelection(long chatId, Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        if ("Video".equalsIgnoreCase(message.getText())) {
            sendMessage.setText("We don't do video for now.\nCome again later.");
            sendMessage.setReplyMarkup(KeyboardFactory.getVideoOrAudioKeyboard());
            sender.execute(sendMessage);
        } else if ("Audio".equalsIgnoreCase(message.getText())) {
            sendMessage.setText("Processing...");
            sendMessage.setReplyMarkup(KeyboardFactory.getNewOrByeKeyboard());
            sender.execute(sendMessage);
            chatStates.put(chatId, NEW_OR_BYE_SELECTION);
        }
        else {
            sendMessage.setText("We don't produce " + message.getText() + ". Please select from the options below.");
            sendMessage.setReplyMarkup(KeyboardFactory.getVideoOrAudioKeyboard());
            sender.execute(sendMessage);
        }
    }

    private void replyToURL(long chatId, Message incomingMessage) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("I will convert the following URL: " + incomingMessage.getText() + ".\n Which format would you like to have?");
        message.setReplyMarkup(KeyboardFactory.getVideoOrAudioKeyboard());
        sender.execute(message);
        chatStates.put(chatId, FORMAT_SELECTION);
    }

    public boolean userIsActive(Long chatId) {
        return chatStates.containsKey(chatId);
    }
}