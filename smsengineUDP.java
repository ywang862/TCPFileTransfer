import java.net.*;
import java.io.*;
import java.net.DatagramPacket;
import java.util.Scanner;
import java.util.Iterator;
import java.net.SocketException;
import java.util.ArrayList;


/**
 * @author Yufeng Wang
 * @version 1.0
 * the UDP Server that read the suspicious_words file, receive msg file from client, 
 * calculate the spam score and pass results back to the client
 * Call: java smsengine [Portnumber] [suspicious words] to initiate server
 */
public class smsengineUDP {


    /**
    * @param s, the string input
    * @return if the string is ascii value
    * the function tests whether the string contains purely acsii value.
    **/
    private static boolean isASCII(String s) 
    {
    for (int i = 0; i < s.length(); i++) 
        if (s.charAt(i) > 127) 
            return false;
    return true;
    }
	
	public static void main (String[] args)  throws IOException{

    //Check command line input length
    if (args.length != 2) {
      System.out.println("Invalid input."
       + " Sample input would be <port number> "
        + "<txt file>\n");
         return;
    }
    //initialize respective local variables and take in value
    int clientportnumber;
    int suswordcount = 0;
    ArrayList<String> susWordList = new ArrayList<String>();
    ArrayList<String> found_sus_word = new ArrayList<String>();
    String filename = args[1];
		int portnumber;

    //Check the validity of port number input
    try {
      portnumber = Integer.parseInt(args[0]);
    } catch (NumberFormatException e) {
      System.out.println("Invalid port number is given.");
      return;
    }

    if (portnumber <= 1024 || portnumber > 65536) {
      System.out.println("Port number should be between 1024 and 65536.");
      return;
    }
    //check the accessibility of the file
    File f = new File(filename);
    if (!f.canRead() & f.exists()) {
      System.out.println("Access authority is required to read file");
      return;
    }
    //Read the suspicious-words file and check the validity of the file simultaneously
		try {
			Scanner scan = new Scanner(new File(filename));
			while (scan.hasNext()){
				susWordList.add(scan.next());
        suswordcount++;
			}
			scan.close();
		} catch (FileNotFoundException ex) {	
			System.out.println("the file is not found");
      return;
		}
    
    try {
      int wordcount = 0;
      int totalocunt = 0;
    //initiate the server socket
		  DatagramSocket serverSocket = new DatagramSocket(portnumber);
      System.out.println("serverSocket is created, waiting for response");
      byte[] buf = new byte[255];
      DatagramPacket received_packet = new DatagramPacket(buf, buf.length);
      boolean bad_input = false;
      boolean running = true; 
      while (running) {
      //read data from the client and calculate the spam score
      serverSocket.receive(received_packet);
      totalocunt++;
      String clientdata =new String(received_packet.getData(),0,received_packet.getLength());
      for (String ss : susWordList) {
        if (ss.equals(clientdata)) {
          wordcount++;
          if (ss.length() == 0 || ss.length() > 1000 || !isASCII(ss)) {
            bad_input = true;
          }
          found_sus_word.add(ss);
        }
      }
      //find the client's IP address so as the port number
      InetAddress clientIP = InetAddress.getByName(received_packet.getAddress().getHostAddress());
      clientportnumber = received_packet.getPort();
      float spamscore = wordcount/(totalocunt);
      //create packet that will be sent back to client
      DatagramPacket sent_packet = new DatagramPacket(new byte[255],255,clientIP,clientportnumber);
      String data_back;
      if (bad_input) {
        data_back = new String("0,1,bad,input");
      } else {
        data_back = new String(suswordcount + "," + spamscore + ",");
        for (String aa : found_sus_word) {
          data_back = data_back.concat(aa + ",");
        }
      }
      //if the terminal code is received, then send back the packet to client
      if (clientdata.equals("wyfSBCJBC123")) {
        sent_packet.setData(data_back.getBytes());
        sent_packet.setLength(data_back.getBytes().length);
        try {
          serverSocket.send(sent_packet);
          serverSocket.setSoTimeout(2000); 
        } catch (InterruptedIOException e) {
          System.out.println("send back failed");
        }
        System.out.println("Transmission Completed...System Closed...");
        running = false;
      }
    }
    serverSocket.close();
  } catch (BindException e) {
    System.out.println("the port has already been bound");
  }

}
}