import socket
import io

try:
	address = "206.21.94.201" #hardcoded as per our design. may need adjusted for testing
	port = 1101
	server = socket.socket()
	server.connect((address, port))
	#camera = picamera.PiCamera()
	#camera.framerate = 30
	#camera.color_effects = (128, 128)
	#camera.resolution = (640, 480) #1280x720 results in unacceptable latency
	#camera.start_preview()
	#time.sleep(2) #camera warm-up time
	buffer = server.makefile('w')
	buffer.write("DRONE\n")
	buffer.flush()
	#camera.start_recording(buffer, format='h264')
	#camera.wait_recording() #todo: either find a way to record indefinitely
	while True:
		telemetry = server.recv(1) #blocking
		#print(format(telemetry, "8b"))
		#time.sleep(5)
	camera.stop_recording()
except KeyboardInterrupt:
	print("dead")
#finally:
#	buffer.close()
#	server.close()
