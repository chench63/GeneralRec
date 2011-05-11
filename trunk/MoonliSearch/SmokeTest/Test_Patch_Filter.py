#!/usr/bin/python

from Patch_Filter import httpFilter, typeFilter, nameFilter

def testHttpFilter():
        str1 = 'a.jsp'
	str2 = 'b.sdakfj'
        
        bool1 = httpFilter(str1)
	bool2 = httpFilter(str2)

	if bool1 and (not bool2):
		print 'HttpFilter is Right!'
	else:
		print 'HttpFilter Crash';

def testTypeFilter():
	str1 = 'http://www.qq.com/a.jsp'
	str2 = 'http://www.qq.com/a.html?id=200'
	str3 = 'http://www.qq.com/asjflkaj.safa'

	bool1 = typeFilter(str1)
	bool2 = typeFilter(str2)
	bool3 = typeFilter(str3)

	if bool1 and bool2 and (not bool3):
		print 'TypeFilter is Right'
	else:
		print 'TypeFilter Crash'

def testNameFilter():
	str1 = 'http://www.qq.com/adf.html?id=1'
	str2 = 'http://www.qq.com/index.html'
	
	bool1 = nameFilter(str1)
	bool2 = nameFilter(str2)

	if bool1 and (not bool2):
		print 'NameFilter is Right!'
	else:
		print 'NameFilter Crash';





def main():
	testHttpFilter()
	testTypeFilter()
	testNameFilter()

if __name__ == '__main__':
	main()
