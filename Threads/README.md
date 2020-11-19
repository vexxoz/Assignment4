a) A description of your project and a detailed description of what it does
  - This project allows a client to connect and modify a list of Strings. There are three different versions, the first being one that is single threaded and only one client can connect at a time. The second version allows an unbounded number of clients allowed with synchronization to prevent multithreaded errors. The last allows a bounded amount of clients to join.
  
b) An explanation of how we can run the program
  - To run programs are outlined below:
    1) gradle singleThread -Pport="8888"
    2) gradle threadedServer -Pport="8888"
    3) gradle threadPoolServer -Pport="8888" -Pbound="5"
    
c) Explain how to "work" with your program, what inputs does it expect etc.
  - The input it requires is a port to run on, and for the bounded version, the max number of clients.
  
e) A detailed description of the protocol you decided to use, so what information goes into your protocol.
  - The protocol is very very simple its just a string of words such as: add <string>, remove <index>, display, reverse <index>. It can be accessed from nc.
