package voronoi.communicator;

import java.io.*;
import java.net.*;

public class Client {

  public static void main(String[] args) throws Exception {
    Socket socket = null;
    PrintWriter out = null;
    BufferedReader in = null;
    boolean first = true;
    
    //String name = args[0];
    /*** my code **/
    Player player = new Player();

    try {
      socket = new Socket("localhost", 20000);
      out = new PrintWriter(socket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    } catch (UnknownHostException e) {
      System.err.println("Don't know about host: localhost.");
      System.exit(1);
    } catch (IOException e) {
      System.err.println("Couldn't get I/O for the connection to: localhost.");
      System.exit(1);
    }
    System.out.println("Connection with server success!");
    BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
    String fromServer;
    String fromUser;

    while ((fromServer = in.readLine()) != null) {
      System.out.println("Server: " + fromServer);
      if (fromServer.equals("WIN") || fromServer.equals("LOSE"))
        break;
      if(fromServer.equals("YOURTURN")){
        //fromUser = stdIn.readLine();
        fromUser = player.play();
        System.out.println("final result from player " + fromUser);
        if (fromUser != null) {
          out.println(fromUser);
        }
      }
      else{
        if(!first){
          player.addStates(fromServer);
        }
        else{
          first = false;
          player.init(fromServer);
        }
      }
    }

    out.close();
    in.close();
    stdIn.close();
    socket.close();
  }

  
}
