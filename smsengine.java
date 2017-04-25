import java.io.*;
import java.net.*;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.Exception.*;
import java.util.Scanner;
import java.util.Iterator;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;



/**
 * @author Yufeng Wang
 * @version 1.0
 * the Server that read the suspicious_words file, receive msg file from client, 
 * calculate the spam score and pass results back to the client
 * Call: java smsengine [Portnumber] [suspicious words] to initiate server
 */

public class smsengine {

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

	public static void main (String[] args) throws IOException {

        //initialize respective local variables and take in value
        int portnumber;
        ArrayList<String> sensWords = new ArrayList<String>();
        ArrayList<String> clientarraylist = new ArrayList<String>();
        int num_sus_word = 0;
        ArrayList<String> found_sus_word = new ArrayList<String>();


        //Check command line input length
        if (args.length != 2) {
            System.out.println("Invalid input."
            + " Sample input would be <port number> "
            + "<txt file>\n");
            return;
        }

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
        //check the file access
        String filename = args[1];
        File f = new File(filename);
        if (!f.canRead() & f.exists()) {
        	System.out.println("Access authority is required to read file");
        	return;
        }
		
        //Read the suspicious-words file and check the validity of the file simultaneously
        try {
            Scanner scann = new Scanner(new File(filename));
            while (scann.hasNext()){
                sensWords.add(scann.next());
                num_sus_word++;
            }
            scann.close();
        } catch (FileNotFoundException e) {
            System.out.println("The file name you have entered cannot be found");
            return;
        }
       
       //interaction with client
		try {
            int totalcount = 0;
            int wordcount = 0;

            //initiate the server socket
			ServerSocket serversocket = new ServerSocket(portnumber);
			System.out.println("waiting for response");
            //bind the client Address with server socket
			Socket connectToClient = serversocket.accept();
            //connectToClient.bind(connectToClient.getRemoteSocketAddress());
			System.out.println("Connection from: " +connectToClient.getInetAddress().getHostAddress() + " has been initiated");
			DataInputStream infromclient = new DataInputStream(connectToClient.getInputStream());
			DataOutputStream outToClient = new DataOutputStream(connectToClient.getOutputStream());
			boolean run = true;
            boolean bad_input = false;
			while (run) {
                //receive file from client and calculate spam score and add to Arraylist
					String clientarray=infromclient.readUTF();
                    totalcount++;
                    for (String ss : sensWords) {
                        if (ss.equals(clientarray)) {
                            wordcount++;
                            //test if the received packet is larger than 1000 or contains non-acsii value
                            if (ss.length() == 0 || ss.length() > 1000 || !isASCII(ss)) {
                                bad_input = true;
                            }
                            found_sus_word.add(ss);
                            System.out.println(ss);
                        }
                    }    
                if (clientarray.equals("wyfSBCJBC123")) {
                    //if the termination code is detected, stop reading and start pass results back to client
                    float number = (wordcount/(totalcount - 1));
                    String string_number;
                    if (bad_input) {
                        string_number = new String("0,1,bad,input");
                    } else {
                    string_number = new String(num_sus_word+ "," + number + ",");
                        for (String aa : found_sus_word) {
                            string_number = string_number.concat(aa + ",");
                        }
                    }
                    //pass termination code to client
                    outToClient.writeUTF(string_number);
					outToClient.writeUTF("wyfSTCJTC123");
                    outToClient.flush();
                    System.out.println(string_number);
					run = false;
                    System.out.println("All Transmission has been Completed...\n"
                        + "System Closed :D\n");
				}
			}
        //close the serversocket
		serversocket.close();
		} catch(IOException e) {
			System.out.println(e.getMessage() + "Possibly lost connection...");
		} 

	}

}
