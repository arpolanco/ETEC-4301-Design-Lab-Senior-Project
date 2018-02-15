import socket
import time
import picamera
import io
import struct

try:
	address = "192.168.0.4" #hardcoded as per our design. may need adjusted for testing
	port = 1101
	host = socket.socket()
	host.connect((address, port))	
	camera = picamera.PiCamera()
	camera.resolution = (640, 480)
	camera.start_preview()
	time.sleep(2) #camera warm-up time
	buffer = host.makefile('wb')
	stream = io.BytesIO()
	for carlos in camera.capture_continuous(stream, 'jpeg'):
		buffer.write(struct.pack('<L', stream.tell()))
		buffer.flush()
		stream.seek(0)
		buffer.write(stream.read())
		stream.seek(0)
		stream.truncate()
		time.sleep(0.1)
	buffer.write(struct.pack('<L', 0))
finally:
	buffer.close()
	host.close()
