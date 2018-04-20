import socket
import arduino
import time
import sys

class DroneClient:
    def __init__(self, address, port='ACM0', baud='9600'):
        self.address = address
        self.port = port
        self.baud = baud
        self.socketInit(address)
        self.serialInit(port, baud)

    def socketInit(self, address):
        try:
            #address = "206.21.94.58" #hardcoded as per our design. may need adjusted for testing
            port = 1101
            self.host = socket.socket()
            self.host.settimeout(0.1)
            self.host.connect((address, port))
            #camera = picamera.PiCamera()
            #camera.framerate = 30
            #camera.color_effects = (128, 128)
            #camera.resolution = (640, 480) #1280x720 results in unacceptable latency
            #camera.start_preview()
            #time.sleep(2) #camera warm-up time
            buffer = self.host.makefile('wb')
            buffer.write(("DRONE\n").encode('utf-8'))
            buffer.flush()
            #camera.start_recording(buffer, format='h264')
            #camera.wait_recording() #todo: either find a way to record indefinitely
            #while True:
            #    pass
            #    #time.sleep(5)
            #camera.stop_recording()
        except KeyboardInterrupt:
            print("dead")
        #finally:
            #buffer.close()
            #host.close()

    #def socketInit(address):
    #    #address = "206.21.94.100" #hardcoded as per our design. may not work for testing
    #    print('Sending image to ' + address)
    #    port = 1101
    #    host = socket.socket()
    #    host.connect((address,port))
    #    host.send(open("temp.jpg", "rb").read())
    #    host.close()

    def serialInit(self, port='ACM0', baud=9600):
        self.ard = arduino.Arduino(port, baud)
        time.sleep(2)
        data = self.ard.recv()
        while data != None and data != '' and data != b'':
            time.sleep(0.05)
            #ard.send((str(i)).encode('utf-8'))
            #print(ard.recv())
            print(data)
            data = self.ard.recv()

    def loop(self):
        print('.', end='', flush=True)
        try:
            data = self.host.recv(1024)
            if data != None and data != '' and data != b'':
                print('Server: ', data)
        except:
            pass
        data = self.ard.recv()
        if data != None and data != '' and data != b'':
            print('\nSerial: ', data)
    
    def close(self):
        self.ard.close()

if (__name__ == "__main__"):
    client = None
    if len(sys.argv) < 2:
        raise Exception('Must specify the server ip address at minimum!')
    if len(sys.argv) == 3:
        client = DroneClient(sys.argv[1], sys.argv[2])
    elif len(sys.argv) == 4:
        client = DroneClient(sys.argv[1], sys.argv[2], sys.argv[3])
    else:
        client = DroneClient(sys.argv[1])
    print('Test passed!')
    print('Beginning loop!')
    while True:
        client.loop()


