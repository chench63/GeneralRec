#!/usr/bin/python

from Patch_Posting import Posting

p = Posting()
def TestPath():
	root = '/home/ppia/CrawlerWebInfo'
	p.readPath(root)

def TestParUrl():
	fileNormal = '/home/ppia/CrawlerWebInfo/www.zjut.edu.cn/xyw/news/a.jsp'
	fileSpec = '/home/ppia/CrawlerWebInfo/www.zjut.edu.cn/zjut/xyw/news/zbb_show?newsid=9.jsp'
	
	fileNormalTar = 'http://www.zjut.edu.cn/xyw/news/a.jsp'
	fileSpecTar = 'http://www.zjut.edu.cn/zjut/xyw/news/zbb_show.jsp?newsid=9'
	if cmp( p.parUrl(fileNormal) ,fileNormalTar) or cmp( p.parUrl(fileSpec) ,fileSpecTar):
		print 'parUrl From Posting Crashed!'
	else:
		print 'parUrl From Posting is Right!'


if __name__ == '__main__':
	TestParUrl()
