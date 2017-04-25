Name: Yufeng Wang
Email address: mraku3@gatech.edu
Class name: CS3251
Section: A
Date: FEB.2, 2017
Assignment title: TCP and UDP Applications Programming


Names and descriptions of all files submitted:
	
	smsclient.java
		Java file that implements client function using TCP, and only interacts with smsengine.java

	smsengine.java
		Java file that implements server function using TCP, and only interacts with smsclient.java

	smsclientUDP.java
		Java file that implements client function using UDP, and only interacts with smsengineUDP.java

	smsengineUDP.java
		Java file that implements server function using UDP, and only interacts with smsclientUDP.java

	README.txt						
		instructions for compiling and running files and implementation details


Instruction for compiling and running java file:
	Compiling: 
		1. Open Command Line and direct to the source folder
		2. Use javac *.java to compile all of the java source code.

	Testing:
		TCP
			1. Use java smsengine <portnumber> <suspicious_word_document> to initiate TCP server
			2. Use java smsclient <serverIP> <ServerPortnumber> <messagedocument> to connect with TCP server and obtain results
			3. suspicious-words.txt is used as a sample text document for Server 
			4. msg.txt is used as a sample text document for client

		UDP
			1. Use java smsengineUDP <portnumber> <suspicious_word_document> to initiate UDP server
			2. Use java smsclientUDP <serverIP> <ServerPortnumber> <messagedocument> to connect with UDP server and obtain results			
			3. suspicious-words.txt is used as a sample text document for Server 
			4. msg.txt is used as a sample text document for client


Description of application protocol:
TCP server and client interactions:
Both sensor and server are implemented in Java. The entire implementation first checks the users' input. Upon successful creation of
socket and txt file reading, server starts to listen for information from the client. Once the client finishes reading the txt file, it will send data through format of String through socket. At the end of sending process, the client will send "wyfSBCJBC123" as the passcode, informing the server all the data has been completely sent. After receiving data from the client, server will calculate the spamming socre, number of suspicious words so as the exact word that has been judged as suspicious and return these results back to the client. Similar to the implementation of client, the server will also send "wyfSBCJBC123" to confirm that all data has been sent successfully, and client may close socket. Distinct from TCP, the UDP client and server both has timeout option, while no repsonse is heard from server, the same message will be sent for three times. This is used to handle packet losses. 


Know bugs and limitations:
I did not use multithread to handle simultaneous access to server from multiple clients. Also, the way that I have implemented server would not allow both UDP and TCP access at the same time. Each server only support one of them. 
