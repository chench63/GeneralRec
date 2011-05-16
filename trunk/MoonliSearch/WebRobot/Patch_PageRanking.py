#!/usr/bin/python

import string
import os
from time import sleep,ctime
import threading
import copy


FILENAME = 'FILENAME =/home/ppia/glocal/MoonliSearch/Posting/lib/webRankInfo'



def BackUpPro(pageRanking,i):
	driver = dataText()
	OWN = 1
	while True:
		sleep(30*OWN)
		driver.rank = pageRanking.rankDeepCopy()	
		driver.store()
		OWN += 1


def BACKUPPERMINUTE(PageRanking):
	r = threading.Thread(target=BackUpPro,
			args=(PageRanking,1 ))
	r.setDaemon(True)
	r.start()



def SAVEANDQUIT(pageRanking):
	driver = dataText()
	driver.rank = pageRanking.rankDeepCopy()
	rankTmp = sorted(driver.rank)
	driver.rank = reversed(rankTmp)	
	driver.store()




class dataInter(object):
	def load(self):
		pass
	def store(self):
		pass


class dataText(dataInter):
	def __init__(self):
		self.rank = []	

	def load(self):
		self.rank = []
		fi = open(FILENAME,'r') 		
		Tmp = [0,'url']  #rank's List Templete
		for eachlines in fi.readlines():
			if eachlines[:-1].isdigit():
				Tmp[0] = eachlines[:-1]
						
			else:
				Tmp[1] = eachlines[:-1]
				self.rank.append( [Tmp[0],Tmp[1]] )	
				
		fi.close
	
	def store(self):
		cmdLine = 'cp '+ FILENAME + '  ' + FILENAME+'_bak'
		os.system(cmdLine)
		fo = open(FILENAME,'w')
		for eachList in self.rank:
			for eachEle in eachList:
				fo.write('%s\n' %eachEle)
		fo.close()
		print 'Save the Rank..at' , ctime()



class PageRanking(object):
	def __init__(self,url):
		self.rank = [ [1,url] ]
		self.rankTable = [url]
	#	self.backupPerMinute()
	
	def addNewUrlInQue(self,url):
		TmpEle = [1,url]
		self.rankTable.append(url)
		self.rank.append(TmpEle)	

	def oneGoal(self,url):
		try:
			indexNum = self.rankTable.index(url)
			self.rank[indexNum][0] += 1
		except (ValueError):
			self.addNewUrlInQue(url)	

	def rankDeepCopy(self):
		tmpRank = copy.deepcopy( self.rank )
		return tmpRank



		

















