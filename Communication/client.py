import socket

def run():
	address = "206.21.94.100" #hardcoded as per our design. may need adjusted for testing
	port = 1101
	host = socket.socket()
	host.connect((address, port))
	host.send(open("temp.jpg", "rb").read())
	host.close()

if (__name__ == "__main__"):
	run()
	print("Sent temp.jpg to server")