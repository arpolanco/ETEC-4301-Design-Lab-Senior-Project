import serial 

class Arduino:
    def __init__(self, port='ttyACM0', baud='115200'):
        self.port = '/dev/' + port
        self.baud = baud
        print('Connecting on', self.port, 'using baud rate', self.baud, '...')
        self.serial = serial.Serial(self.port, self.baud, timeout=0.01)
        print('Connected!')

    def send(self, data, encode=True):
        if encode:
            self.serial.write(data.encode())
        else:
            self.serial.write(data)

    def recv(self):
        return self.serial.readline()
    
    def close(self):
        self.serial.close()


