import socket
import time
import picamera
import io
import struct
import threading

try:
	address = "206.21.94.200" #hardcoded as per our design. may need adjusted for testing
	port = 1101
	host = socket.socket()
	host.connect((address, port))
	#camera = picamera.PiCamera()
	#camera.framerate = 30
	#camera.resolution = (640, 480) #1280x720 results in unacceptable latency
	#time.sleep(2) #camera warm-up time
	buffer = host.makefile('w')
	#camera.start_recording(buffer, format='h264')
	#camera.wait_recording() #todo: either find a way to record indefinitely
	buffer.write("DRONE\n")
	buffer.flush()
	while True:
        	data = host.recv(1)
		buffer.write(data)
		buffer.write('\n')
		buffer.flush()
                #pass
		#time.sleep(5)
	#camera.stop_recording()
except KeyboardInterrupt:
	print("dead")
#finally:
#	buffer.close()
#	host.close()
