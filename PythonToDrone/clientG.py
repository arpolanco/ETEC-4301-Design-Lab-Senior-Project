import socket
import time
import picamera
import serial

try:
	address = "206.21.94.58" #hardcoded as per our design. may need adjusted for testing
	port = 1101
	host = socket.socket()
	host.connect((address, port))
	#camera = picamera.PiCamera()
	#camera.framerate = 30
	#camera.color_effects = (128, 128)
	#camera.resolution = (640, 480) #1280x720 results in unacceptable latency
	#camera.start_preview()
	#time.sleep(2) #camera warm-up time
	buffer = host.makefile('wb')
	buffer.write("DRONE\n")
	buffer.flush()
	#camera.start_recording(buffer, format='h264')
	#camera.wait_recording() #todo: either find a way to record indefinitely
	while True:
		pass
		#time.sleep(5)
	camera.stop_recording()
except KeyboardInterrupt:
	print("dead")
#finally:
#	buffer.close()
#	host.close()

