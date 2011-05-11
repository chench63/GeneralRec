#!/usr/bin/python

import string
import copy

KEYINDEX = '...key:'


class LoadLib(object):
	def __init__(self):
		self.posting = []
		self.key = []
		self.__load()
		self.__loadKey()
	def __load(self):
		fi = open('Posting.lib','r')
		fiIndex = 0
	
		TmpKey = [KEYINDEX,[]]		#Posting Element
		Tmplist = [0,'Http','FewWord']  #(pageRanking,Url, KeyWordView)

		for eachline in fi.readlines():
			keyIndex = eachline.find(KEYINDEX)
			if keyIndex == 0:    #Find the KeyWord
				if len(TmpKey[1]) != 0:  #The Posting is not empty	
					self.posting.append( copy.deepcopy(TmpKey) )
				TmpKey = [ eachline[len(KEYINDEX):-1], [] ]  #[KeyWord,[urlPosting List...]]
				fiIndex = 0
				continue
			
			Tmplist[fiIndex%3]=eachline[:-1]
			if fiIndex%3 == 2:
				TmpKey[1].append( copy.deepcopy(Tmplist) )
			fiIndex +=1
		self.posting.append( copy.deepcopy(TmpKey) )  # add the last Posting element
	
	def __loadKey(self):
		fi = open('Keyword.lib','r')
		for eachline in fi.readlines():
			self.key.append( [eachline,[]] )	
	
	def getKey(self):
		return self.key

	def getPosting(self):
		return self.posting


class StoreLib(object):
	def __init__(self):
		self.posting = []

	def setPosting(self,posting):
		self.posting = copy.deepcopy(posting)
		
	def storePosting(self):
		fo = open('Posting.lib','w')
		for eachKey in self.posting: 
			fo.write(  '%s%s\n' %(KEYINDEX,eachKey[0])   )
			for eachlist in eachKey[1]:  #loop: (0,'Http','FewWord')
				for eachEle in eachlist:
					fo.write( '%s\n' %eachEle )













