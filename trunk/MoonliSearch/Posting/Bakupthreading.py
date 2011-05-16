#!/usr/bin/python

import string
import os
from time import sleep,ctime
import threading
import copy

from LibOper import StoreLib



def BackUpPro(Posting,i):
	driver = StoreLib()
	OWN = 1
	while True:
		sleep(15*OWN)
		driver.setPosting(Posting.posting)	
		driver.storePosting()
		OWN += 1


def BACKUPPERMINUTE(Posting):
	r = threading.Thread(target=BackUpPro,
			args=(Posting,1 ))
	r.setDaemon(True)
	r.start()



def SAVEANDQUIT(Posting):
	driver = StoreLib()
	driver.setPosting(Posting.posting)

	for eachKey in driver.posting:
		keyTmp = sorted(eachKey[1])
		eachKey[1] = reversed(keyTmp)

	driver.storePosting()





