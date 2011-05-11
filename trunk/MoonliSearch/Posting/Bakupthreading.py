#!/usr/bin/python

import string
import os
from time import sleep,ctime
import threading
import copy




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
