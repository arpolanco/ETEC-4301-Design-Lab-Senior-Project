import socket
import select
import threading
import png

def handle(client):
	f = open("temp.jpg", "wb")
	while (True):
		data = client.recv(1024)
		if len(data) == 0:
			break
		f.write(data)
	f.close()
	print("Received image, saved to temp.jpg")

class Server(object):
	def __init__(self, address, port):
		self.host = socket.socket()
		self.host.bind((address,port))
		self.host.listen(5)
		self.host.settimeout(1.0)
	
	def listen(self):
		while (True):
			try:
				try:
					client, address = self.host.accept()
					#print("Client connected")
					thread = threading.Thread(target=handle,args=[client])
					thread.start()
				except socket.timeout:
					pass
			except KeyboardInterrupt:
				break
	
def run():
	address = socket.gethostbyname(socket.gethostname()) #seems to only work on windows
	print("Connecting to " + address)
	port = 1101
	server = Server(address, port)
	server.listen()
	return True

if (__name__ == "__main__"):
	result = run()
	print(result)
