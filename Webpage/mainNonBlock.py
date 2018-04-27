import socket
import select
import time

#Reference:
#   https://docs.python.org/25/howto/sockets.html

sSock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
addr = '206.21.94.120'#socket.gethostname()
port = 80

sSock.setblocking(0)
sSock.bind((addr, port))
sSock.listen(5)

print('Finished initialization')
print('Listening on', addr, 'port', port)

readL = [sSock]
writeL = []
errL = []

try:
    while True:
        rL, wL, eL = select.select(readL, writeL, errL)
        for sock in rL:
            if sock == sSock:
                cSock, cAddr = sSock.accept()
                readL.append(cSock)
                print('Received connection from', cAddr)
            else:
                data = sock.recv(1024)
                if data:
                    #print('RECV:', data) 
                    print('GET:', data.split()[1])
                    with open('settings.html', 'rb') as fp:
                        sock.send(fp.read())
                    readL.remove(sock)
                    sock.close()
        print('*', end='', flush=True) 
        time.sleep(0.01)
    sSock.close()
except:
    sSock.close()
