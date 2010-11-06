package voronoi.util;

public class Pair<T, S>{
    private T fst;
    private S snd;
    public Pair(T fst, S snd){
      this.fst = fst;
      this.snd = snd;
    }
    public T getFst() {
      return fst;
    }
    public S getSnd() {
      return snd;
    }
  }