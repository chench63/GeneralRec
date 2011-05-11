#!/usr/bin/python

from LibOper import LoadLib,StoreLib


exPostingEle =[ [1,'www.qq.com','Tencent'],
		[100,'www.sdo.com','snda'],
		[1000,'www.sina.com','sina']
	      ]

keyWordEle1 = ['Tencent',[exPostingEle[0],exPostingEle[1]]]
keyWordEle2 = ['snda',[exPostingEle[0],exPostingEle[1],exPostingEle[2]]]

posting = [keyWordEle1,keyWordEle2]


def TestLoadLibKey():
	ll = LoadLib()
	a=ll.getKey()
	for each in a:
		print each
	print len(a)


def TestLoadLibPosting():
	ll = LoadLib()
	return ll.getPosting()

def TestStoreLib():
	sl = StoreLib()

	

	sl.setPosting(posting)
	sl.storePosting()

def main():
	TestStoreLib()
	a = TestLoadLibPosting()
	
	boolIndex =  True
	index = 0
	while True:   #todo.....
		if not cmp(a[index] , posting[index]):			
			boolIndex = False
			print '%s\n%s\n\n' %(a[index],posting[index])
		index += 1
		if index == 2:
			break

	if boolIndex == True:
		print 'The Operating in Post.lib is Right!'
	else:
		print 'The Operating in Post.lib Crashed'


if __name__ == '__main__':
	main()
	
