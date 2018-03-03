import socket
import time
import picamera
import io
import struct
import threading

try:
	address = "192.168.0.4" #hardcoded as per our design. may need adjusted for testing
	port = 1101
	host = socket.socket()
	host.connect((address, port))
	camera = picamera.PiCamera()
	camera.framerate = 30
	#camera.color_effects = (128, 128)
	camera.resolution = (640, 480)
	#camera.start_preview()
	time.sleep(2) #camera warm-up time
	buffer = host.makefile('wb')
	camera.start_recording(buffer, format='h264')
	camera.wait_recording(100) #todo: either find a way to record indefinitely
	camera.stop_recording()
except KeyboardInterrupt:
	print("dead")
#finally:
#	buffer.close()
#	host.close()
