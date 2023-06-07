package edu.hitsz.server;

import edu.hitsz.server.dao.HistoryRecord;
import edu.hitsz.server.dao.HistoryRecordDAO;
import edu.hitsz.server.dao.UserDAO;
import edu.hitsz.server.dao.UserInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.*;
import java.util.concurrent.*;

public class MyServer {

    private final ExecutorService threadPool;

    private final UserDAO userDAO;
    private final HistoryRecordDAO historyDAO;

    private final List<List<Socket>> waitQueue;

    private final Map<Socket, Socket> matchTable;

    private final Map<Socket, String> socketNameTable;

    private final Map<Socket, HistoryRecord> socketHistoryTable;

    private final Map<Socket, DataInputStream> socketInTable;
    private final Map<Socket, DataOutputStream> socketOutMap;

    /**
     * 状态: true: 存活; false: 结束
     */
    private final Map<Socket, Boolean> playerState;

    public static void main(String args[]){
        new MyServer();
    }


    public MyServer() {
        threadPool = Executors.newCachedThreadPool();
        socketInTable = new HashMap<>();
        socketOutMap = new HashMap<>();
        socketHistoryTable = new HashMap<>();
        socketNameTable = new HashMap<>();
        playerState = new HashMap<>();
        matchTable = new HashMap<>();
        waitQueue = new LinkedList<>();
        waitQueue.add(new LinkedList<>());
        waitQueue.add(new LinkedList<>());
        waitQueue.add(new LinkedList<>());

        userDAO = new UserDAO("users");
        historyDAO = new HistoryRecordDAO("history");
        try {
            InetAddress addr = InetAddress.getLocalHost();
            System.out.println("local host:" + addr);

            //server socket
            ServerSocket serverSocket = new ServerSocket(9999);
            System.out.println("listen port 9999");

            while (true) {
                System.out.println("waiting client connect");
                Socket socket = serverSocket.accept();
                System.out.println("accept client connect: " + socket);

                threadPool.execute(getTask(socket));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private DataInputStream getInputStream(Socket socket) throws IOException {
        return new DataInputStream(new BufferedInputStream(socket.getInputStream()));
    }

    private DataOutputStream getOutputStream(Socket socket) throws IOException {
        return new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    private Runnable getTask(Socket socket){
        return () -> {
            try {
                DataInputStream in = getInputStream(socket);
                DataOutputStream out = getOutputStream(socket);
                socketInTable.put(socket, in);
                socketOutMap.put(socket, out);
                boolean isLoop = true;
                while (isLoop) {
                    String content;
                    try {
                        content = in.readUTF();
                    }catch (EOFException e) {
                        break;
                    }

                    JSONObject json = new JSONObject(content);
                    System.out.println(json);
//                    System.out.println("NAME: "+json.getString("name"));
//                    System.out.println("OP: "+json.getString("op"));
                    int op = json.getInt("op");
                    boolean isSuccess;


                    switch (op) {
                        case 0: // 注册
                            isSuccess = register(json);
                            System.out.println(isSuccess);
                            out.writeUTF(getBooleanJson("isSuccess", isSuccess).toString());
                            out.flush();
                            break;
                        case 1: // 登录
                            isSuccess = login(json);
                            out.writeUTF(getBooleanJson("isSuccess", isSuccess).toString());
                            out.flush();
                            break;
                        case 2:
                            isLoop = false;
                            out.writeUTF(getBooleanJson("isExist", true).toString());
                            out.flush();
                            break;
                        case 3:
                            playRequest(json, socket, in, out);
                            return;
                        default:
                            break;
                    }
                    break;
                }

                System.out.println("close socket: "+socket);

                socket.close();
            }catch (JSONException | IOException | InterruptedException e) {
                e.printStackTrace();
            }

        };
    }

    private boolean register(JSONObject json) throws JSONException {
        System.out.println("register");
        String usrname = json.getString("name");
        String password = json.getString("password");

        try {
            userDAO.insert(usrname, password, 0);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
        return true;
    }


    private boolean login(JSONObject json) throws JSONException {
        System.out.println("login");
        String usrname = json.getString("name");
        String password = json.getString("password");
        UserInfo userInfo = userDAO.selectName(usrname);
        return userInfo != null && userInfo.password.equals(password);
    }

    private boolean playRequest(JSONObject json, Socket socket,
                                DataInputStream in, DataOutputStream out
                                ) throws JSONException, InterruptedException, IOException {
        System.out.println("play");
        int gameType = json.getInt("gameType");
        // 0 / 1 / 2
        if (gameType<0 || gameType >2) {
            return false;
        }
        socketNameTable.put(socket, json.getString("name"));

        boolean isWait = false;

        List<Socket> gameTypeList = waitQueue.get(gameType);
        synchronized (gameTypeList) {
            gameTypeList.add(socket);
            if (gameTypeList.size()==2) {
//                play(gameTypeList.get(0), gameTypeList.get(1));
                Socket s1 = gameTypeList.get(0);
                Socket s2 = gameTypeList.get(1);
                synchronized (matchTable){
                    matchTable.put(s1, s2);
                    matchTable.put(s2, s1);
                }
                System.out.println("match: "+s1.getPort()+" "+s2.getPort());
                for(Socket s: gameTypeList) {
                    DataOutputStream playerOut = socketOutMap.get(s);
                    JSONObject matchJson = new JSONObject();
                    matchJson.put("match", true);
                    matchJson.put("otherName", socketNameTable.get(matchTable.get(s)));
                    playerOut.writeUTF(matchJson.toString());
                    playerOut.flush();
                }
                gameTypeList.clear();
                isWait = false;
            }else {
//                DataOutputStream out = getOutputStream(socket);
                out.writeUTF(getBooleanJson("match", false).toString());
                out.flush();
                System.out.println("socket wait");
                isWait = true;
            }
        }

        while (isWait) {
            Thread.sleep(100);
            synchronized (gameTypeList) {
                if (gameTypeList.size() == 0) {
                    break;
                }
            }
        }


        return play(socket, in, out);
    }

    private boolean play(Socket player,
                         DataInputStream in,
                         DataOutputStream out
    ) throws IOException, JSONException, InterruptedException {

        playerState.put(player, true);
        Socket otherPlayer = matchTable.get(player);

        System.out.println("start playing");

        int score=0, hp;

        while (true) {
            // TODO: 游戏逻辑
            boolean clientExit = false;
            JSONObject json = new JSONObject();
            try{
                json = new JSONObject(in.readUTF());
            }catch (IOException e){
                clientExit = true;
                System.out.println(player.toString() + " exit");
            }


//            System.out.println("socket port: "+player.getPort()+" socre: "+score);
            JSONObject sendJson = new JSONObject();
            sendJson.put("end", false);

            if (json.has("hp")) {
                hp = json.getInt("hp");
                sendJson.put("otherHp", hp);
            }
            if (json.has("score")) {
                score = json.getInt("score");
                sendJson.put("otherScore", score);
            }



            DataOutputStream otherOut = socketOutMap.get(otherPlayer);
            try {
                otherOut.writeUTF(sendJson.toString());
                otherOut.flush();
            }catch (IOException ignored){

            }

            if (clientExit || json.getBoolean("end")) {
                playerState.replace(player, false);
                HistoryRecord newRecord = new HistoryRecord(socketNameTable.get(player), score, getDate());
                socketHistoryTable.put(player, newRecord);
                historyDAO.insert(newRecord);
                break;
            }
        }

        boolean isLastFinish = true;

        while (playerState.get(otherPlayer)) {
            isLastFinish = false;
//            System.out.println(player.getPort());
            Thread.sleep(500);
        }

        if (isLastFinish) {
            // 最后完成的发信息
            // TODO: 排行榜
            List<HistoryRecord> historyRecords = historyDAO.getHead100();
            int thisRank = -1;
            int otherRank = -1;
            JSONArray hists10 = new JSONArray();
            int histLen = 0;
            for(int i=0;i < historyRecords.size();i++) {
                HistoryRecord hist = historyRecords.get(i);
                if (hist.equals(socketHistoryTable.get(player))) {
                    thisRank = i;
                }else if (hist.equals(socketHistoryTable.get(otherPlayer))) {
                    otherRank = i;
                }
                if (i < 10) {
                    hists10.put(hist.toJSON());
                    histLen += 1;
                }
            }

            // 给最后完成的发
            JSONObject thisJson = new JSONObject();
            thisJson.put("end", true);
            thisJson.put("thisRank", thisRank);
            thisJson.put("otherRank", otherRank);
            thisJson.put("thisData", socketHistoryTable.get(player).toJSON());
            thisJson.put("otherData", socketHistoryTable.get(otherPlayer).toJSON());
            thisJson.put("histData", hists10);
            thisJson.put("histDataLength", histLen);
            try {
                out.writeUTF(thisJson.toString());
                out.flush();
            }catch (IOException ignored) {

            }



            // 给先完成的发
            JSONObject otherJson = new JSONObject();
            otherJson.put("end", true);
            otherJson.put("thisRank", otherRank);
            otherJson.put("otherRank", thisRank);
            otherJson.put("thisData", socketHistoryTable.get(otherPlayer).toJSON());
            otherJson.put("otherData", socketHistoryTable.get(player).toJSON());
            otherJson.put("histData", hists10);
            otherJson.put("histDataLength", histLen);
            DataOutputStream otherOut = socketOutMap.get(otherPlayer);
            try{
                otherOut.writeUTF(otherJson.toString());
                otherOut.flush();
            }catch (IOException ignored) {

            }

            matchTable.remove(player);
            socketNameTable.remove(player);
            matchTable.remove(otherPlayer);
            socketNameTable.remove(otherPlayer);
            socketHistoryTable.remove(player);
            socketHistoryTable.remove(otherPlayer);

            System.out.println("close socket: "+player);
//            in.close();
            try {
//                out.close();
                player.close();
            }catch (IOException ignored){
                
            }
            System.out.println("close socket: "+otherPlayer);
//            socketInTable.get(otherPlayer).close();
            try {
//                otherOut.close();

                otherPlayer.close();
            }catch (IOException ignored){

            }

            socketInTable.remove(player);
            socketInTable.remove(otherPlayer);
            socketOutMap.remove(player);
            socketOutMap.remove(otherPlayer);
        }
        return true;
    }

    private JSONObject getBooleanJson(String key, boolean b) throws JSONException {
        JSONObject json = new JSONObject();
        json.put(key, b);
        return json;
    }

    public static final SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
    private String getDate() {
        Date date = new Date();
        return format.format(date);
    }
}
