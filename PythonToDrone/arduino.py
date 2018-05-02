import serial 

class Arduino:
    def __init__(self, port='ttyACM0', baud='115200'):
        self.port = '/dev/' + port
        self.baud = baud
        print('Connecting on', self.port, 'using baud rate', self.baud, '...')
        self.serial = serial.Serial(self.port, self.baud, timeout=0.01)
    
    def send(self, data, encode=True):
        if encode:
            self.serial.write(data.encode())
        else:
            self.serial.write(data)

    def recv(self):
        return self.serial.readline()
    
    def close(self):
        self.serial.close()
    
    def sendByteString(self, bStr, recv=True, doPrint=False):
        i = 0
        val = 0
        if not len(bStr) == 8:
            print("The String must have 8 bits!")
        else:
            while i < len(bStr):
                c = bStr[i]
                if c not in "01":
                    print("error with input string: ", c, " not allowed!")
                    i = 9
                else:
                    tmp = (ord(c)-48)<<(7-i)
                    val = val + tmp
                    i += 1

            if doPrint:
                print(val)
                print(chr(val))
            
            self.send(str(chr(val)).encode("latin_1"), False)
