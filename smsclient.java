import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * @author Yufeng Wang
 * @version 1.0
 * the client that read the message file, pass the file to server,
 * and then collect results from the server. 
 * Call: java smsclient [ServerIP] [ServerPortnumber] [messageFile] after server initiated
 */
public class smsclient {
	public static void main(String[] args){
		//Check command line input length
		if (args.length != 3) {
            System.out.println("Invalid input. "
                    + "Sample input would be:\n" + "<host name> + <port number>" +
                    "+ <filename>\n");
            return;
        }

        //initialize respective local variables and take in value
		String servername;
		String score = null;
		String filename = args[2];
		int portnumber = 3333;
		InetAddress serverIP = null;
		ArrayList<String> msglist = new ArrayList<String>();

		try {
			serverIP =InetAddress.getByName(args[0]);
		} catch (UnknownHostException e) {
			System.out.println("the UnknownHostException is found. Host name could not be resolved");
			return;
		}

		//Check the validity of port number input
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
		try {
			Scanner s = new Scanner(new File(filename));
			while (s.hasNext()){
    			msglist.add(s.next());
			}
			s.close();
			} catch (FileNotFoundException e) {
				System.out.println("file not found");
				return;
			}

		//Interaction with server	
		try {
			//Initiate the client socket
			Socket connectToServer;
			try {
				connectToServer = new Socket(serverIP, portnumber);
				connectToServer.setSoTimeout(200);
			} catch (IOException e) {
				System.out.println("creation of Socket failed, please check your IP");
				return;
			}
			DataInputStream infromServer = new DataInputStream(connectToServer.getInputStream());
			DataOutputStream outToServer = new DataOutputStream(connectToServer.getOutputStream());
			boolean running = true;


			while (running) {
				//send the file text to server after connection	
				for (String element : msglist) {
					outToServer.writeUTF(element);
				}
				//send termination code to the server
				outToServer.writeUTF("wyfSBCJBC123");
				outToServer.flush();
				//read from server
				String hahaha = infromServer.readUTF();
				System.out.println("transfer Complete");

				if (hahaha.equals("wyfSTCJTC123")) {

					running = false;
					if (score.equals("0,1,bad,input")) {
						System.out.println("0 1 bad input");
					} else {
						String[] aa = score.split(",");

						System.out.println("The number of suspicious words is: "+aa[0]);

						System.out.println("The spam score is: " + aa[1]);
						if (aa.length > 2) {
							System.out.println("The suspicious words: ");
							for (int i = 2; i < aa.length; i++) {
								System.out.println(aa[i] + " ");
							}
						} else {
							System.out.println("no suspicious words are found");
						}
					}

					
				} else {
					score=hahaha;
				}

			}
			connectToServer.close();
		//catch server exception
		} catch (NullPointerException e) {
				System.out.println("NullPointerException found");
		} catch (IOException e) {
        	System.out.println("IOexception has been found");
        }




	}
}