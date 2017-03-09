package com.drediki.wwclient;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Client of Server "Whoere"
 * @author DreDiki
 * @version 1.0
 * @see User
 * @see Position
 *
 *
 * <p>Based On NodeJS and Socket.IO</p>
 */
public class Client {
    public static final String SERVER_DEFAULT = "http://121.250.223.13:233";
//    public static final String SERVER_DEFAULT = "http://drediki.com:2333"; //My Sever
//    public static final String ERROR_COMMON = "Common Error";//-1
//    public static final String ERROR_NOT_CONNECTED = "Error:the client hasn't connected to the server.";//-2
//    public static final int ERROR_ = ;

    public enum ACTION {
        CONNECT, DISCONNECT, LOGIN, LOGOUT, UPDATEPOSITION, BROADCAST ,JOINROOM
    }

    private Gson gson;
    private Socket socket;
    private User self;
    private FeedbackListener feedbackListener;
    private ReceiveListener receiveListener;

    /**
     * Constructor
     * Nothing to say.
     */
    public Client() {
        gson = new Gson();
        feedbackListener = new defaultFeedBackListener();
        receiveListener = new defaultReceiveListener();
    }

    /**
     * Update Position Of Current User
     * @param currentPosition
     */
    public void update(Position currentPosition) {
        if (currentPosition == null) return;
        if (!socket.connected()) {
            feedbackListener.onFailed(ACTION.UPDATEPOSITION, FeedbackListener.RESULT_ERROR_CONNECT);
            return;
        }
        System.out.println("Trying to update");
        socket.emit("updatePosition", gson.toJson(currentPosition));
        return;
    }

    /**
     * Check the connectivity to the server.
     * Notice that it doesn't mean user has login to the server.
     * @return if this client has connected to the serrver.
     */
    private boolean isConnected() {
        return socket.connected();
    }

    /**
     * Empty Method Now
     */
    public void search(String way,String args) {
        return;
    }

    /**
     * Connect to the default server {@link Client#SERVER_DEFAULT}
     * Call connect method before login.
     */
    public void connect() {
        connect(SERVER_DEFAULT);
    }

    /**
     * Connect to the custom server
     * @param serverName custom server address
     */
    public void connect(String serverName) {
        if (socket != null && socket.connected()) {
            feedbackListener.onFailed(ACTION.CONNECT, FeedbackListener.RESULT_ERROR_HASONLINE);
            return;
        }
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
                            Integer code = (Integer) args[1];
                            if (code == 0) {
                                feedbackListener.onSuccess(ACTION.LOGIN, FeedbackListener.RESULT_SUCCESS);
                            } else {
                                feedbackListener.onFailed(ACTION.LOGIN, code);
                            }
                        }
                        break;
                        case "logout": {
                            Integer code = (Integer) args[1];
                            if (code == 0) {
                                feedbackListener.onSuccess(ACTION.LOGOUT, FeedbackListener.RESULT_SUCCESS);
                            } else {
                                feedbackListener.onFailed(ACTION.LOGOUT, code);
                            }
                        }
                        break;
                    }
                }
            });
            socket.on("chat", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    if (args.length < 2) return;
                    String jsonUser = args[0].toString();
                    String message = (String) args[1];
                    User user = gson.fromJson(jsonUser, User.class);
//                    System.out.println(jsonUser + " \nSend message privately to you:" + message);
                    receiveListener.chat(user,message);
                }
            });
            socket.on("broadcast", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    if (args.length < 3) return;
                    String namespace = (String) args[0];
                    String jsonUser = args[1].toString();
                    User user = gson.fromJson(jsonUser, User.class);
                    String message = (String) args[2];
                    receiveListener.broadcast(user, namespace, message);
                }
            });
            socket.on("nearby", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    if (args.length < 2) return;
                    String action = (String) args[0];
                    String jsonUser = args[1].toString();
                    User user = gson.fromJson(jsonUser, User.class);
                    switch (action) {
                        case "find":
                            receiveListener.nearby(user, ReceiveListener.ACTION_FIND);
                            break;
                        case "leave":
                            receiveListener.nearby(user, ReceiveListener.ACTION_LEAVE);
                            break;
                    }
                }
            });
            socket.on("users", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    if (args.length < 2) return;
                    String action = (String) args[0];
                    String jsonUsers = args[1].toString();
                    Type collectionType = new TypeToken<ArrayList<User>>() {}.getType();
                    ArrayList<User> users =gson.fromJson(jsonUsers,collectionType);
                    System.out.println(jsonUsers);
                    receiveListener.users(users,action);
                }
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
            feedbackListener.onFailed(ACTION.CONNECT, FeedbackListener.RESULT_ERROR_EXCEPTION);
        }
    }

    /**
     * Disconnect from the server.
     */
    public void disconnect() {
        socket.disconnect();
    }

    /**
     * Join the talking room by name.
     * If the room doesn't exist,server will create one.
     * The room name cannot be "all",or contains "nearby",
     * which are used as default namespace for broadcast
     * @param roomname
     */
    public void joinRoom(String roomname) {
        if(roomname.equals("all")||roomname.contains("nearby")){
            feedbackListener.onFailed(ACTION.JOINROOM,FeedbackListener.RESULT_ERROR_INFORMATION);
            return;
        }
        socket.emit("room", "join", roomname);
        return;
    }

    /**
     * Leave the custom room {@link Client#joinRoom(String)} before.
     * @param roomname
     */
    public void leaveRoom(String roomname) {
        if(roomname.equals("all")||roomname.contains("nearby")){
            feedbackListener.onFailed(ACTION.JOINROOM,FeedbackListener.RESULT_ERROR_INFORMATION);
            return;
        }
        socket.emit("room", "join", roomname);
        return;
    }

    /**
     * Talk to someone privately
     * @param target who to tell
     * @param message
     */
    public void talk(User target, String message) {
        socket.emit("chat", target.getId(), message);
        return;
    }

    /**
     * broadcast message to nearby users.
     * @param message
     */
    public void broadcastToNearBy(String message) {
        broadcast("nearby",message);
    }

    /**
     * broadcast message to all users online.
     * @param message
     */
    public void broadcastToAll(String message) {
        broadcast("all",message);
    }

    /**
     * broadcast message to the namespace.
     * if you want to talk in room,
     * namespace is the room name you joined.
     * @param namespace
     * @param message message you want to send
     */
    public void broadcast(String namespace, String message) {
        if (message == null || message.isEmpty()||namespace == null || namespace.isEmpty()) {
            feedbackListener.onFailed(ACTION.BROADCAST,FeedbackListener.RESULT_ERROR_INFORMATION);
            return;
        }
        socket.emit("broadcast", namespace, message);
    }


    /**
     * require all nearby users' information
     * information will send back to {@link ReceiveListener} if succeed.
     */
    public void getNearbyUsers() {
        socket.emit("users","nearby");
    }

    /**
     * require all users' information
     * information will send back to {@link ReceiveListener} if succeed.
     */
    public void getAllUsers(){
        socket.emit("users","all");
    }


    /**
     * call this method after connected to the server.
     * @see User
     * @param user
     */
    public void login(User user) {
        if (user == null) return;
        if (socket == null || !socket.connected()) {
            feedbackListener.onFailed(ACTION.LOGIN, FeedbackListener.RESULT_ERROR_CONNECT);
            return;
        }
        socket.emit("login", gson.toJson(user));
        self = user;
        self.setId(socket.id());
    }

    /**
     * logout,
     * nothing to say.
     */
    public void logout() {
        if (self == null) return;
        if (socket == null || !socket.connected()) {
            feedbackListener.onFailed(ACTION.LOGOUT, FeedbackListener.RESULT_ERROR_CONNECT);
            return;
        }
        socket.emit("logout", gson.toJson(self));
        self = null;
    }

    /**
     * Delete the user account from the server.
     * If the user is login as a guest,you don't have to do this.
     * And the account must login first.
     */
    public void deleteAccount() {
        if (self == null) return;
        if (socket == null || !socket.connected()) {
            feedbackListener.onFailed(ACTION.LOGOUT, FeedbackListener.RESULT_ERROR_CONNECT);
            return;
        }
        socket.emit("delete", gson.toJson(self));
        self = null;
    }

    /**
     * Unrecommended.
     * Don't use it unless you know what you are doing.
     * @return {@link io.socket.client.Socket} Used by the client.
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * After sending request to the server,
     * this listener will tell you the result.
     * listener shouldn't be null.
     * @see Client.FeedbackListener
     * @param feedbackListener
     */
    public void setFeedbackListener(FeedbackListener feedbackListener) {
        if (feedbackListener != null)
            this.feedbackListener = feedbackListener;
    }

    /**
     * After sending request to the server,
     * this listener will give you the data.
     * listener shouldn't be null.
     * @see Client.ReceiveListener
     * @param receiveListener
     */
    public void setReceiveListener(ReceiveListener receiveListener) {
        if (receiveListener != null)
            this.receiveListener = receiveListener;
    }

    /**
     * FeedbackListener
     * @author DreDiki
     * @see Client.ACTION
     */
    public interface FeedbackListener {
        int RESULT_SUCCESS = 0;
        int RESULT_ERROR_UNKNOWN = -1;
        int RESULT_ERROR_EXCEPTION = -2;
        int RESULT_ERROR_CONNECT = -3;
        int RESULT_ERROR_AUTHENTICATION = -4;
        int RESULT_ERROR_HASONLINE = -5;
        int RESULT_ERROR_OPERATION_TOO_FREQUENT = -6;
        int RESULT_ERROR_INFORMATION = -7;

        /**
         * Operation succeed.
         * @param action
         * @param resultCode
         */
        void onSuccess(ACTION action, int resultCode);

        /**
         * Operation failed.
         * @param action
         * @param resultCode
         */
        void onFailed(ACTION action, int resultCode);
    }

    /**
     * ReceiveListener
     */
    public interface ReceiveListener {
        int ACTION_LEAVE = -1;
        int ACTION_FIND = 1;

        /**
         * @param from user who send message
         * @param namespace "all" means broadcast to all people."nearby" means broadcast sent from nearby person
         * @param message
         */
        void broadcast(User from, String namespace, String message);

        /**
         *
         * @param from user who send message to you privately
         * @param message
         */
        void chat(User from, String message);

        /**
         * action is either {@link #ACTION_LEAVE} or  {@link #ACTION_FIND}
         * @param from
         * @param action
         */
        void nearby(User from, int action);

        /**
         * after called {@link Client#getNearbyUsers()}  {@link Client#getAllUsers()}
         *  return users' information from server if succeed,
         * @param users may contains null object,please take care.
         * @param message
         */
        void users(ArrayList<User> users,String message);
    }

    /**
     * defaultReceiveListener
     * @see com.drediki.wwclient.Client.ReceiveListener
     */
    private class defaultReceiveListener implements ReceiveListener {

        @Override
        public void broadcast(User from, String namespace, String message) {
            System.out.println(from.getName() + " Send message in"+namespace+": " + message);
        }

        @Override
        public void chat(User from, String message) {
            System.out.println(from.getName() + " Send message privately:" + message);
        }

        @Override
        public void nearby(User from, int action) {
            System.out.println(from.getName() + "Actioned:" + action);
        }

        @Override
        public void users(ArrayList<User> users, String message) {
            if(users==null||users.isEmpty())return;
            for(User user:users){
                if(user==null)continue;
                System.out.println("Get Information Of:" + user.getName());
            }
        }
    }

    /**
     * defaultFeedBackListener
     * @see com.drediki.wwclient.Client.FeedbackListener
     */
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
