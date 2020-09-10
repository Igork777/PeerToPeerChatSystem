package com.Semester3.PeerToPeerChat;

import javax.json.Json;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.Socket;

public class Peer {
    public static void main(String[] args) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter username & port for this peer: ");
        String[] setupValues = bufferedReader.readLine().split(" ");
        ServerThread serverThread = new ServerThread(setupValues[1]);
        serverThread.start();
        new Peer().updateListenToPeers(bufferedReader, setupValues[0], serverThread);
    }

    public void updateListenToPeers(BufferedReader bufferedReader, String username, ServerThread serverThread) throws Exception{
        System.out.println("Enter (space separated) hostname port :");
        System.out.println("  peers to receive messages (s to skip)");
        String input = bufferedReader.readLine();
        String[] inputValues = input.split(" ");
        if(!input.equals("s")){
            for (int i = 0; i < inputValues.length ; i++) {
                String [] address = inputValues[i].split(":");
                Socket socket = null;
                try {
                    socket = new Socket(address[0], Integer.parseInt(address[1]));
                    new PeerThread(socket).start();
                } catch (Exception e) {
                    if(socket != null){
                        socket.close();
                    }
                    else{
                        System.out.println("Invalid input. Skipping the next step");
                    }
                }
            }
            communicate(bufferedReader, username, serverThread);
        }
    }

    public void communicate(BufferedReader bufferedReader, String username, ServerThread serverThread) throws IOException {
        try {
            System.out.println("You can now communicate (e to exit, c to change)");
            boolean flag = true;
            while (flag){
               String message = bufferedReader.readLine();
               if(message.equals("e")){
                   flag = false;
                   break;
               }
               else if (message.equals("c")){
                   updateListenToPeers(bufferedReader, username, serverThread);
               }
               else{
                   StringWriter stringWriter = new StringWriter();
                   Json.createWriter(stringWriter).writeObject(Json.createObjectBuilder().add("username", username).add("message", message).build());
                   serverThread.sendMessage(stringWriter.toString());
               }
            }
            System.exit(0);
        }catch (Exception e){

        }
    }
}
