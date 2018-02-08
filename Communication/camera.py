from time import sleep
from picamera import PiCamera

camera = PiCamera()
camera.resolution = (640, 480)
camera.start_preview()
# Camera warm-up time
sleep(2)
camera.capture("temp.jpg")
