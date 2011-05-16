#!/usr/bin/python

from Patch_Posting import Posting
from LibOper import LoadConf
from Bakupthreading import BACKUPPERMINUTE,SAVEANDQUIT


def Posting_Main():
	posting = Posting()
	lConf = LoadConf()

	basePath = lConf.getBasePath()

	BACKUPPERMINUTE(posting)
	posting.readPath(basePath)
	SAVEANDQUIT(posting)


if __name__ == '__main__':
	Posting_Main()
	
