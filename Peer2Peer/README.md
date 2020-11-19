a) A description of your project and a detailed description of what it does
  - This project is a peer to peer game allowing multiple people to join in together by typing in each others ip/ports and will take turns playing a QandA game.
  
b) An explanation of how we can run the program
  - To run the program just do gradle runPeer -Pname="david" -Pport="7777"
  
c) Explain how to "work" with your program, what inputs does it expect etc.
  - All it requires is a name and a port number. Also it needs the IP/Port of the peer players
  
e) A detailed description of the protocol you decided to use, so what information goes into your protocol.
  - The protocol I used was based off of simple json with a MessageType, a message, and a username. Since every peer gets every message I used the username to help the clients figure out what messages were directed at them or for other people and to allow them to display the results correctly. Also the MessageType allows the clietns to know what the purpose of the message is and conveys important game states to each client. The message part is just some text that is meant to be displayed to the user explaining the message.
  
