import socket
import sys
import comms_pb2 as broadcast

ip = "localhost"
port = 7000
address = (ip, port)
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

print("Connecting to server!");
sock.connect(address)

while True:
	print(sock.recv(2048))

sock.close()
