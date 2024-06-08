import javax.swing.*;

public class Main {
  public static void main(String[] args) {
    // Create an instance of Main
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        new TaskGUIFrame();
      }
    });
  }
}