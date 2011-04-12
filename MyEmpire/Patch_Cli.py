#!/usr/bin/python
#filename: Patch_Cli.py

#Litels with Patch_Ser.py

from socket import *
import os
import MyEmpire
import string 
from Patch_War.py import Empire_War_Entity, Empire_War

BUFSIZ = 1024

class Empire_Cli:
	def __init__(self):
		self.HOST = raw_input( 'Service IP>')
		self.PORT = int(raw_input( 'Port>' ))
		self.ADDR = (self.HOST, self.PORT)
		self.udpCliSock = socket(AF_INET, SOCK_DGRAM)

	def join(self):
		data = 'Connect'
		self.udpCliSock.sendto(data,self.ADDR)
		data, self.ADDR = self.udpCliSock.recvfrom(BUFSIZ)
		if not data:
			return False
		print data

	def loop(self):
		key = ''
		while True:
			try:
				Empire_War.cls()
				self.join()
				op =  raw_input('>')
				if op == 'q' or op == 'quit':
					key = 'q'
					break
				elif op == 'ready':
					key = 'ready'
					break				
			except (KeyboardInterrupt,EOFError,ValueError):
				continue
		print key
		self.udpCliSock.sendto(key, self.ADDR)
		data, self.ADDR = self.udpCliSock.recvfrom(BUFSIZ)
		if not data:
			print 'Quit'
		print data
	
	def sendReady(enermy , Empire_War_Entity):
		if enermy != 0:
			key = 'enermy: ' + enermy
		
		key += ' attack: ' + Empire_War_Entity.attackNum 
		key += ' steal: '+ Empire_War_Entity.stealNum 
		self.udpCliSock.sendto(key, self.ADDR)
		
	def recvStat(self):
		data, self.ADDR = self.udpCliSock.recvfrom(BUFSIZ)
		if not data:
			print 'Error.. Losing Data!'
			data = None
		return data

	def analyse(self, data, key):
		if key == 'attack':
			index = data.find('attack')
			while key[index] in string.digits:
				num +=key[index]
				index += 1 
		elif key == 'steal':
			index = len(key)
			while key[index-1] in string.digits:
				num +=key[index]
				index -= 1
		return int(index)
		
	def updateStat(self, Empire_War_Entity, rate):
		Empire_War_Entity.attackNum = int(Empire_War_Entity.attackNum * rate)
		Empire_War_Entity.stealNum = int(Empire_War_Entity.stealNum * rate)
		Empire_War_Entity.proFdkNum = int(Empire_War_Entity.proFdkNum * rate)
		Empire_War_Entity.proArmNum = int(Empire_War_Entity.proArmNum * rate)
		Empire_War_Entity.march =  Empire_War_Entity.attackNum +Empire_War_Entity.stealNum +Empire_War_Entity.proFdkNum +Empire_War_Entity.proArmNum 

	def calStat(self, data, Empire_War_Entity):
		p_attackNum = self.analyse(data, 'attack')
		p_stealNum =  self.analyse(data, 'steal')
		
		Empire_War_Entity.provision -= ( p_stealNum - Empire_War_Entity.proFdNum ) * 1
		rest = (Empire_War_Entity.march + Empire_War_Entity.proArmNum)-p_attackNum 
		rate =  rest / (Empire_War_Entity.march + Empire_War_Entity.proArmNum)
		self.updateStat(Empire_War_Entity, rate)		
		

	def __del__(self):
		self.udpCliSock.close()





test = Empire_Cli()
test.loop()

		
