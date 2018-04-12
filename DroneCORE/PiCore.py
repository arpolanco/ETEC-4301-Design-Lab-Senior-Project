import pygame
import time
import serial
import sys

port = sys.argv[1]
#ser = serial.Serial('/dev/'+port, 115200)

while True:
	sendByte = 0;
	command = input("Enter the Command you want: ")
	if command == 'T':
		sendByte = 0x80
		command = input("Enter the value you want: ")
		sendByte = sendByte | int(command);
		
	print(sendByte)
