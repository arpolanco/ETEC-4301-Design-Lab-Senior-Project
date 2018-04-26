from arduino import Arduino as Ard
import socket
import io

isPhoneBased = False

try:
    if isPhoneBased:
        address = "206.21.94.201" #hardcoded as per our design. may need adjusted for testing
        port = 1101
        print('Connecting to', address, port)
        server = socket.socket()
        server.connect((address, port))
        print("Socket created.")
        buffer = server.makefile('w')
        print('Make file')
        buffer.write("DRONE\n")
        buffer.flush()
        #Should be connected to Server at this point
        print("Connected??")
    ardy = Ard("ttyUSB0")

    line = ardy.recv()
    while not line == b'Mode = TUNING\n':
        if not line == b'':
            print(line.decode().replace("\n\r", ""))
        line = ardy.recv()
    
    if isPhoneBased:    
        val = 0
        byte_string = '01110001'
        #ardy.sendByteString(byte_string)

    print('Initialization complete! Beginning while loop!')
    running = True
    while(running):
        if isPhoneBased:
            command = server.recv(1) #blocking
            print(command)
            ardy.send(command, False)
            line = ardy.recv()
            while not line == b'Received: \r\n':
                if not line == b'':
                    print(line)
                line = ardy.recv()
            line = ardy.recv()
            print('RECV:', line)
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
                ardy.sendByteString(bStr, recv=False)
                bStr = format(int(L[3]), '#010b')
                ardy.sendByteString(bStr)
                
            else:
                ardy.sendByteString(byte_string) 

    ardy.close()
    print("All Done")
except KeyboardInterrupt:
    print("DEAD!!!!!!!")
