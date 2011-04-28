#!/usr/bin/python

from Patch_PageRanking import BACKUPPERMINUTE,PageRanking

baseurl = 'http://www.qq.com/a.html'
url = 'http://www.baidu.com/afli.html'
tstPage = PageRanking(baseurl)
tstPage.oneGoal(url)


def testThread():
	BACKUPPERMINUTE(tstPage)	

def main():
	testThread()
	while True:
		pass

if __name__ == '__main__':
	main()
	
