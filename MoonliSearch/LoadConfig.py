#!/usr/bin/python

from time import ctime


class LoadConfig(object):
	def __init__(self):
		self.LoadConfig()
	
	def LoadConfig(self):
		self.conf = []
		self.confTable = []
		fi = open('moonliSearch.conf','r')
		for eachline in fi.readlines():
			self.matchConf(eachline)

	def matchConf(self,line):
		self.Keyconf = ['BasePath','FILENAME',
				'BASEPATH']
		for eachKey in self.Keyconf:
			if eachKey in line:
				ele = line[:-1]
				self.conf.append(ele)
				self.confTable.append(eachKey)

	def StoreConfig(self):
		PostingConf = ['BasePath']
		if self.__StoreConfig('./Posting/Posting.conf',PostingConf) == False:
			return False

		WebRobotConf = ['FILENAME','BASEPATH']
		if self.__StoreConfig('./WebRobot/WebRobot.conf',WebRobotConf) == False:
			return False

		return True


	def __StoreConfig(self,path,conf):
		try:
			fi = open(path,'w')
			for eachConf in conf:
				index = self.confTable.index(eachConf)
				fi.write('%s\n' %self.conf[index])
			fi.close()
		except (ValueError):
			print 'Load Error : %s' %path
			return False
		return True


def main():
	lc = LoadConfig()
	if lc.StoreConfig() == True:
		print 'Complete The Config at: '  , ctime()
	else:
		print 'Config Failed at: '  , ctime()	



if __name__ == '__main__':
	main()


