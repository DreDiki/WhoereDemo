package com.drediki.wwclient;

import com.google.gson.Gson;
import com.sun.istack.internal.NotNull;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class Client {
    public static final String SERVER_DEFAULT = "http://121.250.223.13:233";
    //    public static final String SERVER_DEFAULT = "http://drediki.com:2333"; //My Sever
    public static final String ERROR_COMMON = "Common Error";//-1
    public static final String ERROR_NOT_CONNECTED = "Error:the client hasn't connected to the server.";//-2
//    public static final int ERROR_ = ;

    public enum ACTION {
        CONNECT, DISCONNECT, LOGIN, LOGOUT
    }

    private Gson gson;
    private Socket socket;
    private User self;
    private ErrorOccursListener errorOccursListener;
    private FeedbackListener feedbackListener;

    public Client() {
        gson = new Gson();
        errorOccursListener = new defaultErrorListener();
        feedbackListener = new defaultFeedBackListener();
    }

    public boolean update(Position currentPosition) {
        if (currentPosition == null) return false;
        if (!socket.connected()) {
            errorOccursListener.error(-2, ERROR_NOT_CONNECTED);
            return false;
        }
        System.out.println("Trying to update");
        socket.emit("updatePosition", gson.toJson(currentPosition));
        return true;
    }

    public boolean joinRoom(String roomname) {

        return true;
    }

    public boolean search() {

        return true;
    }

    public void connect() {
        connect(SERVER_DEFAULT);
    }

    public void connect(String serverName) {
        try {
            System.out.println("Trying to load");
            socket = IO.socket(serverName);
            socket.connect();
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
//                    socket.emit()
                    feedbackListener.onSuccess(ACTION.CONNECT, FeedbackListener.RESULT_SUCCESS);
                }
            });
            socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    feedbackListener.onSuccess(ACTION.DISCONNECT, FeedbackListener.RESULT_SUCCESS);
                }
            });
            socket.on("feedback", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    if (args == null || args.length < 1) return;
                    String s = (String) args[0];
                    switch (s) {
                        case "login": {
                            Integer code = (Integer)args[1];
                            if(code==0){
                                feedbackListener.onSuccess(ACTION.LOGIN,FeedbackListener.RESULT_SUCCESS);
                            }else {

                            }
                        }
                        break;
                        case "logout":

                            break;
                    }
                }
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
            feedbackListener.onFailed(ACTION.CONNECT, FeedbackListener.RESULT_ERROR_EXCEPTION);
        }
    }

    public boolean disconnect() {
        socket.disconnect();
        return true;
    }

    public boolean talk(User target, String message) {

        return true;
    }

    public void login(User user) {
        if (user == null) return;
        if (!socket.connected()) {
            errorOccursListener.error(-2, ERROR_NOT_CONNECTED);
            return;
        }
        socket.emit("login", gson.toJson(user));
        self = user;
        self.setId(socket.id());
    }


    public Socket getSocket() {
        return socket;
    }

    public void setErrorOccursListener(ErrorOccursListener errorOccursListener) {
        if (errorOccursListener != null)
            this.errorOccursListener = errorOccursListener;
    }

    public void setFeedbackListener(FeedbackListener feedbackListener) {
        if (feedbackListener != null)
            this.feedbackListener = feedbackListener;
    }

    public interface FeedbackListener {
        int RESULT_SUCCESS = 0;
        int RESULT_ERROR_UNKNOWN = -1;
        int RESULT_ERROR_EXCEPTION = -2;

        void onSuccess(ACTION action, int resultCode);

        void onFailed(ACTION action, int resultCode);
    }

    public interface ReceiveListener {

    }

    public interface ErrorOccursListener {
        void error(int code, String description);//todo return boolean: handled?
    }

    private class defaultErrorListener implements ErrorOccursListener {
        @Override
        public void error(int code, String description) {
            System.out.println("Error Occurs:" + description + ";Error Code:" + code);
        }
    }

    private class defaultFeedBackListener implements FeedbackListener {

        @Override
        public void onSuccess(ACTION action, int resultCode) {
            System.out.println(action + " Success,code:" + resultCode);
        }

        @Override
        public void onFailed(ACTION action, int resultCode) {
            System.out.println(action + " Error,code:" + resultCode);
        }
    }
}
