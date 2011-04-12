#!/usr/bin/python
#filename: Patch_War.py

class Empire_War_Entity:
	def __init__(self, march, provision):
		self.march = march
		self.provision = provision
		self.attackNum = 0
		self.stealNum = 0
		self.proFdNum = 0
		self.proArmNum = 0


class Empire_War:  #Boundry(*fr) And Control(*op)
	def __init__(self, *friend, *enermy):
		self.friend = friend
		self.enermy = enermy		

	def enermyFr(self, Empire_War_Entity):
		print 'The Army:\t ', Empire_War_Entity.march, \
		      '\t\tThe Food:\t ', Empire_War_Entity.provision

	def opFr(self):
		print '''\n1.Attack the Enermy  \
			 \n2.Steal the Food \
		         \n3.Protect the Food \
			 \n4.Protect Army \
			 \n5.Ready'''
	
	def statFr(self, Empire_War_Entity):
		print '\n\nAttack Enermy Nums:', Empire_War_Entity.attackNum \
			,'Steal Food Nums:', Empire_War_Entity.stealNum \
			,'Protect Food Nums:', Empire_War_Entity.proFdNum \
			,'Protect Army Nums:', Empire_War_Entity.proArmNum


	def inputCheck(self,key):
		index = raw_input(key)
		while not index.isdigit():
			index = raw_input(key)
		return int(index)

	def numCheck(self,Empire_War_Entity):
		numSum = Empire_War_Entity.attackNum + Empire_War_Entity.stealNum + \
			Empire_War_Entity.proFdNum + Empire_War+Entity.proArmNum
		if numSum <= Empire_War_Entity.march:
			return True
		else:
			return False
	
	def opAttack(self, Empire_War_Entity):
		while True :
			enermy = 0
			while enermy not in self.enermy:
				enermy = raw_input('Attack Who(Ip)?')
			
			index = self.inputCheck('How Many to Attack the Enermy?')	
			Empire_War_Entity.attackNum = index
			if self.numCheck(Empire_War_Entity):
				break
			else:
				print 'Not Enough Soldior'
		return enermy

	def opSteal(self, Empire_War_Entity):
		while True :
			index = self.inputCheck('How Many to Steal the Food?')
			Empire_War_Entity.stealNum = index
			if self.numCheck(Empire_War_Entity):
				break
			else:
				print 'Not Enough Soldior'	

	def opProFd(self, Empire_War_Entity):
		while True :
			index = self.inputCheck('How Many to defend the Food?')
			Empire_War_Entity.proFdNum = index
			if self.numCheck(Empire_War_Entity):
				break
			else:
				print 'Not Enough Soldior'	

	def opProArm(self, Empire_War_Entity):
		while True :
			index = self.inputCheck('How Many to Protest the Army?')
			Empire_War_Entity.proArmNum = index	
			if self.numCheck(Empire_War_Entity):
				break
			else:
				print 'Not Enough Soldior'	

	def inputCheckDig(key):
		index = 100 # to make it in the Loop of 'While'
		while type(index) != int or index > 6 or index < 0 :
			try:
				index = raw_input(key)
				if index.isdigit():
					index = int(index)
			except (EOFError, KeyboardInterrupt, ValueError):
				index = 100
				continue	
		return index 

	@classmethod
	def cls(self):
		os.system('clear')

	def mainFr(self, Empire_War_Entity):
		print 'Friend:'
		for each in self.friend
			self.enermyFr(each)
		
		print '\n\nEnermy:'
		for eachEner in self.enermy
			self.enermyFr(eachEner)

		self.statFr(Empire_War_Entity)
		self.opFr()


	def opLoop(self, Empire_War_Entity, Empire_Cli):
		index = 100
		enermy = 0
		while index != 0 :
			self.cls()
			self.mainFr(Empire_War_Entity)
			index = self.inputCheckDig('Your CMD?')
			if index == 1:
				enermy = self.opAttack(Empire_War_Entity)
			elif index == 2:
				self.opSteal(Empire_War_Entity)
			elif index == 3:
				self.opProFd(Empire_War_Entity)
			elif index == 4:
				self.opProArm(Empire_War_Entity)
			elif index == 5:
				#waitHere
				Empire_Cli.sendReady(enermy , Empire_War_Entity)
				enermy = 0
				break






