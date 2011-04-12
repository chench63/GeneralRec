#!/usr/bin/python
#file:  MyEmpire.py


from random import randint,choice
import os
import time



class Empire_Entity:
	def __init__(self):
		self.gold = 100
		self.food = 100
		self.population = 1000
		
		self.security = 10
		self.economy = 10
		self.algriculture = 10
		
		self.army = 1000

		print 'Initialization Compelete!'


class Empire_Control:
		
	def advanceSec(self, Empire_Entity):
		rand = RandomLucky.myRand(1,10)
		Empire_Entity.security += rand
		return rand

	def advanceEco(self, Empire_Entity):
		rand = RandomLucky.myRand(1,10)
		Empire_Entity.economy += rand
		return rand

	def advanceAlgri(self, Empire_Entity):
		rand = RandomLucky.myRand(1,10)
		Empire_Entity.algriculture += rand
		return rand

	
	def conscription(self, Empire_Entity, popNum):
	#"You can rewrite the Method for fun"
		if popNum <= Empire_Entity.population and \
		  popNum * 0.01 <= Empire_Entity.gold :
			Empire_Entity.army += popNum
			Empire_Entity.gold -= popNum * 0.01
			Empire_Entity.population -= popNum
			return True
		elif popNum*0.01 > Empire_Entity.gold :
			return 'gold'
		else:
			return 'population'		


	def oops(self,Empire_Entity):
		lucky = ['gold', 'food', 'general', 'gold', 'gold']
		op = choice(lucky)
		if op == 'gold':
			Empire_Entity.gold += RandomLucky.myRand(1,100)
			return 'gold'
		elif op == 'food':
			Empire_Entity.food += RandomLucky.myRand(1,100)
			return 'food'
		else:
			return 'general'
		
	def cereMonth(self,Empire_Entity):
		# It could be very-ry radiculous, e.g: Earthquake leading economy reducing an so on
		Empire_Entity.gold += RandomLucky.myRand(Empire_Entity.economy * 10,
							Empire_Entity.economy * 100)
		Empire_Entity.food += RandomLucky.myRand(Empire_Entity.algriculture * 10,
							Empire_Entity.algriculture * 100)
		Empire_Entity.population += RandomLucky.myRand(Empire_Entity.security * 10, 
							 Empire_Entity.security * 100)


class RandomLucky:     #If you Need
	@classmethod
	def myRand(cls,star,end):
		return randint(star,end)



class Empire_Boundry:
	@classmethod
	def cls(cls):
	#	os.system('cls')         in Windows OS
		os.system('clear')
	
	def mainFr(self,Empire_Entity):
		print '\nThe Gold:\t\t', Empire_Entity.gold \
			,'\nThe Food:\t\t', Empire_Entity.food \
			,'\nThe Population:\t\t', Empire_Entity.population \
			,'\n\n\n\n' \
			,'\nThe Economy:\t\t', Empire_Entity.economy \
			,'\nThe Algriculture:\t', Empire_Entity.algriculture \
			,'\nThe Security:\t\t', Empire_Entity.security \
			,'\nThe Army:\t\t', Empire_Entity.army

		print '''\t\t\t0.Quti the Game:
			1.Advance Economy:
			2.Advance Algriculture:
			3.Advance Security:
			4.Conscription:
			5.Lucky Search (For.General.Gold.Food.)
			6.Fire To Enemy:
		      '''

	def ecoFr(self, rand):
		print 'economy +',rand
	
	def algriFr(self, rand):
		print 'algriculture +',rand			

	def secrFr(self, rand):
		print 'security +',rand		
	
	def conscrptFr(self):
		Empire_Boundry.cls()
		popNum = raw_input('How Many Soldiers?') 
		while not popNum.isdigit() :
			popNum = raw_input('How Many Soldiers?')
		return int(popNum)

 	def luckyFr(self, oops):
		if oops == 'gold':
			#Empire_Entity.gold += RandomLucky.myRand(1,100)
			print 'Find gold Luckily'
		elif oops == 'food':
			#Empire_Entity.food += RandomLucky.myRand(1,100)
			print 'Find food Luckily'
		else:
			print 'God in Hell'





class Filter:    #Python don't support  SingleBoast ?
	def advFil(self, func,Empire_Entity):
		Empire_Boundry.cls()
		func(Empire_Entity)
		time.sleep(1)
		Empire_Boundry.cls()

	def advHead(self):
		Empire_Boundry.cls()
	
	def advTail(self):
		time.sleep(1)
		Empire_Boundry.cls()


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


def main():
	entity = Empire_Entity()
	boundry = Empire_Boundry()
	control = Empire_Control()
	filt = Filter()
	COUNT = 0

	boundry.cls()
	boundry.mainFr(entity)
	index = inputCheckDig('Your CMD?')
	while index != 0 :
		filt.advHead()
		if index == 1 :
			rand = control.advanceEco(entity)
			boundry.ecoFr(rand)
		elif index == 2:
			rand = control.advanceAlgri(entity)
			boundry.algriFr(rand)
		elif index == 3:
			rand = control.advanceSec(entity)
			boundry.secrFr(rand)
		elif index == 4:
			popNum = boundry.conscrptFr()
			res = control.conscription(entity, popNum)
			if res == 'gold' :
				print 'No More Gold'
			elif res == 'population' :
				print 'No More Man'
			else :
				print 'Get The Command'
			#boundry.
		elif index == 5:
			op = control.oops(entity)
			boundry.luckyFr(op)
		elif index == 6:
			#doWar
			noting = 1
		filt.advTail()
		boundry.mainFr(entity)

		COUNT += 1
		if COUNT % 3 == 0:
			control.cereMonth(entity)

		index = inputCheckDig('Your CMD?')
	else :
		print 'Games Over! Thanks'


if __name__ == '__main__':
	main()







