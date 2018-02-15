import socket
import threading
import struct
import io
from PIL import Image

def handle(client):
	buffer = client.makefile('rb')
	i = 0
	try:
		while True:
			image_len = struct.unpack('<L', buffer.read(4))[0]
			if not image_len:
				break
			stream = io.BytesIO()
			stream.write(buffer.read(image_len))
			stream.seek(0)
			image = Image.open(stream)
			#instead of this, just render to the screen !!!
			image.save("temp"+str(i)+".jpg")
			i+=1
			#print('Image is %dx%d' % image.size)
			#image.verify()
			#print('Image is verified')
	finally:
		buffer.close()

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
	port = 1101
	server = Server(address, port)
	server.listen()
	return True

if (__name__ == "__main__"):
	result = run()
	print(result)
