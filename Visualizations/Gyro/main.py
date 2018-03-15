import math
from vector import *
from matrix import *
from rasterizer import *
import pygame
import time
import serial
import sys

port = sys.argv[1]

pygame.init()
screen = pygame.display.set_mode((800,600))

ser = serial.Serial('/dev/'+port, 115200)

cam = RasterizerCamera(VectorN(400,300,500,1), VectorN(400,300,0,1))
lights = []
lights.append(RasterizerLight(VectorN(400,100,500,1), VectorN(1,1,1), VectorN(1,1,1)))
lights.append(RasterizerLight(VectorN(200,100,-500,1), VectorN(1,1,1), VectorN(1,1,1)))
R = Rasterizer(cam, lights)

master = RasterizerObject()
R.add_obj(master)
masterRotX = 0
masterRotY = 0
masterRotZ = 0
accelX = 0
accelY = 0
accelZ = 0


sun = RasterizerObject('box.obj', 'sun.mtl')
master.addChild(sun)
sunTransformBase = scale(50,50,50)


fill = False
prevTime = time.time()
font = pygame.font.SysFont('arial', 20)
done = False
 
try:
    while True:
        print('A')
        line = ser.readline().decode().strip()
        print(line)
        if 'send' in line.lower():
            print("Got Send Request")
            print("Sending Letter\n")
            ser.write(b'A')
            break
except:
    print("Error sending A")

while not done:
    curTime = time.time()
    elapsed = curTime - prevTime
    prevTime = curTime
    evt = pygame.event.poll()
    if evt.type == pygame.QUIT:
        done = True
    elif evt.type == pygame.KEYDOWN:
        if evt.key == pygame.K_q or evt.key == pygame.K_ESCAPE:
            done = True
        elif evt.key == pygame.K_SPACE:
            fill = not fill
    mouseInput = pygame.mouse.get_pressed()
    if mouseInput[0]:
        pass
    try:
        line = ser.readline().decode().strip()
        lineSplit = line.split()
        #print(line)
        if "ypr" == lineSplit[0]:
           masterRotY = float(lineSplit[1]) 
           masterRotX = float(lineSplit[2]) 
           masterRotZ = float(lineSplit[3])
        if "areal" == lineSplit[0]:
           accelX = float(lineSplit[1]) 
           accelY = float(lineSplit[2]) 
           accelZ = float(lineSplit[3])
    except:
        pass
    #masterRotZ += 45 * elapsed
    master.transform = rotateY(math.radians(masterRotY)) * rotateX(math.radians(masterRotX)) * rotateZ(math.radians(masterRotZ)) * translate(400,300,0)

    sun.transform = sunTransformBase
    
    screen.fill((0,0,0))
    R.render(screen, fill)
    screen.blit(font.render("FPS:" + str(1/elapsed), 0, (255,255,255)),(0,0,100,100))
    pygame.draw.rect(screen, (255,0,0), (0,256,5,int(accelX / 32))) 
    pygame.draw.rect(screen, (0,255,0), (10,256,5,int(accelY / 32))) 
    pygame.draw.rect(screen, (0,0,255), (20,256,5,int(accelZ / 32))) 
    pygame.display.flip()
pygame.quit()
