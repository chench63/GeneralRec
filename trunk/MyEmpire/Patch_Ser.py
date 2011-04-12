#!/usr/b	in/python
#filename:Patch_V1.0.py

#It's a patch that makes the game online,in some point

from socket import *
from time import ctime
import os
import threading
from Queue import Queue
from time import ctime

HOST = ''
PORT = 21566
BUFSIZ = 1024
ADDR = ( HOST, PORT )


S_THREADS = 3	#size of thread sending diagram
R_THREADS = 1	#size of thread receiving diagram

que = Queue(32)  #Queue of Information..

def doRecv(Empire_Ser):	#receive and direct the diagram
	while True:
		data, addr = Empire_Ser.udpSerSock.recvfrom(BUFSIZ)
		host = Empire_Ser.analyse(data)
		port = addr[1]
		addr = (host, port)
		gram = (data, addr)
		que.put(gram,1)
		print data,'\tAddr: ',addr

def doSend(Empire_Ser):
	while True:
		gram = que.get(1)
		data = gram[0]
		addr = gram[1]
		Empire_Ser.udpSerSock.sendto(data, addr)

class Empire_Ser:
	def __init__(self):
		self.udpSerSock = socket(AF_INET, SOCK_DGRAM)
		self.udpSerSock.bind(ADDR)
		self.CliQue = []

	def join(self):
		print 'Waiting for other Gamer...'
		data, addr = self.udpSerSock.recvfrom(BUFSIZ)
		ip = addr[0]
		if data == 'Connect':
			self.udpSerSock.sendto('[%s] > Line Stablished' %
			(ctime() ) ,addr  )
		elif data == 'q':
			self.CliQue.remove( ip  )
			self.udpSerSock.sendto('[%s] > You Successfully Quit'% 
						(ctime()), addr )
		elif data == 'ready':
			self.udpSerSock.sendto('[%s] > Waiting for Service Start The Game' %
						(ctime()), addr )

		if ip not in self.CliQue:
			self.CliQue.append( ip  )
	

	def jloop(self):
		while True:
			self.join()	
			for each in enumerate(self.CliQue):
				print 'The Gamer Addr: ', each ,'is Ready'
			try:
				op =  raw_input('Start Game(y)?')
				if op == 'y':
					break
			except(KeyboardInterrupt, EOFError, ValueError):
				continue


	def analyse(self, data):	#analyse the data just recieved
		enermyIndex = data.find('enermy')
		attackIndex =  data.find('attack')
		if enermyIndex == -1:
			return 0
		else:
			enermyIndex = len('enermy: ')
			return data[enermyIndex :attackIndex]
	
	def startSer(self):
		s_threads = []
		r_threads = []

		for r in R_THREADS:
			r = threading.Thread(target=doRecv,
				args=(self))
			r.start()
			r_threads.append(r)
		for s in S_THREADS:
			s = threading.Thread(target=doSend,
				args = (self) )
			s.start()
			s_threads.append(s)	
		

		for r in R_THREADS:
			r_threads[r].join()
		for s in S_THREADS:
			s_threads[s].join() 




#data = 'enermy: 192.168.0.1 attack: 1000 steal:1000'
#test = Empire_Ser()
#key = test.analyse(data)
#print key
#test.jloop()



def main():
	print 'Start at: ', ctime()
	ser = Empire_Ser()
	ser.jloop()
	ser.startSer()
	print 'all Done at: ', ctime()

if __name__ == '__main__':
	main()













