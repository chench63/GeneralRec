#!/usr/bin/python

import string
import os
import os.path

from LibOper import LoadLib
from Patch_PageRanking import dataText


class Posting(object):
	def __init__(self):
		ll = LoadLib()
		dt = dataText()
		dt.load()
		self.pageRanking =  dt.rank
		self.key = ll.getKey()
		self.posting = []

		self.pageRankingIndex  = []
		self.buildIndex()


	def buildIndex(self):
		for each in self.pageRanking:	
			self.pageRankingIndex.append(each[1]);

 
	#Binary Search
	def find(self,key):
		pass

	def readPath(self,path):
		for root,dirnames,filenames in os.walk(path):	
				for eachfile in filenames:
					filepath = os.path.join(root,eachfile)
					self.readFile(filepath)

	def readFile(self,fileName):
		fi = open(fileName,'r')
		for eachline in fi.readlines():
			pass

	def matchLine(self,eachline,fileName):
		for eachKey in self.key:
			if eachKey in eachline:
				url=self.parUrl(fileName)	


	def parUrl(self,fileName):
		ext = os.path.splitext(fileName) 
		urlIndex = ext[0].find('www.')
		urlOri = ext[0][urlIndex:]	#remove the extends And the BasePath

		indexQuery = urlOri.rfind('?') 
		if indexQuery == -1:
			return 'http://'+urlOri+ext[1]
		else:
			return 'http://'+urlOri[:indexQuery]+ext[1]+urlOri[indexQuery:]



