import java.net.*;
import java.io.*;
import java.net.DatagramPacket;
import java.util.Scanner;
import java.util.Iterator;
import java.net.SocketException;
import java.util.ArrayList;
import java.net.InetAddress;

/**
 * @author Yufeng Wang
 * @version 1.0
 * the UDP client that read the message file, pass the file to server,
 * and then collect results from the server. 
 * Call: java smsclient [ServerIP] [ServerPortnumber] [messageFile] after server initiated
 */

public class smsclientUDP {
	
	public static void main (String[] args) throws IOException{
    
    //Check command line input length
        if (args.length != 3) {     
            System.out.println("Invalid input. "
                    + "Sample input would be:\n" + "<host name> + <port number>" +
                    "+ <filename>\n");
            return;
        }

        String filename = args[2];
        InetAddress serverIP = null;;
		int portnumber = 1333;

        //check the validity of server IP
        try {
            serverIP =InetAddress.getByName(args[0]);
        } catch (UnknownHostException e) {
            System.out.println("the UnknownHostException is found. The host name is unresolved ");
            return;
        }
        //check the validity of port number
        try {
            portnumber = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid port number is given.");
            return;
        }
        if (portnumber <= 1024 || portnumber > 65536) {
            System.out.println("Port number should be between 1024 and 65536.");
            return;
        }
        //check the accesibility of the file
        File f = new File(filename);
        if (!f.canRead() & f.exists()) {
            System.out.println("Access authority is required to read file");
            return;
        }
        //Read the suspicious file and check the validity of the file simultaneously
		ArrayList<String> susWordList = new ArrayList<String>();
		try {
			Scanner scan = new Scanner(new File(filename));
			while (scan.hasNext()){
				susWordList.add(scan.next());
			}
			scan.close();
		} catch (FileNotFoundException ex) {	
			System.out.println("the file is not found");
            return;
		}

        //initialize the UDP socket

    	int attempt_times = 0; 
    	DatagramSocket clientsocket= new DatagramSocket();
    	DatagramPacket sent_packet = new DatagramPacket(new byte[255],255,serverIP,portnumber);
    	DatagramPacket received_packet = new DatagramPacket(new byte[255], 255);
    	boolean respond = false;

        //while loop to set the maximum number of retry is 3 if no response is detected
    	while (attempt_times < 3 & !respond) {
            //send the data to server
    		for (String ss2 : susWordList) {
    			sent_packet.setData(ss2.getBytes());
    			sent_packet.setLength(ss2.getBytes().length);

    			clientsocket.send(sent_packet);
                //set the socket timeout for every package
    			clientsocket.setSoTimeout(2000); 
    		}
            // send termination code to the server
    		String passcode = "wyfSBCJBC123";
    		sent_packet.setData(passcode.getBytes());
    		sent_packet.setLength(passcode.getBytes().length);
    		clientsocket.send(sent_packet);
    		
            try {
                // receive data from server and show results
    			clientsocket.receive(received_packet);
    			String serverdata =new String(received_packet.getData(),0,received_packet.getLength());
                if (serverdata.equals("0,1,bad,input")) {
                        System.out.println("0 1 bad input");
                } else {

                String[] aa = serverdata.split(",");
                System.out.println("The number of suspicious words is: "+aa[0]);
                System.out.println("The spam score is: " + aa[1]);
                if (aa.length > 2) {
                    System.out.print("The total number of words: ");
                    for (int i = 2; i < aa.length; i++) {
                            System.out.println(aa[i] + " ");
                        }
                    } else {
                        System.out.println("no suspicious words are found");
                    }

                }
    			respond = true;

    		} catch (InterruptedIOException e) {
    			attempt_times++;
    			System.out.println("The server has not respond for 2 seconds");
                int lefttime = 3 - attempt_times;
    			System.out.println("Retrying..." + lefttime+ " left..." );
    			continue;
    		}
    	}
        clientsocket.close();
	}
}
    

    
        


