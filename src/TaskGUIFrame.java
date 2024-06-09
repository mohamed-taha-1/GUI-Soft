import javax.crypto.spec.IvParameterSpec;import javax.swing.*;import java.awt.*;import java.awt.event.*;import java.io.*;import java.nio.charset.StandardCharsets;import java.nio.file.Files;import java.security.SecureRandom;import javax.crypto.*;import javax.crypto.spec.SecretKeySpec;import javax.swing.SpinnerNumberModel;public class TaskGUIFrame  extends JFrame {  private JPanel mainPanel;  private JLabel headerLapel;  private JButton btnChoose;  private JButton btnEncrypt;  private JButton btnDcrypt;  private File selectedFile ;  private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";  private static final String ENCRYPTED_FILE_EXTENSION = ".enc";  private static final int BUFFER_SIZE = 4096;  private JButton btnSaveEncrypted;  private JButton btnSaveDcrypted;  private File encryptedFile;  private File decryptedFile;  private JLabel filePathLabel;  private JLabel fileSizeLabel;  private JLabel fileExtensionLabel;  private JProgressBar encryptionProgressBar;  private JProgressBar decryptionProgressBar;  private boolean isEncrypted=false;  public TaskGUIFrame(){    setTitle("File  methods ");    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    setSize(400, 400);    // Initialize components    mainPanel = new JPanel();    headerLapel = new JLabel("File Chooser ");    btnChoose = new JButton("Choose File");    btnEncrypt = new JButton("Encrypt");    btnDcrypt = new JButton("Decrypt");    btnDcrypt.setBackground(Color.GREEN);    btnSaveEncrypted = new JButton("Save Encrypted file");    btnSaveEncrypted.setBackground(Color.ORANGE);    btnSaveDcrypted = new JButton("Save Decrypted file");    btnSaveDcrypted.setBackground(Color.ORANGE);    btnChoose.setBackground(Color.GREEN);    btnEncrypt.setBackground(Color.GREEN);    filePathLabel = new JLabel("File Path: ");    fileSizeLabel = new JLabel("File Size: ");    fileExtensionLabel = new JLabel("File Extension: ");    // Initialize Progress Bars    encryptionProgressBar = new JProgressBar();    decryptionProgressBar = new JProgressBar();    encryptionProgressBar.setStringPainted(true);    decryptionProgressBar.setStringPainted(true);    // Add ActionListener to btnChoose    btnChoose.addActionListener(new ActionListener() {      @Override      public void actionPerformed(ActionEvent e) {        selectedFile=null;        encryptedFile=null;        decryptedFile=null;        JFileChooser fileChooser = new JFileChooser();        int result = fileChooser.showOpenDialog(TaskGUIFrame.this);        if (result == JFileChooser.APPROVE_OPTION) {          selectedFile = fileChooser.getSelectedFile();          // -- Update labels after selection --          if (getFileExtension(selectedFile).equals("enc")){            isEncrypted=true;            encryptedFile=selectedFile;          }          filePathLabel.setText("File Path: " + selectedFile.getAbsolutePath());          fileSizeLabel.setText("File Size: " + formatSize( selectedFile.length() ) );          fileExtensionLabel.setText("File Extension: " + getFileExtension(selectedFile));        }      }    });    // Add ActionListener to btnEncrypt    btnEncrypt.addActionListener(new ActionListener() {      @Override      public void actionPerformed(ActionEvent e) {        if (encryptedFile == null ) {            try {              encryptedFile = encryptFile(selectedFile, encryptionProgressBar);              if (getFileExtension(encryptedFile).equals("enc")) isEncrypted=true;              JOptionPane.showMessageDialog(TaskGUIFrame.this, "File encrypted successfully!");            } catch (Exception ex) {              JOptionPane.showMessageDialog(TaskGUIFrame.this, "Error encrypting file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);            }        } else {          JOptionPane.showMessageDialog(TaskGUIFrame.this, "Please select a file first!", "Error", JOptionPane.ERROR_MESSAGE);        }      }    });    btnSaveEncrypted.addActionListener(new ActionListener() {      @Override      public void actionPerformed(ActionEvent e) {        if (encryptedFile != null || isEncrypted) {          JFileChooser fileChooser = new JFileChooser();          int result = fileChooser.showSaveDialog(TaskGUIFrame.this);          if (result == JFileChooser.APPROVE_OPTION) {            File destinationFile = fileChooser.getSelectedFile();            try {              Files.copy(encryptedFile.toPath(), destinationFile.toPath());              JOptionPane.showMessageDialog(TaskGUIFrame.this, "Encrypted file saved successfully!");            } catch (IOException ex) {              JOptionPane.showMessageDialog(TaskGUIFrame.this, "Error saving encrypted file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);            }          }        } else {          JOptionPane.showMessageDialog(TaskGUIFrame.this, "No encrypted file available to save!", "Error", JOptionPane.ERROR_MESSAGE);        }      }    });    btnSaveDcrypted.addActionListener(new ActionListener() {      @Override      public void actionPerformed(ActionEvent e) {          if (decryptedFile != null) {            JFileChooser fileChooser = new JFileChooser();            fileChooser.setSelectedFile(decryptedFile);  // Pre-select the decrypted file with the correct extension            int result = fileChooser.showSaveDialog(TaskGUIFrame.this);            if (result == JFileChooser.APPROVE_OPTION) {              File destinationFile = fileChooser.getSelectedFile();              // Ensure the file has the correct extension              String destinationFilePath = destinationFile.getAbsolutePath();              if (!destinationFilePath.endsWith(decryptedFile.getName().substring(decryptedFile.getName().lastIndexOf(".")))) {                destinationFilePath += decryptedFile.getName().substring(decryptedFile.getName().lastIndexOf("."));                destinationFile = new File(destinationFilePath);              }              try {                Files.copy(decryptedFile.toPath(), destinationFile.toPath());                JOptionPane.showMessageDialog(TaskGUIFrame.this, "Decrypted file saved successfully!");              } catch (IOException ex) {                JOptionPane.showMessageDialog(TaskGUIFrame.this, "Error saving decrypted file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);              }            }          } else {            JOptionPane.showMessageDialog(TaskGUIFrame.this, "No decrypted file available to save!", "Error", JOptionPane.ERROR_MESSAGE);          }      }    });    btnDcrypt.addActionListener(new ActionListener() {      @Override      public void actionPerformed(ActionEvent e) {        if (encryptedFile !=null ) {            try {               decryptedFile = decryptFile(encryptedFile, decryptionProgressBar );              JOptionPane.showMessageDialog(TaskGUIFrame.this, "File decrypted successfully!");              // Here you can do something with the decryptedFile, for example, display it or save it.            } catch (Exception ex) {              JOptionPane.showMessageDialog(TaskGUIFrame.this, "Error decrypting file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);            }        } else {          JOptionPane.showMessageDialog(TaskGUIFrame.this, "Please select a file first!", "Error", JOptionPane.ERROR_MESSAGE);        }      }    });    mainPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 15)); // Left-aligned with 5 pixels vertical gap and 10 pixels horizontal gap    mainPanel.setPreferredSize(new Dimension(350, 300)); // Set a preferred size for the mainPanel to limit its width    Dimension panelSize = mainPanel.getSize();    int buttonY = 50; // Vertical position of the buttons    int buttonX = panelSize.width - 120; // Horizontal position of the buttons    btnChoose.setBounds(buttonX, buttonY, 100, 100);    btnEncrypt.setBounds(buttonX, buttonY + 120, 100, 100);    btnSaveEncrypted.setBounds(buttonX, buttonY + 240, 100, 100);    mainPanel.add(headerLapel);    mainPanel.add(btnChoose);    mainPanel.add(fileExtensionLabel);    mainPanel.add(filePathLabel);    mainPanel.add(fileSizeLabel);    mainPanel.add(btnEncrypt);    mainPanel.add(encryptionProgressBar);    mainPanel.add(btnSaveEncrypted);    mainPanel.add(btnDcrypt);    mainPanel.add(decryptionProgressBar);    mainPanel.add(btnSaveDcrypted);    getContentPane().add(mainPanel);    setVisible(true);  }  public static String formatSize(long size) {    String[] units = {"B", "KB", "MB", "GB", "TB"};    int unitIndex = 0;    double readableSize = size;    while (readableSize >= 1024 && unitIndex < units.length - 1) {      readableSize /= 1024;      unitIndex++;    }    return String.format("%.2f %s", readableSize, units[unitIndex]);  }  public static File encryptFile(File file, JProgressBar progressBar) throws Exception {    // Generate a random 256-bit AES key    KeyGenerator keyGen = KeyGenerator.getInstance("AES");    keyGen.init(256);    SecretKey secretKey = keyGen.generateKey();    // Initialize the cipher with the key    Cipher cipher = Cipher.getInstance(AES_ALGORITHM);    SecureRandom secureRandom = new SecureRandom();    byte[] iv = new byte[cipher.getBlockSize()];    secureRandom.nextBytes(iv);    IvParameterSpec ivParams = new IvParameterSpec(iv);    cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParams);    // Get the original file extension    String originalExtension = getFileExtension(file);    // Create the encrypted file    File encrFile = new File(file.getAbsolutePath() + ENCRYPTED_FILE_EXTENSION);    try (FileInputStream fis = new FileInputStream(file);         FileOutputStream fos = new FileOutputStream(encrFile);         CipherOutputStream cos = new CipherOutputStream(fos, cipher)) {      // Write the IV and the secret key at the beginning of the file      fos.write(iv);      fos.write(secretKey.getEncoded());      // Write the original extension length and extension      byte[] extBytes = originalExtension.getBytes(StandardCharsets.UTF_8);      fos.write(extBytes.length);      fos.write(extBytes);      // Read the file in chunks and write the encrypted data      byte[] buffer = new byte[BUFFER_SIZE];      int bytesRead;      while ((bytesRead = fis.read(buffer)) != -1) {        cos.write(buffer, 0, bytesRead);      }    }    progressBar.setValue(0);    progressBar.setString("100%");    progressBar.setMaximum(200);    return encrFile;  }  public static File decryptFile(File encryptedFile, JProgressBar progressBar) throws Exception {    String basePath = encryptedFile.getAbsolutePath();    if (basePath.endsWith(".enc")) {      basePath = basePath.substring(0, basePath.length() - 4);    }    // Read the encrypted file    try (FileInputStream fis = new FileInputStream(encryptedFile)) {      // Read the IV and the secret key from the beginning of the file      byte[] iv = new byte[16];      fis.read(iv);      byte[] keyBytes = new byte[32];      fis.read(keyBytes);      // Read the original extension length and extension      int extLength = fis.read();      byte[] extBytes = new byte[extLength];      fis.read(extBytes);      String originalExtension = new String(extBytes, StandardCharsets.UTF_8);      // Initialize the cipher for decryption      SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");      Cipher cipher = Cipher.getInstance(AES_ALGORITHM);      cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));      // Create the decrypted file      File decryptedFile = new File(basePath + originalExtension);      try (FileOutputStream fos = new FileOutputStream(decryptedFile);           CipherInputStream cis = new CipherInputStream(fis, cipher)) {        // Read the encrypted file in chunks and write the decrypted data        byte[] buffer = new byte[BUFFER_SIZE];        int bytesRead;        while ((bytesRead = cis.read(buffer)) != -1) {          fos.write(buffer, 0, bytesRead);        }      }      progressBar.setValue(0);      progressBar.setString("100%");      progressBar.setMaximum(200);      return decryptedFile;    }  }  // Method to get file extension from absolute path  private static String getFileExtension(File file) {    String name = file.getName();    int lastIndexOf = name.lastIndexOf(".");    return lastIndexOf == -1 ? "" : name.substring(lastIndexOf);  }}