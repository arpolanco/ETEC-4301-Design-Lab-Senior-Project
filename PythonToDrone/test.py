from arduino import Arduino as Ard
import socket
import io

try:
    #address = "206.21.94.201" #hardcoded as per our design. may need adjusted for testing
    #port = 1101
    #print('Connecting to', address, port)
    #server = socket.socket()
    #server.connect((address, port))
    #print("Socket created.")
    #buffer = server.makefile('w')
    #print('Make file')
    #buffer.write("DRONE\n")
    #buffer.flush()
    #Should be connected to Server at this point
    print("Connected??")
    ardy = Ard("ttyUSB0")
        
    line = ardy.recv()
    while not line == b'Mode = TUNING\n':
        if not line == b'':
            print(line.decode().replace("\n\r", ""))
        line = ardy.recv()

    print('Initialization complete! Beginning while loop!')
    running = True
    while(running):
        #command = server.recv(1) #blocking
        #print(command)
        #ardy.send(command, False)
        #line = ardy.recv()
        #while not line == b'Received: \r\n':
        #    if not line == b'':
        #        print(line)
        #    line = ardy.recv()

        #line = ardy.recv()
        #print('RECV:', line)
        val = 0
        byte_string = input("Insert the next byte you want to send in the form of 0's and 1's:  ")
        if "q" in byte_string:
            running = False
        else:
            i = 0
            if not len(byte_string) == 8:
                print("The String must have 8 bits!")
            else:
                while i < len(byte_string):
                    c = byte_string[i]
                    if c not in "01":
                        print("error with input string: ", c, " not allowed!")
                        i = 9
                    else:
                        tmp = (ord(c)-48)<<(7-i)
                        #print(tmp)
                        val = val + tmp
                        i += 1

            
                print(val)
                print(chr(val))
                ardy.send(str(chr(val)).encode("latin_1"), False)

                line = ardy.recv()
                while not line == b'Received: \r\n':
                    if not line == b'':
                        print(line)
                    line = ardy.recv()
        
                line = ardy.recv()
                print("RECV: ",line)

    ardy.close()
    print("All Done")
except KeyboardInterrupt:
    print("DEAD!!!!!!!")
