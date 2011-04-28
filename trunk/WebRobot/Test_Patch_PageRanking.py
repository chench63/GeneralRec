#!/usr/bin/python

from Patch_PageRanking import dataText, PageRanking

test = dataText()
reStore = []
reLoad = []

baseurl = 'http://www.qq.com/a.html'
url = 'http://www.baidu.com/afli.html'
tstPage = PageRanking(baseurl)



def testStore():
	test.rank = [ [1,'http://www.qq.com/a.html'],[2,'http://www.baidu.com/adfa.jsp']]
	test.store()
	reStore = test.rank
#	print 'Write in File %s' %test.rank

def testLoad():
	test.load()
	reLoad = test.rank
#	print 'Read from File %s' %test.rank


def testaddNewUrlInQue():	
	tstPage.addNewUrlInQue(url)

def testoneGoal():
	tstPage.oneGoal(url)


def main():
	testStore()	
	testLoad()
	
	if not cmp(reStore,reLoad):
		print 'The Store()	Load() is Right!'
	else:
		print 'The Store()	Load() Crash'

	testaddNewUrlInQue()
#	print 'testaddNewUrlInQue() \n%s  \n%s'  %(tstPage.rank, tstPage.rankTable)
	if cmp(tstPage.rankTable[0],baseurl) or cmp(tstPage.rankTable[1],url) or \
		tstPage.rank[0][0] != 1 or tstPage.rank[1][0] != 1 :
		print 'addNewUrlInQue() Crash!'
	else:
		print 'addNewUrlInQue() is right!'

	testoneGoal()
#	print 'testoneGoal() \n%s  \n%s'  %(tstPage.rank, tstPage.rankTable)
	if tstPage.rank[0][0] != 1 or tstPage.rank[1][0] != 2:
		print 'oneGoal() Crash!'
	else:
		print 'oneGoal() is right!'





if __name__ == '__main__':
	main()
