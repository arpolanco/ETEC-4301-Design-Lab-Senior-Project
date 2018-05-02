from arduino import Arduino as Ard
import socket
import io
import sys

isPhoneBased = False

if len(sys.argv) > 1 and sys.argv[1] != 'None':
    serialPort = sys.argv[1]
    if len(sys.argv) > 2 and sys.argv[2] != 'None':
        address = sys.argv[2]
        isPhoneBased = True
else:
    raise Exception("You need to pass in the Serial Port and optionally the IP address of the server!!!")

try:
    ardy = Ard(serialPort)
    if isPhoneBased:
        port = 1101
        print('Connecting to', address, port)
        server = socket.socket()
        server.connect((address, port))
        server.setblocking(0)
        print("Socket created.")
        buffer = server.makefile('w')
        print('Make file')
        buffer.write("DRONE\n")
        buffer.flush()
        #Should be connected to Server at this point
        print("Connected??")
        identity = b''
        while True:
            try:
        	    identity = server.recv(1) #blocking
            except socket.error as msg:
                pass
            if not identity == b'':
                print("Drone ID: ", identity[0])
                break
		 
    print('Succesfuly connected to Arduino!')
    running = True
    while(running):
        if isPhoneBased:
            command = b''
            try:
                command = server.recv(1) #blocking
            except socket.error as msg:
                pass
            if not command == b'':
                print("Command to Send: ", command)
                ardy.send(command, False)
            line = ardy.recv()
            i = 0
            while not line == b'' or i > 8:
                print(line.decode("utf-8"))
                line = ardy.recv()
                i += 1
        else: 
            byte_string = input("Insert the next byte you want to send in the form of 0's and 1's:  ")
            if "q" in byte_string:
                running = False
            elif 'k' in byte_string:
                L = byte_string.split()
                bStr = '01'
                if L[1] == 'p':
                    bStr += '00'
                elif L[1] == 'r':
                    bStr += '01'
                elif L[1] == 'y':
                    bStr += '10'
                else:
                    print('Improperly formatted! Expected k (p|r|y) (p|i|d) #')
                    continue
                if L[2] == 'p':
                    bStr += '00'
                elif L[2] == 'i':
                    bStr += '01'
                elif L[2] == 'd':
                    bStr += '10'
                else:
                    print('Improperly formatted! Expected k (p|r|y) (p|i|d) #')
                    continue
                bStr += '00'
                print(bStr)
                ardy.sendByteString(bStr, recv=False)
                bStr = format(int(L[3]), '#010b')
                val = int(L[3])
                bStr = ''
                for i in range(8):
                    if val & (1 << i):
                        bStr = '1' + bStr
                    else:
                        bStr = '0' + bStr


                print(bStr)
                ardy.sendByteString(bStr)
                
            else:
                ardy.sendByteString(byte_string) 

    ardy.close()
    print("All Done")
except KeyboardInterrupt:
    ardy.sendByteString('01110001')
    ardy.send('q')
    print("Quitting!")
    print("Good Bye!")
