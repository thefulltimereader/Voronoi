package voronoi.communicator;

import java.awt.geom.Point2D;

public class Player {
  int name;

  public void setStatus(String fromServer) {
    String[] split = fromServer.split(" ");
    
  }

  public String play() {
    
    return null;
  }

  public void init(String fromServer) {
    String[] split = fromServer.split(" ");
    int bSize = Integer.valueOf(split[0]); 
    int turns = Integer.valueOf(split[1]); 
    int numPlayers = Integer.valueOf(split[2]); 
    name= Integer.valueOf(split[3]);
    
    
  }
  /**
   * Updates other player's input to the internals
   * @param fromServer
   */
  public void addStates(String fromServer) {
    String[] split = fromServer.split(" ");
    int x =Integer.valueOf(split[0]); 
    int y = Integer.valueOf(split[1]); 
    int playerName = Integer.valueOf(split[2]); 
    Point2D.Double pt = new Point2D.Double(x, y);
    
  }

}
