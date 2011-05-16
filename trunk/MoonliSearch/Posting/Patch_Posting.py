#!/usr/bin/python

import string
import os
import os.path
import copy

from LibOper import LoadLib
from Patch_PageRanking import dataText
from time import sleep,ctime


class Posting(object):
	def __init__(self):
		ll = LoadLib()
		dt = dataText()
		dt.load()

		self.pageRanking =  dt.rank
		self.posting = []
		self.pageRankingIndex  = []
		self.key = ll.getKey()

		self.__buildEn()


	def __buildEn(self):   #Initialize the Essential member Var
		for each in self.pageRanking:	
			self.pageRankingIndex.append(each[1]);

		for each in self.key:
			postingEle = [ each[0],[] ]
			self.posting.append(postingEle)

 
	#Vectory Search
	def find(self,key):
		keyIndex = self.key.index(key)
		return copy.deepcopy( self.posting[keyIndex][1:] )   #return the url List

	def readPath(self,path):
		for root,dirnames,filenames in os.walk(path):
				print '\n\n\nEnter Root:%s' %root	
				for eachfile in filenames:
					filepath = os.path.join(root,eachfile)
					self.__readFile(filepath)
			#		print 'read...%s' %eachfile

	def __readFile(self,fileName):
		fi = open(fileName,'r')
		for eachline in fi.readlines():
			self.__matchLine(eachline,fileName)

	def __matchLine(self,eachline,fileName):
		keyIndex = 0
		for eachKey in self.key:
			if eachKey[0] in eachline:
				try:
					print fileName
					url=self.parUrl(fileName)
					rankIndex = self.pageRankingIndex.index(url)
					pageScore = self.pageRanking[rankIndex][0]

					keyEle = [pageScore,url,eachline[:-1]]
					
				except (ValueError):
					keyEle = [-1,url,eachline[:-1]]
				
				print '****Add New Posing %s\n%s' %(eachKey[0] , url)		
				self.posting[keyIndex][1].append(keyEle)
			keyIndex += 1	


	def parUrl(self,fileName):
		ext = os.path.splitext(fileName) 
		urlIndex = ext[0].find('http:') + len('http:')
		urlOri = ext[0][urlIndex:]	#remove the extends And the BasePath

		indexQuery = urlOri.rfind('?') 
		if indexQuery == -1:
			return 'http://'+urlOri+ext[1]
		else:
			return 'http://'+urlOri[:indexQuery]+ext[1]+urlOri[indexQuery:]



