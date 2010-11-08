package voronoi.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class EvolutionLogger {
  static private FileHandler fileTxt;
  static private SimpleFormatter formatterTxt;

  static private FileHandler fileHTML;
  static private Formatter formatterHTML;

  static public void setup() throws IOException{
    Logger logger = Logger.getLogger("");
    logger.setLevel(Level.INFO);
    fileTxt = new FileHandler("evolution"+Calendar.getInstance().getTime()+".txt");
    fileHTML = new FileHandler("evolution"+Calendar.getInstance().getTime()+".html");
    
    formatterTxt = new SimpleFormatter();
    fileTxt.setFormatter(formatterTxt);
    logger.addHandler(fileTxt);
    
    formatterHTML = new MyHtmlFormatter();
    fileHTML.setFormatter(formatterHTML);
    logger.addHandler(fileHTML);
  }
/**
 * Logger util
 * @author http://www.vogella.de/articles/Logging/article.html
 *
 */
  private static class MyHtmlFormatter extends Formatter {
    // This method is called for every log records
    public String format(LogRecord rec) {
      StringBuffer buf = new StringBuffer(1000);
      // Bold any levels >= WARNING
      buf.append("<tr>");
      buf.append("<td>");

      if (rec.getLevel().intValue() >= Level.WARNING.intValue()) {
        buf.append("<b>");
        buf.append(rec.getLevel());
        buf.append("</b>");
      } else {
        buf.append(rec.getLevel());
      }
      buf.append("</td>");
      buf.append("<td>");
      buf.append(calcDate(rec.getMillis()));
      buf.append(' ');
      buf.append(formatMessage(rec));
      buf.append('\n');
      buf.append("</tr>\n");
      return buf.toString();
    }

    private String calcDate(long millisecs) {
      SimpleDateFormat date_format = new SimpleDateFormat("MMM dd,yyyy HH:mm");
      Date resultdate = new Date(millisecs);
      return date_format.format(resultdate);
    }

    // This method is called just after the handler using this
    // formatter is created
    public String getHead(Handler h) {
      return "<HTML>\n<HEAD>\n" + (new Date()) + "\n</HEAD>\n<BODY>\n<PRE>\n"
          + "<table border>\n  "
          + "<tr><th>Time</th><th>Log Message</th></tr>\n";
    }

    // This method is called just after the handler using this
    // formatter is closed
    public String getTail(Handler h) {
      return "</table>\n  </PRE></BODY>\n</HTML>\n";
    }
  }

}
